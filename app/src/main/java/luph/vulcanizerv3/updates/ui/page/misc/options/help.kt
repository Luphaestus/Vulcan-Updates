package luph.vulcanizerv3.updates.ui.page.misc.options


import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.ModDetailsStore
import luph.vulcanizerv3.updates.ui.components.ClickableOverlay
import luph.vulcanizerv3.updates.ui.components.MarkDownContentTest
import luph.vulcanizerv3.updates.ui.components.MarkdownGenerator
import luph.vulcanizerv3.updates.ui.components.PageNAv
import luph.vulcanizerv3.updates.ui.components.RandomIndicator
import luph.vulcanizerv3.updates.ui.page.OpenRoute
import luph.vulcanizerv3.updates.ui.page.RouteParams
import luph.vulcanizerv3.updates.ui.page.showNavigation
import luph.vulcanizerv3.updates.utils.download.getRemoteText
import kotlin.random.Random


data class helpItem(val name: String)


@Composable
@Preview(showBackground = true)
fun ShowHelp(navController: NavController = rememberNavController(),
             view: View? = null
) {
    showNavigation.show = false
    var remoteText by remember { mutableStateOf("") }

  val name = remember {
    if (view == null) "examplnjnjnjnjnj dssddsfdsfsdfe helpddd" else RouteParams.pop(helpItem::class.java)?.name
        ?: run {
            navController.popBackStack()
            return@remember ""
        }
}

    LaunchedEffect(key1 = name) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.e("Help", MainActivity.applicationContext()
                .getString(R.string.github_link) + "help/" + name)
            remoteText = getRemoteText(
                MainActivity.applicationContext()
                    .getString(R.string.github_link) + "help/" + name
            )!!
        }
    }

    if (view != null && remoteText.isEmpty()) {
        Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background) .padding(start = 16.dp, end=16.dp, bottom=16.dp)) {
            PageNAv(name.substring(0, name.length - 3), navController)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                RandomIndicator()
            }
        }
    } else {
        LazyColumn(
            Modifier.background(MaterialTheme.colorScheme.background).fillMaxSize().padding(start = 16.dp, end=16.dp, bottom=16.dp)
        ) {
            item { PageNAv(name.substring(0, name.length - 3), navController) }

            item {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    if (view == null) {
                        MarkDownContentTest()
                    } else {
                        if (remoteText.isNotEmpty()) {
                            MarkdownGenerator(
                                remoteText
                            )
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun HelpItemButton(name: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                shape = ShapeDefaults.Large)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically) {

        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            Text(text = name, style = MaterialTheme.typography.titleMedium)
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun HelpOption(
    navController: NavController = rememberNavController(),
    view: View? = null
) {
    showNavigation.show = false
    PullToRefreshBox(ModDetailsStore.isUpdating().value, { ModDetailsStore.refresh() }) {
        LazyColumn(
            Modifier.background(MaterialTheme.colorScheme.background).fillMaxSize().padding(start = 16.dp, end=16.dp, bottom=16.dp)
        ) {
            item { PageNAv("Help Documentation", navController) }

            ModDetailsStore.getHelpList().value.forEach { helpName ->
                item {
                    ClickableOverlay(onClick = {

                        RouteParams.push(helpItem(helpName))
                        OpenRoute("ShowHelp", navController, view!!, fadeIn(), fadeOut())
                    }) {
                        HelpItemButton(
                            helpName.substring(0, helpName.length - 3),
                            Modifier
                        )
                    }
                }
            }
        }
    }
}