package ir.adicom.kamkam.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.adicom.kamkam.R
import ir.adicom.kamkam.ui.home.toFa
import ir.adicom.kamkam.ui.theme.KamkamTheme

@Composable
fun HeatmapCard(hourly: HourlyUi, rangeTitle: String, modifier: Modifier = Modifier) {
    var selectedHour by rememberSaveable { mutableStateOf<Int?>(null) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.report_hourly_title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(rangeTitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (hourly.total == 0) {
                Text(
                    stringResource(R.string.report_hourly_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                )
                return@Column
            }

            InsightText(hourly, Modifier.padding(horizontal = 4.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                hourly.parts.forEach { part ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Column(modifier = Modifier.width(52.dp)) {
                            Text(
                                "${part.startHour.toFa()}–${part.endHour.toFa()}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                            )
                            Text(
                                stringResource(R.string.report_percent, part.percent.toFa()),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            part.cells.forEach { cell ->
                                HourCell(
                                    cell = cell,
                                    isSelected = cell.hour == selectedHour,
                                    onClick = { selectedHour = if (selectedHour == cell.hour) null else cell.hour },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }

            // پیام تپ روی یک خانه
            selectedHour?.let { h ->
                val count = hourly.parts.flatMap { it.cells }.first { it.hour == h }.count
                Text(
                    stringResource(R.string.report_hourly_cell_desc, h.toFa(), (h + 1).toFa(), count.toFa()),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }

            Legend(Modifier.align(Alignment.End))
        }
    }
}

@Composable
private fun InsightText(hourly: HourlyUi, modifier: Modifier = Modifier) {
    val start = hourly.peakStartHour
    val end = hourly.peakEndHour
    if (start == null || end == null) {
        Text(
            stringResource(R.string.report_hourly_not_enough),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier,
        )
        return
    }
    val range = "${start.toFa()} تا ${end.toFa()}"
    val text = stringResource(R.string.report_hourly_insight, range)
    val accent = MaterialTheme.colorScheme.primary
    Text(
        text = buildAnnotatedString {
            append(text)
            val i = text.indexOf(range)
            if (i >= 0) addStyle(SpanStyle(fontWeight = FontWeight.Bold, color = accent), i, i + range.length)
        },
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier,
    )
}

@Composable
private fun HourCell(cell: HourCellUi, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bg = levelColor(cell.level)
    val fg = if (bg.luminance() > 0.35f) Color(0xFF171D1B) else Color.White
    val desc = stringResource(R.string.report_hourly_cell_desc, cell.hour.toFa(), (cell.hour + 1).toFa(), cell.count.toFa())
    val shape = RoundedCornerShape(10.dp)

    Column(
        modifier = modifier
            .height(46.dp)
            .background(bg, shape)
            .then(if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, shape) else Modifier)
            .semantics(mergeDescendants = true) { contentDescription = desc }
            .clickable(role = Role.Button, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(cell.hour.toFa(), style = MaterialTheme.typography.labelSmall, color = fg.copy(alpha = 0.85f))
        if (cell.count > 0) {
            Text(cell.count.toFa(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = fg)
        }
    }
}

/** رنگ هر سطح: ترکیب سطح کارت و رنگ اصلی؛ سطح ۰ خاکستری ملایم */
@Composable
private fun levelColor(level: Int): Color {
    val scheme = MaterialTheme.colorScheme
    return when (level) {
        0 -> scheme.surfaceVariant
        1 -> lerp(scheme.surface, scheme.primary, 0.2f)
        2 -> lerp(scheme.surface, scheme.primary, 0.45f)
        3 -> lerp(scheme.surface, scheme.primary, 0.72f)
        else -> scheme.primary
    }
}

@Composable
private fun Legend(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(stringResource(R.string.report_hourly_legend_low), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        (1..4).forEach { Box(Modifier.size(16.dp).background(levelColor(it), RoundedCornerShape(4.dp))) }
        Text(stringResource(R.string.report_hourly_legend_high), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun sampleCounts() = IntArray(24).also {
    mapOf(7 to 2, 8 to 3, 9 to 1, 10 to 3, 12 to 2, 13 to 3, 14 to 4, 15 to 5, 16 to 2, 18 to 1, 20 to 2, 21 to 1, 22 to 1)
        .forEach { (h, n) -> it[h] = n }
}

@Preview(name = "با داده", showBackground = true, widthDp = 390)
@Composable
private fun HeatmapPreview() = KamkamTheme {
    HeatmapCard(buildHourlyUi(sampleCounts()), "۴ تا ۱۰ آبان", Modifier.padding(16.dp))
}

@Preview(name = "داده‌ی کم", showBackground = true, widthDp = 390)
@Composable
private fun HeatmapFewPreview() = KamkamTheme {
    HeatmapCard(buildHourlyUi(IntArray(24).also { it[9] = 1; it[14] = 2 }), "۴ تا ۱۰ آبان", Modifier.padding(16.dp))
}

@Preview(name = "خالی", showBackground = true, widthDp = 390)
@Composable
private fun HeatmapEmptyPreview() = KamkamTheme {
    HeatmapCard(buildHourlyUi(IntArray(24)), "۴ تا ۱۰ آبان", Modifier.padding(16.dp))
}