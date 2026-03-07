package luph.vulcanizerv3.updates.utils

import android.util.Log
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand

fun installTwrpModule(modulePath: String) {
    val adjustededpath = modulePath.replace("user/0/","")
    runRootShellCommand("echo  install  /data$adjustededpath >> /cache/recovery/openrecoveryscript")
    runRootShellCommand("echo cmd rm -rf /data$adjustededpath >> /cache/recovery/openrecoveryscript")
}