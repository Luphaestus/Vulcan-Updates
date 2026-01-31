package luph.vulcanizerv3.updates.utils.apkmanager

import android.content.Context
import android.content.pm.PackageManager
import luph.vulcanizerv3.updates.MainActivity

fun getAppVersion(context: Context = MainActivity.applicationContext()): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName?: "Unknown"
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
}