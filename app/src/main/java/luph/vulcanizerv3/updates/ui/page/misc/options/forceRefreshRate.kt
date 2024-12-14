package luph.vulcanizerv3.updates.ui.page.misc.options

import android.content.ContentResolver
import android.content.Context
import android.graphics.drawable.Icon
import android.hardware.display.DisplayManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.ui.components.PageNAv
import com.airbnb.lottie.compose.*
import com.github.theapache64.twyper.Twyper
import com.github.theapache64.twyper.rememberTwyperController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kotlinx.coroutines.withContext
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.ui.page.showNavigation
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.RadioButtonPreference
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.time.Duration

val dotsCount = arrayOf(1, 1, 2, 5, 4, 2, 1, 3, 2, 3)

@Composable
@Preview(showBackground = true)
fun RefreshRateDemo(is60: Boolean = false) {
    val scrollState = rememberLazyListState()
    val density = LocalDensity.current


    var scrollHeight = 0
    var animateTarget by remember { mutableStateOf(0) }
    val animateScroll by animateDpAsState(targetValue = animateTarget.dp, animationSpec = tween(durationMillis = 5000 ))

    LaunchedEffect(Unit) {
          withContext(Dispatchers.Default) {
              while (true) {
                  if (animateTarget == 0) {
                      animateTarget = scrollHeight
                  } else {
                      animateTarget = 0
                  }
                  kotlinx.coroutines.delay(6000)
              }
          }
      }
    var adjustedOffset : Dp by remember { mutableStateOf(0.dp) }
    if (is60) {
        var shouldUpdate by remember { mutableStateOf(true) }
        if (shouldUpdate)
        {
            shouldUpdate = false
            adjustedOffset = animateScroll.value.dp
        }
        else {
            shouldUpdate = true
        }
    }


    Column(
        Modifier.padding(horizontal = 8.dp)
        .background(
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp),
            shape = MaterialTheme.shapes.large
        ).border(3.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.large)
        .padding(horizontal = 4.dp, vertical = 8.dp)

    ) {

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Icon(
                Icons.Outlined.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 8.dp).size(18.dp)
            )
        }
        LazyColumn(
            modifier = Modifier
                .height(216.dp),
            userScrollEnabled = false,
            state = scrollState


        ) {
            item {
                Column(modifier = Modifier.onGloballyPositioned { coordinates ->
                    val height = with(density) { coordinates.size.height.toDp() - 216.dp }
                    scrollHeight = height.value.toInt()
                }.offset(y = if (is60) -adjustedOffset else -animateScroll)) {
                    dotsCount.forEachIndexed { index, it ->
                        Column(
                            Modifier.padding(horizontal = 8.dp, vertical = 4.dp).fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.background,
                                    shape = MaterialTheme.shapes.medium
                                ),
                        ) {
                            Column(
                                Modifier.fillMaxWidth().padding(8.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = if (index == 0) Alignment.End else Alignment.Start
                            ) {
                                for (i in 0 until it) {
                                    Box(
                                        Modifier.padding(vertical = 8.dp)
                                            .background(
                                                color = Color.hsl(
                                                    (Random(index * 10 + i).nextInt(360)).toFloat(),
                                                    0.5f,
                                                    0.6f
                                                ), shape = CircleShape
                                            )
                                            .size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun setRefreshRate(refreshRate: Int) {
    if (refreshRate == -1) {
        runRootShellCommand("settings delete system min_refresh_rate", false)
        runRootShellCommand("settings delete system peak_refresh_rate", false)
        runRootShellCommand("settings put secure refresh_rate_mode 1", false)
    } else {
        runRootShellCommand("settings put system min_refresh_rate $refreshRate.0", false)
        runRootShellCommand("settings put system peak_refresh_rate $refreshRate.0", false)
        runRootShellCommand("settings put secure refresh_rate_mode 2", false)
    }

}

fun getRefreshRates(contentResolver: ContentResolver): Int {
   return Settings.System.getFloat(contentResolver, "min_refresh_rate", -1f).toInt()
}


fun getSupportedRefreshRates(): List<Int> {
    val refreshRates = mutableListOf(-1)
    val context = MainActivity.applicationContext()
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    val supportedRefreshRates = displayManager.getDisplay(display.displayId).supportedRefreshRates
    for (rate in supportedRefreshRates) {
        refreshRates.add(rate.toInt())
    }
    return refreshRates
}

@Composable
@Preview(showBackground = true)
fun ForceRefreshRate(
    navController: NavController = rememberNavController(),
    view: View? = null
) {
    showNavigation.show = false
   LazyColumn(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp)) {
        item {
            PageNAv("Force Refresh Rate", navController)
        }

        item {
            Row(Modifier.background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp), MaterialTheme.shapes.large).padding(16.dp)) {
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("60Hz", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom=2.dp))
                    RefreshRateDemo(true)
                }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("120Hz", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom=2.dp))
                    RefreshRateDemo(true)
                }
            }
        }
       item {
           val refreshRates = getSupportedRefreshRates()
           val contentResolver = LocalContext.current.contentResolver
           var selectedRate by remember { mutableStateOf(getRefreshRates(contentResolver)) }
           ProvidePreferenceLocals {
               refreshRates.forEach {
                   RadioButtonPreference(
                       selected = selectedRate == it,
                       title = { Text(if (it == -1) "System Default" else "${it}Hz") },
                   ) {
                          selectedRate = it
                   }
               }
           }
           Spacer(Modifier.height(16.dp))
           Button(onClick = {
               getRefreshRates(contentResolver)
               setRefreshRate(selectedRate)
           }, Modifier.padding(start = 16.dp).width(128.dp)) {
                Text("Apply")
           }
       }
    }
}


