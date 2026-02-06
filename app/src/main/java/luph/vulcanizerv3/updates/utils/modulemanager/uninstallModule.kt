package luph.vulcanizerv3.updates.utils.modulemanager

import android.os.Looper
import android.widget.Toast
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.utils.root.ROOTStatus
import luph.vulcanizerv3.updates.utils.root.getROOTStatus
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand

fun uninstallModule(module: String): Boolean {
    return when (getROOTStatus()) {
        ROOTStatus.NONE -> {
            Looper.prepare()
            Toast.makeText(
                MainActivity.applicationContext(),
                "Root is required",
                Toast.LENGTH_SHORT
            ).show()
            false
        }

        ROOTStatus.KSU -> {
            runRootShellCommand("ksud module uninstall \"$module\"").value.second
        }

        ROOTStatus.MAGISK ->
            runRootShellCommand("rm -rf \"/data/adb/modules/$module\"").value.second
    }
}