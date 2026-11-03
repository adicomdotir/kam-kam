package ir.adicom.kamkam.ui.plan

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.adicom.kamkam.data.PlanDraft
import ir.adicom.kamkam.data.PlanLimits
import ir.adicom.kamkam.data.entity.ReductionPlan
import ir.adicom.kamkam.data.repository.PlanRepository
import ir.adicom.kamkam.data.repository.SmokeRepository
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
import java.time.LocalDate
import javax.inject.Inject

sealed interface CreatePlanEvent {
    data object Created : CreatePlanEvent
}


@HiltViewModel
class CreatePlanViewModel @RequiresApi(Build.VERSION_CODES.O)
@Inject constructor (
    private val smoke: SmokeRepository,
    private val plans: PlanRepository,
    private val clock: Clock,
) : ViewModel() {

    private val draft = MutableStateFlow<PlanDraft?>(null)
    private val baselineHintDays = MutableStateFlow<Int?>(null)
    private val _events = Channel<CreatePlanEvent>(Channel.BUFFERED)
    val events: Flow<CreatePlanEvent> = _events.receiveAsFlow()
    private var starting = false

    /** null تا وقتی خط پایه‌ی پیشنهادی محاسبه نشده؛ از پرش عدد روی صفحه جلوگیری می‌کند. */
    val uiState: StateFlow<CreatePlanUiState?> =
        combine(draft, baselineHintDays) { d, days -> d?.toUiState(days) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        viewModelScope.launch {
            val suggested = smoke.suggestedBaseline(LocalDate.now(clock), clock.zone)
            baselineHintDays.value = suggested?.days
            draft.value = PlanDraft(baseline = suggested?.value ?: PlanLimits.DEFAULT_BASELINE).normalized()
        }
    }

    fun onBaselineDelta(delta: Int) = change { it.copy(baseline = it.baseline + delta) }
    fun onStepSizeDelta(delta: Int) = change { it.copy(stepSize = it.stepSize + delta) }
    fun onStepDaysDelta(delta: Int) = change { it.copy(stepDays = it.stepDays + delta) }
    fun onFinalTargetDelta(delta: Int) = change { it.copy(finalTarget = it.finalTarget + delta) }

    private fun change(transform: (PlanDraft) -> PlanDraft) {
        draft.update { cur -> cur?.let(transform)?.normalized() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onStart() {
        val d = draft.value ?: return
        if (starting) return
        starting = true
        viewModelScope.launch {
            plans.start(
                ReductionPlan(
                    startEpochDay = LocalDate.now(clock).toEpochDay(),
                    baseline = d.baseline,
                    stepSize = d.stepSize,
                    stepDays = d.stepDays,
                    finalTarget = d.finalTarget,
                )
            )
            _events.send(CreatePlanEvent.Created)
        }
    }
}