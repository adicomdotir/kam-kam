package ir.adicom.kamkam.ui.report

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ReportRoute(
    onOpenDay: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val actions = remember(viewModel, onOpenDay) {
        ReportActions(
            onPeriodSelected = viewModel::onPeriodSelected,
            onOlder = viewModel::onOlder,
            onNewer = viewModel::onNewer,
            onOpenDay = onOpenDay,
        )
    }

    state?.let { ReportScreen(state = it, actions = actions, modifier = modifier) }
}