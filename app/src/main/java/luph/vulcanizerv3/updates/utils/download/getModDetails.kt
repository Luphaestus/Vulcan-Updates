package luph.vulcanizerv3.updates.utils.download

import android.util.Log
import androidx.compose.runtime.MutableState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.data.ModType
import luph.vulcanizerv3.updates.data.SerializableManager

fun getModJson(): Json {
    return Json { ignoreUnknownKeys = true }
}

fun fetchModDetails(
    path: String,
    version: String? = null,
    json: Json = getModJson(),
): ModDetails? {
    val detailsPath = "details.json"

    val url = "${MainActivity.applicationContext().getString(R.string.github_link)}$path/$detailsPath"
    val repoName: String = path.split("/")[1]

    val cachedData = SerializableManager<String>().load("$repoName.dat")

    if (cachedData != null) {
        val cachedModDetails: ModDetails = json.decodeFromString(cachedData)
        if (version != null && version == cachedModDetails.version) {
            return cachedModDetails
        }
    }

    val stringDetails = getRemoteText(url)?: return null

    if (stringDetails == "404: Not Found") {
        Log.e("fetchModDetails", "404: Not Found")
        Log.e("fetchModDetails", "url: ${MainActivity.applicationContext().getString(R.string.github_link)}$path/$detailsPath")
        return null
    }

    val details: ModDetails = json.decodeFromString(stringDetails)
    details.url = "${MainActivity.applicationContext().getString(R.string.github_link)}$path/"
    details.updateType = when (details.updateTypeString) {
        "apk" -> ModType.APK
        "twrp" -> ModType.TWRP
        "module" -> ModType.MODULE
        else -> null
    }
    SerializableManager<String>().save("$repoName.dat", json.encodeToString(details))
    return details
}

fun getModDetails(paths: Map<String, String>?): MutableList<ModDetails> {
    try {
        val json = Json { ignoreUnknownKeys = true }
        if (paths.isNullOrEmpty()) return mutableListOf()
        val decodedDetails = mutableListOf<ModDetails>()

        paths.forEach { (path, version) ->
            try {
                val details = fetchModDetails(path, version, json)
                if (details != null) {
                    decodedDetails.add(details)
                }
            } catch (e: Exception) {
                Log.e("getModDetails", "Exception: $e")
                Log.e("getModDetails", "path: $path")
                Log.e("getModDetails", "version: $version")
            }
        }

        if (decodedDetails.isEmpty()) {
            ModDetailsStore.setOffline(true)
        }
        return decodedDetails
    } catch (e: Exception) {
        ModDetailsStore.setOffline(true)
        Log.e("getModDetails", "Exception: ${e}")
        Log.e("getModDetails", "paths: ${paths}")
        Log.e("paths", "paths: $paths")
        return mutableListOf()
    }
}