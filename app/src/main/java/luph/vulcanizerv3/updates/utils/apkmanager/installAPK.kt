package luph.vulcanizerv3.updates.utils.apkmanager

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.utils.root.isRooted
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand
import java.io.File

fun installAPKNoRoot(path: String): Boolean {
    val file = File(path)
    if (!file.exists()) {
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
        false
    }
}

fun installAPKRoot(path: String): Boolean {
    val file = File(path)
    if (!file.exists()) {
        return false
    }
    return runRootShellCommand("pm install -r $path").value.contains("Success")
}

fun installAPK(path: String): Boolean {
    return if (isRooted()) {
        installAPKRoot(path)
    } else {
        installAPKNoRoot(path)
    }
}
