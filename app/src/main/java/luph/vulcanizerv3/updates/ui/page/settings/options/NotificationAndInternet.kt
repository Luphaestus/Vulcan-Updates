package luph.vulcanizerv3.updates.ui.page.settings.options

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import luph.vulcanizerv3.updates.MessageService
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.SignalCellularNodata
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.serialization.Serializable
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.data.ThemeManager
import luph.vulcanizerv3.updates.data.Themes
import luph.vulcanizerv3.updates.ui.components.MultipleExpandingCircleAnimations
import luph.vulcanizerv3.updates.ui.components.PageNAv
import luph.vulcanizerv3.updates.ui.components.SettingsElementBase
import luph.vulcanizerv3.updates.ui.page.showNavigation
import luph.vulcanizerv3.updates.ui.theme.getColourScheme
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.SliderPreference
import me.zhanghai.compose.preference.SwitchPreference

data class NotificationAndInternetPreferences (
    var wifi: MutableState<Float> = mutableStateOf(2f),
    var data: MutableState<Float> = mutableStateOf(1f),
    var notifyCoreUpdates: MutableState<Boolean> = mutableStateOf(true),
    var notifyAppUpdates: MutableState<Boolean> = mutableStateOf(true),
)

@Serializable
data class NotificationAndInternetPreferencesSerilizeable (
    var wifi: Float = 2f,
    var data: Float = 1f,
    var notifyCoreUpdates: Boolean = true,
    var notifyAppUpdates: Boolean = true
)
{
    val fileName = "NotificationAndInternetPreferences"
}

fun subscribe(topic: String) {
    Firebase.messaging.subscribeToTopic(topic)
        .addOnCompleteListener { task ->
            var msg = "Subscribed"
            if (!task.isSuccessful) {
                msg = "Subscribe failed"
            }
            Log.d("Internet and wa", msg)
        }
}

fun unsubscribe(topic: String) {
    Firebase.messaging.unsubscribeFromTopic(topic)
        .addOnCompleteListener { task ->
            var msg = "Unsubscribed"
            if (!task.isSuccessful) {
                msg = "Unsubscribe failed"
            }
            Log.d("Internet and wa", msg)
        }
}

fun unsubscribeAll() {
    ModDetailsStore.getAllPackages().forEach {
        unsubscribe(it)
    }
}


@Composable
@Preview(showBackground = true)
fun NotificationAndInternet(
    navController: NavController = rememberNavController(),
    view: View? = null
) {
    showNavigation.show = false

    LazyColumn {
        item {
            Column(Modifier.padding(start = 16.dp, end=16.dp, bottom=16.dp)) {
                PageNAv("Updates", navController)
            }
        }

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
                    title = { Text("Over Wi-FI")},
                    summary = {
                        Text(when (ModDetailsStore.getNotificationAndInternetPreferences().value.wifi.value) {
                            0f -> "Never download anything using Wi-Fi"
                            1f -> "Only use Wi-FI when downloading mod information"
                            2f -> "Always use Wi-Fi"
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
                    title = { Text("Over Mobile Data")},
                    summary = {
                        Text(when (ModDetailsStore.getNotificationAndInternetPreferences().value.data.value) {
                            0f -> "Never download anything using Mobile Data"
                            1f -> "Only use Wi-FI when downloading mod information"
                            2f -> "Always use Mobile Data"
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
                    title = { Text("Mod Update Notifications") },
                    summary = { Text("Get notified about updates for installed mods.") },
                    icon = { Icon(Icons.Outlined.Apps, contentDescription = "Mod Updates Icon") }
                )

            }
        }
    }
}