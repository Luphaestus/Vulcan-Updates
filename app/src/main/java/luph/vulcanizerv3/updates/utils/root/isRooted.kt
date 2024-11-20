package luph.vulcanizerv3.updates.utils.root

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader




fun isRooted(): Boolean {
    val result = runRootShellCommand("echo test").value
    Log.e("isRooted", "Command result: $result")
    return !result.contains("su")
}