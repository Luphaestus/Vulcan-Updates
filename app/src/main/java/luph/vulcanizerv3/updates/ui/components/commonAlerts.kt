package luph.vulcanizerv3.updates.ui.components

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.ui.components.info.UpdateAlert
import luph.vulcanizerv3.updates.ui.page.OpenRoute
import luph.vulcanizerv3.updates.ui.page.RouteParams
import luph.vulcanizerv3.updates.utils.root.ROOTStatus
import luph.vulcanizerv3.updates.utils.root.getROOTStatus

@Composable
fun rootRequiredAlert(positiveClickLambda: () -> Unit={}, negativeClickLambda: () -> Unit={}): MutableState<Boolean> {
    val modalShowing = remember {
        mutableStateOf(getROOTStatus() == ROOTStatus.NONE)
    }
    UpdateAlert(
        stringResource(R.string.root_required),
        stringResource(R.string.root_required_desc),
        modalShowing,
        stringResource(R.string.cancel),
        positiveClickText = stringResource(R.string.root_required_guide),
        positiveClick = {
            positiveClickLambda()
            MainActivity.instance!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://vulcanupdates.web.app/help/How%20to%20Provide%20ROOT%20Access%20for%20Vulcan%20Updates")))
        },
        negativeClick = {
            negativeClickLambda()
        }
    )
    return modalShowing
}

@Composable
fun noNetworkAlert(navController: NavController, view: View?, positiveClickLambda: () -> Unit={}, negativeClickLambda: () -> Unit={}): MutableState<Boolean> {
    val modalShowing = remember { mutableStateOf(ModDetailsStore.isOffline().value) }
    UpdateAlert(
        stringResource(R.string.no_network2),
        stringResource(R.string.network_desc),
        modalShowing,
        stringResource(R.string.cancel),
        positiveClickText = stringResource(R.string.network_settings),
        positiveClick = {
            positiveClickLambda()
            view?.let {OpenRoute("Notifications & Internet", navController, it, enter = fadeIn(), exit = fadeOut())}
        },
        negativeClick = {
            negativeClickLambda()
        }
    )
    return modalShowing
}