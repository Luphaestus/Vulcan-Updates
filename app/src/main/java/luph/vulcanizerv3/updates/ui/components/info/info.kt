package luph.vulcanizerv3.updates.ui.components.info

import android.util.Log
import android.view.View
import android.window.BackEvent
import android.window.OnBackInvokedCallback
import androidx.activity.BackEventCompat
import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.analytics.logEvent
import com.ketch.Status


import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.DETAILFILE
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.data.ModType
import luph.vulcanizerv3.updates.data.UpdateStatus
import luph.vulcanizerv3.updates.data.buttonData
import luph.vulcanizerv3.updates.data.infoAlert
import luph.vulcanizerv3.updates.data.infoBoxesData
import luph.vulcanizerv3.updates.ui.components.ImageCarousel
import luph.vulcanizerv3.updates.ui.components.PageNAv
import luph.vulcanizerv3.updates.ui.page.RouteParams
import luph.vulcanizerv3.updates.ui.page.settings.options.subscribe
import luph.vulcanizerv3.updates.ui.page.showNavigation
import luph.vulcanizerv3.updates.utils.root.runShellCommand
import java.io.File
import kotlin.coroutines.cancellation.CancellationException


@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ModInfo(navController: NavController = NavController(MainActivity.applicationContext()),
            view: View = MainActivity.instance!!.window.decorView, passedModDetails: ModDetails? = null) {
    val initialModDetails: ModDetails = passedModDetails ?: RouteParams.pop(ModDetails::class.java)
        ?: ModDetails()
    val modDetails = remember { initialModDetails }
    val downloadId = remember { mutableIntStateOf(0) }
    val downloadProgressPercentage = remember { mutableIntStateOf(0) }

    val infoState = remember { mutableStateOf(UpdateStatus.NOT_INSTALLED) }

    showNavigation.show = false




    val corePackages = arrayOf("luph.vulcanizerv3.updates", "persist.sys.vulcan.pif.version", "persist.sys.vulcan.romversion")

    fun updateModStatus(){
        if (ModDetailsStore.getAllPackages().contains(modDetails.packageName)) {
            when (infoState.value) {
                UpdateStatus.INSTALLED -> {
                    ModDetailsStore.installedMods.value += modDetails.packageName
                    ModDetailsStore.installedModsUpdate.value -= modDetails.packageName
                }


                UpdateStatus.UPDATE_AVAILABLE -> {
                    ModDetailsStore.installedMods.value += modDetails.packageName
                    ModDetailsStore.installedModsUpdate.value += modDetails.packageName
                }

                UpdateStatus.NOT_INSTALLED -> {
                    ModDetailsStore.installedMods.value -= modDetails.packageName
                    ModDetailsStore.installedModsUpdate.value -= modDetails.packageName
                }

                else -> {}
            }
        }
    }

    val infoAlert = infoAlert()
    val buttonData = buttonData(modDetails, infoState, downloadId, changeUpdateType = { updateStatus, buttonData -> changeUpdateType(updateStatus, buttonData) }, infoAlert, navController, view,  {updateModStatus()})
    val infoBoxesData = infoBoxesData(modDetails)



    LaunchedEffect(downloadId.intValue) {
        if (infoState.value != UpdateStatus.UPDATING) {
            if (corePackages.contains(modDetails.packageName))
                when (modDetails.packageName) {
                    "luph.vulcanizerv3.updates" -> {
                        if (ModDetailsStore.isAppUpdatedNeeded().value) {
                            changeUpdateType(UpdateStatus.UPDATE_AVAILABLE, buttonData)
                        } else {
                            changeUpdateType(UpdateStatus.INSTALLED, buttonData)
                        }
                    }
                    else -> {
                        when (modDetails.updateType) {
                            ModType.SHELL -> {
                                if (runShellCommand("getprop ${modDetails.packageName}").value.first.trim() == modDetails.version.trim())
                                    changeUpdateType(UpdateStatus.INSTALLED, buttonData)
                                else changeUpdateType(UpdateStatus.UPDATE_AVAILABLE, buttonData)
                            }

                            else -> {
                                changeUpdateType(UpdateStatus.INSTALLED, buttonData)
                            }
                        }
                    }
                }
            else {
                if (modDetails.packageName in ModDetailsStore.getInstalledMods().value) {
                    if (ModDetailsStore.getInstalledModsUpdate().value.contains(modDetails.packageName))
                        changeUpdateType(UpdateStatus.UPDATE_AVAILABLE, buttonData)
                    else changeUpdateType(UpdateStatus.INSTALLED, buttonData)
                } else {
                    changeUpdateType(UpdateStatus.NOT_INSTALLED, buttonData)
                }
            }
        }


        downloadId.intValue = getDownloadID(modDetails.url) ?: -1
        val downloadData = InfoDownloadData(
            downloadId,
            downloadProgressPercentage,
            infoState,
            corePackages,
            modDetails,
            buttonData,
            infoBoxesData,
        )
        observeDownloadProgress(downloadData)

    }

    InfoPopup(infoAlert, modDetails, navController, view)

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {

        PageNAv(stringResource(R.string.mod_info_title), navController)


        LazyColumn(Modifier.background(MaterialTheme.colorScheme.surface)) {
            item {
                InfoPane(modDetails, downloadProgressPercentage, infoState, infoBoxesData, view)
                InfoButtons(buttonData, corePackages)

                ImageCarousel(
                    (1..modDetails.images).map { index -> "${modDetails.url}$index.jpg" },
                    modifier = Modifier
                        .height(168.dp)
                        .padding(start = 8.dp, bottom = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

              InfoBoxes(infoBoxesData)
            }
        }
    }
}



