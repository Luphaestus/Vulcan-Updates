package luph.vulcanizerv3.updates.ui.page.misc.options

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.ui.components.PageNAv
import luph.vulcanizerv3.updates.ui.components.Tinder
import luph.vulcanizerv3.updates.ui.page.settings.options.slightlyDeemphasizedAlpha
import luph.vulcanizerv3.updates.ui.page.showNavigation




@Composable
@Preview(showBackground = true)
fun ChangeBootOption(
    navController: NavController = rememberNavController(),
    view: View? = null) {
    showNavigation.show = false



    Scaffold(
        bottomBar = {
            Row {
                OutlinedButton(
                    onClick = {
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
//                    enabled = pageNumber > -sharedForm.size
                ) {
                    Text("Previous")
                }
                Button(

                    onClick = {
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                ) {
                    Text("Next")
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(bottom = padding.calculateBottomPadding() + 8.dp + (10f * 5).dp)
                .padding(horizontal = 16.dp)
        ) {
            PageNAv("Boot Animation", navController)

            Text(
                "currentQuestion.question",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 10.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(color = MaterialTheme.colorScheme.inverseOnSurface)
                    .padding(20.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha),
            )


            val baseUrl = stringResource(R.string.github_link)
            val device = "n986b"

            val items = remember {
                mutableListOf(
                    "colourful",
                    "burnice",
                    ""
                )
            }

            Tinder(
                modifier = Modifier.background(Color.Transparent),
                items = items
            ) { item, index ->
                if (item.isNotEmpty()) {
                    ConstraintLayout(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val (main, likedislike) = createRefs()

                        val painter =
                            rememberImagePainter("${baseUrl}animations/$device/$item.jpg")

                        Box(
                            Modifier.fillMaxHeight().constrainAs(main) {
                                centerHorizontallyTo(parent)
                            },
                            contentAlignment = Alignment.TopCenter,

                            ) {
                            Image(
                                painter = painter,
                                contentDescription = "Boot Animation Preview",
                                modifier = Modifier.fillMaxHeight()
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.shapes.large
                                    )
                                    .clip(MaterialTheme.shapes.large),
                                contentScale = ContentScale.Crop
                            )


                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp),
                                        MaterialTheme.shapes.medium
                                    ).padding(16.dp).alpha(0.5f),
                            ) {
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha)
                                )
                            }
                        }
                    }
                } else {
                    ConstraintLayout(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val (main, upload) = createRefs()

                        val painter =
                            rememberImagePainter("${baseUrl}animations/$device/${items[0]}.jpg")

                        Box(
                            Modifier.fillMaxHeight().constrainAs(main) {
                                centerHorizontallyTo(parent)
                            },
                            contentAlignment = Alignment.TopCenter,

                            ) {
                            Image(
                                painter = painter,
                                contentDescription = "Boot Animation Preview",
                                modifier = Modifier.fillMaxHeight()
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.shapes.large
                                    )
                                    .clip(MaterialTheme.shapes.large),
                                contentScale = ContentScale.Crop
                            )


                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(
                                    horizontal = 32.dp,
                                    vertical = 16.dp
                                )
                                    .background(
                                        MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp),
                                        MaterialTheme.shapes.medium
                                    ).padding(16.dp).alpha(0.5f),
                            ) {
                                Text(
                                    text = "Custom Boot Animation",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha)
                                )
                            }
                        }
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(
                                horizontal = 32.dp,
                                vertical = 16.dp
                            )
                                .background(
                                    MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp),
                                    MaterialTheme.shapes.medium
                                ).padding(16.dp).alpha(0.5f).constrainAs(upload) {
                                    centerTo(parent)
                                },
                        ) {
                            Text(
                                text = "Upload your logo.jpg",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha)
                            )
                            Button(onClick = { /*TODO*/ }) {
                                Text("Upload")
                            }
                        }
                    }
                }
            }
        }
    }
}