package luph.vulcanizerv3.updates.utils.apkmanager

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.utils.root.ROOTStatus
import luph.vulcanizerv3.updates.utils.root.getROOTStatus
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand
import java.io.File

fun installAPKNoRoot(path: String): Boolean {
    val file = File(path)
    if (!file.exists()) {
        Toast.makeText(MainActivity.applicationContext(), "File does not exist", Toast.LENGTH_SHORT).show()
        return false
    }
    return try {
        val uri: Uri = FileProvider.getUriForFile(
            MainActivity.applicationContext(),
            "luph.vulcanizerv3.updates.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        MainActivity.applicationContext().startActivity(intent)
        true
    } catch (e: Exception) {
        Log.e("installAPKNoRoot", e.toString())
        false
    }
}

fun installAPKRoot(path: String): Boolean {
    val file = File(path)
    Log.e("installAPKRoot", file.toString())
    if (!file.exists()) {
        return false
    }
    return runRootShellCommand("pm install -r \"$path\"").value.second
}

fun installAPK(path: String): Boolean {
    return if (ROOTStatus.NONE != getROOTStatus()) {
        installAPKRoot(path)
    } else {
        installAPKNoRoot(path)
    }
}
