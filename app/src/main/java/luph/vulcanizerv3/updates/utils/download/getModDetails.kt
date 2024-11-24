package luph.vulcanizerv3.updates.utils.download

import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.data.ModType
import luph.vulcanizerv3.updates.data.SerializableManager


fun getModDetails(paths: Map<String, String>?): MutableList<ModDetails> {
    try {
        val json = Json { ignoreUnknownKeys = true }
        val baseUrl = "https://raw.githubusercontent.com/Luphaestus/UpdatesRepo/refs/heads/master/"
        val detailsPath = "details.json"
        if (paths.isNullOrEmpty()) return mutableListOf()
        val decodedDetails = mutableListOf<ModDetails>()

        paths.forEach { (path, version) ->
            val url = "$baseUrl$path/$detailsPath"
            Log.i("getModDetails", "url: $path")
            val repoName: String = path.split("/")[1]

            val cachedData = SerializableManager<String>().load("$repoName.dat")

            if (cachedData != null) {
                val cachedModDetails: ModDetails = json.decodeFromString(cachedData)
                if (version == cachedModDetails.version) {
                    Log.i("getModDetails", "Loaded from cache")
                    decodedDetails.add(cachedModDetails)
                    return@forEach
                }
            }

            val stringDetails =
                getRemoteText(url)!!
            if (stringDetails == "404: Not Found") {
                Log.e("getModDetails", "404: Not Found")
                Log.e("getModDetails", "url: $baseUrl$path/$detailsPath")
            }
            val details: ModDetails = json.decodeFromString(stringDetails)
            details.url = "$baseUrl$path/"
            details.updateType = when (details.updateTypeString) {
                "apk" -> ModType.APK
                "twrp" -> ModType.TWRP
                "module" -> ModType.MODULE
                else -> {
                    null
                }
            }
            decodedDetails.add(details)
            SerializableManager<String>().save("$repoName.dat", json.encodeToString(details))
        }
        return decodedDetails
    } catch (e: Exception) {
        ModDetailsStore.setOffline(true)
        Log.e("getModDetails", "Exception: ${e}")
        return mutableListOf()
    }
}