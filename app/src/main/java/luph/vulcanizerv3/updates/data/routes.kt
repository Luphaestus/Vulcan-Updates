package luph.vulcanizerv3.updates.data

import android.view.View
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import luph.vulcanizerv3.updates.ui.EmptyComingSoon
import luph.vulcanizerv3.updates.ui.mods.ModInfo
import luph.vulcanizerv3.updates.ui.page.home.HomeModDetailsExpanded
import luph.vulcanizerv3.updates.ui.page.home.HomePage
import luph.vulcanizerv3.updates.ui.components.BadgeContent
import luph.vulcanizerv3.updates.ui.page.settings.SettingsPage
import luph.vulcanizerv3.updates.ui.page.settings.options.ColorAndStyleOption
import luph.vulcanizerv3.updates.ui.page.updates.UpdatesPage

data class Route(
    val name: String,
    val selectedIcon: ImageVector = Icons.Filled.QuestionMark,
    val unselectedIcon: ImageVector=Icons.Outlined.QuestionMark,
    val content: @Composable (NavController, View) -> Unit = { _, _ -> EmptyComingSoon(name) },
    val showInMenu: Boolean = false,
    val showBadge: () -> Boolean = { false },
    val badgeContent: () -> BadgeContent? = { null },
    val enterTransition : EnterTransition = EnterTransition.None,
    val exitTransition : EnterTransition = EnterTransition.None
    )
val routes = listOf(
    Route("Home", Icons.Filled.Home, Icons.Outlined.Home, {navController, view -> HomePage(navController, view) }, showBadge = {
        ModDetailsStore.getNewMods().value.isNotEmpty()
    }, badgeContent = { BadgeContent.Text("New")}, showInMenu = true),
    Route("Home Details Expanded", content = { navController, view -> HomeModDetailsExpanded(navController, view) }),


    Route("Updates", Icons.Filled.Star, Icons.Outlined.Star, { navController, view -> UpdatesPage(navController, view) }, showBadge = { true }, badgeContent = { BadgeContent.Count(200) }, showInMenu = true),
    Route("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, content = { navController, view-> SettingsPage(navController, view) }, showInMenu = true),

    Route("Colour and Style", content = { navController, view -> ColorAndStyleOption(navController, view) }),
    Route("Mod Info", content = { navController, view -> ModInfo(navController, view) })
)

data object NavigationAnim
{
    var enter = mutableStateOf(fadeIn(animationSpec = tween(700)))
    var exit = mutableStateOf(fadeOut(animationSpec = tween(700)))
    var popExit = mutableStateOf(fadeOut(animationSpec = tween(700)))
    var popEnter = mutableStateOf(fadeIn(animationSpec = tween(700)))
}