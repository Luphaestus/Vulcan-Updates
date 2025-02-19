package luph.vulcanizerv3.updates.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.runtime.MutableIntState
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
import luph.vulcanizerv3.updates.ui.page.settings.options.NotificationAndInternetPreferencesSerilizeable
import luph.vulcanizerv3.updates.ui.page.settings.options.subscribe
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import luph.vulcanizerv3.updates.ui.page.oobe.OOBEDataPreference
import luph.vulcanizerv3.updates.ui.page.oobe.OOBEDataSerializable
import luph.vulcanizerv3.updates.utils.getprop

enum class ModType {
    APK, TWRP, MODULE, SHELL
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
    val shell: String = "",

    //Calculated
    var url: String = "error: url not found",
    var updateType: ModType? = null,
)

enum class DETAILFILE(val type: String) {
    ICON("icon.png"),
    FILE("file"),
}

object ModDetailsStore {
    private const val MOD_LIST_FILE = "mod_list.dat"

    private var modDetails = mutableStateOf<MutableList<ModDetails>>(mutableListOf())
    private var keywords =
        mutableStateOf<MutableMap<String, MutableList<ModDetails>>>(mutableMapOf())

    private var coreDetails = mutableStateOf<MutableMap<String, ModDetails?>>(mutableMapOf())

    private var modList = mutableStateOf<Map<String, String>>(emptyMap())
    private val serializableManager = SerializableManager<String>()
    private val helpList = mutableStateOf<List<String>>(listOf())

    private var offline = mutableStateOf(false)

    private var packageToModMap = mutableMapOf<String, ModDetails>()
    private var newMods = mutableStateOf<List<String>>(listOf())
    var installedMods = mutableStateOf<Set<String>>(setOf())
    var installedModsUpdate =mutableStateOf<Set<String>>(setOf())
    private var isUpdating = mutableStateOf(false)

    private var isUpdateForced = mutableStateOf(false)
    var showUpdateForceDialog = mutableStateOf(false)

    val notificationAndInternetPreferences = mutableStateOf(NotificationAndInternetPreferences())

    val loadedOOBE = mutableStateOf(false)
    val oobeData = mutableStateOf(OOBEDataPreference())

    init {
        refresh()
    }

    fun getAllMods(): State<MutableList<ModDetails>> {
        return modDetails
    }

    fun getModKeywords(): State<Map<String, List<ModDetails>>> {
        return keywords
    }

    fun getCoreDetails(): State<Map<String, ModDetails?>> {
        return coreDetails
    }

    fun isAppUpdatedNeeded(): State<Boolean> {
        return mutableStateOf(getAppVersion() != coreDetails.value.get("app")?.version && coreDetails.value.get("app")?.version != null)
    }

    fun numbCoreUpdatesNeeded(): State<Int> {
        var numbCoreUpdatesNeeded = mutableStateOf(0)
        if (isAppUpdatedNeeded().value) numbCoreUpdatesNeeded.value++
        arrayOf("rom", "pif").forEach {
            coreDetails.value.get(it)?.let {
                if (compareVersionNames(it.version, getprop(it.packageName))) numbCoreUpdatesNeeded.value++
            }
        }
        return numbCoreUpdatesNeeded
    }

