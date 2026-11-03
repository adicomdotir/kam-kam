package ir.adicom.kamkam.ui.edit

import ir.adicom.kamkam.R
import ir.adicom.kamkam.ui.components.IntensityRow
import ir.adicom.kamkam.ui.components.TriggerChips
import ir.adicom.kamkam.ui.home.TriggerUi
import ir.adicom.kamkam.ui.theme.KamkamTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun EditLogScreen(
    state: EditLogUiState,
    actions: EditLogActions,
    modifier: Modifier = Modifier,
) {
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).imePadding(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            IconButton(onClick = actions.onClose) {
                Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.edit_close))
            }
            Text(
                stringResource(if (state.isEdit) R.string.edit_log_title else R.string.add_log_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Section(stringResource(R.string.edit_day)) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.days.forEach { d ->
                        FilterChip(
                            selected = d.offset == state.selectedDayOffset,
                            onClick = { actions.onDaySelected(d.offset) },
                            label = { Text(d.label) },
                        )
                    }
                }
            }

            Section(stringResource(R.string.edit_time)) {
                val clickLabel = stringResource(R.string.edit_time_change)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClickLabel = clickLabel, role = Role.Button) { showTimePicker = true },
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            state.timeText,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            Section(stringResource(R.string.sheet_trigger)) {
                TriggerChips(state.triggers, state.selectedTriggerId, actions.onTriggerSelected)
            }

            Section(stringResource(R.string.sheet_intensity)) {
                IntensityRow(state.intensity, actions.onIntensitySelected)
            }

            Section(stringResource(R.string.edit_note)) {
                OutlinedTextField(
                    value = state.note,
                    onValueChange = actions.onNoteChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.edit_note_placeholder)) },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                )
            }
        }

        Column {
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 16.dp)) {
                Button(onClick = actions.onSave, modifier = Modifier.fillMaxWidth().height(56.dp), shape = CircleShape) {
                    Text(stringResource(R.string.sheet_save), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                if (state.isEdit) {
                    TextButton(
                        onClick = actions.onDelete,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    ) { Text(stringResource(R.string.edit_delete)) }
                }
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            hour = state.hour,
            minute = state.minute,
            onConfirm = { h, m ->
                actions.onTimePicked(h, m)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
        )
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    hour: Int,
    minute: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val pickerState = rememberTimePickerState(initialHour = hour, initialMinute = minute, is24Hour = true)
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(pickerState.hour, pickerState.minute) }) {
                Text(stringResource(R.string.time_picker_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.time_picker_cancel)) }
        },
        text = { TimePicker(state = pickerState) },
    )
}

private val sampleTriggers = listOf(
    TriggerUi(1, "استرس"), TriggerUi(2, "قهوه"), TriggerUi(3, "بعد غذا"),
    TriggerUi(4, "جمع دوستانه"), TriggerUi(5, "بی‌حوصلگی"), TriggerUi(6, "رانندگی"),
)

private val sampleState = EditLogUiState(
    isEdit = true,
    days = listOf(
        DayChipUi(0, "امروز"), DayChipUi(1, "دیروز"), DayChipUi(2, "۸ آبان"),
        DayChipUi(3, "۷ آبان"), DayChipUi(4, "۶ آبان"),
    ),
    selectedDayOffset = 0,
    hour = 14, minute = 20, timeText = "۱۴:۲۰",
    triggers = sampleTriggers,
    selectedTriggerId = 3,
    intensity = 3,
    note = "",
)

private const val SPEC = "spec:width=390dp,height=844dp,dpi=420"

@Preview(name = "ویرایش", device = SPEC, showBackground = true)
@Composable
private fun EditLogPreview() = KamkamTheme { EditLogScreen(sampleState, EditLogActions()) }

@Preview(name = "افزودن", device = SPEC, showBackground = true)
@Composable
private fun AddLogPreview() = KamkamTheme {
    EditLogScreen(sampleState.copy(isEdit = false, selectedTriggerId = null, intensity = null), EditLogActions())
}