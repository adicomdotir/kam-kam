package ir.adicom.kamkam.ui.craving

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** باید داخل NavBackStackEntry صدا زده شود تا SavedStateHandle به ViewModel برسد. */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CravingRoute(
    onClose: () -> Unit,
    onDone: (resisted: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CravingViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is CravingEvent.Done -> onDone(event.resisted)
            }
        }
    }

    val actions = remember(viewModel, onClose) {
        CravingActions(
            onClose = onClose,
            onTriggerSelected = viewModel::onTriggerSelected,
            onResisted = viewModel::onResisted,
            onSmoked = viewModel::onSmoked,
            onExtend = viewModel::onExtend,
        )
    }

    state?.let { CravingScreen(state = it, actions = actions, modifier = modifier) }
}