package luph.vulcanizerv3.updates.ui.components

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import luph.vulcanizerv3.updates.ui.page.showNavigation

@Composable
fun SelectableSettingGroupItem(
    destination: String,
    title: String,
    navController: NavController,
    view: View,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    selected: Boolean = false,
    desc: String? = null,
    icon: ImageVector? = null,
    onClick: () -> Unit = {},
) {
    showNavigation.show = true
    TransitionBox(destination, navController, view,
        modifier = modifier
            .alpha(if (enable) 1f else 0.5f),
        onClick = {
            view.playSoundEffect(SoundEffectConstants.CLICK)
            onClick()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(
                    color = if (selected) MaterialTheme.colorScheme.onSurface else Color.Unspecified,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(8.dp, 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = title,
                    modifier = Modifier.padding(start = 8.dp, end = 16.dp),
                    tint = if (selected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(

                    text = title,
                    maxLines = if (desc == null) 2 else 1,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    color = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
                )
               desc?.let {
                   Text(
                       modifier = Modifier.wrapContentHeight(),
                       text = it,
                       color = if (selected) MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                       else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                       maxLines = 2,
                       style = MaterialTheme.typography.bodyMedium,
                   )
               }
            }
        }
    }
}
