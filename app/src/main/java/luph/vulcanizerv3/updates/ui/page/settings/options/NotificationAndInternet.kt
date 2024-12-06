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
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
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
import me.zhanghai.compose.preference.SwitchPreference


object NotificationAndInternetPreferences {
    var useMobileDataDownload by mutableStateOf(false)
    var useMobileDataForUpdates by mutableStateOf(false)
    var notifyCoreUpdates by mutableStateOf(false)
    var notifyAppUpdates by mutableStateOf(false)
}

fun subsvribe(topic: String) {
    Firebase.messaging.subscribeToTopic(topic)
        .addOnCompleteListener { task ->
            var msg = "Subscribed"
            if (!task.isSuccessful) {
                msg = "Subscribe failed"
            }
            Log.d("Internet and wa", msg)
        }
}

fun unSubsvribe(topic: String) {
    Firebase.messaging.unsubscribeFromTopic(topic)
        .addOnCompleteListener { task ->
            var msg = "Unsubscribed"
            if (!task.isSuccessful) {
                msg = "Unsubscribe failed"
            }
            Log.d("Internet and wa", msg)
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
                PageNAv(stringResource(R.string.color_and_style), navController)
            }
        }

        item {
            ProvidePreferenceLocals {

                SwitchPreference(
                    value = NotificationAndInternetPreferences.useMobileDataDownload,
                    onValueChange = { NotificationAndInternetPreferences.useMobileDataDownload = it },
                    title = { Text("Mobile Data") },
                    summary = { Text("Enable or disable downloads using mobile data") },
                    icon = { Icon(Icons.Outlined.Wifi, contentDescription = null) }
                )
                SwitchPreference(
                    value = NotificationAndInternetPreferences.useMobileDataForUpdates,
                    onValueChange = { NotificationAndInternetPreferences.useMobileDataForUpdates = it },
                    title = { Text("Use Mobile Data for Updates") },
                    summary = { Text("Enable or disable using mobile data to check for updates when the app is open") },
                    icon = { Icon(Icons.Outlined.Wifi, contentDescription = null) }
                )
                Spacer(modifier = Modifier.height(32.dp))

                SwitchPreference(
                    value = NotificationAndInternetPreferences.notifyCoreUpdates,
                    onValueChange = {
                        NotificationAndInternetPreferences.notifyCoreUpdates = it
                        if (it) subsvribe("Core") else unSubsvribe("Core")
                    },
                    title = { Text("Notify Core Updates") },
                    summary = { Text("Enable or disable notifications for core updates") },
                    icon = { Icon(Icons.Outlined.Wifi, contentDescription = null) }
                )

                SwitchPreference(
                    value = NotificationAndInternetPreferences.notifyAppUpdates,
                    onValueChange = {
                        NotificationAndInternetPreferences.notifyAppUpdates = it
                        ModDetailsStore.getInstalledMods().value.forEach {packageName ->
                            if (it) subsvribe(packageName)
                            else unSubsvribe(packageName)
                        }
                    },
                    title = { Text("Notify App Updates") },
                    summary = { Text("Enable or disable notifications for app updates") },
                    icon = { Icon(Icons.Outlined.Wifi, contentDescription = null) }
                )
            }
        }
    }
}