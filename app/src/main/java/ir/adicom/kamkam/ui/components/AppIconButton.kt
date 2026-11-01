package ir.adicom.kamkam.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AppIconButtonVariant { Standard, Filled, Tonal, Outlined }
enum class AppIconButtonSize(val buttonSize: Dp, val iconSize: Dp) {
    Small(32.dp, 18.dp),
    Medium(40.dp, 22.dp),
    Large(48.dp, 24.dp)
}

/**
 * A customizable icon button.
 *
 * @sample ir.adicom.samples.AppIconButtonSamples
 */
@Composable
fun AppIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: AppIconButtonVariant = AppIconButtonVariant.Standard,
    size: AppIconButtonSize = AppIconButtonSize.Medium,
    tint: Color = LocalContentColor.current,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    val sizedModifier = modifier.size(size.buttonSize)

    when (variant) {
        AppIconButtonVariant.Standard -> IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = sizedModifier
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(size.iconSize)
            )
        }

        AppIconButtonVariant.Filled -> FilledIconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = sizedModifier,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = containerColor
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(size.iconSize)
            )
        }

        AppIconButtonVariant.Tonal -> FilledTonalIconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = sizedModifier,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = containerColor
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(size.iconSize)
            )
        }

        AppIconButtonVariant.Outlined -> OutlinedIconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = sizedModifier
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(size.iconSize)
            )
        }
    }
}