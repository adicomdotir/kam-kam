package ir.adicom.kamkam.ui.edit

import ir.adicom.kamkam.ui.home.TriggerUi

data class DayChipUi(val offset: Int, val label: String)

data class EditLogUiState(
    val isEdit: Boolean,
    val days: List<DayChipUi>,
    val selectedDayOffset: Int,
    val hour: Int,
    val minute: Int,
    val timeText: String,
    val triggers: List<TriggerUi>,
    val selectedTriggerId: Long?,
    val intensity: Int?,
    val note: String,
)

data class EditLogActions(
    val onClose: () -> Unit = {},
    val onDaySelected: (Int) -> Unit = {},
    val onTimePicked: (hour: Int, minute: Int) -> Unit = { _, _ -> },
    val onTriggerSelected: (Long) -> Unit = {},
    val onIntensitySelected: (Int) -> Unit = {},
    val onNoteChange: (String) -> Unit = {},
    val onSave: () -> Unit = {},
    val onDelete: () -> Unit = {},
)