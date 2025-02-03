package luph.vulcanizerv3.updates.ui.page.settings.options


import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.LocaleList
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.ui.components.ClickableOverlay
import luph.vulcanizerv3.updates.ui.components.PageNAv
import luph.vulcanizerv3.updates.ui.components.SettingsElementBase
import luph.vulcanizerv3.updates.ui.page.showNavigation
import java.util.Locale
import kotlin.random.Random

fun openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    MainActivity.applicationContext().startActivity(intent)
}

@Composable
fun personAck(name: String, role: String, pfp: Int?=null, modifier: Modifier = Modifier) {
    Row(modifier = modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                shape = ShapeDefaults.Large)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        if (pfp != null)
            Image(
                painter = painterResource(id = pfp),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        else {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.split(" ").map { it.first() }.joinToString(""),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(text = name, style = MaterialTheme.typography.titleMedium)
            Text(text = role, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun AcknowledgementOption(
    navController: NavController = NavController(MainActivity.applicationContext()),
    view: View = MainActivity.instance!!.window.decorView
) {
    showNavigation.show = false

    LazyColumn(
        Modifier.background(MaterialTheme.colorScheme.background).fillMaxSize().padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        item { PageNAv(stringResource(R.string.acknowledgments_title), navController) }

        item {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "VulcanROM links",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        item {
            ClickableOverlay(onClick = {openUrl("https://t.me/note20updates") }) {
                personAck(
                    "VulcanROM Announcement channel",
                    "Progress updates and future releases",
                    R.drawable.vulcanannoince
                )
            }
        }
        item {
            ClickableOverlay(onClick = {openUrl("https://t.me/VulcanROM") }) {
                personAck(
                    "VulcanROM Support group",
                    "For help, feedback, and general samsung discussions",
                    R.drawable.vulcandiscussion
                )
            }
        }

        item {
            ClickableOverlay(onClick = {openUrl("https://xdaforums.com/t/vulcan-rom-one-ui-6-port.4658874/") }) {
                personAck(
                    "VulcanROM XDA thread",
                    "",
                    R.drawable.xda
                )
            }
        }

        item {
            ClickableOverlay(onClick = {openUrl("https://www.paypal.com/donate/?hosted_button_id=XNSUWAEZ9WVJA") }) {
                personAck(
                    "Sponsor VulcanROM",
                    "Help me keep the project free and open-source",
                    R.drawable.paypal
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Core contributors",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        item {
            personAck(
                "Luphaestus",
                "Developer of Vulcan ROM and Vulcan Updates",
                R.drawable.luph
            )
        }
        item { personAck("ExtremeXT", "For his Exynos 990 & s20x fixes", R.drawable.extreme) }
        item { personAck("Igor", "\uD83D\uDC39", R.drawable.igor) }
        item { personAck("BlackMesa", "Misc framework patches", R.drawable.salvo) }

        item {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Contributors",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        item { personAck("Oskar", "Tester ● Translator ● ODIN Pack ● FJORD GAPER", R.drawable.oskar) }
        item { personAck("Mesazane", "Tester ● Translator ● FJORD GAPER", R.drawable.mez) }
        item { personAck("Emulond", "Tester ● Prop Prodigy ● Keybox King ● Idea Innovator", R.drawable.emulond) }
        item { personAck("dupa z lasu", "Tester ● Translator", R.drawable.dzl) }
        item { personAck("Ciprian Dinca", "Tester ● Translator ● OneUI 7 apps ● Accessibility Advocate") }
        item { personAck("Jaola Tymon", "Tester") }
        item { personAck("Vlad", "Tester") }
        item { personAck("O C", "Tester ● Translator") }
        item { personAck("Mirel", "Tester") }
        item { personAck("BlueMech", "Tester", R.drawable.bm) }
        item { personAck("Walter White / Sussy Baka", "Tester", R.drawable.walter) }
        item { personAck("Razr1804", "Tester") }
        item { personAck("Denni", "Tester") }
        item { personAck("Javivi", "Tester", R.drawable.javvi) }
        item { personAck("Abode", "Tester", R.drawable.abode) }
        item { personAck("Leon", "Tester", R.drawable.leon) }
        item { personAck("ƬƦΘレ乇メ ༒ \uD83D\uDC51⃤", "Tester", R.drawable.troll) }
        item { personAck("Rick Sanchez", "Translator", R.drawable.rick) }
        item { personAck("El Vettorato", "Translator", R.drawable.ttorato) }
        item { personAck("Emre Kesin", "Translator", R.drawable.kesin) }
        item { personAck("Boyan", "Translator ● Gwałt w fiordzie", R.drawable.boyan) }

        item {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Libraries",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        item {
            ClickableOverlay(onClick = {openUrl("https://github.com/fornewid/photo-compose") }) {
                personAck(
                    "Photo-compose",
                    "⚖️ Apache-2.0 license",
                    R.drawable.photocompose
                )
            }
        }
        item {
            ClickableOverlay(onClick = {openUrl("https://github.com/coil-kt/coil") }) {
                personAck(
                    "Coil",
                    "⚖️ Apache-2.0 license",
                    R.drawable.coil
                )
            }
        }
        item {
            ClickableOverlay(onClick = {openUrl("https://github.com/ktorio/ktor") }) {
                personAck(
                    "Ktor",
                    "⚖️ 4CC0-1.0 license",
                    R.drawable.ktor
                )
            }
        }
        item {
            ClickableOverlay(onClick = {openUrl("https://github.com/khushpanchal/Ketch") }) {
                personAck(
                    "Ketch",
                    "⚖️ Apache-2.0 license",
                    R.drawable.ketch
                )
            }
        }
    }
}