package luph.vulcanizerv3.updates.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import luph.vulcanizerv3.updates.ui.ext.roundClick

@Composable
fun DisplayText(
    modifier: Modifier = Modifier,
    text: String,
    desc: String,
    onTextClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 24.dp,
                top = 24.dp,
                end = 24.dp,
                bottom = 24.dp,
            )
    ) {
        Text(
            modifier = Modifier.roundClick(enabled = onTextClick != null) { onTextClick?.invoke() },
            text = text,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        RYExtensibleVisibility(visible = desc.isNotEmpty()) {
            Text(
                text = desc,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

class SubheadingConfig(
    val modifier: Modifier = Modifier.padding(horizontal = 16.dp),
    val style: TextStyle,
    val color: Color,
)

@Composable
fun getSubheadingConfig(): SubheadingConfig {
    return SubheadingConfig(
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun Subheading(
    text: String,
    @SuppressLint("ModifierParameter") modifier: Modifier = getSubheadingConfig().modifier,
) {
    Text(
        modifier = modifier,
        text = text,
        style = getSubheadingConfig().style,
        color = getSubheadingConfig().color,
    )
}

@Composable
fun SubheadingButton(
    text: String,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = {},
        modifier = getSubheadingConfig().modifier,
    ) {
        Text(
            text = text,
            style = getSubheadingConfig().style,
            color = getSubheadingConfig().color,
        )
    }
}
