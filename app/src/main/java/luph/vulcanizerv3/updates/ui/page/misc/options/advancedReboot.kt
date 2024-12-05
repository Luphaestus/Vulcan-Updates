package luph.vulcanizerv3.updates.ui.page.misc.options

import android.app.AlertDialog
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.ui.components.PageNAv
import luph.vulcanizerv3.updates.ui.components.info.UpdateAlert
import luph.vulcanizerv3.updates.ui.page.showNavigation
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceLocals

@Composable
@Preview(showBackground = true)
fun AdvancedReboot(
    navController: NavController = rememberNavController(),
    view: View? = null)
{
    showNavigation.show = false

    val bootLocations =
        arrayOf("system", "recovery", "download", "fastboot", "sideload", "bootloader")
    var selectedLocation by remember { mutableStateOf("") }

    if (selectedLocation.isNotEmpty())
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Confirm Reboot") },
            text = { Text("Are you sure you want to reboot to $selectedLocation?") },
            confirmButton = {
                Button(onClick = {
                    runRootShellCommand("reboot $selectedLocation", false)
                })
                { Text("Reboot") }
            },
            dismissButton = {
                TextButton(onClick = {selectedLocation = ""}){
                    Text("Cancel")
                }
            }
        )


    LazyColumn(
        Modifier.background(MaterialTheme.colorScheme.background).fillMaxSize().padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        item { PageNAv("Advanced Reboot", navController) }
        item {
            ProvidePreferenceLocals {
                bootLocations.forEach {
                    Preference(
                        title = { Text(it.capitalize()) },
                        onClick = {
                            selectedLocation = it
                        },
                        modifier = Modifier.padding(vertical = 8.dp).background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp), MaterialTheme.shapes.small),
                    )
                }
            }
        }
    }
}
