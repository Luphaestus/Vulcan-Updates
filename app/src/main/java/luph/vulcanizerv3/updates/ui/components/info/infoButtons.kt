package luph.vulcanizerv3.updates.ui.components.info

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import luph.vulcanizerv3.updates.data.ModType
import luph.vulcanizerv3.updates.data.UpdateStatus
import luph.vulcanizerv3.updates.data.buttonData
import luph.vulcanizerv3.updates.utils.apkmanager.openAPK
import luph.vulcanizerv3.updates.utils.apkmanager.uninstallAPK
import luph.vulcanizerv3.updates.utils.modulemanager.uninstallModule





fun changeUpdateType(updateStatus: UpdateStatus, buttonData: buttonData) {
    buttonData.infoState.value = updateStatus
    buttonData.canCancel = true
    if (buttonData.isInstallTwoButtons(updateStatus)) {
        Log.e("buttonVisible", "status: $updateStatus")
        buttonData.firstButtonVisible = true
        buttonData.buttonAnimWeightValue = 1f
    } else {
        Log.e("buttonNotVisible", "status: $updateStatus")
        buttonData.doToggleButtonVisibility = true
        buttonData.buttonAnimWeightValue = 0.00000001f
    }
}

@Composable
fun InfoButtons(buttonData: buttonData) {
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
                        if (success)
                            buttonData.changeUpdateType(UpdateStatus.NOT_INSTALLED, buttonData)
                    }
                },
                modifier = Modifier
                    .weight(buttonAnimWeight)
                    .padding(end = if (buttonData.firstButtonVisible) 8.dp else 0.dp)
                    .height(42.dp),
                enabled = canCancel.value && (buttonData.infoState.value != UpdateStatus.UPDATING && buttonData.modDetails.packageName != "luph.vulcanizerv3.updates")
            ) {
                Text(text = buttonData.getFirstButtonStrings(buttonData.infoState.value))
            }
        }
        Button(
            onClick = {
                if (buttonData.infoState.value == UpdateStatus.NOT_INSTALLED || buttonData.infoState.value == UpdateStatus.UPDATE_AVAILABLE) {
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