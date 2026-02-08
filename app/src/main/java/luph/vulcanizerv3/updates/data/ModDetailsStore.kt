package luph.vulcanizerv3.updates.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import compareVersionNames
import getAPKUpdateStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.ui.page.settings.options.NotificationAndInternetPreferences
import luph.vulcanizerv3.updates.utils.SerializableManager
import luph.vulcanizerv3.updates.utils.apkmanager.getAppVersion
import luph.vulcanizerv3.updates.utils.download.fetchModDetails
import luph.vulcanizerv3.updates.utils.download.getModDetails
import luph.vulcanizerv3.updates.utils.download.getModList
import luph.vulcanizerv3.updates.utils.download.parseModKeywords
import luph.vulcanizerv3.updates.utils.modulemanager.getModuleInstalledList
import luph.vulcanizerv3.updates.utils.modulemanager.getModuleVersions
import luph.vulcanizerv3.updates.utils.root.ROOTStatus
import luph.vulcanizerv3.updates.utils.root.getROOTStatus

enum class ModType {
    APK, TWRP, MODULE
}


@Serializable
data class ModDetails(
    //From JSON
    var name: String = "error: mod name not found",
    val author: String = "error: author not found",
    val version: String = "error: version not found",
    var updateTypeString: String = "error: update type not found",
    val srcLink: String = "error: source link not found",
    val keywords: List<String> = List(0) { "error: keywords not found" },
    val openName: String = "error: open name not found",
    val packageName: String = "error: package name not found",
    val require: String? = null,
    val images: Int = 0,
    val README: String = "error: README not found",
    val changeLog: String = "error: change log not found",
    val READMEsummary: String = "error: README summary not found",
    val changeLogSummary: String = "error: change log summary not found",
    val timestamp: Long = 0,

    //Calculated
    var url: String = "error: url not found",
    var updateType: ModType? = null,
)

enum class DETAILFILE(val type: String) {
    ICON("icon.jpg"),
    FILE("file"),
}

object ModDetailsStore {
    private const val MOD_LIST_FILE = "mod_list.dat"

    private var modDetails = mutableStateOf<MutableList<ModDetails>>(mutableListOf())
    private var keywords =
        mutableStateOf<MutableMap<String, MutableList<ModDetails>>>(mutableMapOf())
    private var appDetails = mutableStateOf<ModDetails?>(null)


    private var modList = mutableStateOf<Map<String, String>>(emptyMap())
    private val serializableManager = SerializableManager<String>()
    private val helpList = mutableStateOf<List<String>>(listOf())

    private var offline = mutableStateOf(false)

    private var packageToModMap = mutableMapOf<String, ModDetails>()
    private var newMods = mutableStateOf<List<String>>(listOf())
    private var installedMods = mutableStateOf<Set<String>>(setOf())
    private var installedModsUpdate =mutableStateOf<Set<String>>(setOf())
    private var isUpdating = mutableStateOf(false)

    init {
        refresh()
    }

    fun getAllMods(): State<MutableList<ModDetails>> {
        return modDetails
    }

    fun getModKeywords(): State<Map<String, List<ModDetails>>> {
        return keywords
    }

    fun getAppDetails(): State<ModDetails?> {
        return appDetails
    }

    fun isAppUpdatedNeeded(): State<Boolean> {
        return mutableStateOf(getAppVersion() != appDetails.value?.version && appDetails.value?.version != null)
    }

    fun isAppUpdateForced(): State<Boolean> {
        if (appDetails.value == null) return mutableStateOf(false)
        return mutableStateOf(appDetails.value?.require !=  getAppVersion().filter { it.isLetter() } && isAppUpdatedNeeded().value)
    }

    fun getAllModKeywords(): State<Map<String, MutableList<ModDetails>>> {
        val currentModKeywords = keywords
        if (currentModKeywords.value.isEmpty()) return currentModKeywords
        currentModKeywords.value["All Mods"] = modDetails.value
        return currentModKeywords
    }

