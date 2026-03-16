package luph.vulcanizerv3.updates.utils.apkmanager

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.utils.root.ROOTStatus
import luph.vulcanizerv3.updates.utils.root.getROOTStatus
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand
import ru.solrudev.ackpine.installer.PackageInstaller
import ru.solrudev.ackpine.session.await
import java.io.File
import kotlin.coroutines.cancellation.CancellationException
import ru.solrudev.ackpine.installer.createSession
import ru.solrudev.ackpine.session.Session
import ru.solrudev.ackpine.session.parameters.Confirmation

suspend fun installAPKNoRoot(path: String): Boolean {
    var context = MainActivity.applicationContext()
    Log.e("installAPKNoRoot", "no root")
    val file = File(path)
    if (!file.exists()) {
        Toast.makeText(MainActivity.applicationContext(),
            MainActivity.applicationContext().getString(R.string.file_does_not_exist), Toast.LENGTH_SHORT).show()
        return false
    }

    return try {
        val uri: Uri = FileProvider.getUriForFile(
            MainActivity.applicationContext(),
            "luph.vulcanizerv3.updates.fileprovider",
            file
        )
        val packageInstaller = PackageInstaller.getInstance(context)
        when (val result = packageInstaller.createSession(uri) {
            confirmation = Confirmation.IMMEDIATE
        }.await()) {
            Session.State.Succeeded -> true
            is Session.State.Failed -> false
        }
    } catch (e: Exception) {
        Log.e("installAPKNoRoot", e.toString())
        false
    }
}

fun installAPKRoot(path: String): Boolean {
    Log.e("installAPKRoot", "root")
    val file = File(path)
    Log.e("installAPKRoot", file.toString())
    if (!file.exists()) {
        return false
    }
    return runRootShellCommand("pm install -r \"$path\"").value.second
}

suspend fun installAPK(path: String): Boolean {
    Log.e("pta", path)
    return if (ROOTStatus.NONE != getROOTStatus()) {
        installAPKRoot(path)
    } else {
        installAPKNoRoot(path)
    }
}
