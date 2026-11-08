package ir.adicom.kamkam.ui.craving

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.adicom.kamkam.data.repository.CravingRepository
import ir.adicom.kamkam.data.repository.TriggerRepository
import ir.adicom.kamkam.ui.home.HomeFormatter
import ir.adicom.kamkam.ui.home.TriggerUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Clock
import javax.inject.Inject

sealed interface CravingEvent {
    data class Done(val resisted: Boolean) : CravingEvent
}

/**
 * زمان‌ها در SavedStateHandle می‌مانند، پس با چرخش صفحه، رفتن اپ به پس‌زمینه
 * و حتی kill شدن پردازش تایمر درست ادامه پیدا می‌کند. تایمر از ساعت سیستم حساب می‌شود،
 * نه از شمارنده‌ی داخل UI.
 */
@HiltViewModel
class CravingViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val repo: CravingRepository,
    triggers: TriggerRepository,
    private val formatter: HomeFormatter,
    private val clock: Clock,
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val startedAt: Long = handle.get<Long>(K_STARTED) ?: clock.millis().also { handle[K_STARTED] = it }
    private val roundStart = handle.getStateFlow(K_ROUND_START, handle.get<Long>(K_ROUND_START) ?: startedAt)
    private val selectedTrigger = handle.getStateFlow<Long?>(K_TRIGGER, null)

    private val _events = Channel<CravingEvent>(Channel.BUFFERED)
    val events: Flow<CravingEvent> = _events.receiveAsFlow()
    private var busy = false

    private val ticker: Flow<Unit> = flow {
        while (true) {
            emit(Unit)
            delay(1_000)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<CravingUiState?> =
        combine(ticker, roundStart, selectedTrigger, triggers.observeAll()) { _, start, selected, all ->
            val elapsed = ((clock.millis() - start) / 1000).toInt().coerceAtLeast(0)
            CravingUiState(
                remainingSeconds = (CRAVING_ROUND_SECONDS - elapsed).coerceAtLeast(0),
                elapsedInRound = elapsed,
                triggers = all.filter { !it.isArchived || it.id == selected }
                    .map { TriggerUi(it.id, formatter.triggerLabel(it)) },
                selectedTriggerId = selected,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun onTriggerSelected(id: Long) {
        handle[K_TRIGGER] = if (selectedTrigger.value == id) null else id
    }

    /** «۱۰ دقیقه‌ی دیگر»: دور جدید از همین لحظه شروع می‌شود؛ مدت کل از شروع اول حساب می‌ماند. */
    @RequiresApi(Build.VERSION_CODES.O)
    fun onExtend() {
        handle[K_ROUND_START] = clock.millis()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onResisted() = finish(resisted = true)
    @RequiresApi(Build.VERSION_CODES.O)
    fun onSmoked() = finish(resisted = false)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun finish(resisted: Boolean) {
        if (busy) return
        busy = true
        viewModelScope.launch {
            val now = clock.millis()
            val seconds = ((now - startedAt) / 1000).toInt().coerceAtLeast(0)
            val trigger = selectedTrigger.value
            if (resisted) repo.recordResisted(startedAt, trigger, seconds)
            else repo.recordSmoked(startedAt, now, trigger, seconds)
            _events.send(CravingEvent.Done(resisted))
        }
    }

    companion object {
        private const val K_STARTED = "startedAt"
        private const val K_ROUND_START = "roundStart"
        private const val K_TRIGGER = "triggerId"
    }
}