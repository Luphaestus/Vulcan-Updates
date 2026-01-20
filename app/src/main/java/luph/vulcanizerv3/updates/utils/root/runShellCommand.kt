package luph.vulcanizerv3.updates.utils.root

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun runShellCommand(command: String, waitForCompletion: Boolean=true): State<String> {
    val output = mutableStateOf("")
    try {
        val process = Runtime.getRuntime().exec(command)
        if (waitForCompletion) {
            val reader = process.inputStream.bufferedReader()
            val result = reader.readText()
            output.value = result
            Log.e("runShellCommand", result)
        } else {
            GlobalScope.launch(Dispatchers.IO) {
                val reader = process.inputStream.bufferedReader()
                reader.forEachLine { line ->
                    output.value += line + "\n"
                    Log.e("runShellCommand", line)
                }
            }
        }
    } catch (e: Exception) {
        val error = e.toString()
        output.value = error
        Log.e("runShellCommand", error)
    }
    return output
}