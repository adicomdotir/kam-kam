package ir.adicom.kamkam.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.adicom.kamkam.R
import ir.adicom.kamkam.data.dao.TriggerCount
import ir.adicom.kamkam.data.entity.Trigger
import ir.adicom.kamkam.ui.home.toFa
import ir.adicom.kamkam.ui.theme.KamkamTheme

@Composable
fun TriggersCard(triggers: TriggersUi, rangeTitle: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.report_triggers_title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(rangeTitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (triggers.rows.isEmpty()) {
                Text(
                    stringResource(R.string.report_hourly_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                return@Column
            }

            InsightLine(triggers)

            Column {
                triggers.rows.forEachIndexed { i, row -> TriggerRow(row, isTop = i == 0 && row.kind == TriggerRowKind.TAGGED) }
            }
        }
    }
}

@Composable
private fun InsightLine(triggers: TriggersUi) {
    val name = triggers.topLabel
    if (name == null) {
        Text(
            stringResource(R.string.report_triggers_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }
    val text = stringResource(R.string.report_triggers_insight, name, triggers.topPercent.toFa())
    val accent = MaterialTheme.colorScheme.primary
    Text(
        text = buildAnnotatedString {
            append(text)
            val i = text.indexOf(name)
            if (i >= 0) addStyle(SpanStyle(fontWeight = FontWeight.Bold, color = accent), i, i + name.length)
        },
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun TriggerRow(row: TriggerRowUi, isTop: Boolean) {
    val scheme = MaterialTheme.colorScheme
    val label = when (row.kind) {
        TriggerRowKind.TAGGED -> row.label.orEmpty()
        TriggerRowKind.OTHER -> stringResource(R.string.report_triggers_other)
        TriggerRowKind.UNTAGGED -> stringResource(R.string.report_triggers_untagged)
    }
    val barColor: Color = when {
        row.kind == TriggerRowKind.UNTAGGED -> scheme.onSurfaceVariant.copy(alpha = 0.45f)
        row.kind == TriggerRowKind.OTHER -> lerp(scheme.surface, scheme.primary, 0.35f)
        isTop -> scheme.primary
        else -> lerp(scheme.surface, scheme.primary, 0.72f)
    }
    val labelColor = if (row.kind == TriggerRowKind.UNTAGGED) scheme.onSurfaceVariant else scheme.onSurface
    val desc = stringResource(R.string.report_trigger_row_desc, label, row.count.toFa(), row.percent.toFa())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .semantics(mergeDescendants = true) { contentDescription = desc },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = labelColor,
            maxLines = 1,
            modifier = Modifier.width(76.dp),
        )
        Box(modifier = Modifier.weight(1f).height(12.dp).background(scheme.background, RoundedCornerShape(6.dp))) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(row.fraction.coerceIn(0.02f, 1f))
                    .background(barColor, RoundedCornerShape(6.dp)),
            )
        }
        Text(
            stringResource(R.string.report_trigger_count, row.count.toFa(), row.percent.toFa()),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.width(72.dp),
        )
    }
}

private val sampleTriggers = listOf(
    Trigger(1, defaultKey = "stress"), Trigger(2, defaultKey = "coffee"), Trigger(3, defaultKey = "after_meal"),
    Trigger(4, defaultKey = "boredom"), Trigger(5, defaultKey = "social"), Trigger(6, defaultKey = "driving"),
    Trigger(7, name = "کار شبانه"),
)
private val sampleNames = mapOf(
    "stress" to "استرس", "coffee" to "قهوه", "after_meal" to "بعد غذا",
    "boredom" to "بی‌حوصلگی", "social" to "جمع دوستانه", "driving" to "رانندگی",
)
private fun label(t: Trigger) = t.name ?: sampleNames[t.defaultKey].orEmpty()

@Preview(name = "با داده", showBackground = true, widthDp = 390)
@Composable
private fun TriggersPreview() = KamkamTheme {
    TriggersCard(
        buildTriggersUi(
            counts = listOf(TriggerCount(3, 9), TriggerCount(2, 7), TriggerCount(1, 5), TriggerCount(4, 4), TriggerCount(5, 2)),
            triggers = sampleTriggers, totalLogs = 30, labelOf = ::label,
        ),
        "۴ تا ۱۰ آبان", Modifier.padding(16.dp),
    )
}

@Preview(name = "بیش از ۵ محرک", showBackground = true, widthDp = 390)
@Composable
private fun TriggersManyPreview() = KamkamTheme {
    TriggersCard(
        buildTriggersUi(
            counts = listOf(
                TriggerCount(3, 9), TriggerCount(2, 7), TriggerCount(1, 5), TriggerCount(4, 4),
                TriggerCount(5, 2), TriggerCount(6, 2), TriggerCount(7, 1),
            ),
            triggers = sampleTriggers, totalLogs = 33, labelOf = ::label,
        ),
        "۴ تا ۱۰ آبان", Modifier.padding(16.dp),
    )
}

@Preview(name = "اکثراً بدون برچسب", showBackground = true, widthDp = 390)
@Composable
private fun TriggersFewTaggedPreview() = KamkamTheme {
    TriggersCard(
        buildTriggersUi(listOf(TriggerCount(2, 2)), sampleTriggers, totalLogs = 12, labelOf = ::label),
        "۴ تا ۱۰ آبان", Modifier.padding(16.dp),
    )
}