package luph.vulcanizerv3.updates.utils.apkmanager

import android.content.pm.PackageManager
import luph.vulcanizerv3.updates.MainActivity

fun getAPKVersion(packageName: String): String? {
    if (!isAPKInstalled(packageName)) return null

    val packageManager: PackageManager = MainActivity.applicationContext().packageManager
    val packageInfo = packageManager.getPackageInfo(packageName, 0)
    return packageInfo.versionName
}