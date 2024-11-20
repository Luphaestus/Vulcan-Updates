package luph.vulcanizerv3.updates.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.sp

@Composable
fun ScalableText(text: String, modifier: Modifier = Modifier) {
    val fontSize = remember { mutableStateOf(9.sp) }

    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = fontSize.value),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.onGloballyPositioned { coordinates ->
            val width = coordinates.size.width
            fontSize.value = (width / 16).sp
        }
    )
}