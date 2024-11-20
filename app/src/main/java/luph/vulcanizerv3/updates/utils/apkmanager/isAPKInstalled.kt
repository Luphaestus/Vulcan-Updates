package luph.vulcanizerv3.updates.utils.apkmanager

import android.content.pm.PackageManager
import luph.vulcanizerv3.updates.MainActivity

fun isAPKInstalled(packageName: String): Boolean {
    val packageManager: PackageManager = MainActivity.applicationContext().packageManager
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}