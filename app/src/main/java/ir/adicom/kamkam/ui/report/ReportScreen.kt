package ir.adicom.kamkam.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ir.adicom.kamkam.R
import ir.adicom.kamkam.data.ReportPeriod
import ir.adicom.kamkam.ui.home.toFa
import ir.adicom.kamkam.ui.theme.KamkamTheme

@Composable
fun ReportScreen(
    state: ReportUiState,
    actions: ReportActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            stringResource(R.string.report_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp),
        )

        PeriodTabs(state.period, actions.onPeriodSelected)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // در RTL فلش «قبل» سمت راست است؛ AutoMirrored خودکار جهتش را درست می‌کند.
            IconButton(onClick = actions.onOlder) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = stringResource(R.string.report_older))
            }
            Text(state.rangeTitle, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            IconButton(onClick = actions.onNewer, enabled = state.canGoNewer) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = stringResource(R.string.report_newer))
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(state.total.toFa(), stringResource(R.string.report_stat_total), Modifier.weight(1f))
            StatCard(state.averageText, stringResource(R.string.report_stat_average), Modifier.weight(1f))
            state.inGoal?.let {
                StatCard(
                    stringResource(R.string.report_in_goal_value, it.inGoal.toFa(), it.of.toFa()),
                    stringResource(R.string.report_stat_in_goal),
                    Modifier.weight(1f),
                )
            }
        }

        BarChartCard(state.bars)

        if (state.days.isNotEmpty()) {
            Text(
                stringResource(R.string.report_days_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp),
            )
            Column {
                state.days.forEachIndexed { i, day ->
                    DayRow(day, onClick = { actions.onOpenDay(day.epochDay) })
                    if (i != state.days.lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun PeriodTabs(selected: ReportPeriod, onSelect: (ReportPeriod) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ReportPeriod.values().forEach { p ->
            val isSelected = p == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                    .selectable(selected = isSelected, role = Role.Tab, onClick = { onSelect(p) }),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    stringResource(
                        when (p) {
                            ReportPeriod.DAILY -> R.string.report_tab_daily
                            ReportPeriod.WEEKLY -> R.string.report_tab_weekly
                            ReportPeriod.MONTHLY -> R.string.report_tab_monthly
                        }
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private val ChartHeight = 140.dp
private val ValueLabelReserve = 20.dp

@Composable
private fun BarChartCard(bars: List<BarUi>) {
    val maxValue = bars.maxOf { maxOf(it.value, it.goal ?: 0) }.coerceAtLeast(1)
    val unit: Dp = (ChartHeight - ValueLabelReserve) / maxValue
    val hasGoal = bars.any { it.goal != null }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
            // جهت چیدن را سیستم با RTL تنظیم می‌کند: قدیمی‌ترین سمت راست
            Row(modifier = Modifier.fillMaxWidth()) {
                bars.forEach { bar -> BarColumn(bar, unit, Modifier.weight(1f)) }
            }
            if (hasGoal) {
                Text(
                    stringResource(R.string.report_goal_legend),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun BarColumn(bar: BarUi, unit: Dp, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val barColor = when {
        bar.overGoal -> MaterialTheme.colorScheme.tertiary   // بالای هدف: کهربایی، نه قرمز
        bar.isCurrent -> primary
        else -> primary.copy(alpha = 0.4f)
    }
    val goalColor = MaterialTheme.colorScheme.onSurfaceVariant
    val desc = stringResource(R.string.report_bar_desc, "${bar.label} ${bar.sublabel}".trim(), bar.value.toFa())

    Column(
        modifier = modifier.semantics(mergeDescendants = true) { contentDescription = desc },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ChartHeight)
                .drawBehind {
                    bar.goal?.let { g ->
                        val y = size.height - unit.toPx() * g
                        drawLine(
                            color = goalColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)),
                        )
                    }
                },
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    bar.value.toFa(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (bar.isCurrent) FontWeight.Bold else FontWeight.Normal,
                )
                Spacer(Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(unit * bar.value)
                        .background(barColor, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 3.dp, bottomEnd = 3.dp)),
                )
            }
        }
        Text(
            bar.label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (bar.isCurrent) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(top = 6.dp),
        )
        if (bar.sublabel.isNotEmpty()) {
            Text(bar.sublabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DayRow(day: DayRowUi, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable(role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(day.title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(day.count.toFa(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        Spacer(Modifier.width(8.dp))
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private val sampleState = ReportUiState(
    period = ReportPeriod.DAILY,
    canGoNewer = false,
    rangeTitle = "۴ تا ۱۰ آبان",
    total = 30,
    averageText = "۴٫۳",
    inGoal = InGoalUi(6, 7),
    bars = listOf(
        BarUi("د", "۴", 6, 5, false), BarUi("س", "۵", 5, 5, false), BarUi("چ", "۶", 4, 5, false),
        BarUi("پ", "۷", 5, 5, false), BarUi("ج", "۸", 3, 5, false), BarUi("ش", "۹", 4, 5, false),
        BarUi("ی", "۱۰", 3, 5, true),
    ),
    days = listOf(
        DayRowUi(1, "یکشنبه ۱۰ آبان · امروز", 3),
        DayRowUi(2, "شنبه ۹ آبان", 4),
        DayRowUi(3, "جمعه ۸ آبان", 3),
    ),
)

private const val SPEC = "spec:width=390dp,height=844dp,dpi=420"

@Preview(name = "روزانه", device = SPEC, showBackground = true)
@Composable
private fun ReportDailyPreview() = KamkamTheme { ReportScreen(sampleState, ReportActions()) }

@Preview(name = "ماهانه", device = SPEC, showBackground = true)
@Composable
private fun ReportMonthlyPreview() = KamkamTheme {
    ReportScreen(
        sampleState.copy(
            period = ReportPeriod.MONTHLY,
            canGoNewer = true,
            rangeTitle = "خرداد تا آبان",
            inGoal = null,
            days = emptyList(),
            bars = listOf(
                BarUi("خرداد", "", 150, null, false), BarUi("تیر", "", 142, null, false),
                BarUi("مرداد", "", 130, null, false), BarUi("شهریور", "", 118, null, false),
                BarUi("مهر", "", 104, null, false), BarUi("آبان", "", 30, null, true),
            ),
        ),
        ReportActions(),
    )
}