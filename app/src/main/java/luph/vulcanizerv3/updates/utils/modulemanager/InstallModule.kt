package luph.vulcanizerv3.updates.utils.modulemanager

import android.util.Log
import android.widget.Toast
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand
import java.io.File

fun installModule(path: String): Boolean {
    val file = File(path)
    Log.e("installAPKRoot", file.toString())
    if (!file.exists()) {
        Toast.makeText(MainActivity.applicationContext(), "File does not exist", Toast.LENGTH_SHORT).show()
        return false
    }
    return runRootShellCommand("magisk --install-module \"$path\"").value.contains("- Done")
}
