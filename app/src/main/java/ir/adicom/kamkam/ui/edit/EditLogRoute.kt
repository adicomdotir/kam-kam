package ir.adicom.kamkam.ui.edit

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/** باید داخل NavBackStackEntry صدا زده شود تا logId از آرگومان ناوبری به ViewModel برسد. */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditLogRoute(
    onClose: () -> Unit,
    onDeleted: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditLogViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                EditLogEvent.Saved, EditLogEvent.Closed -> onClose()
                is EditLogEvent.Deleted -> onDeleted(event.id)
            }
        }
    }

    val actions = remember(viewModel, onClose) {
        EditLogActions(
            onClose = onClose,
            onDaySelected = viewModel::onDaySelected,
            onTimePicked = viewModel::onTimePicked,
            onTriggerSelected = viewModel::onTriggerSelected,
            onIntensitySelected = viewModel::onIntensitySelected,
            onNoteChange = viewModel::onNoteChange,
            onSave = viewModel::onSave,
            onDelete = viewModel::onDelete,
        )
    }

    state?.let { EditLogScreen(state = it, actions = actions, modifier = modifier) }
}