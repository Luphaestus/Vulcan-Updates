package luph.vulcanizerv3.updates.ui.components.info

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.analytics.logEvent
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.DETAILFILE
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.data.ModDetailsStore.isUsingMobileData
import luph.vulcanizerv3.updates.data.ModDetailsStore.notificationAndInternetPreferences
import luph.vulcanizerv3.updates.data.ModType
import luph.vulcanizerv3.updates.data.UpdateStatus
import luph.vulcanizerv3.updates.data.buttonData
import luph.vulcanizerv3.updates.ui.page.OpenRoute
import luph.vulcanizerv3.updates.ui.page.RouteParams
import luph.vulcanizerv3.updates.ui.page.settings.options.subscribe
import luph.vulcanizerv3.updates.utils.apkmanager.openAPK
import luph.vulcanizerv3.updates.utils.apkmanager.uninstallAPK
import luph.vulcanizerv3.updates.utils.modulemanager.uninstallModule





fun changeUpdateType(updateStatus: UpdateStatus, buttonData: buttonData) {
    val stackTrace = Thread.currentThread().stackTrace
    val caller = stackTrace[3] // The caller is usually at index 3
    Log.e("isInstallTwoButtons", "Called from: ${caller.className}.${caller.methodName} at line ${caller.lineNumber}")

    buttonData.infoState.value = updateStatus
    buttonData.canCancel = true
    if (buttonData.isInstallTwoButtons(updateStatus)) {
        Log.e("isInstallTwoButtons", "status: $updateStatus")
        buttonData.firstButtonVisible = true
        buttonData.buttonAnimWeightValue = 1f
    } else {
        Log.e("isInstallTwoButtons", "status: $updateStatus")
        buttonData.doToggleButtonVisibility = true
        buttonData.buttonAnimWeightValue = 0.00000001f
    }
}

