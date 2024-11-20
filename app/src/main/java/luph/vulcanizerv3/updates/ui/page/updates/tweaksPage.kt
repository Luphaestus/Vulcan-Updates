package luph.vulcanizerv3.updates.ui.page.updates

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.ui.components.DisplayText
import luph.vulcanizerv3.updates.ui.components.RYScaffold
import luph.vulcanizerv3.updates.ui.components.Subheading
import luph.vulcanizerv3.updates.ui.components.SubheadingButton
import androidx.compose.foundation.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.ui.components.MarkDownContentTest
import luph.vulcanizerv3.updates.ui.components.TransitionBox
import luph.vulcanizerv3.updates.ui.page.showNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionCard(
    title: String,
    desc: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val showModal = remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp)
            .padding(bottom = 16.dp, top = 12.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                shape = ShapeDefaults.Medium
            )
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        FilledTonalButton(onClick = {
            showModal.value = true
        }) {
            Text(
                text = "Show changelog",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (showModal.value) {
        ModalBottomSheet(onDismissRequest = { showModal.value = false }) {
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                item {
                    MarkDownContentTest()
                }
            }
        }
    }
}

@Composable
fun ModUpdateCard(
    appName: String,
    version: String,
    description: String,
    appIcon: Int,
    navController: NavController,
    view: View,
    modifier: Modifier = Modifier,
) {

    TransitionBox(destination = "Mod Info", navController = navController, view = view) {
        Column(
            modifier = modifier
                .padding(end = 16.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    shape = ShapeDefaults.Medium
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically // Center the image vertically
            ) {
                Image(
                    painter = painterResource(id = appIcon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 16.dp)
                )
                Column(modifier = Modifier.padding(start = 0.dp)) {
                    Text(
                        text = appName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = version,
                        style = MaterialTheme.typography.bodySmall, // Use a smaller typography style
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) // Dimmer color
                    )
                }
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) // Dimmer color
            )
        }
    }
}

@Composable
fun UpdateCarousel(navController: NavController, view: View) {
    showNavigation.show.value = true

    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        LazyRow {
            items(10) { index ->
                ModUpdateCard(
                    appName = "Vulcan ROM $index",
                    version = "Version 1.0.0",
                    description = "Brief description of the updates",
                    appIcon = R.drawable.avatar_0,
                    navController = navController,
                    view = view
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatesPage(navController: NavController, view: View) {
    RYScaffold(
        content = {
            LazyColumn {
                item {
                    DisplayText(text = stringResource(R.string.updates_title), desc = "")
                }
                item {
                    Subheading(stringResource(R.string.core_title))

                    VersionCard(
                        title = "Vulcan ROM",
                        desc = "~1 Month ago",
                        onClick = {}
                    )

                    VersionCard(
                        title = "Vulcan Updates",
                        desc = "~1 Month ago",
                        onClick = {}
                    )

                    VersionCard(
                        title = "Play Integrity",
                        desc = "~1 Month ago",
                        onClick = {}
                    )
                }

                item {
                    SubheadingButton(stringResource(R.string.updates_title)) {}
                    UpdateCarousel(navController, view)
                }

                item {
                    SubheadingButton(stringResource(R.string.installed_title)) {}
                    UpdateCarousel(navController, view)
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
            }
            val bundle = Bundle().apply {
                putString("message", "Hello World")
            }
        }
    )
}
