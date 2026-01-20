package luph.vulcanizerv3.updates.utils.apkmanager

import luph.vulcanizerv3.updates.MainActivity

fun openAPK (packageName : String) : Boolean {
    return try {
        val intent = MainActivity.applicationContext().packageManager.getLaunchIntentForPackage(packageName)
        MainActivity.applicationContext().startActivity(intent)
        true
    } catch (e: Exception) {
        false
    }
}