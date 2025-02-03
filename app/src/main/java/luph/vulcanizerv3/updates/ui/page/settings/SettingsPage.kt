package luph.vulcanizerv3.updates.ui.page.settings

import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.ui.components.DisplayText
import luph.vulcanizerv3.updates.ui.components.RYScaffold
import luph.vulcanizerv3.updates.ui.components.SelectableSettingGroupItem
import luph.vulcanizerv3.updates.ui.page.showNavigation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun SettingsPage(navController: NavController, view: View) {
    showNavigation.show = true
    BackHandler {}

    RYScaffold(
        content = {
            LazyColumn {
                item {
                    DisplayText(text = stringResource(R.string.settings_title), desc = "")
                }
                item {
                    SelectableSettingGroupItem(
                        "Colour and Style",
                        title = stringResource(R.string.color_and_style),
                        navController,
                        view,
                        desc = stringResource(R.string.colour_and_style_desc),
                        icon = Icons.Outlined.Palette,
                    )
                }
                item {
                    SelectableSettingGroupItem(
                        "Notifications & Internet",
                        title = stringResource(R.string.notifications_internet),
                        navController,
                        view,
                        desc = stringResource(R.string.manage_your_notification_preferences_and_internet_settings),
                        icon = Icons.Outlined.TouchApp,
                    )
                }
                item {
                    SelectableSettingGroupItem(
                        "Language",
                        title = stringResource(R.string.language),
                        navController,
                        view,
                        desc = stringResource(R.string.language_desc),
                        icon = Icons.Outlined.Language,
                    )
                }
                item {
                    SelectableSettingGroupItem(
                        "Feedback",
                        title = "Feedback",
                        navController,
                        view,
                        desc = stringResource(R.string.report_bugs_and_request_features),
                        icon = Icons.Outlined.BugReport,
                    )
                }
                item {
                    SelectableSettingGroupItem(
                        "Acknowledgement",
                        title = stringResource(R.string.acknowledgments_title),
                        navController,
                        view,
                        desc = stringResource(R.string.acknowledging_resources_and_contributions),
                        icon = Icons.Outlined.TipsAndUpdates,
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
