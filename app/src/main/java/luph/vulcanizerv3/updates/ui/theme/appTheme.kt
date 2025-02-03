package luph.vulcanizerv3.updates.ui.theme

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.os.Build
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.google.firebase.analytics.logEvent
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.ui.theme.brown.darkScheme
import luph.vulcanizerv3.updates.ui.theme.brown.lightScheme
import luph.vulcanizerv3.updates.ui.theme.brown.typography
import luph.vulcanizerv3.updates.ui.theme.green.greenTheme
import luph.vulcanizerv3.updates.utils.getStandardAnimationSpeed

data class Theme(
    val dark: ColorScheme,
    val mediumContrastDark: ColorScheme,
    val highContrastDark: ColorScheme,

    val light: ColorScheme,
    val mediumContrastLight: ColorScheme,
    val highContrastLight: ColorScheme
)


fun isContrastAvailable(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
}

fun selectSchemeForContrast(isDark: Boolean, theme: Theme, contrast: Float): ColorScheme {
    var colorScheme = if (isDark) darkScheme else lightScheme
    if (isContrastAvailable()) {

        colorScheme = when (contrast) {
            in 0.0f..0.33f -> if (isDark)
                theme.dark else theme.light

            in 0.34f..0.66f -> if (isDark)
                theme.mediumContrastDark else theme.mediumContrastLight

            in 0.67f..1.0f -> if (isDark)
                theme.highContrastDark else theme.highContrastLight

            else -> if (isDark) theme.dark else theme.light
        }
        return colorScheme
    } else return colorScheme
}

@SuppressLint("NewApi")
fun getColourScheme(isDark: Boolean, theme: Theme?, contrast: Float?): ColorScheme {
    val darkTheme = isDark
    return when {
        theme == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = MainActivity.applicationContext()
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> selectSchemeForContrast(
            darkTheme,
            theme ?: greenTheme,
            contrast ?: (MainActivity.applicationContext()
                .getSystemService(Context.UI_MODE_SERVICE) as UiModeManager).contrast
        )
    }
}


fun changeStatusBarColor(view: View, newColor: Int) {
    val window = (view.context as Activity).window
    val currentColor = window.statusBarColor

    if (currentColor != newColor) {
        val colorAnimation = ObjectAnimator.ofArgb(currentColor, newColor)
        colorAnimation.duration = (getStandardAnimationSpeed()*0.7).toLong()
        colorAnimation.setEvaluator(ArgbEvaluator())
        colorAnimation.addUpdateListener { animator ->
            window.statusBarColor = animator.animatedValue as Int
        }
        colorAnimation.start()
    }
}

@Composable
fun ContrastAwareTheme(
    darkTheme: Boolean? = isSystemInDarkTheme(),
    theme: Theme? = null,
    contrast: Float? = null,
    content: @Composable() () -> Unit
) {
    MainActivity.getFirebaseAnalytics().logEvent("theme") {
        param("dark", darkTheme.toString())
        param("theme", theme.toString())
        param("contrast", contrast.toString())
    }

    val isDark = darkTheme ?: isSystemInDarkTheme()
    val colourScheme = getColourScheme(isDark, theme, contrast)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            changeStatusBarColor(view, colourScheme.primary.toArgb())
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isDark
        }
    }

    MaterialTheme(
        colorScheme = colourScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
