package ir.adicom.kamkam.ui.home

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.adicom.kamkam.data.database.AppDatabase
import ir.adicom.kamkam.data.repository.PlanRepository
import ir.adicom.kamkam.data.entity.ReductionPlan
import ir.adicom.kamkam.data.entity.SmokeLog
import ir.adicom.kamkam.data.repository.SmokeRepository
import ir.adicom.kamkam.data.entity.Trigger
import ir.adicom.kamkam.data.minuteTicker
import ir.adicom.kamkam.data.repository.TriggerRepository
import ir.adicom.kamkam.data.startMillis
import ir.adicom.kamkam.data.todayFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import java.util.concurrent.TimeUnit

/** رویدادهای یک‌باره: ناوبری و Snackbar */
sealed interface HomeEvent {
    data class LogDeleted(val id: Long) : HomeEvent
    data class OpenEdit(val id: Long) : HomeEvent
    data object OpenCraving : HomeEvent
    data object OpenAddCustomTime : HomeEvent
    data object OpenSettings : HomeEvent
    data object OpenCreatePlan : HomeEvent
}

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel(
    private val smoke: SmokeRepository,
    triggers: TriggerRepository,
    plans: PlanRepository,
    private val formatter: HomeFormatter,
    private val clock: Clock,
) : ViewModel() {

    /** ثبت همان لحظه در دیتابیس نوشته می‌شود؛ پنل فقط جزئیات را به‌روز می‌کند. */
    private data class SheetDraft(
        val logId: Long,
        val timestamp: Long,
        val triggerId: Long? = null,
        val intensity: Int? = null,
    )

    private data class HomeData(
        val date: LocalDate,
        val logs: List<SmokeLog>,
        val lastTimestamp: Long?,
        val plan: ReductionPlan?,
        val triggers: List<Trigger>,
    )

    private val draft = MutableStateFlow<SheetDraft?>(null)
    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val data: Flow<HomeData> = todayFlow(clock).flatMapLatest { date ->
        val from = date.startMillis(clock.zone)
        val to = date.plusDays(1).startMillis(clock.zone)
        combine(
            smoke.observeBetween(from, to),
            smoke.observeLastTimestamp(),
            plans.observeActive(),
            triggers.observeAll(),
        ) { logs, last, plan, all -> HomeData(date, logs, last, plan, all) }
    }

    /** null یعنی هنوز بارگذاری نشده؛ صفحه تا آماده شدن چیزی نشان نمی‌دهد (بدون چشمک «بدون برنامه»). */
    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<HomeUiState?> =
        combine(data, minuteTicker(), draft) { d, _, sheet -> buildState(d, sheet) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        // soft delete های قدیمی‌تر از ۳۰ روز پاک می‌شوند.
        viewModelScope.launch {
            smoke.purgeDeletedBefore(clock.millis() - TimeUnit.DAYS.toMillis(30))
        }
    }

    private fun buildState(d: HomeData, sheet: SheetDraft?): HomeUiState {
        val now = clock.millis()
        val triggerById = d.triggers.associateBy { it.id }
        val epochDay = d.date.toEpochDay()

        return HomeUiState(
            dateText = formatter.date(now),
            todayCount = d.logs.size,
            plan = d.plan?.let {
                PlanUi(
                    stageLabel = formatter.stage(it.stageForEpochDay(epochDay)),
                    target = it.targetForEpochDay(epochDay),
                )
            },
            sinceLastText = d.lastTimestamp?.let {
                formatter.sinceLast(((now - it) / 60_000).coerceAtLeast(0))
            },
            logs = d.logs.map { log ->
                LogItemUi(
                    id = log.id,
                    timeText = formatter.time(log.timestamp),
                    triggerLabel = log.triggerId?.let { triggerById[it] }?.let(formatter::triggerLabel),
                )
            },
            sheet = sheet?.let {
                PostLogSheetUi(
                    timeText = formatter.time(it.timestamp),
                    triggers = d.triggers.filter { t -> !t.isArchived }
                        .map { t -> TriggerUi(t.id, formatter.triggerLabel(t)) },
                    selectedTriggerId = it.triggerId,
                    intensity = it.intensity,
                )
            },
        )
    }

    // ---------- رویدادهای UI ----------

    fun onLogSmoke() {
        viewModelScope.launch {
            val ts = clock.millis()
            val id = smoke.log(ts)
            draft.value = SheetDraft(logId = id, timestamp = ts)
        }
    }

    fun onSheetTriggerSelected(id: Long) {
        val d = draft.updateAndGet { cur ->
            cur?.copy(triggerId = if (cur.triggerId == id) null else id)
        } ?: return
        viewModelScope.launch { smoke.updateDetails(d.logId, d.triggerId, d.intensity) }
    }

    fun onSheetIntensitySelected(value: Int) {
        val d = draft.updateAndGet { cur ->
            cur?.copy(intensity = if (cur.intensity == value) null else value)
        } ?: return
        viewModelScope.launch { smoke.updateDetails(d.logId, d.triggerId, d.intensity) }
    }

    /** «ذخیره» و «رد کردن» و بستن بیرونی؛ جزئیات انتخاب‌شده قبلاً ذخیره شده. */
    fun onSheetClose() {
        draft.value = null
    }

    fun onSheetUndo() {
        val d = draft.value ?: return
        draft.value = null
        viewModelScope.launch { smoke.softDelete(d.logId, clock.millis()) }
    }

    fun onDeleteLog(id: Long) {
        viewModelScope.launch {
            smoke.softDelete(id, clock.millis())
            _events.send(HomeEvent.LogDeleted(id))
        }
    }

    fun onUndoDelete(id: Long) {
        viewModelScope.launch { smoke.restore(id) }
    }

    fun onEditLog(id: Long) = emit(HomeEvent.OpenEdit(id))
    fun onCraving() = emit(HomeEvent.OpenCraving)
    fun onAddWithCustomTime() = emit(HomeEvent.OpenAddCustomTime)
    fun onOpenSettings() = emit(HomeEvent.OpenSettings)
    fun onCreatePlan() = emit(HomeEvent.OpenCreatePlan)

    private fun emit(event: HomeEvent) {
        viewModelScope.launch { _events.send(event) }
    }
}