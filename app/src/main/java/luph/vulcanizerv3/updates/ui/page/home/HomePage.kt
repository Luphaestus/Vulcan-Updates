package luph.vulcanizerv3.updates.ui.page.home

import android.content.Context
import android.content.Context.ACCESSIBILITY_SERVICE
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mohamedbenrejeb.pencilloader.PencilLoader
import com.spr.jetpack_loading.components.indicators.BallBeatIndicator
import com.spr.jetpack_loading.components.indicators.BallClipRotateMultipleIndicator
import com.spr.jetpack_loading.components.indicators.BallClipRotatePulseIndicator
import com.spr.jetpack_loading.components.indicators.BallPulseRiseIndicator
import com.spr.jetpack_loading.components.indicators.BallPulseSyncIndicator
import com.spr.jetpack_loading.components.indicators.BallRespectivelyExitIndicator
import com.spr.jetpack_loading.components.indicators.BallRotateIndicator
import com.spr.jetpack_loading.components.indicators.BallScaleIndicator
import com.spr.jetpack_loading.components.indicators.BallScaleMultipleIndicator
import com.spr.jetpack_loading.components.indicators.BallScaleRippleIndicator
import com.spr.jetpack_loading.components.indicators.BallScaleRippleMultipleIndicator
import com.spr.jetpack_loading.components.indicators.BallSpinFadeLoaderIndicator
import com.spr.jetpack_loading.components.indicators.BallTrianglePathIndicator
import com.spr.jetpack_loading.components.indicators.BallZigZagDeflectIndicator
import com.spr.jetpack_loading.components.indicators.BallZigZagIndicator
import com.spr.jetpack_loading.components.indicators.CircularPulsatingIndicator
import com.spr.jetpack_loading.components.indicators.CubeTransitionIndicator
import com.spr.jetpack_loading.components.indicators.LineSpinFadeLoaderIndicator
import com.spr.jetpack_loading.components.indicators.PacmanIndicator
import com.spr.jetpack_loading.components.indicators.PulsatingDot
import com.spr.jetpack_loading.components.indicators.SemiCircleSpinIndicator
import com.spr.jetpack_loading.components.indicators.SquareSpinIndicator
import com.spr.jetpack_loading.components.indicators.TriangleSpinIndicator
import com.spr.jetpack_loading.components.indicators.gridIndicator.BallGridBeatIndicator
import com.spr.jetpack_loading.components.indicators.gridIndicator.GridFadeAntiDiagonal
import com.spr.jetpack_loading.components.indicators.gridIndicator.GridFadeDiagonal
import com.spr.jetpack_loading.components.indicators.gridIndicator.GridPulsatingDot
import com.spr.jetpack_loading.components.indicators.lineScaleIndicator.LineScaleIndicator
import com.spr.jetpack_loading.components.indicators.shape_unveil_indicator.CircleShapeIndicator
import com.spr.jetpack_loading.components.indicators.shape_unveil_indicator.TriangleShapeIndicator
import com.spr.jetpack_loading.enums.PunchType
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.ui.components.DisplayText
import luph.vulcanizerv3.updates.ui.components.HomeModDetailsCardCarousel
import luph.vulcanizerv3.updates.ui.components.NoInternet
import luph.vulcanizerv3.updates.ui.components.RYScaffold
import luph.vulcanizerv3.updates.ui.page.showNavigation


@Composable
fun RandomIndicator() {
    val indicators = listOf<@Composable () -> Unit>(
        { PulsatingDot(color = MaterialTheme.colorScheme.primary) },
        { GridPulsatingDot(color = MaterialTheme.colorScheme.primary) },
        { CircularPulsatingIndicator(color = MaterialTheme.colorScheme.primary) },
        { BallClipRotatePulseIndicator(color = MaterialTheme.colorScheme.primary) },
        { SquareSpinIndicator(color = MaterialTheme.colorScheme.primary) },
        { BallClipRotateMultipleIndicator(color = MaterialTheme.colorScheme.primary) },
        { BallPulseRiseIndicator(color = MaterialTheme.colorScheme.primary) },
        { BallRotateIndicator(color = MaterialTheme.colorScheme.primary) },
        { CubeTransitionIndicator(color = MaterialTheme.colorScheme.primary) },
        { BallZigZagIndicator(color = MaterialTheme.colorScheme.primary) },
        { BallZigZagDeflectIndicator(color = MaterialTheme.colorScheme.primary) },
        { BallTrianglePathIndicator(color = MaterialTheme.colorScheme.primary) },
        { BallScaleIndicator(color = MaterialTheme.colorScheme.primary) },
        {
            LineScaleIndicator(
                color = MaterialTheme.colorScheme.primary,
                punchType = PunchType.RANDOM_PUNCH
            )
        },
        { BallScaleMultipleIndicator(color = MaterialTheme.colorScheme.primary) },
        { BallPulseSyncIndicator(color = MaterialTheme.colorScheme.primary) },
        { BallBeatIndicator(color = MaterialTheme.colorScheme.primary) },
        { BallScaleRippleIndicator(color = MaterialTheme.colorScheme.primary) },
        { BallScaleRippleMultipleIndicator(color = MaterialTheme.colorScheme.primary) },
        { BallSpinFadeLoaderIndicator(color = MaterialTheme.colorScheme.primary) },
        { LineSpinFadeLoaderIndicator(color = MaterialTheme.colorScheme.primary) },
        { TriangleSpinIndicator(color = MaterialTheme.colorScheme.primary) },
        { PacmanIndicator(color = MaterialTheme.colorScheme.primary) },
        { BallGridBeatIndicator(color = MaterialTheme.colorScheme.primary) },
        { SemiCircleSpinIndicator(color = MaterialTheme.colorScheme.primary) },
        { GridFadeDiagonal(color = MaterialTheme.colorScheme.primary) },
        { GridFadeAntiDiagonal(color = MaterialTheme.colorScheme.primary) },
        { BallRespectivelyExitIndicator(color = MaterialTheme.colorScheme.primary) },
        { TriangleShapeIndicator(color = MaterialTheme.colorScheme.primary) },
        { CircleShapeIndicator(color = MaterialTheme.colorScheme.primary) },
        { PencilLoader(strokeWidth = 14.dp, modifier = Modifier.size(125.dp)) }
    )
    val randomIndicator = indicators.random()
    randomIndicator()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController, view: View) {
    val modDetailsState = ModDetailsStore.getAllModKeywords()
    val listState = rememberLazyListState()
    showNavigation.show = true
    val am = MainActivity.applicationContext()
        .getSystemService(ACCESSIBILITY_SERVICE) as? AccessibilityManager
    val isExploreByTouchEnabled: Boolean = am?.isTouchExplorationEnabled ?: false

    RYScaffold(
        content = {

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
                        PullToRefreshBox(ModDetailsStore.isUpdating().value, {ModDetailsStore.refresh()}) {
                            LazyColumn(state = listState) {
                                item {
                                    DisplayText(
                                        text = stringResource(R.string.home_title),
                                        desc = ""
                                    )
                                }
                                modDetailsState.value.forEach { (category, modList) ->
                                    item {
                                        HomeModDetailsCardCarousel(
                                            modDetails = modList,
                                            categoryName = category,
                                            navController = navController,
                                            view = view,
                                            screenReader = isExploreByTouchEnabled
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    NoInternet()
                }
            }
        })
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    val context = LocalContext.current
    HomePage(navController = rememberNavController(), view = View(context))
}