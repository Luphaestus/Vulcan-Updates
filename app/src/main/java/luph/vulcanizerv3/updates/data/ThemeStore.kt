package luph.vulcanizerv3.updates.data

import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import luph.vulcanizerv3.updates.ui.theme.Theme
import luph.vulcanizerv3.updates.ui.theme.blue.blueTheme
import luph.vulcanizerv3.updates.ui.theme.brown.brownTheme
import luph.vulcanizerv3.updates.ui.theme.green.greenTheme
import luph.vulcanizerv3.updates.ui.theme.red.redTheme
import luph.vulcanizerv3.updates.ui.theme.red2.tmpTheme
import luph.vulcanizerv3.updates.ui.theme.yellow.yellowTheme

val Themes = mapOf(
    "blue" to blueTheme,
    "brown" to brownTheme,
    "green" to greenTheme,
    "red" to redTheme,
    "yellow" to yellowTheme,
    "red2" to tmpTheme
)


@Serializable
data class ThemeStore(
    var theme: String? = null,
    var darkTheme: Boolean? = null,
    var contrast: Float? = null
)

@Serializable
object ThemeManager {
    private var _darkTheme = mutableStateOf<Boolean?>(false)
    private var _theme = mutableStateOf<String?>(null)
    private var _contrast = mutableStateOf<Float?>(null)

    private val file = "Theme.dat"
    private val sm = SerializableManager<String>()
    private var themeStore: ThemeStore? = null

    init {
        themeStore = Json.decodeFromString(sm.load(file) ?: "{}")
        if (themeStore != null) {
            _theme.value = themeStore!!.theme
            _darkTheme.value = themeStore!!.darkTheme
            _contrast.value = themeStore!!.contrast
        }
    }

    fun save() {
        sm.save(file, Json.encodeToString(themeStore))
    }

    var darkTheme: Boolean?
        get() = _darkTheme.value
        set(value) {
            _darkTheme.value = value
            themeStore!!.darkTheme = value
            save()
        }

    var contrast: Float?
        get() = _contrast.value
        set(value) {
            _contrast.value = value
            themeStore!!.contrast = value
            save()
        }

    var theme: String?
        get() = _theme.value
        set(value) {
            _theme.value = value
            themeStore!!.theme = value
            save()
        }

    fun getThemeTheme(): Theme? {
        return Themes[_theme.value]
    }


}