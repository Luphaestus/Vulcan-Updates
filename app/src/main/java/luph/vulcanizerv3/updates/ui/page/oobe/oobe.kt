package luph.vulcanizerv3.updates.ui.page.oobe

import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import kotlinx.serialization.Serializable
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.ui.page.settings.options.subscribe
import luph.vulcanizerv3.updates.ui.page.settings.options.unsubscribe
import luph.vulcanizerv3.updates.ui.page.settings.options.unsubscribeAll
import luph.vulcanizerv3.updates.ui.page.showNavigation
import luph.vulcanizerv3.updates.ui.page.updates.VersionCard
import luph.vulcanizerv3.updates.utils.apkmanager.getAppVersion
import luph.vulcanizerv3.updates.utils.getStandardAnimationSpeed
import luph.vulcanizerv3.updates.utils.root.runShellCommand
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.SliderPreference
import me.zhanghai.compose.preference.SwitchPreference

@Serializable
data class OOBEDataSerializable (
    var version: String = "",
    val fileName: String = "oobedata"
)

data class OOBEDataPreference(
    var version: MutableState<String> = mutableStateOf(""),
)


data object OOBEDataRemember {
    var page = mutableIntStateOf(-1)
}

@Composable
@Preview(showBackground = true)
fun PageTitle(title: String="Title") {
    Row(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                shape = ShapeDefaults.Large
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Composable
fun PagerScreen(pages: List<@Composable ((Int, MutableIntState) -> Unit)>, currentIndex: MutableIntState) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val containerWidth = with(LocalDensity.current) { maxWidth.toPx() + 16.dp.toPx() }

        pages.forEachIndexed { index, page ->
            val translationX by animateFloatAsState(
                targetValue = (currentIndex.intValue - index) * containerWidth,
                animationSpec = tween(durationMillis = getStandardAnimationSpeed()),
                label = "PageAnimation"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        this.translationX = -translationX
                    }
            ) {
                Column {
                    page(index, currentIndex)
                }
            }
        }
    }
}


@Composable
fun WelcomePage(index: Int, currentIndex: MutableIntState, navController: NavController, view: View, nextButtonEnabled: MutableState<Boolean>) {
    PageTitle("Update Vulcan ROM components")

    VersionCard(
        remember { mutableStateOf(ModDetailsStore.getCoreDetails().value["rom"])},
        runShellCommand("getprop ${ModDetailsStore.getCoreDetails().value["rom"]?.packageName}").value.first,
        navController,
        view
    )

    VersionCard(
        remember { mutableStateOf(ModDetailsStore.getCoreDetails().value["app"])},
        getAppVersion(view.context),
        navController,
        view
    )

    VersionCard(
        remember { mutableStateOf(ModDetailsStore.getCoreDetails().value["pif"])},
        runShellCommand("getprop ${ModDetailsStore.getCoreDetails().value["pif"]?.packageName}").value.first,
        navController,
        view
    )

    nextButtonEnabled.value = ModDetailsStore.numbCoreUpdatesNeeded().value == 0
    
}

