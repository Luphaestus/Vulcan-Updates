package luph.vulcanizerv3.updates.utils.download

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.serialization.json.Json


fun getModList(path: String = "Mods"): Map<String, String> {
    val baseUrl =
        "https://raw.githubusercontent.com/Luphaestus/UpdatesRepo/refs/heads/master/$path/list"
    val client = HttpClient(CIO) {
        engine {
            requestTimeout = 1000
        }
    }
    try {
        val jsonString = getRemoteText(baseUrl, client)
        val modVersions: Map<String, String> = Json.decodeFromString(jsonString!!)

        return modVersions
    } catch (e: Exception) {
        Log.e("Error getting mod list", e.toString())
        return emptyMap()
    } finally {
        client.close()
    }
}