package luph.vulcanizerv3.updates.ui.page.updates

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.State
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.analytics.logEvent
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.DETAILFILE
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.ui.components.DisplayText
import luph.vulcanizerv3.updates.ui.components.MarkDownContentTest
import luph.vulcanizerv3.updates.ui.components.MarkdownGenerator
import luph.vulcanizerv3.updates.ui.components.RYScaffold
import luph.vulcanizerv3.updates.ui.components.Subheading
import luph.vulcanizerv3.updates.ui.components.SubheadingButton
import luph.vulcanizerv3.updates.ui.components.TransitionBox
import luph.vulcanizerv3.updates.ui.ext.roundClick
import luph.vulcanizerv3.updates.ui.page.OpenRoute
import luph.vulcanizerv3.updates.ui.page.RouteParams
import luph.vulcanizerv3.updates.ui.page.home.modList
import luph.vulcanizerv3.updates.ui.page.showNavigation
import luph.vulcanizerv3.updates.utils.apkmanager.getAPKVersion
import luph.vulcanizerv3.updates.utils.apkmanager.getAppVersion
import luph.vulcanizerv3.updates.utils.download.fetchModDetails
import luph.vulcanizerv3.updates.utils.root.runShellCommand
import java.time.Instant
import kotlin.time.Duration

@Composable
fun VersionCard(
    modDetails: ModDetails?,
    currentVersion: String = "null",
    navController: NavController,
    view: View
) {
    val timeAgoText = remember { mutableStateOf("") }
    showNavigation.show = true
    BackHandler {}
    Box() {
        if (modDetails != null) {
            LaunchedEffect(modDetails.timestamp) {
                while (true) {
                    timeAgoText.value = "~${timeAgo(modDetails.timestamp)}"
                    delay(60000)
                }
            }
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .padding(bottom = 16.dp, top = 12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                        shape = ShapeDefaults.Medium
                    )
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = modDetails.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = timeAgoText.value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                TransitionBox(
                    "Mod Info",
                    navController = navController,
                    view = view,
                    onClick = { RouteParams.push(modDetails) }) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = if (currentVersion.trim() == modDetails.version.trim()) "Info" else "Update",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = modDetails == null,
            exit = scaleOut(tween()),
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                        shape = ShapeDefaults.Medium
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .weight(2.5f)
                        .shimmer()
                ) {
                    Box(
                        Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .height(16.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceColorAtElevation(30.dp),
                                shape = MaterialTheme.shapes.small
                            )
                    )
                    Box(
                        Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .height(12.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceColorAtElevation(15.dp),
                                shape = MaterialTheme.shapes.small
                            )
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .shimmer()
                ) {
                    Box(
                        Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .height(32.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceColorAtElevation(15.dp),
                                shape = MaterialTheme.shapes.large
                            )
                    )
                }
            }
        }
    }
}

fun timeAgo(unixTimestamp: Long): String {
    val now = Instant.now()
    val past = Instant.ofEpochSecond(unixTimestamp)
    val duration = java.time.Duration.between(past, now)

    return when {
        duration.toMinutes() < 1 -> MainActivity.applicationContext().getString(R.string.just_now)
        duration.toMinutes() < 60 -> MainActivity.applicationContext().getString(R.string.minutes_ago, duration.toMinutes())
        duration.toHours() < 24 -> MainActivity.applicationContext().getString(R.string.hours_ago, duration.toHours())
        duration.toDays() < 30 -> MainActivity.applicationContext().getString(R.string.days_ago, duration.toDays())
        duration.toDays() < 365 -> MainActivity.applicationContext().getString(R.string.months_ago, duration.toDays() / 30)
        else -> MainActivity.applicationContext().getString(R.string.years_ago, duration.toDays() / 365)
    }
}

