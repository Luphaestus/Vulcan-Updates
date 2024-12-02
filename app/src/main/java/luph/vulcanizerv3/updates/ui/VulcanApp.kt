package luph.vulcanizerv3.updates.ui

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.ui.components.ModInfo
import luph.vulcanizerv3.updates.ui.page.NavBarHandler
import luph.vulcanizerv3.updates.ui.page.OpenRoute
import luph.vulcanizerv3.updates.ui.page.RouteParams
import luph.vulcanizerv3.updates.ui.page.misc.options.DeviceInfo
import luph.vulcanizerv3.updates.ui.page.settings.options.AcknowledgementOption
import luph.vulcanizerv3.updates.ui.page.settings.options.FeedbackOption

@Composable
fun VulcanApp(
    windowSize: WindowSizeClass,
) {
    if (ModDetailsStore.isAppUpdateForced().value) {
        ModInfo(passedModDetails = ModDetailsStore.getAppDetails().value)
        return
    }
    NavBarHandler(windowSize)
//    DeviceInfo()
}


