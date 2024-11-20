package luph.vulcanizerv3.updates.ui.page.home

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.ui.components.NoInternet
import luph.vulcanizerv3.updates.ui.components.HomeModDetailsCardCarousel
import luph.vulcanizerv3.updates.ui.page.showNavigation
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.dp
import com.mohamedbenrejeb.pencilloader.PencilLoader
import androidx.compose.foundation.layout.size
import com.spr.jetpack_loading.components.indicators.*
import com.spr.jetpack_loading.components.indicators.gridIndicator.BallGridBeatIndicator
import com.spr.jetpack_loading.components.indicators.gridIndicator.GridFadeAntiDiagonal
import com.spr.jetpack_loading.components.indicators.gridIndicator.GridFadeDiagonal
import com.spr.jetpack_loading.components.indicators.gridIndicator.GridPulsatingDot
import com.spr.jetpack_loading.components.indicators.lineScaleIndicator.LineScaleIndicator
import com.spr.jetpack_loading.components.indicators.shape_unveil_indicator.CircleShapeIndicator
import com.spr.jetpack_loading.components.indicators.shape_unveil_indicator.TriangleShapeIndicator
import com.spr.jetpack_loading.enums.PunchType

@Composable
fun RandomIndicator() {
    val indicators = listOf<@Composable () -> Unit>(
        { PulsatingDot() },
        { GridPulsatingDot() },
        { CircularPulsatingIndicator() },
        { BallClipRotatePulseIndicator() },
        { SquareSpinIndicator() },
        { BallClipRotateMultipleIndicator() },
        { BallPulseRiseIndicator() },
        { BallRotateIndicator() },
        { CubeTransitionIndicator() },
        { BallZigZagIndicator() },
        { BallZigZagDeflectIndicator() },
        { BallTrianglePathIndicator() },
        { BallScaleIndicator() },
        { LineScaleIndicator(punchType = PunchType.RANDOM_PUNCH) },
        { BallScaleMultipleIndicator() },
        { BallPulseSyncIndicator() },
        { BallBeatIndicator() },
        { BallScaleRippleIndicator() },
        { BallScaleRippleMultipleIndicator() },
        { BallSpinFadeLoaderIndicator() },
        { LineSpinFadeLoaderIndicator() },
        { TriangleSpinIndicator() },
        { PacmanIndicator() },
        { BallGridBeatIndicator() },
        { SemiCircleSpinIndicator() },
        { GridFadeDiagonal() },
        { GridFadeAntiDiagonal() },
        { BallRespectivelyExitIndicator()},
        { TriangleShapeIndicator() },
        { CircleShapeIndicator() },
        { PencilLoader(strokeWidth = 14.dp, modifier = Modifier.size(125.dp)) }
    )
    val randomIndicator = indicators.random()
    randomIndicator()
}

@Composable
fun HomePage(navController: NavController, view: View) {
    val modDetailsState = ModDetailsStore.getAllModKeywords()
    val listState = rememberLazyListState()
    showNavigation.show.value = true

    Box(Modifier.background(MaterialTheme.colorScheme.surface))
    {
        if (!ModDetailsStore.isOffline().value) {
            if (modDetailsState.value.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()

                ) {
                    RandomIndicator()
                }
            } else {
                LazyColumn(state = listState) {
                    modDetailsState.value.forEach { (category, modList) ->
                        item {
                            HomeModDetailsCardCarousel(
                                modDetails = modList,
                                categoryName = category,
                                navController = navController,
                                view = view
                            )
                        }
                    }
                }
            }
        } else {
            NoInternet()
        }
    }
}
@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    val context = LocalContext.current
    HomePage(navController = rememberNavController(), view = View(context))
}