package ir.adicom.kamkam.ui.day

import android.app.Application
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.adicom.kamkam.R
import kotlinx.coroutines.launch

/** باید داخل NavBackStackEntry صدا زده شود تا epochDay از آرگومان ناوبری به ViewModel برسد. */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayDetailRoute(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    deletedLogId: Long? = null,
    onDeletedLogHandled: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: DayDetailViewModel = viewModel(
        factory = DayDetailViewModel.factory(LocalContext.current.applicationContext as Application)
    ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val message = stringResource(R.string.home_log_deleted)
    val undo = stringResource(R.string.home_undo)

    suspend fun showUndo(id: Long) {
        val result = snackbarHost.showSnackbar(message, undo, duration = SnackbarDuration.Short)
        if (result == SnackbarResult.ActionPerformed) viewModel.onUndoDelete(id)
    }

    // حذفی که در صفحه‌ی ویرایش انجام شده
    LaunchedEffect(deletedLogId) {
        val id = deletedLogId ?: return@LaunchedEffect
        showUndo(id)
        onDeletedLogHandled()
    }

    Scaffold(modifier = modifier, snackbarHost = { SnackbarHost(snackbarHost) }) { padding ->
        state?.let {
            DayDetailScreen(
                state = it,
                onBack = onBack,
                onEdit = onEdit,
                onDelete = { id ->
                    viewModel.onDelete(id)
                    scope.launch { showUndo(id) }
                },
                modifier = Modifier.padding(padding),
            )
        }
    }
}