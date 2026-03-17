package luph.vulcanizerv3.updates.ui.components.info

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import com.google.firebase.analytics.logEvent
import com.ketch.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.data.DETAILFILE
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.data.ModType
import luph.vulcanizerv3.updates.data.UpdateStatus
import luph.vulcanizerv3.updates.data.buttonData
import luph.vulcanizerv3.updates.data.infoBoxesData
import luph.vulcanizerv3.updates.ui.page.settings.options.subscribe
import luph.vulcanizerv3.updates.utils.apkmanager.installAPK
import luph.vulcanizerv3.updates.utils.modulemanager.installModule
import luph.vulcanizerv3.updates.utils.root.runShellCommand
import java.io.File


suspend fun getDownloadID(url: String): Int? {
    MainActivity.getKetch().getAllDownloads().forEach { downloadModel ->
        if (url + DETAILFILE.FILE.type == downloadModel.url)
            return downloadModel.id
    }
    return null
}


class InfoDownloadData(
    val downloadID: MutableIntState,
    val progress: MutableIntState,
    val infoState: MutableState<UpdateStatus>,
    val corePackages: Array<String>,
    val modDetails: ModDetails,
    val buttonData: buttonData,
    val infoBoxesData: infoBoxesData,
)

@SuppressLint("SetWorldReadable")
suspend fun observeDownloadProgress(data: InfoDownloadData) {
    MainActivity.getKetch().observeDownloadById(data.downloadID.intValue)
        .flowOn(Dispatchers.IO)
        .collect { downloadModel ->
            data.progress.intValue = downloadModel.progress

            downloadModel.status.let {
                if (data.infoState.value == UpdateStatus.INSTALLED) return@collect

                if (it == Status.SUCCESS) {
                    val downloadedFIle = File(downloadModel.path + "/${data.modDetails.name}")
                    downloadedFIle.setReadable(true, false)

                    Log.e("ModInfo", "success")
                    MainActivity.getKetch().getAllDownloads().filter { it.status == Status.SUCCESS }.forEach {
                        MainActivity.getKetch().clearDb(it.id, false)
                    }

                    data.buttonData.canCancel = false
                    MainActivity.getFirebaseAnalytics().logEvent("downloaded_mod") {
                        param("mod_name", data.modDetails.name)
                        param("mod_version", data.modDetails.version)
                        param("mod_author", data.modDetails.author)
                        data.modDetails.keywords.forEach { keyword ->
                            param("keyword", keyword)
                        }
                    }
                    Log.e("ModInfo", data.modDetails.updateType.toString())
                    GlobalScope.launch(Dispatchers.IO) {
                        var success = false
                        when (data.modDetails.updateType) {
                            ModType.APK -> {
                                success = installAPK(downloadedFIle.absolutePath)
                                data.buttonData.canCancel = true
                            }

                            ModType.MODULE -> {
                                val result = installModule(downloadedFIle.absolutePath)
                                success = result.second
                                if (!success) data.infoBoxesData.showErrorText.value = result.first
                                data.buttonData.canCancel = true
                            }

                            ModType.SHELL -> {
                                val shellFile = File(downloadModel.path, "shell.sh")
                                shellFile.writeText("FILEPATH=\"${downloadedFIle.absolutePath}\"\n" + data.modDetails.shell)
                                val res = runShellCommand("sh ${shellFile.absolutePath}").value
                                success = res.second
                                data.buttonData.canCancel = true
                            }

                            else -> {}
                        }

                        downloadedFIle.delete()

                        if (success) {
                            changeUpdateType(UpdateStatus.INSTALLED, data.buttonData)

                            if (ModDetailsStore.getNotificationAndInternetPreferences().value.notifyAppUpdates.value)
                                if (data.corePackages.contains(data.modDetails.packageName))
                                    subscribe("Core")
                                else
                                    subscribe(data.modDetails.packageName)
                            MainActivity.getFirebaseAnalytics().logEvent("installed_mod") {
                                param("mod_name", data.modDetails.name)
                                param("mod_version", data.modDetails.version)
                                param("mod_author", data.modDetails.author)
                                data.modDetails.keywords.forEach { keyword ->
                                    param("keyword", keyword)
                                }
                            }
                        }
                        else changeUpdateType(UpdateStatus.NOT_INSTALLED, data.buttonData)

                    }
                } else if (it in listOf(Status.FAILED, Status.CANCELLED, Status.PAUSED)) {
                    changeUpdateType(UpdateStatus.NOT_INSTALLED, data.buttonData)
                    MainActivity.getKetch().clearDb(data.downloadID.intValue)
                } else {
                    changeUpdateType(UpdateStatus.UPDATING, data.buttonData)
                    data.buttonData.canCancel = true
                }
            }
        }
}

fun startDownload(downloadID: MutableIntState, url: String, name: String)
{
    downloadID.intValue = MainActivity.getKetch().download(
        url,
        MainActivity.applicationContext().cacheDir.absolutePath,
        name
    )
}
fun cancelDownload(downloadID: MutableIntState)
{
    MainActivity.getKetch().cancel(downloadID.intValue)
}