package luph.vulcanizerv3.updates.utils.download

import android.util.Log
import kotlinx.serialization.json.Json
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.data.ModType


fun getModDetails(paths: List<String>?): MutableList<ModDetails> {
    val json = Json { ignoreUnknownKeys = true }
    val baseUrl = "https://raw.githubusercontent.com/Luphaestus/UpdatesRepo/refs/heads/master/"
    val detailsPath = "details.json"
    if (paths.isNullOrEmpty()) return mutableListOf()
    val decodedDetails = mutableListOf<ModDetails>()
    paths.forEach {
        val stringDetails =
            getRemoteText("$baseUrl$it/$detailsPath")!!
        if (stringDetails == "404: Not Found") {
            Log.e("getModDetails", "404: Not Found")
            Log.e("getModDetails", "url: $baseUrl$it/$detailsPath")
        }
        val details: ModDetails = json.decodeFromString(stringDetails)
        details.url = "$baseUrl$it/"
        details.updateType = when (details.updateTypeString) {
            "apk" -> ModType.APK
            "twrp" -> ModType.TWRP
            "module" -> ModType.MODULE
            else -> {
                null
            }
        }
        decodedDetails.add(details)
    }
    return decodedDetails
}