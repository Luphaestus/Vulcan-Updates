package luph.vulcanizerv3.updates.ui

import android.util.Log
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import luph.vulcanizerv3.updates.ui.page.NavBarHandler
import luph.vulcanizerv3.updates.utils.root.isRooted


@Composable
fun VulcanApp(
    windowSize: WindowSizeClass,
) {
    Log.e("isRooted", "isRooted: ${isRooted()}")
    NavBarHandler(windowSize)
}