    fun isAppUpdateForced(): State<Boolean> {
        return isUpdateForced
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

    fun updateInstalledMods() {
        CoroutineScope(Dispatchers.Default).launch {
            installedMods.value = emptySet()
            getModKeywords().value.get("Apk")?.forEach {
                val status = getAPKUpdateStatus(it.packageName, it.version)
                if (status != APKUpdateStatus.NOT_INSTALLED) {
                    installedMods.value += it.packageName
                    if (status == APKUpdateStatus.UPDATE_NEEDED) {
                        installedModsUpdate.value += it.packageName
                    }
                }
            }
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
                }
            }
        }
    }

    fun loadNotificationAndInternetPreferences() {
        SerializableManager<String>().load(NotificationAndInternetPreferencesSerilizeable().fileName)?.let {
            val serilable:NotificationAndInternetPreferencesSerilizeable = Json.decodeFromString(it)
            notificationAndInternetPreferences.value = NotificationAndInternetPreferences(
                mutableStateOf(serilable.wifi),
                mutableStateOf(serilable.data),
                mutableStateOf(serilable.notifyCoreUpdates),
                mutableStateOf(serilable.notifyAppUpdates)
            )
        }
    }

    fun saveNotificationAndInternetPreferences() {
        SerializableManager<String>().save(
            NotificationAndInternetPreferencesSerilizeable().fileName,
            Json.encodeToString(NotificationAndInternetPreferencesSerilizeable(
                notificationAndInternetPreferences.value.wifi.value,
                notificationAndInternetPreferences.value.data.value,
                notificationAndInternetPreferences.value.notifyCoreUpdates.value,
                notificationAndInternetPreferences.value.notifyAppUpdates.value
            ))
        )
    }

    fun getOOBEPreferences(): State<OOBEDataPreference> {
        if (!loadedOOBE.value) loadOOBEPreferences()
        return oobeData
    }

    fun loadOOBEPreferences() {
        SerializableManager<String>().load(OOBEDataSerializable().fileName)?.let {
            val serilable: OOBEDataSerializable = Json.decodeFromString(it)
            oobeData.value = OOBEDataPreference(
                mutableStateOf(serilable.version),
            )
        }
    }

    fun saveOOBEPreferences() {
        SerializableManager<String>().save(
            OOBEDataSerializable().fileName,
            Json.encodeToString(OOBEDataSerializable(
                oobeData.value.version.value,
            ))
        )
    }

    fun getNotificationAndInternetPreferences(): State<NotificationAndInternetPreferences> {
        return notificationAndInternetPreferences
    }

    fun getAllPackages(): List<String> {
        return packageToModMap.keys.toList()
    }


    fun refresh() {
        CoroutineScope(Dispatchers.Default).launch {
            loadNotificationAndInternetPreferences()
            if (isUsingMobileData())
            {
                when (notificationAndInternetPreferences.value.data.value) {
                    0f -> {
                        offline.value = true
                        return@launch
                    }
                    else -> {
                        offline.value = false
                    }
                }
            } else {
                when (notificationAndInternetPreferences.value.wifi.value) {
                    0f -> {
                        offline.value = true
                        return@launch
                    }
                    else -> {
                        offline.value = false
                    }
                }
            }
            if (notificationAndInternetPreferences.value.notifyCoreUpdates.value) subscribe("Core")
            isUpdating.value = true

            modList.value = getModList()
            offline.value = modList.value.isEmpty()
            newMods.value = newMods()

            val tmpModDetails = emptyList<ModDetails>().toMutableList()
            getModDetails(modList.value).forEach {
                tmpModDetails.add(it)
            }

            if (modList.value.isEmpty()) {
                offline.value = true
            }
            else {
                saveModList()
            }

            coreDetails.value["rom"] = fetchModDetails("core/Vulcan-ROM")
            coreDetails.value["app"] = fetchModDetails("core/Vulcan-Updates")
            coreDetails.value["pif"] = fetchModDetails("core/Keybox")

            keywords.value = parseModKeywords(tmpModDetails)
            modDetails.value = tmpModDetails

            packageToModMap = getModDetails(modList.value).associateBy { it.packageName }.toMutableMap()

            updateInstalledMods()

            CoroutineScope(Dispatchers.Main).launch{ helpList.value = luph.vulcanizerv3.updates.utils.download.getHelpList() }

            val updateForceValueBefore = isUpdateForced.value
            if (coreDetails.value.get("app") != null) {
                isUpdateForced.value =
                    coreDetails.value.get("app")?.require != getAppVersion().filter { it.isLetter() } && isAppUpdatedNeeded().value
                if (isUpdateForced.value && !updateForceValueBefore) {
                    showUpdateForceDialog.value = true
                }
            }

            isUpdating.value = false
        }
        return
    }
}

