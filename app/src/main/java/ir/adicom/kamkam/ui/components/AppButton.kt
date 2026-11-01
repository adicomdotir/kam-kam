package ir.adicom.kamkam.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

enum class AppButtonVariant { Primary, Secondary, Outline, Text }
enum class AppButtonSize { Small, Medium, Large }

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    variant: AppButtonVariant = AppButtonVariant.Primary,
    size: AppButtonSize = AppButtonSize.Medium,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    fillMaxWidth: Boolean = true,
) {
    val isInteractive = enabled && !loading

    val contentPadding = when (size) {
        AppButtonSize.Small -> PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        AppButtonSize.Medium -> PaddingValues(horizontal = 20.dp, vertical = 10.dp)
        AppButtonSize.Large -> PaddingValues(horizontal = 24.dp, vertical = 14.dp)
    }

    val iconSize = when (size) {
        AppButtonSize.Small -> 16.dp
        AppButtonSize.Medium -> 18.dp
        AppButtonSize.Large -> 20.dp
    }

    val modifierFinal = if (fillMaxWidth) modifier.fillMaxWidth() else modifier

    // Shared inner content (text + optional loader)
    @Composable
    fun ButtonContent() {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(iconSize),
                strokeWidth = 2.dp,
                color = LocalContentColor.current
            )
            Spacer(Modifier.width(8.dp))
        } else if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )
            Spacer(Modifier.width(8.dp))
        }

        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (!loading && trailingIcon != null) {
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )
        }
    }

    when (variant) {
        AppButtonVariant.Primary -> Button(
            onClick = onClick,
            modifier = modifierFinal,
            enabled = isInteractive,
            contentPadding = contentPadding,
        ) { ButtonContent() }

        AppButtonVariant.Secondary -> FilledTonalButton(
            onClick = onClick,
            modifier = modifierFinal,
            enabled = isInteractive,
            contentPadding = contentPadding,
        ) { ButtonContent() }

        AppButtonVariant.Outline -> OutlinedButton(
            onClick = onClick,
            modifier = modifierFinal,
            enabled = isInteractive,
            contentPadding = contentPadding,
        ) { ButtonContent() }

        AppButtonVariant.Text -> TextButton(
            onClick = onClick,
            modifier = modifierFinal,
            enabled = isInteractive,
            contentPadding = contentPadding,
        ) { ButtonContent() }
    }
}