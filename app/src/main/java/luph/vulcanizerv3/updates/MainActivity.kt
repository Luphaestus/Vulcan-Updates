package luph.vulcanizerv3.updates

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ComponentCaller
import android.content.Context
import android.content.ContextWrapper
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
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.BaseContextWrappingDelegate
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.core.app.OnNewIntentProvider
import androidx.work.NetworkType
import com.crowdin.platform.Crowdin
import com.crowdin.platform.CrowdinConfig
import com.crowdin.platform.util.getLocaleForLanguageCode
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import luph.vulcanizerv3.updates.data.TELEGRAM_BOT_API
import luph.vulcanizerv3.updates.data.TELEGRAM_FEEDBACK_CHANNEL
import luph.vulcanizerv3.updates.ui.components.info.UpdateAlert
import luph.vulcanizerv3.updates.ui.page.settings.options.getLocale
import luph.vulcanizerv3.updates.ui.page.settings.options.localeSelection
import luph.vulcanizerv3.updates.utils.download.getHelpList
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand
import luph.vulcanizerv3.updates.utils.telegram.postTelegramMessage
import org.json.JSONArray
import java.util.Locale
import kotlin.collections.remove


class MainActivity : AppCompatActivity() {

    private lateinit var ketch: Ketch
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun getDelegate() = BaseContextWrappingDelegate(super.getDelegate())

    fun Context.updateLocale(newLocaleCode: String): Context {
        Log.e("hello", "Locale: $newLocaleCode")
        val newLocale = newLocaleCode.getLocaleForLanguageCode()
        Locale.setDefault(newLocale)
        val configuration = Configuration(this.resources.configuration)
        configuration.setLocale(newLocale)
        return this.createConfigurationContext(configuration)
    }
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextWrapper(newBase.updateLocale(Locale.getDefault().toLanguageTag())))
    }

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

        fun getInstance(): Activity {
            return instance!!
        }

    }

    init {
        instance = this
    }


    @SuppressLint("InlinedApi")
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        Log.e("hello", "L3ocale: ${getLocale()}")
        Crowdin.init(this,
            CrowdinConfig.Builder()
                .withDistributionHash("705a4bb530c8bbe03ca1219wwip")
                .build())

        enableEdgeToEdge()
        firebaseAnalytics = Firebase.analytics
        ketch = Ketch.builder().setNotificationConfig(
            config = NotificationConfig(
                enabled = true,
                smallIcon = R.drawable.logo
            )
        ).build(this)



        var showNotif = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showNotif = true
            }
        }



        setContent {
            var showNotifAlert = remember { mutableStateOf(showNotif) }
            UpdateAlert(stringResource(R.string.please_enable_notifications),
                stringResource(R.string.notification_request_desc), showNotifAlert, "Deny", positiveClickText = "Allow", positiveClick = {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            })
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
    }


}
