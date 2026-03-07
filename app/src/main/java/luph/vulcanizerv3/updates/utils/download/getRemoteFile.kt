package luph.vulcanizerv3.updates.utils.download

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

fun getRemoteFile(url: String, filepath: String? = null): File {
    return runBlocking {
        withContext(Dispatchers.IO) {
            val file = if (filepath != null) File(filepath) else File.createTempFile("temp", ".tmp")
            if (filepath == null) file.deleteOnExit()
            file.deleteOnExit()
            file.outputStream().use { output ->
                java.net.URL(url).openStream().use { input ->
                    input.copyTo(output)
                }
            }
            file
        }
    }
}