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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.data.infoAlert
import luph.vulcanizerv3.updates.ui.page.OpenRoute
import luph.vulcanizerv3.updates.ui.page.RouteParams


@Composable
fun UpdateAlert(title: String, description: String, show: MutableState<Boolean>, negativeClickText: String = stringResource(
    R.string.continue_anyway
), negativeClick: () -> Unit = {}, positiveClickText: String = "Fix", positiveClick: () -> Unit = {}) {
    if (!show.value) return
    AlertDialog(
        onDismissRequest = {
            negativeClick()
            show.value = false
                           },
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
            if (negativeClickText.isEmpty()) return@AlertDialog
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
        stringResource(R.string.no_network2),
        stringResource(R.string.network_desc),
        infoAlert.noNetworkDialog,
        stringResource(R.string.cancel),
        positiveClickText = stringResource(R.string.network_settings),
        positiveClick = {
            RouteParams.push(modDetails)
            OpenRoute("Notifications & Internet", navController, view, enter = fadeIn(), exit = fadeOut())
        }
    )

    UpdateAlert(
        stringResource(R.string.root_required),
        stringResource(R.string.root_required_desc),
        infoAlert.rootRequiredDialog,
        stringResource(R.string.cancel),
        positiveClickText = stringResource(R.string.root_required_guide),
        positiveClick = {
            infoAlert.rootRequiredPositiveLambda.value()
        },
        negativeClick = {
            infoAlert.rootRequiredNegativeLambda.value()
        }
    )


    UpdateAlert(
        stringResource(R.string.detach_module_recommended),
        stringResource(
            R.string.to_prevent_google_play_store_from_overwriting_it_is_recommended_to_install_a_detach_module,
            modDetails.name
        ),
        infoAlert.noDetachDialog,
        negativeClick = {
            infoAlert.noDetachNegativeLambda.value()
        },
        positiveClick = {
            infoAlert.noDetachPositiveLambda.value()
        },
        positiveClickText = stringResource(R.string.install_detach)
    )

    UpdateAlert(
        stringResource(R.string.lsposed_required),
        stringResource(R.string.requires_lsposed_and_will_not_function_without_it, modDetails.name),
        infoAlert.noLSPosedDialog,
        negativeClick = {
            infoAlert.noLsposedNegativeLambda.value()
        },
        positiveClick = {
            infoAlert.noLsposedPositiveLambda.value()
        },
        positiveClickText = stringResource(R.string.install_lsposed)
    )

    UpdateAlert(
        stringResource(R.string.rezygisk_required),
        stringResource(R.string.requires_zygisk_and_will_not_function_without_it, modDetails.name),
        infoAlert.noZygiskDialog,
        negativeClick = {
            infoAlert.noZygiskNegativeLambda.value()
        },
        positiveClick = {
            infoAlert.noZygiskPositiveLambda.value()
        },
        positiveClickText = stringResource(R.string.install_zygisk)
    )
}