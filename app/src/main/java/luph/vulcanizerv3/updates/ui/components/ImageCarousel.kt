package luph.vulcanizerv3.updates.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter
import coil.size.OriginalSize

@Composable
fun ImageCarousel(imageUrls: List<String>, modifier: Modifier = Modifier) {
    val selectedImageIndex = remember { mutableIntStateOf(0) }
    val showDialog = remember { mutableStateOf(false) }

    LazyRow {
        items(imageUrls.size) { index ->
            Image(
                painter = rememberImagePainter(
                    data = imageUrls[index],
                    builder = { size(OriginalSize) }),
                contentDescription = "Photo $index",
                modifier = modifier
                    .fillMaxHeight()
                    .clickable {
                        selectedImageIndex.intValue = index
                        showDialog.value = true
                    },
                contentScale = ContentScale.FillHeight
            )
        }
    }

    if (showDialog.value) {
        FullScreenImageDialog(
            imageUrls = imageUrls,
            selectedImageIndex = selectedImageIndex,
            onDismiss = { showDialog.value = false }
        )
    }
}
