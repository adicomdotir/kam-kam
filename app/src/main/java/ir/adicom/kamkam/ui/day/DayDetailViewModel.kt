package ir.adicom.kamkam.ui.day

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ir.adicom.kamkam.data.database.AppDatabase
import ir.adicom.kamkam.data.repository.SmokeRepository
import ir.adicom.kamkam.data.repository.TriggerRepository
import ir.adicom.kamkam.data.startMillis
import ir.adicom.kamkam.ui.home.AndroidHomeFormatter
import ir.adicom.kamkam.ui.home.HomeFormatter
import ir.adicom.kamkam.ui.home.LogItemUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate

data class DayDetailUiState(val title: String, val logs: List<LogItemUi>)

class DayDetailViewModel(
    epochDay: Long,
    private val smoke: SmokeRepository,
    triggers: TriggerRepository,
    formatter: HomeFormatter,
    private val clock: Clock,
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val date = LocalDate.ofEpochDay(epochDay)
    @RequiresApi(Build.VERSION_CODES.O)
    private val from = date.startMillis(clock.zone)
    @RequiresApi(Build.VERSION_CODES.O)
    private val to = date.plusDays(1).startMillis(clock.zone)

    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<DayDetailUiState?> =
        combine(smoke.observeBetween(from, to), triggers.observeAll()) { logs, all ->
            val byId = all.associateBy { it.id }
            DayDetailUiState(
                title = formatter.date(from),
                logs = logs.map { log ->
                    LogItemUi(
                        id = log.id,
                        timeText = formatter.time(log.timestamp),
                        triggerLabel = log.triggerId?.let { byId[it] }?.let(formatter::triggerLabel),
                    )
                },
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    @RequiresApi(Build.VERSION_CODES.O)
    fun onDelete(id: Long) {
        viewModelScope.launch { smoke.softDelete(id, clock.millis()) }
    }

    fun onUndoDelete(id: Long) {
        viewModelScope.launch { smoke.restore(id) }
    }

    companion object {
        fun factory(app: Application) = viewModelFactory {
            initializer {
                val handle = createSavedStateHandle()
                val db = AppDatabase.get(app)
                DayDetailViewModel(
                    epochDay = handle.get<Long>("epochDay") ?: LocalDate.now().toEpochDay(),
                    smoke = SmokeRepository(db.smokeLogDao()),
                    triggers = TriggerRepository(db.triggerDao()),
                    formatter = AndroidHomeFormatter(app),
                    clock = Clock.systemDefaultZone(),
                )
            }
        }
    }
}
