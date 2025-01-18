package luph.vulcanizerv3.updates.ui.components.info

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.DETAILFILE
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.data.UpdateStatus
import luph.vulcanizerv3.updates.data.infoBoxesData
import luph.vulcanizerv3.updates.utils.download.getDownloadSize

@Composable
fun InfoPane(modDetails: ModDetails, downloadProgressPercentage: MutableIntState, infoState: MutableState<UpdateStatus>, infoBoxesData: infoBoxesData, view:View)
{
    val fileSize = remember { mutableFloatStateOf(-1f) }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            fileSize.floatValue = getDownloadSize(modDetails.url + DETAILFILE.FILE.type) / 1048576f
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                shape = ShapeDefaults.Large
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(124.dp)
                .padding(8.dp)
        ) {

            val targetSize =
                if (infoState.value == UpdateStatus.UPDATING) 60.dp else 124.dp
            val animatedSize by animateDpAsState(
                targetValue = targetSize,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            Image(
                painter = rememberImagePainter(data = modDetails.url + DETAILFILE.ICON.type),
                contentDescription = "Mod Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(animatedSize)
                    .clip(RoundedCornerShape(22.dp))
            )
            if (infoState.value == UpdateStatus.UPDATING) {
                if (downloadProgressPercentage.intValue != 0 && downloadProgressPercentage.intValue != 100) {
                    val animatedProgress by animateFloatAsState(
                        targetValue = downloadProgressPercentage.intValue / 100f,
                        animationSpec = tween(
                            durationMillis = 1500,
                            easing = CubicBezierEasing(0.2f, 0f, 0.8f, 1f)
                        )
                    )
                    CircularProgressIndicator(
                        modifier = Modifier.size(180.dp),
                        progress = animatedProgress
                    )
                } else {
                    CircularProgressIndicator(Modifier.size(180.dp))
                }
            }
        }
        Text(
            text = modDetails.name,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = buildAnnotatedString {
                append(text = stringResource(R.string.by))
                withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                    append(modDetails.author)
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { infoBoxesData.showVersion.value = true },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = stringResource(R.string.version), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = modDetails.version,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .width(2.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(30.dp)
                    )
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = stringResource(R.string.mod_size), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = "${"%.3g".format(fileSize.floatValue)}mb",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .width(2.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(30.dp)
                    )
            )
            ElevatedButton(
                onClick = {
                    val context = view.context
                    val intent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(modDetails.srcLink))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(4.dp)
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = stringResource(R.string.mod_source_code_link_button),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.mod_source_code_link_button),
                        Modifier.fillMaxWidth(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}