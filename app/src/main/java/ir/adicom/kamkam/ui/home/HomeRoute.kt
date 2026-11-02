package ir.adicom.kamkam.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.adicom.kamkam.R

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeRoute(
    onOpenEdit: (Long) -> Unit,
    onOpenCraving: () -> Unit,
    onOpenAddCustomTime: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCreatePlan: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }
    val deletedMessage = stringResource(R.string.home_log_deleted)
    val undoLabel = stringResource(R.string.home_undo)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.LogDeleted -> {
                    val result = snackbarHost.showSnackbar(
                        message = deletedMessage,
                        actionLabel = undoLabel,
                        duration = SnackbarDuration.Short,
                    )
                    if (result == SnackbarResult.ActionPerformed) viewModel.onUndoDelete(event.id)
                }
                is HomeEvent.OpenEdit -> onOpenEdit(event.id)
                HomeEvent.OpenCraving -> onOpenCraving()
                HomeEvent.OpenAddCustomTime -> onOpenAddCustomTime()
                HomeEvent.OpenSettings -> onOpenSettings()
                HomeEvent.OpenCreatePlan -> onOpenCreatePlan()
            }
        }
    }

    val actions = remember(viewModel) {
        HomeActions(
            onLogSmoke = viewModel::onLogSmoke,
            onCraving = viewModel::onCraving,
            onAddWithCustomTime = viewModel::onAddWithCustomTime,
            onEditLog = viewModel::onEditLog,
            onDeleteLog = viewModel::onDeleteLog,
            onOpenSettings = viewModel::onOpenSettings,
            onCreatePlan = viewModel::onCreatePlan,
            onSheetTriggerSelected = viewModel::onSheetTriggerSelected,
            onSheetIntensitySelected = viewModel::onSheetIntensitySelected,
            onSheetSave = viewModel::onSheetClose,
            onSheetUndo = viewModel::onSheetUndo,
            onSheetDismiss = viewModel::onSheetClose,
        )
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHost) },
    ) { padding ->
        state?.let { HomeScreen(state = it, actions = actions, modifier = Modifier.padding(padding)) }
    }
}