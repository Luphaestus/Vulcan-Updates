package luph.vulcanizerv3.updates.utils.modulemanager

import android.os.Looper
import android.util.Log
import android.widget.Toast
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.utils.root.ROOTStatus
import luph.vulcanizerv3.updates.utils.root.getROOTStatus
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand
import java.io.File

fun installModule(path: String): Pair<String, Boolean> {
    val file = File(path)
    if (!file.exists()) {
        Looper.prepare()
        Toast.makeText(MainActivity.applicationContext(), "File does not exist", Toast.LENGTH_SHORT).show()
        return Pair("", false)
    }


    return when (getROOTStatus()) {
        ROOTStatus.NONE -> {
            Looper.prepare()
            Toast.makeText(MainActivity.applicationContext(), "Root is required", Toast.LENGTH_SHORT).show()
            Pair("", false)
        }
        ROOTStatus.KSU -> {
            runRootShellCommand("ksud module install \"$path\"", true).value
        }
        ROOTStatus.MAGISK ->
            runRootShellCommand("magisk --install-module \"$path\"", true).value
        }
}
