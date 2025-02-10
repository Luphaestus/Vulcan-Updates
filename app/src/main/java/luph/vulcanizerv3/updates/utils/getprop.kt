package luph.vulcanizerv3.updates.utils

import luph.vulcanizerv3.updates.utils.root.runShellCommand

fun getprop(prop: String): String {
    return runShellCommand("getprop $prop").value.first
}