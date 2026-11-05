package ir.adicom.kamkam.ui.day

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kamkam.R
import com.example.kamkam.ui.home.LogItemUi
import com.example.kamkam.ui.home.LogRow
import com.example.kamkam.ui.home.toFa
import com.example.kamkam.ui.theme.KamkamTheme

@Composable
fun DayDetailScreen(
    state: DayDetailUiState,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.plan_back))
            }
            Column {
                Text(state.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    stringResource(R.string.day_detail_count, state.logs.size.toFa()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (state.logs.isEmpty()) {
            Text(
                stringResource(R.string.day_detail_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(24.dp),
            )
        } else {
            LazyColumn(contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)) {
                items(state.logs, key = { it.id }) { log ->
                    LogRow(log, onEdit, onDelete)
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
        }
    }
}

@Preview(device = "spec:width=390dp,height=844dp,dpi=420", showBackground = true)
@Composable
private fun DayDetailPreview() = KamkamTheme {
    DayDetailScreen(
        state = DayDetailUiState(
            title = "شنبه ۹ آبان ۱۴۰۵",
            logs = listOf(
                LogItemUi(4, "۲۱:۱۰", "جمع دوستانه"),
                LogItemUi(3, "۱۴:۲۰", "بعد غذا"),
                LogItemUi(2, "۱۰:۵۰", "قهوه"),
                LogItemUi(1, "۰۷:۴۰", null),
            ),
        ),
        onBack = {}, onEdit = {}, onDelete = {},
    )
}