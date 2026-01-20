package luph.vulcanizerv3.updates.utils.download

fun getDownloadSize(url: String): Long {
    val connection = java.net.URL(url).openConnection()
    connection.connect()
    val size = connection.contentLengthLong
    connection.getInputStream().close()
    return size
}