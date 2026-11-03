package ir.adicom.kamkam.ui.home

import ir.adicom.kamkam.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    actions: HomeActions,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
    ) {
        item { HomeHeader(state.dateText, actions.onOpenSettings) }
        item {
            SummaryCard(
                count = state.todayCount,
                plan = state.plan,
                sinceLastText = state.sinceLastText,
                onCreatePlan = actions.onCreatePlan,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        item { ActionButtons(actions.onLogSmoke, actions.onCraving, Modifier.padding(top = 16.dp)) }
        item { TodayHeader(actions.onAddWithCustomTime, Modifier.padding(top = 18.dp)) }
        if (state.logs.isEmpty()) {
            item { EmptyToday() }
        } else {
            items(state.logs, key = { it.id }) { log ->
                LogRow(log, actions.onEditLog, actions.onDeleteLog)
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            }
        }
    }

    state.sheet?.let { sheet ->
        ModalBottomSheet(onDismissRequest = actions.onSheetDismiss) {
            PostLogSheetContent(
                sheet = sheet,
                onTriggerSelected = actions.onSheetTriggerSelected,
                onIntensitySelected = actions.onSheetIntensitySelected,
                onSave = actions.onSheetSave,
                onUndo = actions.onSheetUndo,
                onSkip = actions.onSheetDismiss,
            )
        }
    }
}

@Composable
private fun HomeHeader(dateText: String, onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(dateText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        IconButton(onClick = onOpenSettings) {
            Icon(Icons.Outlined.Settings, contentDescription = stringResource(R.string.home_settings))
        }
    }
}

@Composable
private fun SummaryCard(
    count: Int,
    plan: PlanUi?,
    sinceLastText: String?,
    onCreatePlan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val over = plan != null && count > plan.target
    // بالای هدف قرمز نمی‌شود؛ کهربایی یعنی هشدار نرم
    val accent = if (over) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(count.toFa(), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = accent)
                    Text(
                        text = if (plan != null) stringResource(R.string.home_of_target, plan.target.toFa())
                        else stringResource(R.string.home_today_cigarettes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                }
                if (plan != null) {
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                        Text(
                            plan.stageLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                }
            }

            if (plan != null) {
                LinearProgressIndicator(
                    progress = { (count.toFloat() / plan.target.coerceAtLeast(1)).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = accent,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round,
                )
                if (over) {
                    Text(
                        stringResource(R.string.home_over_target),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.background) {
                    Row(
                        modifier = Modifier.padding(start = 14.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            stringResource(R.string.home_no_plan_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                        )
                        FilledTonalButton(onClick = onCreatePlan) { Text(stringResource(R.string.home_create_plan)) }
                    }
                }
            }

            if (sinceLastText != null) {
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(R.string.home_since_last),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(sinceLastText, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(onLogSmoke: () -> Unit, onCraving: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
            onClick = onLogSmoke,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = CircleShape,
        ) {
            Icon(Icons.Outlined.Add, contentDescription = null)
            Spacer(Modifier.width(10.dp))
            Text(stringResource(R.string.home_log_smoke), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        OutlinedButton(
            onClick = onCraving,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = CircleShape,
        ) {
            Text(stringResource(R.string.home_craving))
        }
    }
}

@Composable
private fun TodayHeader(onAddWithCustomTime: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(stringResource(R.string.home_today_logs), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        TextButton(onClick = onAddWithCustomTime) { Text(stringResource(R.string.home_add_custom_time)) }
    }
}

@Composable
private fun LogRow(log: LogItemUi, onEdit: (Long) -> Unit, onDelete: (Long) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(log.timeText, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, modifier = Modifier.width(56.dp))
        if (log.triggerLabel != null) {
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                Text(
                    log.triggerLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                )
            }
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = { onEdit(log.id) }) {
            Icon(Icons.Outlined.Edit, contentDescription = stringResource(R.string.home_edit_log, log.timeText))
        }
        IconButton(onClick = { onDelete(log.id) }) {
            Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.home_delete_log, log.timeText))
        }
    }
}

@Composable
private fun EmptyToday() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(stringResource(R.string.home_empty_title), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        Text(
            stringResource(R.string.home_empty_body),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostLogSheetContent(
    sheet: PostLogSheetUi,
    onTriggerSelected: (Long) -> Unit,
    onIntensitySelected: (Int) -> Unit,
    onSave: () -> Unit,
    onUndo: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(stringResource(R.string.sheet_logged, sheet.timeText), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    stringResource(R.string.sheet_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            TextButton(onClick = onUndo) { Text(stringResource(R.string.sheet_undo)) }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.sheet_trigger), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                sheet.triggers.chunked(3).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowItems.forEach { t ->
                            FilterChip(
                                selected = t.id == sheet.selectedTriggerId,
                                onClick = { onTriggerSelected(t.id) },
                                label = { Text(t.label) },
                            )
                        }
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.sheet_intensity), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                (1..5).forEach { n ->
                    OutlinedIconToggleButton(
                        checked = sheet.intensity == n,
                        onCheckedChange = { onIntensitySelected(n) },
                        colors = IconButtonDefaults.outlinedIconToggleButtonColors(
                            checkedContainerColor = MaterialTheme.colorScheme.primary,
                            checkedContentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    ) { Text(n.toFa()) }
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onSave, modifier = Modifier.weight(1f).height(52.dp), shape = CircleShape) {
                Text(stringResource(R.string.sheet_save), fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = onSkip) { Text(stringResource(R.string.sheet_skip)) }
        }
    }
}