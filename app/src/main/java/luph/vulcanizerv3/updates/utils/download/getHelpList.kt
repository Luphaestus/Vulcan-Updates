package luph.vulcanizerv3.updates.utils.download

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.serialization.json.Json
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R

fun getHelpList(path: String = "help"): List<String> {
    val baseUrl =
        "${MainActivity.applicationContext().getString(R.string.github_link)}$path/list"
    val client = HttpClient(CIO) {
        engine {
            requestTimeout = 1000
        }
    }
    try {
        val listString = getRemoteText(baseUrl, client)
        return listString?.split(",") ?: emptyList()
    } catch (e: Exception) {
        Log.e("getModList", "$baseUrl $e")
        return emptyList()
    } finally {
        client.close()
    }
}