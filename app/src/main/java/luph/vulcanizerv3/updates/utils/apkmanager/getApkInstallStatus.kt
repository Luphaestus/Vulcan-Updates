
import android.util.Log
import luph.vulcanizerv3.updates.utils.apkmanager.getAPKVersion

enum class APKUpdateStatus {
    NO_UPDATE_NEEDED,
    UPDATE_NEEDED,
    NOT_INSTALLED
}

fun compareVersionNames(installedVersion: String, newVersion: String): Boolean {
    val adjustedInstalledVersion = installedVersion.trimStart('v', 'V').replace(".", "")
    val adjustedNewVersion = newVersion.trimStart('v', 'V').replace(".", "")

    return adjustedInstalledVersion != adjustedNewVersion
}

fun getAPKUpdateStatus(packageName: String, newVersion: String): APKUpdateStatus {
    val installedVersion: String = getAPKVersion(packageName) ?: return APKUpdateStatus.NOT_INSTALLED
    return if (compareVersionNames(installedVersion, newVersion)) {
        APKUpdateStatus.UPDATE_NEEDED
    } else {
        APKUpdateStatus.NO_UPDATE_NEEDED
    }
}