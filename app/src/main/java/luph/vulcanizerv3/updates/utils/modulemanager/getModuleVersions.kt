package luph.vulcanizerv3.updates.utils.modulemanager

import luph.vulcanizerv3.updates.utils.root.runRootShellCommand

fun getModuleVersion(module: String): String? {
    val result = runRootShellCommand("cat \"/data/adb/modules/$module/module.prop\"").value
    return result.split("\n").find { it.startsWith("version=") }?.substringAfter("version=")
}

fun getModuleVersions(moduleList: List<String>): Map<String, String> {
    val moduleVersions = mutableMapOf<String, String>()
    for (module in moduleList) {
        val version = getModuleVersion(module)
        if (version != null) {
            moduleVersions[module] = version
        }
    }
    return moduleVersions
}