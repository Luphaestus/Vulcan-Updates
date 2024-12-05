package luph.vulcanizerv3.updates.utils.apkmanager

import android.content.Intent
import android.net.Uri
import android.util.Log
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.utils.root.ROOTStatus
import luph.vulcanizerv3.updates.utils.root.getROOTStatus
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand

fun uninstallAPKNoRoot(packageName: String): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:$packageName")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        MainActivity.applicationContext().startActivity(intent)
        true
    } catch (e: Exception) {
        Log.e("UninstallAPK", e.toString())
        false
    }
}

fun uninstallApkRoot(packageName: String): Boolean {
    return runRootShellCommand("pm uninstall $packageName").value.second
}

fun uninstallAPK(packageName: String): Boolean {
    return if (getROOTStatus() != ROOTStatus.NONE) {
        uninstallApkRoot(packageName)
    } else {
        uninstallAPKNoRoot(packageName)
    }
}