package luph.vulcanizerv3.updates.utils.download

import android.util.Log

fun getDownloadSize(url: String): Long {
    try {
        val connection = java.net.URL(url).openConnection()
        connection.connect()
        val size = connection.contentLengthLong
        connection.getInputStream().close()
        return size
    }
    catch (e: Exception) {
        Log.e("DownloadSize", "Error getting download size url $url", e)
        return -1
    }
}