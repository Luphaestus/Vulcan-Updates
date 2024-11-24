package luph.vulcanizerv3.updates.ui.components

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import luph.vulcanizerv3.updates.MainActivity

data class CircleStore(
    val offset: Offset,
    val currentColour: Color,
    val newColour: Color,
    var delete: Boolean = false
)

@Composable
fun ExpandingCircleAnimation(bg: MutableState<Color>, circleStore: MutableState<CircleStore>) {
    var circleSize by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        launch(Dispatchers.Default) {
            animateCircleSize { newSize ->
                circleSize = newSize
            }
            circleStore.value.delete = true
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawExpandingCircle(circleSize, circleStore.value.newColour, circleStore.value.offset)
        }
    }
}


private suspend fun animateCircleSize(onSizeChange: (Float) -> Unit) {
    val context = MainActivity.applicationContext()
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    val targetSize = Math.sqrt((size.x * size.x + size.y * size.y).toDouble()).toInt()
    val animationDuration = 300 // Duration in milliseconds

    // Animate the circle size
    for (i in 0..targetSize step 10) {
        onSizeChange(i.toFloat())
        delay(animationDuration / (targetSize / 10).toLong())
    }
}


private fun DrawScope.drawExpandingCircle(size: Float, color: Color, offset: Offset) {
    drawCircle(
        color = color,
        radius = size,
        center = Offset(offset.x, offset.y) // Top right corner
    )
}


@Composable
fun MultipleExpandingCircleAnimations(displayAnimations: MutableList<CircleStore>) {
    val tmpcolor = MaterialTheme.colorScheme.background
    val bg = remember { mutableStateOf(tmpcolor.copy()) }


    Box(
        Modifier
            .fillMaxSize()
            .background(bg.value)
    ) {
        val itemsToRemove = mutableListOf<CircleStore>()
        displayAnimations.forEach { store ->
            ExpandingCircleAnimation(
                bg = bg,
                circleStore = mutableStateOf(store)
            )
            if (store.delete) {
                itemsToRemove.add(store)
                return@forEach
            }
        }

        //i can't fogure out how to do this - i wqant to delete old ones
//        displayAnimations.removeAll(itemsToRemove.dropLast(1))
//        Log.e("displayAnimations", displayAnimations.size.toString())
    }
}
