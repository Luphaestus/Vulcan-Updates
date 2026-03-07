package luph.vulcanizerv3.updates.data

import android.view.View
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.ui.EmptyComingSoon
import luph.vulcanizerv3.updates.ui.components.BadgeContent
import luph.vulcanizerv3.updates.ui.components.info.ModInfo

import luph.vulcanizerv3.updates.ui.page.home.*
import luph.vulcanizerv3.updates.ui.page.settings.*
import luph.vulcanizerv3.updates.ui.page.settings.options.*
import luph.vulcanizerv3.updates.ui.page.updates.*
import luph.vulcanizerv3.updates.ui.page.misc.*
import luph.vulcanizerv3.updates.ui.page.misc.options.*
import luph.vulcanizerv3.updates.utils.getStandardAnimationSpeed


data class Route(
    val name: String,
    val selectedIcon: ImageVector = Icons.Filled.QuestionMark,
    val unselectedIcon: ImageVector = Icons.Outlined.QuestionMark,
    val content: @Composable (NavController, View) -> Unit = { _, _ -> EmptyComingSoon(name) },
    val showInMenu: Boolean = false,
    val showBadge: () -> Boolean = { false },
    val badgeContent: () -> BadgeContent? = { null },
    val enterTransition: EnterTransition = EnterTransition.None,
    val exitTransition: EnterTransition = EnterTransition.None,
    val stringResource: Int = 0,
    val localName: String? = null)

val Routes = listOf(
    Route("Home", Icons.Filled.Home, Icons.Outlined.Home, { navController, view -> HomePage(navController, view) }, showBadge = { ModDetailsStore.getNewMods().value.isNotEmpty() }, badgeContent = { BadgeContent.Text("New") }, showInMenu = true, stringResource = R.string.home_title, localName = MainActivity.applicationContext().getString(R.string.home)),
    Route("Home Details Expanded", content = { navController, view -> HomeModDetailsExpanded(navController, view) }),

    Route("Misc", Icons.Filled.Handyman, Icons.Outlined.Handyman, { navController, view -> MiscPage(navController, view) }, showInMenu = true, stringResource = R.string.updates_title, localName = MainActivity.applicationContext().getString(R.string.misc)),
    Route("Force Refresh Rate", content = { navController, view -> ForceRefreshRate(navController, view) }),
    Route("Change Boot Animation", content = { navController, view -> ChangeBootOption(navController, view) }),
    Route("Device Info", content = { navController, view -> DeviceInfo(navController, view) }),
    Route("Advanced Reboot Options", content = { navController, view -> AdvancedReboot(navController, view) }),
    Route("Help", content = { navController, view -> HelpOption(navController, view) }),
    Route("ShowHelp", content = { navController, view -> ShowHelp(navController, view) }),

    Route("Updates", Icons.Filled.Star, Icons.Outlined.Star, { navController, view -> UpdatesPage(navController, view) }, showBadge = { ModDetailsStore.getInstalledModsUpdate().value.isNotEmpty() || ModDetailsStore.isAppUpdatedNeeded().value }, badgeContent = { BadgeContent.Count(ModDetailsStore.getInstalledModsUpdate().value.size+if (ModDetailsStore.isAppUpdatedNeeded().value) 1 else 0) }, showInMenu = true, stringResource = R.string.updates_title, localName = MainActivity.applicationContext().getString(R.string.updates)),

    Route("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, content = { navController, view -> SettingsPage(navController, view) }, showInMenu = true, stringResource = R.string.settings_title, localName = MainActivity.applicationContext().getString(R.string.settings)),
    Route("Colour and Style", content = { navController, view -> ColorAndStyleOption(navController, view) }),
    Route("Notifications & Internet", content = { navController, view -> NotificationAndInternet(navController, view) }),
    Route("Language", content = { navController, view -> LanguageOption(navController, view) }),
    Route("Feedback", content = { navController, view -> FeedbackOption(navController, view) }),
    Route("Acknowledgement", content = { navController, view -> AcknowledgementOption(navController, view) }),

    Route("Mod Info", content = { navController, view -> ModInfo(navController, view) })
)

data object NavigationAnim {
    var enter = mutableStateOf(fadeIn(animationSpec = tween(getStandardAnimationSpeed())))
    var exit = mutableStateOf(fadeOut(animationSpec = tween(getStandardAnimationSpeed())))
    var popExit = mutableStateOf(fadeOut(animationSpec = tween(getStandardAnimationSpeed())))
    var popEnter = mutableStateOf(fadeIn(animationSpec = tween(getStandardAnimationSpeed())))
}

data class NavigationAnimClass(

    var enter: EnterTransition = fadeIn(animationSpec = tween(getStandardAnimationSpeed())),
    var exit: ExitTransition = fadeOut(animationSpec = tween(getStandardAnimationSpeed())),
    var popExit: ExitTransition = fadeOut(animationSpec = tween(getStandardAnimationSpeed())),
    var popEnter: EnterTransition = fadeIn(animationSpec = tween(getStandardAnimationSpeed()))
)
