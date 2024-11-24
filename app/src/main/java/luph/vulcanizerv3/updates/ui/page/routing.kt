package luph.vulcanizerv3.updates.ui.page

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.data.NavigationAnim
import luph.vulcanizerv3.updates.data.NavigationAnimClass
import luph.vulcanizerv3.updates.data.Route
import luph.vulcanizerv3.updates.data.Routes
import luph.vulcanizerv3.updates.ui.components.BadgeFormatter

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
    fun NavigationItemContent(index: Int, route: Route) {
        badgeFormatter.badge(
            enabled = route.showBadge(),
            icon = if (selectedItem == index) route.selectedIcon else route.unselectedIcon,
            contentDescription = route.name,
            badgeContent = route.badgeContent(),
        )()
    }

    AnimatedVisibility(
        visible = showNavigation.show,
        enter = slideInVertically(animationSpec = tween(700)) { it },
        exit = slideOutVertically(animationSpec = tween(700)) { it }
    ) {
        when (navigationType) {
            NavigationType.BAR -> {
                NavigationBar {
                    Routes.forEachIndexed { index, route ->
                        if (route.showInMenu) {
                            NavigationBarItem(
                                icon = { NavigationItemContent(index, route) },
                                label = { Text(route.name) },
                                selected = selectedItem == index,
                                onClick = {
                                    var navigated: Boolean = false
                                    if (selectedItem < index)
                                        navigated = OpenRoute(
                                            route.name,
                                            navController,
                                            view,
                                            slideInHorizontally(animationSpec = tween(700)) { it },
                                            slideOutHorizontally(animationSpec = tween(700)) { -it })
                                    else if (selectedItem > index)
                                        navigated = OpenRoute(
                                            route.name,
                                            navController,
                                            view,
                                            slideInHorizontally(animationSpec = tween(700)) { -it },
                                            slideOutHorizontally(animationSpec = tween(700)) { it })
                                    else
                                        navigated = OpenRoute(
                                            route.name,
                                            navController,
                                            view,
                                            fadeIn(animationSpec = tween(700)),
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
                                            slideInHorizontally(animationSpec = tween(700)) { it },
                                            slideOutHorizontally(animationSpec = tween(700)) { -it })
                                    else if (selectedItem > index)
                                        OpenRoute(
                                            route.name,
                                            navController,
                                            view,
                                            slideInHorizontally(animationSpec = tween(700)) { -it },
                                            slideOutHorizontally(animationSpec = tween(700)) { it })
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

    isAnimating = true
    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    navController.navigate(name)
    NavigationAnim.enter.value = enter
    NavigationAnim.exit.value = exit
    if (popEnter != null) NavigationAnim.popEnter.value =
        popEnter else NavigationAnim.popEnter.value = enter
    if (popExit != null) NavigationAnim.popExit.value = popExit else NavigationAnim.popExit.value =
        exit

    val animationDuration = 700L
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
fun NavBarHandler(windowSize: WindowSizeClass) {
    val isCompact = windowSize.widthSizeClass == Compact
    val navController = rememberNavController()

//    BackHandler{
//        Log.e("BackHandler", "BackHandler")
//        if (navController.previousBackStackEntry != null) {
//            val animation = RouteParams.pop(NavigationAnimClass::class.java)
//            navController.popBackStack()
//            Log.e("BackHandler", "BackHandler ${animation?.enter}")
//            if (animation != null) {
//                NavigationAnim.enter.value = animation.enter
//                NavigationAnim.exit.value = animation.exit
//                NavigationAnim.popEnter.value = animation.popEnter
//                NavigationAnim.popExit.value = animation.popExit
//            }
//        }
//    }

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
            startDestination = "Home",
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

    }
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
