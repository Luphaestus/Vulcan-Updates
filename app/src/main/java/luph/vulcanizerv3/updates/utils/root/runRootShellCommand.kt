package luph.vulcanizerv3.updates.utils.root

import androidx.compose.runtime.State

fun runRootShellCommand(command: String, waitForCompletion: Boolean = true): State<Pair<String, Boolean>> {
    return runShellCommand("su -c $command", waitForCompletion)
}