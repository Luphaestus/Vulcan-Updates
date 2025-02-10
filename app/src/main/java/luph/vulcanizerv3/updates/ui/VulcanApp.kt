package luph.vulcanizerv3.updates.ui

import android.util.Log
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.ui.components.info.ModInfo
import luph.vulcanizerv3.updates.ui.components.info.UpdateAlert
import luph.vulcanizerv3.updates.ui.page.NavBarHandler
import luph.vulcanizerv3.updates.ui.page.misc.options.ChangeBootOption
import luph.vulcanizerv3.updates.ui.page.misc.options.ChangeQMGOption
import luph.vulcanizerv3.updates.ui.page.misc.options.ForceRefreshRate
import luph.vulcanizerv3.updates.ui.page.oobe.OOBE
import luph.vulcanizerv3.updates.ui.page.settings.options.FeedbackOption
import luph.vulcanizerv3.updates.ui.page.settings.options.LanguageOption
import luph.vulcanizerv3.updates.utils.download.getRemoteFile
import luph.vulcanizerv3.updates.utils.download.unzip
import luph.vulcanizerv3.updates.utils.download.zip
import luph.vulcanizerv3.updates.utils.installTwrpModule
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
private fun UpdateForceDialog(
    showInstallModal: MutableState<Boolean>,
) {
    UpdateAlert(
        title = "Update Required",
        description = "An update is required to continue using the app.",
        show = showInstallModal,
        positiveClickText = stringResource(R.string.ok),
        negativeClickText = "",
    )
}

@Composable
fun VulcanApp(
    windowSize: WindowSizeClass,
) {
    if (ModDetailsStore.isAppUpdateForced().value) {
        UpdateForceDialog(ModDetailsStore.showUpdateForceDialog)
        ModInfo(passedModDetails = ModDetailsStore.getCoreDetails().value["app"])
        return
    }
    NavBarHandler(windowSize)
}

