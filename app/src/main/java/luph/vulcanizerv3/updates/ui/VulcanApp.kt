package luph.vulcanizerv3.updates.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import luph.vulcanizerv3.updates.ui.page.NavBarHandler
import luph.vulcanizerv3.updates.ui.page.settings.options.AcknowledgementOption

@Composable
fun VulcanApp(
    windowSize: WindowSizeClass,
) {
    NavBarHandler(windowSize)
}


