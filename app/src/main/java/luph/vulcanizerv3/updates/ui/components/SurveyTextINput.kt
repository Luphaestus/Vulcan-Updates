package luph.vulcanizerv3.updates.ui.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import luph.vulcanizerv3.updates.data.Options
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

fun checkRegexMapNice(regexMap: Map<String, Regex>, value: String): String? {
    for ((returnString, regex) in regexMap) {
        if (!regex.matches(value)) {
            return returnString
        }
    }
    return null
}


@Composable
fun SurveyTextINput(
    value: String,
    options: Options.TextInput,
    onValueChange: (String) -> Unit = {},
    onValidationError: (() -> Unit)? = null
) {
    var isError by remember { mutableStateOf(false) }
    var supportingText by remember { mutableStateOf("") }


    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            val errorString = checkRegexMapNice(options.regex, newValue)
            if (errorString == null) {
                isError = false
                onValueChange(newValue)
            } else {
                isError = true
                supportingText = errorString
                onValueChange(newValue)
                onValidationError?.invoke()
            }
        },
        isError = isError,
        supportingText = {
            if (isError) {
                Text(text = supportingText, color = MaterialTheme.colorScheme.error)
            }},
        placeholder = {
            Text(text = options.hint)
        },
        leadingIcon = options.icon?.let { { Icon(imageVector = it, contentDescription = null) } },
        singleLine = if (options.lines == 1) true else false,

        modifier = Modifier
            .fillMaxWidth().heightIn(max=550.dp),
        shape = MaterialTheme.shapes.small,
    )
}

