package luph.vulcanizerv3.updates.utils.download

import kotlinx.serialization.json.Json

fun <T> getJson(url: String, deserializer: kotlinx.serialization.KSerializer<T>, ignoreUnknownKeysVal: Boolean = false): T {
    val json = Json { ignoreUnknownKeys = ignoreUnknownKeysVal }
    val stringDetails = getRemoteText(url)!!
    val details: T = json.decodeFromString(deserializer, stringDetails)

    return details
}