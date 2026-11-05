package ir.adicom.kamkam.ui.report

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
)

data class ReportActions(
    val onPeriodSelected: (ReportPeriod) -> Unit = {},
    val onOlder: () -> Unit = {},
    val onNewer: () -> Unit = {},
    val onOpenDay: (Long) -> Unit = {},
)