@Composable
fun NotificationPage(index: Int, currentIndex: MutableIntState, navController: NavController, view: View, nextButtonEnabled: MutableState<Boolean>) {
    PageTitle("Notification and Internet")
    LazyColumn {
        item {
            ProvidePreferenceLocals {
                SliderPreference(
                    value = ModDetailsStore.getNotificationAndInternetPreferences().value.wifi.value,
                    onValueChange = {
                        ModDetailsStore.getNotificationAndInternetPreferences().value.wifi.value = it
                        ModDetailsStore.saveNotificationAndInternetPreferences()},
                    sliderValue = ModDetailsStore.getNotificationAndInternetPreferences().value.wifi.value,
                    onSliderValueChange = {
                        ModDetailsStore.getNotificationAndInternetPreferences().value.wifi.value = it
                        ModDetailsStore.saveNotificationAndInternetPreferences()},
                    title = { Text(stringResource(R.string.over_wi_fi))},
                    summary = {
                        Text(when (ModDetailsStore.getNotificationAndInternetPreferences().value.wifi.value) {
                            0f -> stringResource(R.string.never_download_anything_using_wi_fi)
                            1f -> stringResource(R.string.only_use_wi_fi_when_downloading_mod_information)
                            2f -> stringResource(R.string.always_use_wi_fi)
                            else -> "Unknown"
                        })},
                    valueSteps = 1,
                    valueRange = 0f..2f,
                )



                SliderPreference(
                    value = ModDetailsStore.getNotificationAndInternetPreferences().value.data.value,
                    onValueChange = {
                        ModDetailsStore.getNotificationAndInternetPreferences().value.data.value = it
                        ModDetailsStore.saveNotificationAndInternetPreferences()},
                    sliderValue = ModDetailsStore.getNotificationAndInternetPreferences().value.data.value,
                    onSliderValueChange = {
                        ModDetailsStore.getNotificationAndInternetPreferences().value.data.value = it
                        ModDetailsStore.saveNotificationAndInternetPreferences()},
                    title = { Text(stringResource(R.string.over_wi_fi).replace("Wi-Fi", "DATA"))},
                    summary = {
                        Text(when (ModDetailsStore.getNotificationAndInternetPreferences().value.wifi.value) {
                            0f -> stringResource(R.string.never_download_anything_using_wi_fi).replace("Wi-Fi", "DATA")
                            1f -> stringResource(R.string.only_use_wi_fi_when_downloading_mod_information).replace("Wi-Fi", "DATA")
                            2f -> stringResource(R.string.always_use_wi_fi).replace("Wi-Fi", "DATA")
                            else -> "Unknown"
                        })},
                    valueSteps = 1,
                    valueRange = 0f..2f,
                )

                SwitchPreference(
                    value = ModDetailsStore.getNotificationAndInternetPreferences().value.notifyCoreUpdates.value,
                    onValueChange = {
                        ModDetailsStore.getNotificationAndInternetPreferences().value.notifyCoreUpdates.value = it
                        if (it) subscribe("Core") else unsubscribe("Core")
                        ModDetailsStore.saveNotificationAndInternetPreferences()
                    },
                    title = { Text("Core Update Notifications") },
                    summary = { Text("Receive notifications for ROM, PIF, and Vulkan updates.") },
                    icon = { Icon(Icons.Outlined.Update, contentDescription = "Core Updates Icon") }
                )

                SwitchPreference(
                    value = ModDetailsStore.getNotificationAndInternetPreferences().value.notifyAppUpdates.value,
                    onValueChange = {
                        ModDetailsStore.getNotificationAndInternetPreferences().value.notifyAppUpdates.value = it
                        if (it) {
                            ModDetailsStore.getInstalledMods().value.forEach { packageName ->
                                subscribe(packageName)
                            }
                        } else {
                            unsubscribeAll()
                        }
                        ModDetailsStore.saveNotificationAndInternetPreferences()
                    },
                    title = { Text(stringResource(R.string.mod_update_notifications)) },
                    summary = { Text(stringResource(R.string.get_notified_about_updates_for_installed_mods)) },
                    icon = { Icon(Icons.Outlined.Apps, contentDescription = "Mod Updates Icon") }
                )

            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun OOBE(
    navController: NavController = rememberNavController(),
    view: View = View(navController.context)) {

    var oobedata = ModDetailsStore.getOOBEPreferences()

    showNavigation.show = false
    val nextButtonEnabled = remember { mutableStateOf(true) }


    val pages: List<@Composable (index: Int, currentIndex: MutableIntState) -> Unit> = listOf(
        { index, currentIndex -> WelcomePage(index, currentIndex, navController, view, nextButtonEnabled) },
        { index, currentIndex -> NotificationPage(index, currentIndex, navController, view, nextButtonEnabled) },

        )


    val backStack = remember { mutableStateOf(mutableListOf<Int>()) }
    val currentIndex = OOBEDataRemember.page

    fun updateIndex(index: Int) {
        backStack.value.add(currentIndex.intValue)
        currentIndex.intValue = index
    }

    BackHandler {
        if (backStack.value.isNotEmpty())
        {
            currentIndex.intValue = backStack.value.last()
            backStack.value.removeAt(backStack.value.lastIndex)
        }
    }


    Scaffold(
        bottomBar = {
            Row {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    onClick = { updateIndex(currentIndex.intValue-1) },
                    enabled = currentIndex.intValue > -1
                ) {
                    Text("Previous")
                }
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    onClick = {
                        if (currentIndex.intValue < pages.size)
                            updateIndex(currentIndex.intValue+1)
                      else {
                            oobedata.value.version.value = getAppVersion(view.context)
                            ModDetailsStore.saveOOBEPreferences()
                            navController.navigate("Home")
                      }},
                    enabled = nextButtonEnabled.value
                )
                {
                    Text(if (currentIndex.intValue < pages.size) "Next" else "Finish")
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = if (currentIndex.intValue < pages.size) "Welcome to Vulcan Updates" else "You are all set! Enjoy!",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                ),
            )

            AnimatedVisibility(
                visible = currentIndex.intValue != -1 && currentIndex.intValue < pages.size,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    Modifier.fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                            shape = MaterialTheme.shapes.medium
                        )
                        .animateContentSize()
                        .padding(if (currentIndex.intValue == -1) 0.dp else 16.dp)
                ) {
                    PagerScreen(
                        pages = pages,
                        currentIndex = currentIndex
                    )

                }
            }
        }
    }
}