@Composable
fun ModUpdateCard(
    modDetails: ModDetails,
    navController: NavController,
    view: View,
    modifier: Modifier = Modifier,
    end: Int = 0,
    showInstalledVersion: Boolean = false
) {

    TransitionBox(destination = "Mod Info", navController = navController, onClick = {
        RouteParams.push(modDetails)

    }, view = view) {
        Column(
            modifier = modifier
                .padding(start = 16.dp, end = end.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    shape = ShapeDefaults.Medium
                )
                .padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberImagePainter(data =  modDetails.url + DETAILFILE.ICON.type),
                    contentDescription = "${modDetails.name} Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 16.dp)
                        .clip(RoundedCornerShape(18.dp))
                )
                Column(modifier = Modifier.padding(start = 0.dp)) {
                    Text(
                        text = modDetails.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if(showInstalledVersion) getAPKVersion(modDetails.packageName)?: modDetails.version else modDetails.version,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            Text(
                text = modDetails.changeLogSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(275.dp)
            )
        }
    }
}
@Composable
fun updateCarousel(modPackageNames: List<String>, navController: NavController, view: View, showInstalledVersion: Boolean): List<ModDetails>{
    showNavigation.show = true
    val modDetailsList = remember { mutableStateOf<List<ModDetails>>(listOf()) }
    Box {
        LazyRow {
            items(modPackageNames.size) { index ->
                val modDetails = ModDetailsStore.getModDetails(modPackageNames[index])
                if (modDetails != null) {
                    ModUpdateCard(
                        modDetails = modDetails,
                        navController = navController,
                        view = view,
                        end = if (index == modPackageNames.size - 1) 16 else 0,
                        showInstalledVersion = showInstalledVersion
                    )
                }
            }
        }
    }
    LaunchedEffect(modPackageNames) {
        modDetailsList.value = modPackageNames.mapNotNull { modName ->
            ModDetailsStore.getModDetails(modName)
        }
    }
    return modDetailsList.value
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatesPage(navController: NavController, view: View) {
    showNavigation.show = true
    RYScaffold(
        content = {
            PullToRefreshBox(ModDetailsStore.isUpdating().value, {ModDetailsStore.refresh()}) {
                LazyColumn {
                    item {
                        DisplayText(text = stringResource(R.string.updates_title), desc = "")
                    }
                    item {
                        Subheading(stringResource(R.string.core_title))
                        val modetails2 = ModDetailsStore.getCoreDetails().value["app"]?.copy()
                        modetails2?.name  = "Vulcan ROM"


                        VersionCard(
                            modetails2,
                            "V3",
                            navController,
                            view
                        )

                        VersionCard(
                            ModDetailsStore.getCoreDetails().value["app"],
                            getAppVersion(view.context),
                            navController,
                            view
                        )

                        VersionCard(
                            ModDetailsStore.getCoreDetails().value["pif"],
                            runShellCommand("getprop ${ModDetailsStore.getCoreDetails().value["pif"]?.packageName}").value.first,
                            navController,
                            view
                        )
                    }

                    if (ModDetailsStore.getInstalledModsUpdate().value.isNotEmpty()) {

                        item {
                            val updatedModsList =
                                remember { mutableStateOf<List<ModDetails>>(listOf()) }
                            Box(Modifier.roundClick {
                                RouteParams.push(
                                    MainActivity.applicationContext()
                                        .getString(R.string.updates_title)
                                )
                                RouteParams.push(modList(updatedModsList.value))
                                MainActivity.getFirebaseAnalytics()
                                    .logEvent("opened_mod_category") {
                                        param(
                                            "category",
                                            MainActivity.applicationContext()
                                                .getString(R.string.updates_title)
                                        )
                                    }
                                OpenRoute(
                                    "Home Details Expanded",
                                    navController,
                                    view,
                                    fadeIn(animationSpec = tween(700)),
                                    ExitTransition.None,
                                    EnterTransition.None,
                                    fadeOut(animationSpec = tween(500))
                                )
                            }) {
                                Row(
                                    modifier = Modifier
                                        .padding(20.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = stringResource(R.string.updates_title),
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "Next"
                                    )

                                }
                            }
                            val updateMods by remember { ModDetailsStore.getInstalledModsUpdate() }
                            updatedModsList.value =
                                updateCarousel(updateMods.toList(), navController, view, false)
                        }
                    }
                    if (ModDetailsStore.getInstalledMods().value.isNotEmpty()) {
                        item {
                            val installedDetailsList =
                                remember { mutableStateOf<List<ModDetails>>(listOf()) }
                            Box(Modifier.roundClick {
                                RouteParams.push(
                                    MainActivity.applicationContext()
                                        .getString(R.string.installed_title)
                                )
                                RouteParams.push(modList(installedDetailsList.value))
                                MainActivity.getFirebaseAnalytics()
                                    .logEvent("opened_mod_category") {
                                        param(
                                            "category",
                                            MainActivity.applicationContext()
                                                .getString(R.string.installed_title)
                                        )
                                    }
                                OpenRoute(
                                    "Home Details Expanded",
                                    navController,
                                    view,
                                    fadeIn(animationSpec = tween(700)),
                                    ExitTransition.None,
                                    EnterTransition.None,
                                    fadeOut(animationSpec = tween(500))
                                )
                            }) {
                                Row(
                                    modifier = Modifier
                                        .padding(20.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = stringResource(R.string.installed_title),
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "Next"
                                    )

                                }
                            }
                            val installedMods by remember { ModDetailsStore.getInstalledMods() }
                            installedDetailsList.value =
                                updateCarousel(installedMods.toList(), navController, view, true)
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                    }
                }
            }
        }
    )
}
