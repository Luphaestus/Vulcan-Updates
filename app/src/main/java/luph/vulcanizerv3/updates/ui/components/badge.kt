package luph.vulcanizerv3.updates.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

sealed class BadgeContent {
    data class Count(val value: Int) : BadgeContent()
    data class Text(val text: String) : BadgeContent()
    data class Image(val imageVector: ImageVector) : BadgeContent()
}

class BadgeFormatter {
    private fun badgeFormatIntToString(badge: Int): String {
        return if (badge > 99) {
            "99+"
        } else {
            badge.toString()
        }
    }

    @Composable
    fun badge(
        enabled: Boolean,
        icon: ImageVector,
        contentDescription: String,
        badgeContent: BadgeContent?,
        iconSizeOffset: Float = 0f
    ): @Composable () -> Unit {

        if (!enabled || badgeContent == null) return {
           Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(icon.defaultWidth + iconSizeOffset.dp, icon.defaultHeight + iconSizeOffset.dp)
            )
        }

        return {
            BadgedBox(badge = {
                when (badgeContent) {
                    is BadgeContent.Count -> Badge { Text(badgeFormatIntToString(badgeContent.value)) }
                    is BadgeContent.Text -> Badge { Text(badgeContent.text) }
                    is BadgeContent.Image -> Badge {
                        Icon(
                            imageVector = badgeContent.imageVector,
                            contentDescription = contentDescription,
                            modifier = Modifier.size(icon.defaultWidth + iconSizeOffset.dp, icon.defaultHeight + iconSizeOffset.dp)
                        )
                    }
                }

            }) {
                Icon(imageVector = icon,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(icon.defaultWidth + iconSizeOffset.dp, icon.defaultHeight + iconSizeOffset.dp))
            }
        }
    }
}