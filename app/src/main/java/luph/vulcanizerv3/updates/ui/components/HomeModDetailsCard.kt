package luph.vulcanizerv3.updates.ui.components

import android.view.View
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.analytics.logEvent
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.data.DETAILFILE
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.ui.ext.roundClick
import luph.vulcanizerv3.updates.ui.page.OpenRoute
import luph.vulcanizerv3.updates.ui.page.RouteParams
import kotlin.math.min


@Composable
fun ConcatenateStringsWithColors(
    bulletColor: Color,
    modifier: Modifier = Modifier,
    style: TextStyle,
    vararg pairs: Pair<String, Color>
) {
    val annotatedString = buildAnnotatedString {
        pairs.forEachIndexed { index, pair ->
            withStyle(style = SpanStyle(color = pair.second)) {
                append(pair.first)
            }
            if (index < pairs.size - 1) {
                withStyle(style = SpanStyle(color = bulletColor)) {
                    append(" • ")
                }
            }
        }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = modifier
                .padding(end = 4.dp)
                .size(14.dp)
        )
        Text(text = annotatedString, modifier = modifier, style = style)
    }
}


@Composable
fun HomeModDetailsCard(
    modDetails: ModDetails,
    padding: Dp = 16.dp,
    navController: NavController,
    view: View,
    screenReader: Boolean = false
) {
    if (!screenReader) {
        TransitionBox(
            "Mod Info",
            navController = navController,
            view = view,
            onClick = { RouteParams.push(modDetails) }) {
            Row(
                modifier = Modifier
                    .padding(end = padding)
                    .width(336.dp)
            ) {
                Image(
                    painter = rememberImagePainter(data = modDetails.url + DETAILFILE.ICON.type),
                    contentDescription = "Mod Icon",
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .size(56.dp)
                        .clip(RoundedCornerShape(18.dp))
                )

                Column {
                    val isInstalled = ModDetailsStore.getInstalledMods().value.contains(
                        modDetails.packageName
                    )
                    val needsUpdate = ModDetailsStore.getInstalledModsUpdate().value.contains(
                        modDetails.packageName
                    )

                    val isNew = ModDetailsStore.getNewMods().value.contains(
                        modDetails.url.dropLast(1).substringAfterLast("/")
                    )

                    if (isNew || needsUpdate || isInstalled) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    modifier = Modifier.offset(x = 18.dp),
                                    content = {
                                        Text(
                                            text = if (needsUpdate) "Update" else if (isInstalled) "Installed" else  "New",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    },
                                    containerColor = MaterialTheme.colorScheme.primary,
                                )
                            }
                        )
                        {
                            Text(
                                text = modDetails.name,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                    else {
                        Text(
                            text = modDetails.name,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    Text(
                        text = "Version ${modDetails.version}",
                        modifier = Modifier.padding(top = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    ConcatenateStringsWithColors(
                        bulletColor = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        modDetails.author to MaterialTheme.colorScheme.onSurface,
                        *modDetails.keywords.take(2)
                            .map { it to MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) }
                            .toTypedArray()
                    )
                }
            }
        }
    }
}

@Composable
fun HomeModDetailsCardCarousel(
    modDetails: List<ModDetails>,
    categoryName: String,
    navController: NavController,
    view: View,
    screenReader: Boolean = false
) {

    val gridState = rememberLazyGridState()
    val snapFlingBehavior =
        rememberSnapFlingBehavior(lazyGridState = gridState, snapPosition = SnapPosition.Start)

    Column(verticalArrangement = Arrangement.Center) {
        Box(Modifier.roundClick {
            RouteParams.push(categoryName)
            MainActivity.getFirebaseAnalytics().logEvent("opened_mod_category") {
                param("category", categoryName)
            }
            OpenRoute(
                "Home Details Expanded",
                navController,
                view,
                fadeIn(animationSpec = tween(700)),
                ExitTransition.None,
                EnterTransition.None,
                fadeOut(animationSpec = tween(500))
            )
        }) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                    modifier = Modifier.weight(1f)
                )
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next")

            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((if (screenReader) 0 else 100 * min(3, modDetails.size)).dp)
        ) {
            if (modDetails.isNotEmpty())
            LazyHorizontalGrid(
                rows = GridCells.Fixed(min(3, modDetails.size)),
                state = gridState,
                flingBehavior = snapFlingBehavior,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                var numberInLastColumn = modDetails.size % 3
                if (numberInLastColumn == 0) numberInLastColumn = 3

                itemsIndexed(modDetails) { index, modDetail ->
                    val isLastColumn = index >= modDetails.size - numberInLastColumn
                    HomeModDetailsCard(
                        modDetails = modDetail,
                        if (isLastColumn) 76.dp else 32.dp,
                        navController,
                        view,
                        screenReader,
                    )
                }
            }
        }
    }
}
