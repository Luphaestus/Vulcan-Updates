package luph.vulcanizerv3.updates.ui.page

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.TestModifierUpdaterLayout
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.analytics.logEvent
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.data.NavigationAnim
import luph.vulcanizerv3.updates.data.NavigationAnimClass
import luph.vulcanizerv3.updates.data.Route
import luph.vulcanizerv3.updates.data.Routes
import luph.vulcanizerv3.updates.ui.components.BadgeFormatter
import luph.vulcanizerv3.updates.ui.page.misc.options.helpItem
import luph.vulcanizerv3.updates.utils.getStandardAnimationSpeed


enum class NavigationType {
    BAR, RAIL
}

data object showNavigation {
    private val _show = mutableStateOf(true)
    var show: Boolean
        get() = _show.value
        set(value) {
            _show.value = value
        }
}


@Composable
fun NavBarGenerator(navigationType: NavigationType, navController: NavController) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val view = LocalView.current
    val badgeFormatter = BadgeFormatter()


    @Composable
    fun NavigationItemContent(index: Int, route: Route, iconSizeOffset: Float = 0f) {
        badgeFormatter.badge(
            enabled = route.showBadge(),
            icon = if (selectedItem == index) route.selectedIcon else route.unselectedIcon,
            contentDescription = route.name,
            badgeContent = route.badgeContent(),
            iconSizeOffset
        )()
    }

    AnimatedVisibility(
        visible = showNavigation.show,
        enter = slideInVertically(animationSpec = tween(getStandardAnimationSpeed())) { it },
        exit = slideOutVertically(animationSpec = tween(getStandardAnimationSpeed())) { it }
    ) {
        when (navigationType) {

            NavigationType.BAR -> {


                NavigationBar {
                    val iconOffsets = remember { mutableStateListOf<MutableState<Float>>() }
                    Routes.forEachIndexed { index, route ->

                        if (route.showInMenu) {
                            var iconSizeOffset = remember { mutableStateOf(0f) }
                            if (!iconOffsets.contains(iconSizeOffset)) {
                                iconOffsets.add(iconSizeOffset)
                            }


                            val animatedIconOffset by animateFloatAsState(
                                targetValue = iconSizeOffset.value,
                                animationSpec = if (iconSizeOffset.value > 0) {
                                    spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessLow)
                                } else {
                                    tween(durationMillis = 3000)
                                }
                            )

                            NavigationBarItem(
                                icon = { NavigationItemContent(index, route, animatedIconOffset) },
                                label = { Text(route.localName?:route.name) },
                                selected = selectedItem == index,
                                onClick = {
                                    for (iconOffset in iconOffsets) {

                                        iconOffset.value = 0f
                                    }
                                    iconSizeOffset.value = 3f



                                    var navigated: Boolean = false
                                    if (selectedItem < index)
                                        navigated = OpenRoute(
                                            route.name,
                                            navController,
                                            view,
                                            slideInHorizontally(animationSpec = tween(getStandardAnimationSpeed())) { it },
                                            slideOutHorizontally(animationSpec = tween(getStandardAnimationSpeed())) { -it })
                                    else if (selectedItem > index)
                                        navigated = OpenRoute(
                                            route.name,
                                            navController,
                                            view,
                                            slideInHorizontally(animationSpec = tween(getStandardAnimationSpeed())) { -it },
                                            slideOutHorizontally(animationSpec = tween(getStandardAnimationSpeed())) { it })
                                    else
                                        navigated = OpenRoute(
                                            route.name,
                                            navController,
                                            view,
                                            fadeIn(animationSpec = tween(getStandardAnimationSpeed())),
                                            ExitTransition.None
                                        )

                                    if (navigated) selectedItem = index
                                }
                            )
                        }
                    }
                }
            }

            NavigationType.RAIL -> {
                NavigationRail {
                    Routes.forEachIndexed { index, route ->
                        if (route.showInMenu) {
                            NavigationRailItem(
                                icon = { NavigationItemContent(index, route) },
                                label = { Text(stringResource(route.stringResource)) },
                                selected = selectedItem == index,
                                onClick = {
                                    if (selectedItem < index)
                                        OpenRoute(
                                            route.name,
                                            navController,
                                            view,
                                            slideInHorizontally(animationSpec = tween(getStandardAnimationSpeed())) { it },
                                            slideOutHorizontally(animationSpec = tween(getStandardAnimationSpeed())) { -it })
                                    else if (selectedItem > index)
                                        OpenRoute(
                                            route.name,
                                            navController,
                                            view,
                                            slideInHorizontally(animationSpec = tween(getStandardAnimationSpeed())) { -it },
                                            slideOutHorizontally(animationSpec = tween(getStandardAnimationSpeed())) { it })
                                    else
                                        OpenRoute(
                                            route.name,
                                            navController,
                                            view,
                                            EnterTransition.None,
                                            ExitTransition.None
                                        )
                                    selectedItem = index
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

var isAnimating = false

fun OpenRoute(
    name: String,
    navController: NavController,
    view: View,
    enter: EnterTransition,
    exit: ExitTransition,
    popEnter: EnterTransition? = null,
    popExit: ExitTransition? = null
): Boolean {
    if (isAnimating) return false
    MainActivity.getFirebaseAnalytics().logEvent("navigation") {
        param("page", name)
    }
    isAnimating = true
    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    navController.navigate(name)
    NavigationAnim.enter.value = enter
    NavigationAnim.exit.value = exit
    if (popEnter != null) NavigationAnim.popEnter.value =
        popEnter else NavigationAnim.popEnter.value = enter
    if (popExit != null) NavigationAnim.popExit.value = popExit else NavigationAnim.popExit.value =
        exit

    val animationDuration = 0L
    view.postDelayed({ isAnimating = false }, animationDuration)
    RouteParams.push(
        NavigationAnimClass(
            enter = NavigationAnim.enter.value,
            exit = NavigationAnim.exit.value,
            popEnter = NavigationAnim.popEnter.value,
            popExit = NavigationAnim.popExit.value
        )
    )
    return true
}

@Composable
fun NavBarHandler(windowSize: WindowSizeClass): NavController {
    val isCompact = windowSize.widthSizeClass == Compact
    val navController = rememberNavController()



    Scaffold(
        bottomBar = {
            if (isCompact) {
                NavBarGenerator(NavigationType.BAR, navController)
            }
        }
    ) { innerPadding ->
        if (!isCompact) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically)
                ) {
                    NavBarGenerator(NavigationType.RAIL, navController)
                }
            }
        }
        NavHost(
            navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding),

            ) {
            Routes.forEach { route ->
                composable(route.name,
                    enterTransition = { NavigationAnim.enter.value },
                    exitTransition = { NavigationAnim.exit.value },
                    popEnterTransition = { NavigationAnim.popEnter.value },
                    popExitTransition = { NavigationAnim.popExit.value }) { _ ->
                    route.content(navController, LocalView.current)
                }
            }
        }
        navController.setLifecycleOwner(MainActivity.instance!!)


        // app link stuff
        var openMod by remember { mutableStateOf<String?>(null) }
        var lastNavigated = remember { mutableStateOf("null") }

        var openHelp by remember { mutableStateOf<String?>(null) }
        var lastNavigatedHelp = remember { mutableStateOf("null") }

        var openPage by remember { mutableStateOf<String?>(null) }
        var lastNavigatedPage = remember { mutableStateOf("null") }

        val appLinkIntent: Intent = MainActivity.instance!!.intent
        val appLinkData: Uri? = appLinkIntent.data
        if (appLinkData != null) {
            val segments = appLinkData.pathSegments
            if (segments.size > 1) {
                when (segments[0]) {
                    "mod" -> openMod = segments[1]
                    "help" -> openHelp = segments[1]
                    "page" -> openPage = segments[1]
                }
            }
        }
        val extraData: Bundle? = appLinkIntent.getExtras()
        extraData?.let{
            val segments = it.getString("open")?.split("/")
            segments?.let {
                if (segments.size > 1) {
                    when (segments[0]) {
                        "mod" -> openMod = segments[1]
                        "help" -> openHelp = segments[1]
                        "page" -> openPage = segments[1]
                    }
                }
            }
        }

        if (!ModDetailsStore.isOffline().value && !ModDetailsStore.isUpdating().value && ModDetailsStore.getCoreDetails().value["app"] != null) {
            if (openMod != null && lastNavigated.value != openMod) {
                openMod?.let {
                    val modDetails = when (it) {
                        "Vulcan-Updates" -> ModDetailsStore.getCoreDetails().value["app"]
                        "Keybox" -> ModDetailsStore.getCoreDetails().value["pif"]
                        else -> ModDetailsStore.getModDetails(it)
                    }
                    modDetails?.let {
                        RouteParams.push(it)
                        lastNavigated.value = openMod!!
                        OpenRoute(
                            "Mod Info",
                            navController = navController,
                            view = MainActivity.instance!!.window.decorView,
                            enter = fadeIn(),
                            exit = fadeOut(),
                        )
                    }
                }
            }

            if ( openHelp!=null && lastNavigatedHelp.value != openHelp) {
                openHelp?.let {
                    RouteParams.push(helpItem(it.replace("%20", " ") + ".md"))
                    lastNavigatedHelp.value = openHelp!!
                    OpenRoute(
                        "ShowHelp",
                        navController = navController,
                        view = MainActivity.instance!!.window.decorView,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    )
                }
            }
            // adb shell am start -a android.intent.action.VIEW -d "https://vulcanupdates.web.app/page/xxx"
            // xxx = Route Name " " = %20
            if ( openPage!=null && lastNavigatedPage.value != openPage) {
                lastNavigatedPage.value = openPage!!
                OpenRoute(
                    openPage!!.replace("%20", " "),
                    navController = navController,
                    view = MainActivity.instance!!.window.decorView,
                    enter = fadeIn(),
                    exit = fadeOut(),
                )
            }
        }

        if (ModDetailsStore.getOOBEPreferences().value.version.value == "" )
            navController.navigate("OOBE")
    }

    return navController
}

@Suppress("UNCHECKED_CAST")
object RouteParams {
    private val stacks = mutableMapOf<Class<*>, MutableList<Any>>()

    private fun <T> getStack(clazz: Class<T>): MutableList<Any> {
        return stacks.getOrPut(clazz) { mutableListOf() }
    }

    fun <T> push(item: T) {
        val stack = getStack(item!!::class.java)
        stack.add(item)
    }

    fun <T> pop(clazz: Class<T>): T? {
        val stack = getStack(clazz)
        return if (stack.isNotEmpty()) stack.removeAt(stack.size - 1) as? T else null
    }

    fun <T> peek(clazz: Class<T>): T? {
        val stack = getStack(clazz)
        return if (stack.isNotEmpty()) stack[stack.size - 1] as? T else null
    }
}
