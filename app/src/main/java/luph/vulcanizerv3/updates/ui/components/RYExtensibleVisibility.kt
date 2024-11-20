package luph.vulcanizerv3.updates.ui.components

import androidx.compose.animation.*
import androidx.compose.runtime.Composable

@Composable
fun RYExtensibleVisibility(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        content = content,
    )
}
