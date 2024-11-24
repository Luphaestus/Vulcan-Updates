package luph.vulcanizerv3.updates.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberImagePainter
import de.mr_pine.zoomables.rememberZoomableState
import luph.vulcanizerv3.updates.R

@Composable
fun FullScreenImageDialog(
    imageUrls: List<String>,
    selectedImageIndex: MutableIntState,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            val painter = rememberImagePainter(data = imageUrls[selectedImageIndex.intValue])
            de.mr_pine.zoomables.ZoomableImage(
                coroutineScope = rememberCoroutineScope(),
                zoomableState = rememberZoomableState(),
                painter = painter,
                onSwipeRight = {
                    if (selectedImageIndex.intValue > 0) {
                        selectedImageIndex.intValue -= 1
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.no_more_images_to_the_right),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onSwipeLeft = {
                    if (selectedImageIndex.intValue < imageUrls.size - 1) {
                        selectedImageIndex.intValue += 1
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.no_more_images_to_the_left)
                            , Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}