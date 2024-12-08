package luph.vulcanizerv3.updates.ui.components.info

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.data.infoAlert
import luph.vulcanizerv3.updates.ui.page.OpenRoute
import luph.vulcanizerv3.updates.ui.page.RouteParams


@Composable
fun UpdateAlert(title: String, description: String, show: MutableState<Boolean>, negativeClickText: String = "Continue Anyway" , negativeClick: () -> Unit = {}, positiveClickText: String = "Fix",  positiveClick: () -> Unit = {}) {
    if (!show.value) return
    AlertDialog(
        onDismissRequest = { show.value = false },
        title = { Text(title) },
        text = { Text(description) },
        confirmButton = {
            Button(onClick = {
                show.value = false
                positiveClick()}) {
                Text(positiveClickText)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                show.value = false
                negativeClick()}) {
                Text(negativeClickText)
            }
        }
    )
}

@Composable
fun InfoPopup(infoAlert: infoAlert, modDetails: ModDetails, navController: NavController, view: android.view.View) {
    UpdateAlert(
        "No Network",
        "You may not have an active internet connection, or your network settings may be configured to restrict access. Please check your connection and try again.",
        infoAlert.noNetworkDialog,
        "Cancel",
        positiveClickText = "Network Settings",
        positiveClick = {
            RouteParams.push(modDetails)
            OpenRoute("Notifications & Internet", navController, view, enter = fadeIn(), exit = fadeOut())
        }
    )



    UpdateAlert(
        "Detach module recommended",
        "To prevent Google Play Store from overwriting ${modDetails.name}, it is recommended to install a detach module.",
        infoAlert.noDetachDialog,
        negativeClick = {
            infoAlert.noDetachNegativeLambda.value()
        },
        positiveClick = {
            infoAlert.noDetachPositiveLambda.value()
        },
        positiveClickText = "Install Detach"
    )

    UpdateAlert(
        "LSPosed Required",
        "${modDetails.name} requires LSPosed, and will not function without it.",
        infoAlert.noLSPosedDialog,
        negativeClick = {
            infoAlert.noLsposedNegativeLambda.value()
        },
        positiveClick = {
            infoAlert.noLsposedPositiveLambda.value()
        },
        positiveClickText = "Install LSPosed"
    )

    UpdateAlert(
        "ReZygisk Required",
        "${modDetails.name} requires Zygisk, and will not function without it.",
        infoAlert.noZygiskDialog,
        negativeClick = {
            infoAlert.noZygiskNegativeLambda.value()
        },
        positiveClick = {
            infoAlert.noZygiskPositiveLambda.value()
        },
        positiveClickText = "Install Zygisk"
    )
}