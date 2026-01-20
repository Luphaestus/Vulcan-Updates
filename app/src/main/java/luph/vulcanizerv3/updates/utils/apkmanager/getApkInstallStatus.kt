import luph.vulcanizerv3.updates.utils.apkmanager.getAPKVersion

enum class APKUpdateStatus {
    NO_UPDATE_NEEDED,
    UPDATE_NEEDED,
    NOT_INSTALLED
}

fun getAPKUpdateStatus(packageName: String, newVersion: String): APKUpdateStatus {
    val installedVersion: String = getAPKVersion(packageName) ?: return APKUpdateStatus.NOT_INSTALLED
    return if (installedVersion != newVersion) {
        APKUpdateStatus.UPDATE_NEEDED
    } else {
        APKUpdateStatus.NO_UPDATE_NEEDED
    }
}