@Composable
fun InfoButtons(buttonData: buttonData, coreUpdates: Array<String>) {
    val buttonAnimWeight by animateFloatAsState(
        targetValue = buttonData.buttonAnimWeightValue,
        animationSpec = tween(durationMillis = 1000),
        finishedListener = {
            if (buttonData.doToggleButtonVisibility) {
                buttonData.firstButtonVisible = false
                buttonData.doToggleButtonVisibility = false
            }
        }
    )

    fun startDownload() {
        MainActivity.getFirebaseAnalytics().logEvent("download_mod") {
            param("mod_name", buttonData.modDetails.name)
            param("mod_version", buttonData.modDetails.version)
            param("mod_author", buttonData.modDetails.author)
            buttonData.modDetails.keywords.forEach {keyword ->
                param("keyword", keyword)
            }
        }
        buttonData.changeUpdateType(UpdateStatus.UPDATING, buttonData)
        buttonData.downloadId.intValue = MainActivity.getKetch().download(
            buttonData.modDetails.url + DETAILFILE.FILE.type,
            MainActivity.applicationContext().cacheDir.absolutePath,
            buttonData.modDetails.name
        )
    }


    val canCancel = remember { mutableStateOf(true) }

    Row(Modifier.padding(vertical = 16.dp)) {
        if (buttonData.firstButtonVisible) {
            OutlinedButton(
                onClick = {
                    if (buttonData.infoState.value == UpdateStatus.UPDATING) {
                        MainActivity.getKetch().cancel(buttonData.downloadId.intValue)
                    } else if (buttonData.infoState.value == UpdateStatus.INSTALLED || buttonData.infoState.value == UpdateStatus.UPDATE_AVAILABLE) {
                        var success = false
                        when (buttonData.modDetails.updateType)
                        {
                            ModType.APK -> {
                                success = uninstallAPK(buttonData.modDetails.packageName)
                            }
                            ModType.MODULE -> {
                                success = uninstallModule(buttonData.modDetails.packageName)
                            }
                            else -> {}
                        }
                        if (success) {
                            buttonData.changeUpdateType(UpdateStatus.NOT_INSTALLED, buttonData)
                            subscribe(buttonData.modDetails.packageName)
                        }
                    }
                },
                modifier = Modifier
                    .weight(buttonAnimWeight)
                    .padding(end = if (buttonData.firstButtonVisible) 8.dp else 0.dp)
                    .height(42.dp),
                enabled = (canCancel.value && (buttonData.infoState.value == UpdateStatus.UPDATING) || (!coreUpdates.contains(buttonData.modDetails.packageName) && buttonData.infoState.value != UpdateStatus.UPDATING))
            ) {
                Text(text = buttonData.getFirstButtonStrings(buttonData.infoState.value))
            }
        }
        Button(
            onClick = {
                if (buttonData.infoState.value == UpdateStatus.NOT_INSTALLED || buttonData.infoState.value == UpdateStatus.UPDATE_AVAILABLE) {
                    if (isUsingMobileData())
                    {
                        when (notificationAndInternetPreferences.value.data.value) {
                            2f -> {}
                            else -> {
                                Log.e("network level", notificationAndInternetPreferences.value.data.value.toString())
                                buttonData.infoAlert.noNetworkDialog.value = true
                                return@Button
                            }
                        }
                    } else {
                        when (notificationAndInternetPreferences.value.wifi.value) {
                            2f -> {}
                            else -> {
                                Log.e("network level", notificationAndInternetPreferences.value.wifi.value.toString())
                                buttonData.infoAlert.noNetworkDialog.value = true
                                return@Button
                            }
                        }
                    }
                    if ( buttonData.modDetails.require == "lsposed" && !ModDetailsStore.getInstalledMods().value.contains("zygisk_lsposed")) {
                        buttonData.infoAlert.noLSPosedDialog.value = true
                        buttonData.infoAlert.noLsposedNegativeLambda.value = {
                            startDownload()
                        }
                        buttonData.infoAlert.noLsposedPositiveLambda.value = {
                            RouteParams.push(buttonData.modDetails)
                            RouteParams.push(buttonData.modDetails)
                            RouteParams.push(ModDetailsStore.getModDetails("zygisk_lsposed"))
                            OpenRoute(
                                "Mod Info",
                                navController = buttonData.navController,
                                view = buttonData.view,
                                enter = fadeIn(),
                                exit = fadeOut(),
                            )
                        }
                        return@Button
                    }
                    if ( buttonData.modDetails.require == "zygisk" && !ModDetailsStore.getInstalledMods().value.contains("zygisksu")) {
                        buttonData.infoAlert.noZygiskDialog.value = true
                        buttonData.infoAlert.noZygiskNegativeLambda.value = {
                            startDownload()
                        }
                        buttonData.infoAlert.noZygiskPositiveLambda.value = {
                            RouteParams.push(buttonData.modDetails)
                            RouteParams.push(buttonData.modDetails)
                            RouteParams.push(ModDetailsStore.getModDetails("zygisksu"))
                            OpenRoute(
                                "Mod Info",
                                navController = buttonData.navController,
                                view = buttonData.view,
                                enter = fadeIn(),
                                exit = fadeOut(),
                            )
                        }
                        return@Button
                    }

                    if ( buttonData.modDetails.require == "detatch" && !ModDetailsStore.getInstalledMods().value.contains("com.tsng.hidemyapplist") && !ModDetailsStore.getInstalledMods().value.contains("ru.mike.updatelocker")) {
                        buttonData.infoAlert.noDetachDialog.value = true
                        buttonData.infoAlert.noDetachNegativeLambda.value = {
                            startDownload()
                        }
                        buttonData.infoAlert.noDetachPositiveLambda.value = {
                            RouteParams.push(buttonData.modDetails)
                            RouteParams.push(buttonData.modDetails)
                            RouteParams.push(ModDetailsStore.getModDetails("ru.mike.updatelocker"))
                            OpenRoute(
                                "Mod Info",
                                navController = buttonData.navController,
                                view = buttonData.view,
                                enter = fadeIn(),
                                exit = fadeOut(),
                            )
                        }
                        return@Button
                    }

                    startDownload()

                }
                if (buttonData.infoState.value == UpdateStatus.INSTALLED) {
                    if (buttonData.modDetails.updateType == ModType.APK) {
                        if (buttonData.modDetails.updateType == ModType.APK) {
                            openAPK(buttonData.modDetails.openName)
                        }
                    }
                }
            },
            modifier = Modifier
                .weight(1f)
                .padding(start = if (buttonData.firstButtonVisible) 8.dp else 0.dp)
                .height(42.dp),
            enabled = buttonData.infoState.value != UpdateStatus.UPDATING && (buttonData.infoState.value != UpdateStatus.INSTALLED || buttonData.modDetails.updateType == ModType.APK)
        ) {
            Text(text = buttonData.getSecondButtonStrings(buttonData.infoState.value))
        }
    }
}