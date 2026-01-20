package luph.vulcanizerv3.updates.ui.page.settings.options


import android.app.Activity
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun ColorAndStyleOption(navController: NavController, view: View) {
    val selectedOption = remember { mutableStateOf("System") }
    val darkTheme = selectedOption.value == "Dark"
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Choose a theme:")

        RadioButtonWithLabel(
            label = "System",
            selected = selectedOption.value == "System",
            onClick = {
                selectedOption.value = "System"
                (context as? Activity)?.recreate()
            }
        )

        RadioButtonWithLabel(
            label = "Light",
            selected = selectedOption.value == "Light",
            onClick = {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        RadioButtonWithLabel(
            label = "Dark",
            selected = selectedOption.value == "Dark",
            onClick = {
                selectedOption.value = "Dark"
                (context as? Activity)?.recreate()
            }
        )
    }

}


@Composable
fun RadioButtonWithLabel(label: String, selected: Boolean, onClick: () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}