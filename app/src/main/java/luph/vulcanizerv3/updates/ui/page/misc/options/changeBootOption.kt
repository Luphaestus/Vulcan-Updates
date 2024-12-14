package luph.vulcanizerv3.updates.ui.page.misc.options

import android.app.AlertDialog
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.github.theapache64.twyper.Twyper
import com.github.theapache64.twyper.rememberTwyperController
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.ui.components.PageNAv
import luph.vulcanizerv3.updates.ui.components.info.UpdateAlert
import luph.vulcanizerv3.updates.ui.page.settings.options.slightlyDeemphasizedAlpha
import luph.vulcanizerv3.updates.ui.page.showNavigation
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceLocals

@Composable
@Preview(showBackground = true)
fun ChangeBootOption(
    navController: NavController = rememberNavController(),
    view: View? = null) {
    showNavigation.show = false

    Scaffold(
        bottomBar = {
            Row {
                OutlinedButton(
                    onClick = {
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
//                    enabled = pageNumber > -sharedForm.size
                ) {
                    Text("Previous")
                }
                Button(

                    onClick = {
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                ) {
                    Text("Next")
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(bottom = padding.calculateBottomPadding()+8.dp).padding(horizontal = 16.dp)) {
            PageNAv("Boot Animation", navController)

            Text(
                "currentQuestion.question",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 10.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(color = MaterialTheme.colorScheme.inverseOnSurface)
                    .padding(20.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha),
            )


            val items = remember { mutableStateListOf(*('A'..'Z').toList().toTypedArray()) }

            val twyperController = rememberTwyperController()
            Twyper(
                items = items,
                twyperController = twyperController, // optional
                onItemRemoved = { item, direction ->
                    println("Item removed: $item -> $direction")
                    items.remove(item)
                },
                onEmpty = { // invoked when the stack is empty
                    println("End reached")
                }
            ) { item ->
                Box(Modifier.fillMaxSize()) {
                    Text(
                        item.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}