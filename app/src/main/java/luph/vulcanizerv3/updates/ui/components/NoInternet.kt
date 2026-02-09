package luph.vulcanizerv3.updates.ui.components

import android.view.View
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.data.ModDetailsStore.isUsingMobileData
import luph.vulcanizerv3.updates.data.ModDetailsStore.notificationAndInternetPreferences
import luph.vulcanizerv3.updates.ui.page.OpenRoute

@Composable
fun NoInternet(nav: NavController, view: View) {
    val shouldShowSettingsButton = remember {
        mutableStateOf(
            if (isUsingMobileData()) {
                when (notificationAndInternetPreferences.value.data.value) {
                    0f -> true
                    else -> false
                }
            } else {
                when (notificationAndInternetPreferences.value.wifi.value) {
                    0f -> true
                    else -> false
                }
            }
        )
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SignalWifiOff,
                    contentDescription = stringResource(R.string.no_network),
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(50.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "You're offline",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = if (shouldShowSettingsButton.value) "Your network settings are..." else "Check your connection and try again",
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 4.dp)
                    .animateContentSize(),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                OutlinedButton(
                    onClick = {
                        ModDetailsStore.refresh()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end=8.dp)
                ) {
                    Text("Try again")
                }
                Button(
                    onClick = {
                        OpenRoute("Notifications & Internet", nav, view, fadeIn(), fadeOut())
                    },
                    modifier = Modifier
                        .weight(if (!shouldShowSettingsButton.value) 0.00001f else 1f)
                        .animateContentSize()
                        .padding(start = 16.dp, end=8.dp)
                ) {
                    Text("Network Settings")
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun NoInternetPreview() {
    NoInternet(rememberNavController(), View(null))
}