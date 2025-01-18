package luph.vulcanizerv3.updates.utils.root

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.collections.plusAssign
import kotlin.io.inputStream

fun runShellCommand(command: String, waitForCompletion: Boolean = true): State<Pair<String, Boolean>> {
    var output = ""
    var success = true
    try {
        val process = Runtime.getRuntime().exec(command)
        if (waitForCompletion) {
            val reader = process.inputStream.bufferedReader()
            val errorReader = process.errorStream.bufferedReader()
            val result = reader.readText() + errorReader.readText()
            output = result
            success = process.waitFor() == 0
            return mutableStateOf(Pair(output, success))
        } else {
            GlobalScope.launch(Dispatchers.IO) {
                val reader = process.inputStream.bufferedReader()
                val errorReader = process.errorStream.bufferedReader()
                reader.forEachLine { line ->
                    output += line + "\n"
                }
                errorReader.forEachLine { line ->
                    output += line + "\n"
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