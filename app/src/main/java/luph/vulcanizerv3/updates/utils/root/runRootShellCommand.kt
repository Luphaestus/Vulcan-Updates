package luph.vulcanizerv3.updates.utils.root

import androidx.compose.runtime.State

fun runRootShellCommand(command: String, waitForCompletion: Boolean = true): State<String> {
    return runShellCommand("su -c $command", waitForCompletion)
}