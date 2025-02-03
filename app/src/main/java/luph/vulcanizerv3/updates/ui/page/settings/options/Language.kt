package luph.vulcanizerv3.updates.ui.page.settings.options


import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.crowdin.platform.Crowdin
import com.google.firebase.analytics.logEvent
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.ui.components.ClickableOverlay
import luph.vulcanizerv3.updates.ui.components.PageNAv
import luph.vulcanizerv3.updates.ui.components.SettingsElementBase
import luph.vulcanizerv3.updates.ui.page.showNavigation
import java.util.Locale


fun localeSelection(localeTag: String) {
    Log.e("hello", "Locale: $localeTag")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        MainActivity.applicationContext().getSystemService(LocaleManager::class.java).applicationLocales =
            LocaleList.forLanguageTags(localeTag)
    } else {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(localeTag)
        )
    }
}

fun getLocale(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        MainActivity.applicationContext().getSystemService(LocaleManager::class.java)
            .applicationLocales.get(0)?.toLanguageTag() ?: "en"
    } else {
        val locales = AppCompatDelegate.getApplicationLocales()
        if (locales.isEmpty) "en" else locales.get(0)!!.toLanguageTag()
    }
}

@Composable
fun LanguageItem(title: String, selected: Boolean, onClick: () -> Unit) {


    ClickableOverlay(
        modifier = Modifier
            .padding(4.dp)
            .background(
                MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                shape = MaterialTheme.shapes.large
            )
            .clip(MaterialTheme.shapes.large)
            .padding(horizontal = 16.dp),
        onClick = { onClick() }
    ) {
        SettingsElementBase(title) {
            RadioButton(
                selected = selected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun LanguageOption(
    navController: NavController = NavController(MainActivity.applicationContext()),
    view: View = MainActivity.instance!!.window.decorView
) {
    Crowdin.forceUpdate(MainActivity.applicationContext())
    showNavigation.show = false
    val selectedLanguage = remember { mutableStateOf(getLocale()) }

    val languages = mutableMapOf<String, String>()
    val currentLocal = Locale.getDefault()
    val defaultLanguage = Locale.forLanguageTag(Crowdin.getManifest()?.mapping?.get(0)?.split("/")?.get(2)?:"en")
    languages[defaultLanguage.language] = defaultLanguage.getDisplayName(defaultLanguage)
    Crowdin.getManifest()?.languages?.forEach {
        val locale = Locale.forLanguageTag(it);
        val languageName = locale.getDisplayName(locale)
        languages[it] = languageName
    }
    Log.e("crowdin", languages.toString())
    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 16.dp, end=16.dp, bottom=16.dp)
    ) {
        item {
            PageNAv(stringResource(R.string.language), navController)
        }

        items(languages.keys.toList()) { language ->
            val languageCountry = Crowdin.getSupportedLanguages()?.data?.find { it.data.id == language }?.data?.locale?:language
            LanguageItem(languages[language]?:language, currentLocal.toLanguageTag() == languageCountry) {
                selectedLanguage.value = languageCountry
                localeSelection(languageCountry)
                MainActivity.getFirebaseAnalytics().logEvent("language") {
                    param("language", languages[language] ?: language)
                }
            }
        }
    }
}
