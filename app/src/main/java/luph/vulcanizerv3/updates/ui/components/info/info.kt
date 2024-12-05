package luph.vulcanizerv3.updates.ui.components.info

import android.util.Log
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.analytics.logEvent
import com.ketch.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.*
import luph.vulcanizerv3.updates.ui.components.ImageCarousel
import luph.vulcanizerv3.updates.ui.components.PageNAv
import luph.vulcanizerv3.updates.ui.page.RouteParams
import luph.vulcanizerv3.updates.ui.page.showNavigation
import luph.vulcanizerv3.updates.utils.apkmanager.installAPK
import luph.vulcanizerv3.updates.utils.modulemanager.installModule

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

    val infoAlert = infoAlert()
    val buttonData = buttonData(modDetails, infoState, downloadId, changeUpdateType = { updateStatus, buttonData -> changeUpdateType(updateStatus, buttonData) })
    val infoBoxesData = infoBoxesData(modDetails)

    BackHandler {
        ModDetailsStore.updateInstalledMods(modDetails.updateType == ModType.APK)
        navController.popBackStack()
    }



    LaunchedEffect(downloadId.intValue) {
        if (modDetails.packageName in ModDetailsStore.getInstalledMods().value) {
            if (ModDetailsStore.getInstalledModsUpdate().value.contains(modDetails.packageName))
                changeUpdateType(UpdateStatus.UPDATE_AVAILABLE, buttonData)
            else changeUpdateType(UpdateStatus.INSTALLED, buttonData)
        } else {
            changeUpdateType(UpdateStatus.NOT_INSTALLED, buttonData)
        }


        MainActivity.getKetch().getAllDownloads().forEach { downloadModel ->
            if (buttonData.modDetails.url + DETAILFILE.FILE.type == downloadModel.url)
                downloadId.intValue = downloadModel.id
        }
        MainActivity.getKetch().observeDownloadById(downloadId.intValue)
            .flowOn(Dispatchers.IO)
            .collect { downloadModel ->
                Log.e("downloadStatus", downloadModel.toString())
                downloadProgressPercentage.intValue = downloadModel.progress
                downloadModel.status.let {
                    if (it == Status.SUCCESS) {
                        if (modDetails.packageName in ModDetailsStore.getInstalledMods().value) {
                            return@collect
                        }


                        MainActivity.getFirebaseAnalytics().logEvent("downloaded_mod") {
                            param("mod_name", modDetails.name)
                            param("mod_version", modDetails.version)
                            param("mod_author", modDetails.author)
                            modDetails.keywords.forEach { keyword ->
                                param("keyword", keyword)
                            }
                        }
                        GlobalScope.launch(Dispatchers.IO) {
                            var success = false

                            buttonData.canCancel= false
                            when (modDetails.updateType) {
                                ModType.APK -> {
                                    success = installAPK(downloadModel.path + "/${modDetails.name}")
                                    Log.e("installAPK", success.toString())
                                    buttonData.canCancel = true
                                }

                                ModType.MODULE -> {
                                    success =
                                        installModule(downloadModel.path + "/${modDetails.name}")
                                    buttonData.canCancel = true

                                }

                                else -> {}
                            }
                            if (success) {
                                changeUpdateType(UpdateStatus.INSTALLED, buttonData)
                                MainActivity.getFirebaseAnalytics().logEvent("installed_mod") {
                                    param("mod_name", modDetails.name)
                                    param("mod_version", modDetails.version)
                                    param("mod_author", modDetails.author)
                                    modDetails.keywords.forEach { keyword ->
                                        param("keyword", keyword)
                                    }
                                }
                            } else changeUpdateType(UpdateStatus.NOT_INSTALLED, buttonData)
                        }
                    } else if (it in listOf(Status.FAILED, Status.CANCELLED, Status.PAUSED)) {
                        changeUpdateType(UpdateStatus.NOT_INSTALLED, buttonData)
                        MainActivity.getKetch().clearDb(downloadId.intValue)
                    } else {
                        changeUpdateType(UpdateStatus.UPDATING, buttonData)
                    }
                }
            }
    }

    InfoPopup(infoAlert, modDetails)

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()
            .padding(start = 16.dp, end=16.dp, bottom=16.dp)
    ) {

        PageNAv(stringResource(R.string.mod_info_title), navController)


        LazyColumn(Modifier.background(MaterialTheme.colorScheme.surface)) {
            item {
                InfoPane(modDetails, downloadProgressPercentage, infoState, infoBoxesData, view)
                InfoButtons(buttonData)

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



