package luph.vulcanizerv3.updates.ui.components.info

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.data.infoAlert


@Composable
fun UpdateAlert(title: String, description: String, show: MutableState<Boolean>, negativeClick: () -> Unit = {}, positiveClick: () -> Unit = {}) {
    if (!show.value) return
    AlertDialog(
        onDismissRequest = { show.value = false },
        title = { Text(title) },
        text = { Text(description) },
        confirmButton = {
            Button(onClick = {
                show.value = false
                positiveClick()}) {
                Text("Fix")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                show.value = false
                negativeClick()}) {
                Text("Continue Anyway")
            }
        }
    )
}

@Composable
fun InfoPopup(infoAlert: infoAlert, modDetails: ModDetails) {
    UpdateAlert(
        "No Root Access",
        "Vulcan Updates works best with Root permissions!",
        infoAlert.noRootAPKDialog
    )

    UpdateAlert(
        "Detach module recommended",
        "To prevent Google Play Store from overwriting ${modDetails.name}, it is recommended to install a detach module.",
        infoAlert.noDetachDialog
    )

    UpdateAlert(
        "LSPosed Required",
        "${modDetails.name} requires LSPosed, and will not function without it.",
        infoAlert.noLSPosedDialog
    )

    UpdateAlert(
        "Zygisk Required",
        "${modDetails.name} requires Zygisk, and will not function without it.",
        infoAlert.noZygiskDialog
    )
}