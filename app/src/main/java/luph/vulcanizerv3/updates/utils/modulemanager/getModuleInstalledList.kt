package luph.vulcanizerv3.updates.utils.modulemanager

import android.content.ClipData
import android.content.ClipboardManager
import androidx.core.content.ContextCompat.getSystemService
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.utils.root.ROOTStatus
import luph.vulcanizerv3.updates.utils.root.getROOTStatus
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand

fun getModuleInstalledList(): List<String> {
    try {
        when (getROOTStatus()) {
            ROOTStatus.NONE -> return emptyList()
            ROOTStatus.KSU -> {
                val result = runRootShellCommand("ksud module list").value
                return result.first.split("\n").mapNotNull { line ->
                    val idRegex = """"id":\s*"([^"]+)"""".toRegex()
                    idRegex.find(line)?.groupValues?.get(1)
                }
            }

            ROOTStatus.MAGISK -> {
                val result = runRootShellCommand("ls -1 /data/adb/modules/").value
                return result.first.split("\n")
            }
        }
    }
    catch (e: Exception) {
        val clipboard = getSystemService(MainActivity.applicationContext(), ClipboardManager::class.java) as ClipboardManager
        val clip = ClipData.newPlainText("Exception", e.message)
        clipboard.setPrimaryClip(clip)
        return emptyList()
    }
}