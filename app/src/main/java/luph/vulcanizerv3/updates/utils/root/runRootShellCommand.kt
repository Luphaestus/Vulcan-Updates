package luph.vulcanizerv3.updates.utils.root

import android.util.Log
import androidx.compose.runtime.State

fun runRootShellCommand(command: String, waitForCompletion: Boolean = true): State<Pair<String, Boolean>> {
    val result =  runShellCommand("su -c $command", waitForCompletion)
    if (!result.value.second) {
        Log.e("runRootShellCommand", "Failed to run command: $command output: ${result.value.first}")
    }
    return result
}