package ir.adicom.kamkam.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun DeleteConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "Delete",
    dismissText: String = "Cancel",
    isLoading: Boolean = false,
) {
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        },
        confirmButton = {
            AppButton(
                text = confirmText,
                loading = isLoading,
                enabled = !isLoading,
                onClick = onConfirm,
                fillMaxWidth = false,
                size = AppButtonSize.Small,
                leadingIcon = Icons.Outlined.Delete
            )
        },
        dismissButton = {
            AppButton(
                text = dismissText,
                enabled = !isLoading,
                variant = AppButtonVariant.Text,
                onClick = onDismiss,
                fillMaxWidth = false,
                size = AppButtonSize.Small
            )
        }
    )
}