package luph.vulcanizerv3.updates.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntOffset

@Composable
fun ClickableOverlay(
    modifier: Modifier = Modifier,
    onClick: (Offset) -> Unit,
    content: @Composable () -> Unit
) {
    val boxPosition = remember { mutableStateOf(IntOffset.Zero) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                boxPosition.value = coordinates
                    .positionInWindow()
                    .toIntOffset()
            }
            .indication(interactionSource = interactionSource, LocalIndication.current)
            .pointerInput(Unit) {
                detectTapGestures(

                    onPress = { offset ->
                        val press = PressInteraction.Press(offset)
                        interactionSource.emit(press)
                        tryAwaitRelease()
                        interactionSource.emit(PressInteraction.Release(press))
                    },
                    onTap = { offset ->
                        // Adjust the tap coordinates to the screen coordinates
                        val adjustedX = offset.x + boxPosition.value.x
                        val adjustedY = offset.y + boxPosition.value.y

                        onClick(Offset(adjustedX, adjustedY))
                    }
                )
            }
    ) {
        // Your content goes here
        content()
    }
}