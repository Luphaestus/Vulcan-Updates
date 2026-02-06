package luph.vulcanizerv3.updates.utils.modulemanager

import luph.vulcanizerv3.updates.utils.root.runRootShellCommand

fun uninstallModule(module: String): Boolean {
    return runRootShellCommand("rm -rf \"/data/adb/modules/$module\"").value.equals("")
}