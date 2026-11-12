package ir.adicom.kamkam.ui.report

import ir.adicom.kamkam.data.ReportPeriod

data class BarUi(
    val label: String,
    val sublabel: String,
    val value: Int,
    /** فقط در تب روزانه و فقط برای روزهایی که برنامه‌ی فعال بوده */
    val goal: Int?,
    val isCurrent: Boolean,
) {
    val overGoal: Boolean get() = goal != null && value > goal
}

data class InGoalUi(val inGoal: Int, val of: Int)

data class DayRowUi(val epochDay: Long, val title: String, val count: Int)

data class ReportUiState(
    val period: ReportPeriod,
    val canGoNewer: Boolean,
    val rangeTitle: String,
    val total: Int,
    val averageText: String,
    /** null یعنی در این بازه هیچ روزی هدف نداشته */
    val inGoal: InGoalUi?,
    val bars: List<BarUi>,
    /** فقط تب روزانه، جدیدترین اول */
    val days: List<DayRowUi>,
    val hourly: HourlyUi = HourlyUi.Empty,
)

data class ReportActions(
    val onPeriodSelected: (ReportPeriod) -> Unit = {},
    val onOlder: () -> Unit = {},
    val onNewer: () -> Unit = {},
    val onOpenDay: (Long) -> Unit = {},
)

/** level: ۰ (خالی) تا ۴ (پرتکرارترین) */
data class HourCellUi(val hour: Int, val count: Int, val level: Int)

data class DayPartUi(val startHour: Int, val endHour: Int, val percent: Int, val cells: List<HourCellUi>)

data class HourlyUi(
    val parts: List<DayPartUi>,
    /** null یعنی داده برای جمله‌ی نتیجه‌گیری کافی نیست */
    val peakStartHour: Int?,
    val peakEndHour: Int?,
    val total: Int,
) {
    companion object {
        val Empty = HourlyUi(emptyList(), null, null, 0)
    }
}

fun buildHourlyUi(counts: IntArray): HourlyUi {
    val total = counts.sum()
    val max = counts.maxOrNull() ?: 0

    fun levelOf(n: Int): Int {
        if (n <= 0 || max == 0) return 0
        val r = n.toFloat() / max
        return when {
            r >= 0.8f -> 4
            r >= 0.55f -> 3
            r >= 0.3f -> 2
            else -> 1
        }
    }

    val parts = (0 until 4).map { p ->
        val start = p * 6
        val cells = (start until start + 6).map { h -> HourCellUi(h, counts[h], levelOf(counts[h])) }
        val sum = cells.sumOf { it.count }
        DayPartUi(
            startHour = start,
            endHour = start + 5,
            percent = if (total == 0) 0 else Math.round(sum * 100f / total),
            cells = cells,
        )
    }
    val peak = if (total >= ir.adicom.kamkam.data.MIN_HOURLY_INSIGHT_TOTAL)
        ir.adicom.kamkam.data.peakWindow(counts) else null
    return HourlyUi(parts, peak?.first, peak?.second, total)
}