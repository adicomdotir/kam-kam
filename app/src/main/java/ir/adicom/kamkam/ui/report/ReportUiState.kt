package ir.adicom.kamkam.ui.report

import ir.adicom.kamkam.data.ReportPeriod
import ir.adicom.kamkam.data.dao.TriggerCount
import ir.adicom.kamkam.data.entity.Trigger

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
    val triggers: TriggersUi = TriggersUi.Empty,
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

private const val MIN_TRIGGER_INSIGHT_TOTAL = 5
private const val MAX_TRIGGER_ROWS = 5

enum class TriggerRowKind { TAGGED, OTHER, UNTAGGED }

data class TriggerRowUi(
    val kind: TriggerRowKind,
    /** فقط برای TAGGED؛ «سایر» و «بدون برچسب» از strings.xml می‌آیند */
    val label: String?,
    val count: Int,
    val percent: Int,
    /** نسبت به بلندترین میله، بین ۰ و ۱ */
    val fraction: Float,
)

data class TriggersUi(
    val rows: List<TriggerRowUi>,
    /** null یعنی داده برای جمله‌ی نتیجه‌گیری کافی نیست */
    val topLabel: String?,
    val topPercent: Int,
    val total: Int,
) {
    companion object {
        val Empty = TriggersUi(emptyList(), null, 0, 0)
    }
}

/**
 * counts فقط نخ‌های برچسب‌دار را دارد؛ تعداد بدون برچسب از totalLogs منهای آنها به‌دست می‌آید.
 * درصدها نسبت به کل نخ‌های بازه‌اند (شامل بدون برچسب).
 */
fun buildTriggersUi(
    counts: List<TriggerCount>,
    triggers: List<Trigger>,
    totalLogs: Int,
    labelOf: (Trigger) -> String,
): TriggersUi {
    if (totalLogs <= 0) return TriggersUi.Empty

    val byId = triggers.associateBy { it.id }
    val tagged = counts.filter { byId.containsKey(it.triggerId) }.sortedByDescending { it.count }
    val taggedTotal = tagged.sumOf { it.count }
    val untagged = (totalLogs - taggedTotal).coerceAtLeast(0)
    val rest = tagged.drop(MAX_TRIGGER_ROWS).sumOf { it.count }

    val raw = buildList<Triple<TriggerRowKind, String?, Int>> {
        tagged.take(MAX_TRIGGER_ROWS).forEach { add(Triple(TriggerRowKind.TAGGED, labelOf(byId.getValue(it.triggerId)), it.count)) }
        if (rest > 0) add(Triple(TriggerRowKind.OTHER, null, rest))
        if (untagged > 0) add(Triple(TriggerRowKind.UNTAGGED, null, untagged))
    }
    val maxCount = raw.maxOfOrNull { it.third }?.coerceAtLeast(1) ?: 1
    val rows = raw.map { (kind, label, n) ->
        TriggerRowUi(kind, label, n, Math.round(n * 100f / totalLogs), n.toFloat() / maxCount)
    }

    val top = rows.firstOrNull { it.kind == TriggerRowKind.TAGGED }
    val showInsight = top != null && taggedTotal >= MIN_TRIGGER_INSIGHT_TOTAL
    return TriggersUi(
        rows = rows,
        topLabel = if (showInsight) top?.label else null,
        topPercent = if (showInsight) top?.percent ?: 0 else 0,
        total = totalLogs,
    )
}
