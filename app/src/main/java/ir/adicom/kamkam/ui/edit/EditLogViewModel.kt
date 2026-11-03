package ir.adicom.kamkam.ui.edit

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
import ir.adicom.kamkam.data.database.AppDatabase
import ir.adicom.kamkam.data.entity.SmokeLog
import ir.adicom.kamkam.data.entity.Trigger
import ir.adicom.kamkam.data.repository.SmokeRepository
import ir.adicom.kamkam.data.repository.TriggerRepository
import ir.adicom.kamkam.data.startMillis
import ir.adicom.kamkam.ui.home.HomeFormatter
import ir.adicom.kamkam.ui.home.TriggerUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

sealed interface EditLogEvent {
    data object Saved : EditLogEvent
    data class Deleted(val id: Long) : EditLogEvent
    /** ثبتی که باز شد وجود ندارد یا حذف شده */
    data object Closed : EditLogEvent
}

/** logId == 0 یعنی افزودن ثبت جدید. */
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class EditLogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val smoke: SmokeRepository,
    triggers: TriggerRepository,
    private val formatter: HomeFormatter,
    private val clock: Clock,
) : ViewModel() {

    private data class Form(
        val dayOffset: Int,
        val hour: Int,
        val minute: Int,
        val triggerId: Long?,
        val intensity: Int?,
        val note: String,
    )

    private val logId = checkNotNull(savedStateHandle["logId"]) ?: 0L

    private val form = MutableStateFlow<Form?>(null)
    private val _events = Channel<EditLogEvent>(Channel.BUFFERED)
    val events: Flow<EditLogEvent> = _events.receiveAsFlow()
    private var busy = false

    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<EditLogUiState?> =
        combine(form, triggers.observeAll()) { f, all -> f?.let { toUi(it, all) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        viewModelScope.launch { load() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun load() {
        if (logId == 0L) {
            val now = ZonedDateTime.now(clock)
            form.value = Form(0, now.hour, now.minute, null, null, "")
            return
        }
        val log = smoke.getById(logId)
        if (log == null || log.deletedAt != null) {
            _events.send(EditLogEvent.Closed)
            return
        }
        val at = Instant.ofEpochMilli(log.timestamp).atZone(clock.zone)
        val offset = ChronoUnit.DAYS.between(at.toLocalDate(), LocalDate.now(clock)).toInt().coerceAtLeast(0)
        form.value = Form(offset, at.hour, at.minute, log.triggerId, log.intensity, log.note.orEmpty())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun timestampOf(f: Form): Long =
        ZonedDateTime.of(
            LocalDate.now(clock).minusDays(f.dayOffset.toLong()),
            LocalTime.of(f.hour, f.minute),
            clock.zone,
        ).toInstant().toEpochMilli()

    /** زمان در آینده ممکن نیست؛ به «همین الان» برمی‌گردد. */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun clamp(f: Form): Form {
        if (timestampOf(f) <= clock.millis()) return f
        val now = ZonedDateTime.now(clock)
        return f.copy(hour = now.hour, minute = now.minute)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun toUi(f: Form, all: List<Trigger>): EditLogUiState {
        val today = LocalDate.now(clock)
        val offsets = (0..6).toList() + if (f.dayOffset > 6) listOf(f.dayOffset) else emptyList()
        return EditLogUiState(
            isEdit = logId != 0L,
            days = offsets.map { o ->
                DayChipUi(o, formatter.dayLabel(o, today.minusDays(o.toLong()).startMillis(clock.zone)))
            },
            selectedDayOffset = f.dayOffset,
            hour = f.hour,
            minute = f.minute,
            timeText = formatter.time(timestampOf(f)),
            triggers = all.filter { !it.isArchived || it.id == f.triggerId }
                .map { TriggerUi(it.id, formatter.triggerLabel(it)) },
            selectedTriggerId = f.triggerId,
            intensity = f.intensity,
            note = f.note,
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun change(transform: (Form) -> Form) {
        form.update { cur -> cur?.let(transform)?.let(::clamp) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onDaySelected(offset: Int) = change { it.copy(dayOffset = offset) }
    @RequiresApi(Build.VERSION_CODES.O)
    fun onTimePicked(hour: Int, minute: Int) = change { it.copy(hour = hour, minute = minute) }
    @RequiresApi(Build.VERSION_CODES.O)
    fun onTriggerSelected(id: Long) = change { it.copy(triggerId = if (it.triggerId == id) null else id) }
    @RequiresApi(Build.VERSION_CODES.O)
    fun onIntensitySelected(value: Int) = change { it.copy(intensity = if (it.intensity == value) null else value) }
    @RequiresApi(Build.VERSION_CODES.O)
    fun onNoteChange(text: String) = change { it.copy(note = text.take(200)) }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSave() {
        val f = form.value ?: return
        if (busy) return
        busy = true
        viewModelScope.launch {
            val original = if (logId != 0L) smoke.getById(logId) else null
            smoke.save(
                SmokeLog(
                    id = logId,
                    timestamp = timestampOf(f),
                    triggerId = f.triggerId,
                    intensity = f.intensity,
                    note = f.note.trim().ifEmpty { null },
                    deletedAt = original?.deletedAt,
                )
            )
            _events.send(EditLogEvent.Saved)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onDelete() {
        if (logId == 0L || busy) return
        busy = true
        viewModelScope.launch {
            smoke.softDelete(logId, clock.millis())
            _events.send(EditLogEvent.Deleted(logId))
        }
    }
}