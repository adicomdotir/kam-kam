package ir.adicom.kamkam.ui.home

import java.text.NumberFormat
import java.util.Locale

data class LogItemUi(val id: Long, val timeText: String, val triggerLabel: String?)

data class TriggerUi(val id: Long, val label: String)

data class PlanUi(val stageLabel: String, val target: Int)

data class PostLogSheetUi(
    val timeText: String,
    val triggers: List<TriggerUi>,
    val selectedTriggerId: Long?,
    val intensity: Int?,
)

data class HomeUiState(
    val dateText: String,
    val todayCount: Int,
    val plan: PlanUi?,
    val sinceLastText: String?,
    val logs: List<LogItemUi>,
    val sheet: PostLogSheetUi? = null,
)

data class HomeActions(
    val onLogSmoke: () -> Unit = {},
    val onCraving: () -> Unit = {},
    val onAddWithCustomTime: () -> Unit = {},
    val onEditLog: (Long) -> Unit = {},
    val onDeleteLog: (Long) -> Unit = {},
    val onOpenSettings: () -> Unit = {},
    val onCreatePlan: () -> Unit = {},
    val onSheetTriggerSelected: (Long) -> Unit = {},
    val onSheetIntensitySelected: (Int) -> Unit = {},
    val onSheetSave: () -> Unit = {},
    val onSheetUndo: () -> Unit = {},
    val onSheetDismiss: () -> Unit = {},
)

fun Int.toFa(): String = NumberFormat.getInstance(Locale("fa")).format(this)