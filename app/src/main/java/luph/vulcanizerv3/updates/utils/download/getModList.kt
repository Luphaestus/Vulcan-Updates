package luph.vulcanizerv3.updates.utils.download

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO


fun String.sanitize() = this.replace(Regex("[^A-Za-z0-9,_\\-]"), "")
fun getModList(path: String = "Mods"): ArrayList<String> {
    val baseUrl =
        "https://raw.githubusercontent.com/Luphaestus/UpdatesRepo/refs/heads/master/$path/list"
    val client = HttpClient(CIO) {
        engine {
            requestTimeout = 1000
        }
    }
    return try {
        ArrayList(getRemoteText(baseUrl, client)!!.sanitize().split(",").map { "$path/$it" })
    } catch (e: Exception) {
        arrayListOf()
    } finally {
        client.close()
    }
}