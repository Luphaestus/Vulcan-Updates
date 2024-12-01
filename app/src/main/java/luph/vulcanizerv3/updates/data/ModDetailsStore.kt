package luph.vulcanizerv3.updates.data

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import getAPKUpdateStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import luph.vulcanizerv3.updates.utils.apkmanager.getAppVersion
import luph.vulcanizerv3.updates.utils.apkmanager.isAPKInstalled
import luph.vulcanizerv3.updates.utils.download.fetchModDetails
import luph.vulcanizerv3.updates.utils.download.getHelpList
import luph.vulcanizerv3.updates.utils.download.getModDetails
import luph.vulcanizerv3.updates.utils.download.getModList
import luph.vulcanizerv3.updates.utils.download.parseModKeywords

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
    VERSION("version.json")
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
    private var installedMods = mutableStateOf<List<String>>(listOf())
    private var installedModsUpdate =mutableStateOf<List<String>>(listOf())
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

    fun setOffline(value: Boolean) {
        offline.value = value
    }

    fun getInstalledMods(): State<List<String>> {
        return installedMods
    }

    fun getInstalledModsUpdate(): State<List<String>> {
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

    fun refresh() {
        CoroutineScope(Dispatchers.Default).launch {
            isUpdating.value = true
            modList.value = getModList()
            offline.value = modList.value.isEmpty()
            newMods.value = newMods()
            saveModList()

            modDetails.value = getModDetails(modList.value)
            if (modList.value.isEmpty()) {
                offline.value = true
            }
            appDetails.value = fetchModDetails("core/app")

            keywords.value = parseModKeywords(modDetails.value)
            if (modList.value.isEmpty()) {
                offline.value = true
            }

            installedMods.value = listOf()
            installedModsUpdate.value = listOf()
            getModKeywords().value.get("Apk")?.forEach() {
                val status = getAPKUpdateStatus(it.packageName, it.version)
                if (status != APKUpdateStatus.NOT_INSTALLED) {
                    installedMods.value += it.packageName
                }
                if (status == APKUpdateStatus.UPDATE_NEEDED) {

                    installedModsUpdate.value += it.packageName
                }
            }

            helpList.value = luph.vulcanizerv3.updates.utils.download.getHelpList()

            packageToModMap = getModDetails(modList.value).associateBy { it.packageName }.toMutableMap()
            isUpdating.value = false
        }
        return
    }
}

