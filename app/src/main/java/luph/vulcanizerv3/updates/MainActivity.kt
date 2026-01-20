package luph.vulcanizerv3.updates

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import luph.vulcanizerv3.updates.ui.theme.ContrastAwareTheme
import com.google.accompanist.adaptive.calculateDisplayFeatures
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ketch.Ketch
import com.ketch.NotificationConfig
import luph.vulcanizerv3.updates.ui.VulcanApp

class MainActivity : ComponentActivity() {
    private lateinit var ketch: Ketch

    companion object {
        private const val REQUEST_CODE_POST_NOTIFICATIONS = 1
        var instance: MainActivity? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
        fun getKetch(): Ketch {
            return instance!!.ketch
        }
    }

    init {
        instance = this
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen().apply {}
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ketch = Ketch.builder().setNotificationConfig(
            config = NotificationConfig(
                enabled = true,
                smallIcon = R.drawable.ic_launcher_foreground
            )
        ).build(this)
        setContent {
            ContrastAwareTheme {
                val windowSize = calculateWindowSizeClass(this)
                val displayFeatures = calculateDisplayFeatures(this)
                VulcanApp(
                    windowSize = windowSize,
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
            }
        }
    }




}