    fun getNewMods(): State<List<String>> {
        return newMods
    }

    fun getModDetails(packageName: String): ModDetails? {
        return packageToModMap[packageName]
    }

    fun isOffline(): State<Boolean> {
        return offline
    }

    fun isUsingMobileData(): Boolean {
        val context = MainActivity.applicationContext()
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
        }
        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
    }

    fun setOffline(value: Boolean) {
        offline.value = value
    }

    fun getInstalledMods(): State<Set<String>> {
        return installedMods
    }

    fun getInstalledModsUpdate(): State<Set<String>> {
        return installedModsUpdate
    }

    private fun saveModList() {
        serializableManager.save(MOD_LIST_FILE, Json.encodeToString(modList.value))
    }

    private fun loadModList(): Map<String, String>? {
        val modListString = serializableManager.load(MOD_LIST_FILE) ?: return null
        return Json.decodeFromString(modListString)
    }

    private fun newMods(): List<String> {
        val savedModList = loadModList() ?: emptyMap()
        val modListPaths = mutableListOf<String>()
        modList.value.forEach { (key, value) ->
            if (!savedModList.containsKey(key) || savedModList[key] != value) {
                modListPaths.add(key.split("/").last())
            }
        }
        return modListPaths
    }

    fun getHelpList(): State<List<String>> {
        return helpList
    }

    fun isUpdating(): State<Boolean> {
        return isUpdating
    }

    fun updateInstalledMods(isAPK: Boolean = false) {
        CoroutineScope(Dispatchers.Default).launch {
            if (isAPK) {
                getModKeywords().value.get("Apk")?.forEach {
                    val status = getAPKUpdateStatus(it.packageName, it.version)
                    if (status != APKUpdateStatus.NOT_INSTALLED) {
                        installedMods.value += it.packageName
                    } else {
                        installedMods.value -= it.packageName
                    }
                    if (status == APKUpdateStatus.UPDATE_NEEDED) {
                        installedModsUpdate.value += it.packageName
                    }
                }
            } else {
                val moduleList = getModuleVersions(getModuleInstalledList())

                moduleList.forEach {
                    if (it.key in packageToModMap.keys) {
                        installedMods.value += it.key
                        if (compareVersionNames(
                                it.value,
                                packageToModMap[it.key]?.version ?: "Not Found"
                            )
                        ) {
                            installedModsUpdate.value += it.key
                        }
                    } else {
                        installedMods.value -= it.key
                    }
                }
            }
        }
    }

    fun refresh() {
        CoroutineScope(Dispatchers.Default).launch {
            if (NotificationAndInternetPreferences.useMobileDataDownload && isUsingMobileData()) {
                offline.value = true
                return@launch
            }
            isUpdating.value = true


            val hasRoot = getROOTStatus() != ROOTStatus.NONE

            modList.value = getModList()
            offline.value = modList.value.isEmpty()
            newMods.value = newMods()

            val tmpModDetails = emptyList<ModDetails>().toMutableList()
            getModDetails(modList.value).forEach {
                if (!(!hasRoot && it.updateType == ModType.MODULE))
                {
                    tmpModDetails.add(it)
                }
            }

            if (modList.value.isEmpty()) {
                offline.value = true
            }
            else {
                saveModList()
            }

            appDetails.value = fetchModDetails("core/Vulcan-Updates")

            keywords.value = parseModKeywords(tmpModDetails)
            modDetails.value = tmpModDetails

            packageToModMap = getModDetails(modList.value).associateBy { it.packageName }.toMutableMap()

            updateInstalledMods()
            updateInstalledMods(true)

            CoroutineScope(Dispatchers.Main).launch{ helpList.value = luph.vulcanizerv3.updates.utils.download.getHelpList() }

            isUpdating.value = false
        }
        return
    }
}

