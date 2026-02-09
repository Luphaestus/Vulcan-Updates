
import android.util.Log
import io.github.g00fy2.versioncompare.Version
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.utils.apkmanager.getAPKVersion

enum class APKUpdateStatus {
    NO_UPDATE_NEEDED,
    UPDATE_NEEDED,
    NOT_INSTALLED,
    NOT_INSTALLED_BY_VULCAN,
}

fun getInstallerSource(packageName: String): String? {
    val context = MainActivity.applicationContext()
    val packageManager = context.packageManager

    return try {
        val installerPackageName = packageManager.getInstallerPackageName(packageName)
        installerPackageName ?: "Unknown"
    } catch (e: Exception) {
        e.printStackTrace()
        "Error retrieving installer"
    }
}

fun compareVersionNames(installedVersion: String, newVersion: String): Boolean {
    val adjustedInstalledVersion = installedVersion.dropWhile { !it.isDigit() }
    val adjustedNewVersion = newVersion.dropWhile { !it.isDigit() }

    return  Version(adjustedInstalledVersion) < Version(adjustedNewVersion)
}

fun getAPKUpdateStatus(packageName: String, newVersion: String): APKUpdateStatus {

    val installedVersion: String = getAPKVersion(packageName) ?: return APKUpdateStatus.NOT_INSTALLED
    return if (compareVersionNames(installedVersion, newVersion)) {
        APKUpdateStatus.UPDATE_NEEDED
    } else {
        APKUpdateStatus.NO_UPDATE_NEEDED
    }
}



