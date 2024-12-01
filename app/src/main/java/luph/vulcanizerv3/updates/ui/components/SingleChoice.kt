package luph.vulcanizerv3.updates.ui.components


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.twotone.BugReport
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.Options

@Composable
fun SingleOptionsSection(
    selected: String,
    options: Options.SingleChoice,
    onOptionSelected: (String) -> Unit = {}
) {
    options.options.forEach{(option, image) ->
        key(option) {
            RadioRow(title = option, image = image, selected = option == selected, onOptionSelected = {
                onOptionSelected(option)
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RadioRow(
    title: String = "Radio Row Title",
    selected: Boolean = false,
    image: ImageVector = Icons.Default.ThumbUp,
    onOptionSelected: () -> Unit = {}
) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp)
        .clickable(onClick = onOptionSelected),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    ){
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                image,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.clip(MaterialTheme.shapes.small).size(48.dp),
            )
            Text(text = title, modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp))
            RadioButton(
                selected = selected,
                modifier = Modifier.padding(8.dp),
                onClick = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SingleOptionListPreview() {
    Column {
        SingleOptionsSection("Spark", Options.SingleChoice(
            mapOf("Spark" to Icons.TwoTone.BugReport, "Kotlin" to Icons.TwoTone.BugReport, "Compose" to Icons.TwoTone.BugReport, "Vulcanizer" to Icons.TwoTone.BugReport))
        )
    }
}
