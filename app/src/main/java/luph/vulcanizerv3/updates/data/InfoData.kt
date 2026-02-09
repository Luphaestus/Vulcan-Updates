package luph.vulcanizerv3.updates.data

import android.util.Log
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R

enum class UpdateStatus {
    NOT_INSTALLED, INSTALLED, UPDATE_AVAILABLE, UPDATING
}

data class infoBoxesData(
    val modDetails: ModDetails,
)
{
    val showDescription: MutableState<Boolean> = mutableStateOf(false)
    val showVersion: MutableState<Boolean> = mutableStateOf(false)
    val showErrorText: MutableState<String> = mutableStateOf("")
}

data class buttonData(
    val modDetails: ModDetails,
    val infoState: MutableState<UpdateStatus>,
    val downloadId: MutableIntState,
    val changeUpdateType: (UpdateStatus, buttonData) -> Unit,
    val infoAlert: infoAlert,
    val navController: NavController,
    val view: android.view.View,
) {
    var canCancel by mutableStateOf(true)
    var firstButtonVisible by mutableStateOf(false)
    var buttonAnimWeightValue by mutableFloatStateOf(0.00000001f)
    var doToggleButtonVisibility by mutableStateOf(false)

    private val firstButtonStrings = mapOf(
        UpdateStatus.NOT_INSTALLED to "",
        UpdateStatus.INSTALLED to MainActivity.applicationContext().getString(R.string.uninstall),
        UpdateStatus.UPDATE_AVAILABLE to MainActivity.applicationContext().getString(R.string.uninstall),
        UpdateStatus.UPDATING to MainActivity.applicationContext().getString(R.string.cancel)
    )
    private val secondButtonStrings = mapOf(
        UpdateStatus.NOT_INSTALLED to MainActivity.applicationContext().getString(R.string.install),
        UpdateStatus.INSTALLED to MainActivity.applicationContext().getString(R.string.open),
        UpdateStatus.UPDATE_AVAILABLE to MainActivity.applicationContext().getString(R.string.update),
        UpdateStatus.UPDATING to MainActivity.applicationContext().getString(R.string.open),
    )
    fun getFirstButtonStrings(updateStatus: UpdateStatus): String {
        return firstButtonStrings[updateStatus] ?: "Unknown"
    }
    fun getSecondButtonStrings(updateStatus: UpdateStatus): String {
        return secondButtonStrings[updateStatus] ?: "Unknown"
    }
    fun isInstallTwoButtons(updateStatus: UpdateStatus): Boolean {
        Log.e("isInstallTwoButtons", "status: $updateStatus")
        Log.e("isInstallTwoButtons", "firstButtonVisible: ${updateStatus == UpdateStatus.INSTALLED || updateStatus == UpdateStatus.UPDATE_AVAILABLE || updateStatus == UpdateStatus.UPDATING}")
        return updateStatus == UpdateStatus.INSTALLED || updateStatus == UpdateStatus.UPDATE_AVAILABLE || updateStatus == UpdateStatus.UPDATING
    }

}

data class infoAlert(
    val noNetworkDialog: MutableState<Boolean> = mutableStateOf(false),

    val noLSPosedDialog: MutableState<Boolean> =  mutableStateOf(false),
    var noLsposedNegativeLambda: MutableState<() -> Unit> = mutableStateOf({}),
    var noLsposedPositiveLambda: MutableState<() -> Unit> = mutableStateOf({}),

    val noZygiskDialog: MutableState<Boolean> =  mutableStateOf(false),
    var noZygiskNegativeLambda: MutableState<() -> Unit> = mutableStateOf({}),
    var noZygiskPositiveLambda: MutableState<() -> Unit> = mutableStateOf({}),

    val noDetachDialog: MutableState<Boolean> =  mutableStateOf(false),
    var noDetachNegativeLambda: MutableState<() -> Unit> = mutableStateOf({}),
    var noDetachPositiveLambda: MutableState<() -> Unit> = mutableStateOf({}),
)
