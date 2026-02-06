package luph.vulcanizerv3.updates.utils.modulemanager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.utils.root.ROOTStatus
import luph.vulcanizerv3.updates.utils.root.getROOTStatus
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand
import org.json.JSONArray

fun getModuleVersion(module: String): String? {
    val result = runRootShellCommand("cat \"/data/adb/modules/$module/module.prop\"").value
    return result.first.split("\n").find { it.startsWith("version=") }?.substringAfter("version=")
}

fun getModuleVersions(moduleList: List<String>): Map<String, String> {
    when (getROOTStatus()) {
        ROOTStatus.NONE -> return emptyMap()
        ROOTStatus.KSU -> {
            try {
                val result = runRootShellCommand("ksud module list").value
                val moduleVersions = mutableMapOf<String, String>()
                val modDetails = JSONArray(result.first)
                for (i in 0 until modDetails.length()) {
                    val module = modDetails.getJSONObject(i)
                    moduleVersions[module.getString("id")] = module.getString("version")
                }
                return moduleVersions
            }
            catch (e: Exception) {
                val clipboard = getSystemService(MainActivity.applicationContext(), ClipboardManager::class.java) as ClipboardManager
                val clip = ClipData.newPlainText("Exception", e.message)
                clipboard.setPrimaryClip(clip)
                return emptyMap()
            }
        }

        ROOTStatus.MAGISK -> {


            val moduleVersions = mutableMapOf<String, String>()
            for (module in moduleList) {
                val version = getModuleVersion(module)
                if (version != null) {
                    moduleVersions[module] = version
                }
            }
            return moduleVersions
        }
    }
}