package luph.vulcanizerv3.updates.ui.page.misc

import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.ui.components.DisplayText
import luph.vulcanizerv3.updates.ui.components.RYScaffold
import luph.vulcanizerv3.updates.ui.components.SelectableSettingGroupItem
import luph.vulcanizerv3.updates.ui.page.showNavigation
import luph.vulcanizerv3.updates.utils.root.ROOTStatus
import luph.vulcanizerv3.updates.utils.root.getROOTStatus

@Composable
@Preview(showBackground = true)
fun MiscPage(
    navController: NavController = rememberNavController(),
    view: View = View(navController.context)) {
    showNavigation.show = true
    val isRooted = getROOTStatus() != ROOTStatus.NONE
    BackHandler {}


    RYScaffold(
        content = {
            LazyColumn {
                item {
                    DisplayText(text = stringResource(R.string.misc), desc = "")
                }

//                item {
//                    SelectableSettingGroupItem(
//                        "Force Refresh Rate",
//                        title = stringResource(R.string.force_refresh_rate),
//                        navController,
//                        view,
//                        desc = stringResource(R.string.force_the_refresh_rate_of_the_display),
//                        icon = Icons.Outlined.Refresh,
//                    )
//                }

                item {
                    SelectableSettingGroupItem(
                        "Change Boot Animation",
                        title = stringResource(R.string.change_boot_animation),
                        navController,
                        view,
                        desc = stringResource(R.string.change_the_boot_animation_of_the_device),
                        icon = Icons.Outlined.Palette,
                    )
                }

                item {
                    SelectableSettingGroupItem(
                        "Device Info",
                        title = stringResource(R.string.device_info),
                        navController,
                        view,
                        desc = stringResource(R.string.show_hardware_and_software_information),
                        icon = Icons.Outlined.MonitorHeart,
                    )
                }

                if (isRooted) {
                    item {
                        SelectableSettingGroupItem(
                            "Advanced Reboot Options",
                            title = stringResource(R.string.advanced_reboot_options),
                            navController,
                            view,
                            desc = stringResource(R.string.reboot_into_recovery_download_or_system),
                            icon = Icons.Outlined.TouchApp,
                        )
                    }
                }

                item {
                    SelectableSettingGroupItem(
                        "Help",
                        title = stringResource(R.string.help),
                        navController,
                        view,
                        desc = stringResource(R.string.help_documentation),
                        icon = Icons.AutoMirrored.Outlined.HelpOutline,
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
            }
        }
    )
}
