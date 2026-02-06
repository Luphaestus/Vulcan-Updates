package luph.vulcanizerv3.updates.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.ui.components.info.ModInfo
import luph.vulcanizerv3.updates.ui.page.NavBarHandler
import luph.vulcanizerv3.updates.ui.page.misc.options.ForceRefreshRate

@Composable
fun VulcanApp(
    windowSize: WindowSizeClass,
) {
    if (ModDetailsStore.isAppUpdateForced().value) {
        ModInfo(passedModDetails = ModDetailsStore.getAppDetails().value)
        return
    }
    NavBarHandler(windowSize)
}


