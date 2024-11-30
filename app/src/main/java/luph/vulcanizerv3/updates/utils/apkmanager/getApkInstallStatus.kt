
import android.util.Log
import luph.vulcanizerv3.updates.utils.apkmanager.getAPKVersion

enum class APKUpdateStatus {
    NO_UPDATE_NEEDED,
    UPDATE_NEEDED,
    NOT_INSTALLED
}

fun getAPKUpdateStatus(packageName: String, newVersion: String): APKUpdateStatus {
    var installedVersion: String =
        getAPKVersion(packageName) ?: return APKUpdateStatus.NOT_INSTALLED
    if (newVersion.startsWith("v") && !installedVersion.startsWith("v")) installedVersion = "v$installedVersion"
    if (newVersion.startsWith("V") && !installedVersion.startsWith("V")) installedVersion = "V$installedVersion"

    return if (installedVersion != newVersion) {
        Log.e("APKUpdateStatus", "Installed version: $installedVersion, New version: $newVersion")
        APKUpdateStatus.UPDATE_NEEDED
    } else {
        APKUpdateStatus.NO_UPDATE_NEEDED
    }
}