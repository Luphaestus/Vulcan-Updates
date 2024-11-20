package luph.vulcanizerv3.updates.ui.page.tweaks

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.ui.components.DisplayText
import luph.vulcanizerv3.updates.ui.components.RYScaffold
import luph.vulcanizerv3.updates.ui.components.Subheading
import luph.vulcanizerv3.updates.ui.components.SubheadingButton
import androidx.navigation.NavController
import luph.vulcanizerv3.updates.ui.page.updates.UpdateCarousel
import luph.vulcanizerv3.updates.ui.page.updates.VersionCard

@Composable
fun TweaksPage(navController: NavController, view: View) {
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
