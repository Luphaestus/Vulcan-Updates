package luph.vulcanizerv3.updates.utils.modulemanager

import luph.vulcanizerv3.updates.utils.root.runRootShellCommand

fun getModuleInstalledList(): List<String> {
    val result = runRootShellCommand("ls -1 /data/adb/modules/").value
    return result.split("\n")
}