package luph.vulcanizerv3.updates.ui.components

import android.view.View
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import luph.vulcanizerv3.updates.ui.page.OpenRoute
import luph.vulcanizerv3.updates.utils.getStandardAnimationSpeed

fun Offset.toIntOffset() = IntOffset(x.toInt(), y.toInt())

@Composable
fun TransitionBox(
    destination: String,
    navController: NavController,
    view: View,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val boxPosition = remember { mutableStateOf(IntOffset.Zero) }
    val boxSize = remember { mutableStateOf(IntSize.Zero) }
    val tapped = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                boxPosition.value = coordinates
                    .positionInWindow()
                    .toIntOffset()
                boxSize.value = coordinates.size
            }
            .indication(interactionSource = interactionSource, LocalIndication.current)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        tapped.value = true
                        val press = PressInteraction.Press(offset)
                        interactionSource.emit(press)
                        tryAwaitRelease()
                        tapped.value = false
                        interactionSource.emit(PressInteraction.Release(press))
                    },
                    onTap = { offset ->
                        val adjustedX = offset.x + boxPosition.value.x
                        val adjustedY = offset.y + boxPosition.value.y
                        val transformOriginX = adjustedX / screenWidth
                        val transformOriginY = adjustedY / screenHeight

                        onClick()
                        OpenRoute(
                            destination, navController, view,
                            enter = scaleIn(
                                animationSpec = tween(getStandardAnimationSpeed()),
                                transformOrigin = TransformOrigin(
                                    transformOriginX,
                                    transformOriginY
                                )
                            ),
                            exit = ExitTransition.None,
                            popEnter = EnterTransition.None,
                            popExit = scaleOut(
                                animationSpec = tween((getStandardAnimationSpeed()*0.7).toInt()),
                                transformOrigin = TransformOrigin(
                                    transformOriginX,
                                    transformOriginY
                                )
                            )
                        )
                    }
                )
            }
    ) {
        content()
    }
}
