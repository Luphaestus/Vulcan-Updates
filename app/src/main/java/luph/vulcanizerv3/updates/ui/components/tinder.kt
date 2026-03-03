package luph.vulcanizerv3.updates.ui.components

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.github.theapache64.twyper.TwyperController
import com.github.theapache64.twyper.rememberCardController
import com.github.theapache64.twyper.rememberTwyperController

@Composable
inline fun <reified T> Tinder(
    items: MutableList<T>,
    twyperController: TwyperController = rememberTwyperController(),
    stackCount: Int = 5,
    paddingBetweenCards: Float = 40f,
    modifier: Modifier = Modifier,
    crossinline renderItem: @Composable (T, Int) -> Unit
) {
    Box(modifier = modifier) {
        var currentCardIndex by remember{ mutableStateOf(0) }
         val list = (currentCardIndex until currentCardIndex + stackCount).mapNotNull { i ->
             items[i%items.size]
         }.reversed()

        list.forEachIndexed { index, item ->
            key(currentCardIndex) {
                val cardController = rememberCardController()
                if (index == list.lastIndex) {
                    twyperController.currentCardController = cardController
                }
                if (!(cardController.isCardOut()&&index==list.lastIndex)) {
                    val paddingTop by animateFloatAsState(targetValue = (index * paddingBetweenCards))
                    Card(
                        colors = CardColors(
                            contentColor = Color.Transparent,
                            containerColor = Color.Transparent,
                            disabledContentColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        ),
                        modifier = Modifier
                            .background(Color.Transparent)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragEnd = {
                                        cardController.onDragEnd()
                                    },
                                    onDragCancel = {
                                        cardController.onDragCancel()
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consumePositionChange()
                                        cardController.onDrag(Offset(dragAmount.x, 0.dp.toPx()))
                                    }
                                )
                            }
                            .graphicsLayer(
                                translationX = cardController.cardX,
                                translationY = cardController.cardY+paddingTop,
                                rotationZ = cardController.rotation,
                                scaleX = 1f +  (5f-index)/30,
                            )
                    ) {
                        renderItem(item, index)
                    }
                } else {
                    Log.e("Tinder", "currentCardIndex: $currentCardIndex")
                    if (index == list.lastIndex)
                        currentCardIndex++
                }
            }
        }
    }
}