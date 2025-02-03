package luph.vulcanizerv3.updates.utils.download

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.utils.root.runShellCommand
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

fun zip(directory: File): File {
    return runBlocking {
        withContext(Dispatchers.IO) {
            val zipFile = File.createTempFile("temp", ".zip")
            zipFile.deleteOnExit()
            ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
                directory.walkTopDown().forEach { file ->
                    if (file.isFile) {
                        val entry = ZipEntry(file.relativeTo(directory).path)
                        zos.putNextEntry(entry)
                        file.inputStream().use { it.copyTo(zos) }
                        zos.closeEntry()
                    }
                }
            }
            zipFile
        }
    }
}

fun unzip(
    zipFile: File,
    targetDirectory: String = MainActivity.applicationContext().cacheDir.absolutePath + File.separator + "unzipped_" + System.currentTimeMillis(),): File {
    return runBlocking {
        withContext(Dispatchers.IO) {
            val targetDir = File(targetDirectory)
            targetDir.mkdirs()
            ZipInputStream(BufferedInputStream(FileInputStream(zipFile))).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    val file = File(targetDir, entry.name)
                    if (entry.isDirectory) {
                        file.mkdirs()
                    } else {
                        file.parentFile.mkdirs()
                        file.outputStream().use { zis.copyTo(it) }
                    }
                    entry = zis.nextEntry
                }
            }
            targetDir
        }
    }
}

fun addToZipFile(
    file: File,
    zipFile: File,
    zipEntryPath: String = file.name
) {
    val fis = FileInputStream(file)
    val zos = ZipOutputStream(FileOutputStream(zipFile))
    val entry = ZipEntry(zipEntryPath)
    zos.putNextEntry(entry)
    val buffer = ByteArray(1024)
    var len: Int
    while (fis.read(buffer).also { len = it } > 0) {
        zos.write(buffer, 0, len)
    }
    zos.closeEntry()
    fis.close()
    zos.close()
}


