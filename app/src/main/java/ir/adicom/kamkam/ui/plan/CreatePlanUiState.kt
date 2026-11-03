package ir.adicom.kamkam.ui.plan

import ir.adicom.kamkam.data.PlanDraft
import ir.adicom.kamkam.data.PlanLimits

data class StepperUi(val value: Int, val canDecrement: Boolean, val canIncrement: Boolean)

sealed interface PreviewItem {
    data class Stage(val index: Int, val target: Int, val fraction: Float, val isCurrent: Boolean) : PreviewItem
    data object Gap : PreviewItem
}

data class CreatePlanUiState(
    val baseline: StepperUi,
    val stepSize: StepperUi,
    val stepDays: StepperUi,
    val finalTarget: StepperUi,
    /** null یعنی خط پایه از داده نیامده و کاربر باید خودش تنظیم کند */
    val baselineHintDays: Int?,
    val preview: List<PreviewItem>,
    val weeksToGoal: Int,
)

data class CreatePlanActions(
    val onBack: () -> Unit = {},
    val onBaselineDelta: (Int) -> Unit = {},
    val onStepSizeDelta: (Int) -> Unit = {},
    val onStepDaysDelta: (Int) -> Unit = {},
    val onFinalTargetDelta: (Int) -> Unit = {},
    val onStart: () -> Unit = {},
)

/** تبدیل پیش‌نویس به وضعیت UI؛ مشترک بین ViewModel و Preview. */
fun PlanDraft.toUiState(baselineHintDays: Int?): CreatePlanUiState {
    val targets = stageTargets()
    val preview: List<PreviewItem> = if (targets.size <= 6) {
        targets.mapIndexed { i, t -> stageItem(i + 1, t) }
    } else {
        targets.take(4).mapIndexed { i, t -> stageItem(i + 1, t) } +
                PreviewItem.Gap +
                stageItem(targets.size, targets.last())
    }
    val totalDays = (targets.size - 1) * stepDays

    return CreatePlanUiState(
        baseline = StepperUi(baseline, baseline > PlanLimits.MIN_BASELINE, baseline < PlanLimits.MAX_BASELINE),
        stepSize = StepperUi(stepSize, stepSize > 1, stepSize < maxStepSize),
        stepDays = StepperUi(stepDays, stepDays > PlanLimits.MIN_STEP_DAYS, stepDays < PlanLimits.MAX_STEP_DAYS),
        finalTarget = StepperUi(finalTarget, finalTarget > 0, finalTarget < maxFinalTarget),
        baselineHintDays = baselineHintDays,
        preview = preview,
        weeksToGoal = (totalDays + 6) / 7,
    )
}

private fun PlanDraft.stageItem(index: Int, target: Int) = PreviewItem.Stage(
    index = index,
    target = target,
    fraction = target / baseline.toFloat(),
    isCurrent = index == 1,
)