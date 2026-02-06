package luph.vulcanizerv3.updates.utils.root

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun runShellCommand(command: String, waitForCompletion: Boolean = true): State<Pair<String, Boolean>> {
    var output = ""
    var success = true
    try {
        val process = Runtime.getRuntime().exec(command)
        if (waitForCompletion) {
            val reader = process.inputStream.bufferedReader()
            val result = reader.readText()
            output = result
            Log.e("runShellCommand", result)
            success = process.waitFor() == 0
        } else {
            GlobalScope.launch(Dispatchers.IO) {
                val reader = process.inputStream.bufferedReader()
                reader.forEachLine { line ->
                    output += line + "\n"
                    Log.e("runShellCommand", line)
                }
                success = process.waitFor() == 0
            }
        }
    } catch (e: Exception) {
        val error = e.toString()
        output = error
        Log.e("runShellCommand", error)
        success = false
    }
    return mutableStateOf(Pair(output, success))
}