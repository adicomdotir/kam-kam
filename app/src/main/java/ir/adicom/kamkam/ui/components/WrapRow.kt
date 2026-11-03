package ir.adicom.kamkam.ui.components

import ir.adicom.kamkam.ui.home.TriggerUi
import ir.adicom.kamkam.ui.home.toFa
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * جایگزین ساده‌ی FlowRow: آیتم‌ها را پشت‌سر هم می‌چیند و وقتی جا نشد به ردیف بعد می‌برد.
 * عمداً از API آزمایشی Compose استفاده نمی‌کند تا به نسخه‌ی کتابخانه وابسته نباشد.
 */
@Composable
fun WrapRow(
    modifier: Modifier = Modifier,
    spacing: Dp = 8.dp,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val gap = spacing.roundToPx()
        val maxWidth = constraints.maxWidth
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }

        val rows = mutableListOf<List<Placeable>>()
        var current = mutableListOf<Placeable>()
        var currentWidth = 0
        for (p in placeables) {
            val needed = if (current.isEmpty()) p.width else currentWidth + gap + p.width
            if (current.isNotEmpty() && needed > maxWidth) {
                rows += current
                current = mutableListOf()
                currentWidth = 0
            }
            currentWidth = if (current.isEmpty()) p.width else currentWidth + gap + p.width
            current += p
        }
        if (current.isNotEmpty()) rows += current

        val rowHeights = rows.map { row -> row.maxOf { it.height } }
        val totalHeight = rowHeights.sum() + gap * (rows.size - 1).coerceAtLeast(0)

        layout(maxWidth, totalHeight) {
            var y = 0
            rows.forEachIndexed { i, row ->
                var x = 0
                row.forEach { p ->
                    // placeRelative در RTL خودکار از راست می‌چیند
                    p.placeRelative(x, y + (rowHeights[i] - p.height) / 2)
                    x += p.width + gap
                }
                y += rowHeights[i] + gap
            }
        }
    }
}

@Composable
fun TriggerChips(
    triggers: List<TriggerUi>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    WrapRow(modifier = modifier.fillMaxWidth()) {
        triggers.forEach { t ->
            FilterChip(
                selected = t.id == selectedId,
                onClick = { onSelect(t.id) },
                label = { Text(t.label) },
            )
        }
    }
}

@Composable
fun IntensityRow(
    selected: Int?,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        (1..5).forEach { n ->
            OutlinedIconToggleButton(
                checked = selected == n,
                onCheckedChange = { onSelect(n) },
                colors = IconButtonDefaults.outlinedIconToggleButtonColors(
                    checkedContainerColor = MaterialTheme.colorScheme.primary,
                    checkedContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) { Text(n.toFa()) }
        }
    }
}