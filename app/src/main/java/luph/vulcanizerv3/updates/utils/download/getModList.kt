package luph.vulcanizerv3.updates.utils.download

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.serialization.json.Json
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R


fun getModList(path: String = "mods"): Map<String, String> {
    val baseUrl =
        "${MainActivity.applicationContext().getString(R.string.github_link)}$path/list"
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
        return emptyMap()
    } finally {
        client.close()
    }
}