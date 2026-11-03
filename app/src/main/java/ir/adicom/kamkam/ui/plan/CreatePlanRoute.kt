package ir.adicom.kamkam.ui.plan

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CreatePlanRoute(
    onBack: () -> Unit,
    onCreated: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreatePlanViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                CreatePlanEvent.Created -> onCreated()
            }
        }
    }

    val actions = remember(viewModel, onBack) {
        CreatePlanActions(
            onBack = onBack,
            onBaselineDelta = viewModel::onBaselineDelta,
            onStepSizeDelta = viewModel::onStepSizeDelta,
            onStepDaysDelta = viewModel::onStepDaysDelta,
            onFinalTargetDelta = viewModel::onFinalTargetDelta,
            onStart = viewModel::onStart,
        )
    }

    state?.let { CreatePlanScreen(state = it, actions = actions, modifier = modifier) }
}