package ir.adicom.kamkam.data

object PlanLimits {
    const val MIN_BASELINE = 1
    const val MAX_BASELINE = 60
    const val MIN_STEP_DAYS = 3
    const val MAX_STEP_DAYS = 30
    const val DEFAULT_BASELINE = 10
}

/** پیش‌نویس برنامه، قبل از ذخیره. همیشه بعد از هر تغییر normalized() صدا زده می‌شود. */
data class PlanDraft(
    val baseline: Int,
    val stepSize: Int = 1,
    val stepDays: Int = 7,
    val finalTarget: Int = 0,
) {
    val maxFinalTarget: Int get() = baseline - 1
    val maxStepSize: Int get() = baseline - finalTarget

    fun normalized(): PlanDraft {
        val b = baseline.coerceIn(PlanLimits.MIN_BASELINE, PlanLimits.MAX_BASELINE)
        val f = finalTarget.coerceIn(0, b - 1)
        val s = stepSize.coerceIn(1, b - f)
        val d = stepDays.coerceIn(PlanLimits.MIN_STEP_DAYS, PlanLimits.MAX_STEP_DAYS)
        return PlanDraft(b, s, d, f)
    }

    /** هدف هر مرحله، از خط پایه تا هدف نهایی. */
    fun stageTargets(): List<Int> {
        val step = stepSize.coerceAtLeast(1)
        val list = mutableListOf(baseline)
        var t = baseline
        while (t > finalTarget) {
            t = maxOf(finalTarget, t - step)
            list += t
        }
        return list
    }
}

data class SuggestedBaseline(val value: Int, val days: Int)

/** میانگین ثبت‌ها در روزهای اول؛ null اگر هنوز داده‌ای نیست. */
fun suggestBaseline(totalCount: Int, daysCovered: Int): SuggestedBaseline? {
    if (totalCount <= 0 || daysCovered <= 0) return null
    val avg = Math.round(totalCount.toDouble() / daysCovered).toInt().coerceAtLeast(1)
    return SuggestedBaseline(avg, daysCovered)
}