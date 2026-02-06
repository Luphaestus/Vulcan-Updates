package luph.vulcanizerv3.updates

import android.Manifest
import android.app.ComponentCaller
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.ketch.Ketch
import com.ketch.NotificationConfig
import luph.vulcanizerv3.updates.data.ThemeManager
import luph.vulcanizerv3.updates.ui.VulcanApp
import luph.vulcanizerv3.updates.ui.theme.ContrastAwareTheme
import android.content.Intent
import android.net.Uri
import luph.vulcanizerv3.updates.data.TELEGRAM_BOT_API
import luph.vulcanizerv3.updates.data.TELEGRAM_FEEDBACK_CHANNEL
import luph.vulcanizerv3.updates.utils.download.getHelpList
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand
import luph.vulcanizerv3.updates.utils.telegram.postTelegramMessage
import org.json.JSONArray


class MainActivity : ComponentActivity() {
    private lateinit var ketch: Ketch
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var openMod: String? = null

    companion object {
        private const val REQUEST_CODE_POST_NOTIFICATIONS = 1
        var instance: MainActivity? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        fun getKetch(): Ketch {
            return instance!!.ketch
        }

        fun getFirebaseAnalytics(): FirebaseAnalytics {
            return instance!!.firebaseAnalytics
        }

        fun getOpenMod(): String? {
            return instance!!.openMod
        }

        fun resetOpenMod() {
            instance!!.openMod = null
        }
    }

    init {
        instance = this
    }


    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        ketch = Ketch.builder().setNotificationConfig(
            config = NotificationConfig(
                enabled = true,
                smallIcon = R.drawable.logo
            )
        ).build(this)
        setContent {
            ContrastAwareTheme(
                ThemeManager.darkTheme,
                ThemeManager.getThemeTheme(),
                ThemeManager.contrast
            ) {
                val windowSize = calculateWindowSizeClass(this)
                VulcanApp(
                    windowSize = windowSize,
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            }
        }
    }
}
