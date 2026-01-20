package luph.vulcanizerv3.updates.ui.page.settings

import android.view.View
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.ui.components.DisplayText
import luph.vulcanizerv3.updates.ui.components.RYScaffold

@Composable
fun SettingsPage(navController: NavController, view: View) {
    RYScaffold(
        content = {
            LazyColumn {
                item {
                    DisplayText(text = stringResource(R.string.settings_title), desc = "")
                }
                item {
                    SelectableSettingGroupItem("Colour and Style",
                        title = stringResource(R.string.color_and_style),
                        navController,
                        view,
                        desc = stringResource(R.string.color_and_style_desc),
                        icon = Icons.Outlined.Palette,
                    )
                }
                item {
                    SelectableSettingGroupItem(
                        "temp",
                        title =" stringResource(R.string.interaction)",
                        navController,
                        view,
                        desc = "stringResource(R.string.interaction_desc)",
                        icon = Icons.Outlined.TouchApp,
                    ) {

                    }
                }
                item {
                    SelectableSettingGroupItem(
                        "temp",
                        title =" stringResource(R.string.languages)",
                        navController,
                        view,
                        desc = "Locale.getDefault().toDisplayName()",
                        icon = Icons.Outlined.Language,
                    ) {

                    }
                }
                item {
                    SelectableSettingGroupItem(
                        "temp",
                        title = "stringResource(R.string.troubleshooting)",
                        navController,
                        view,
                        desc = "stringResource(R.string.troubleshooting_desc)",
                        icon = Icons.Outlined.BugReport,
                    ) {

                    }
                }
                item {
                    SelectableSettingGroupItem(
                        "Mod Info",
                        title = "stringResource(R.string.tips_and_support)",
                        navController,
                        view,
                        desc = "stringResource(R.string.tips_and_support_desc)",
                        icon = Icons.Outlined.TipsAndUpdates,
                    ) {

                    }
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
            }
        }
    )
}
