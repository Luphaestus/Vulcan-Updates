package luph.vulcanizerv3.updates.ui.page.misc.options

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import android.view.View
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import luph.vulcanizerv3.updates.MainActivity
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import java.io.ByteArrayInputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import luph.vulcanizerv3.updates.ui.EmptyComingSoon

fun getCertificateChain(): Map<String, String> {
    val context = MainActivity.applicationContext()
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
    val signatures = packageInfo.signingInfo?.apkContentsSigners
    val certChain = signatures?.flatMap { signature ->
        val certs = CertificateFactory.getInstance("X.509").generateCertificates(ByteArrayInputStream(signature.toByteArray())) as Collection<X509Certificate>
        certs.map { it.subjectDN.name }
    }
    return certChain?.let { mapOf("Certificate Chain" to it.joinToString("\n"))}?: mapOf()
}

fun getBootloaderState(): String {
    return try {
        Build.BOOTLOADER
    } catch (e: Exception) {
        Log.e("DeviceInfo", "Error getting bootloader state", e)
        "Unknown"
    }
}



fun deviceInfo(): Map<String, String> {
    val context = MainActivity.applicationContext()
    val displayMetrics = context.resources.displayMetrics
    val screenResolution = "${displayMetrics.widthPixels}x${displayMetrics.heightPixels}"

    return mapOf(
        "Manufacturer" to Build.MANUFACTURER,
        "Board" to Build.BOARD,
        "Hardware" to Build.HARDWARE,
        "Serial No" to Build.SERIAL,
        "Android ID" to Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ),
        "Boot Loader" to Build.BOOTLOADER,
        "Screen Resolution" to screenResolution,
        "User" to Build.USER,
        "Host" to Build.HOST,
        "Model" to Build.MODEL,
        "Device" to Build.DEVICE,
        "Display" to Build.DISPLAY,
        "Fingerprint" to Build.FINGERPRINT,
        "ID" to Build.ID,
        "Product" to Build.PRODUCT,
        "Tags" to Build.TAGS,
        "Type" to Build.TYPE,
        "Time" to Build.TIME.toString()
    )
}

fun getBattery(): Map<String, String> {
    val context = MainActivity.applicationContext()
    val batteryManager = context.getSystemService(android.content.Context.BATTERY_SERVICE) as android.os.BatteryManager
    val batteryLevel = batteryManager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)
    val batteryStatus = batteryManager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_STATUS)
    val batteryCurrentNow = batteryManager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
    val batteryChargeCounter = batteryManager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
    val batteryCapacity = batteryManager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)
    val batteryEnergyCounter = batteryManager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER)
    val batteryCurrentAverage = batteryManager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)

    return mapOf(
        "Battery Level" to batteryLevel.toString(),
        "Battery Status" to batteryStatus.toString(),
        "Battery Current Now" to batteryCurrentNow.toString(),
        "Battery Charge Counter" to batteryChargeCounter.toString(),
        "Battery Capacity" to batteryCapacity.toString(),
        "Battery Energy Counter" to batteryEnergyCounter.toString(),
        "Battery Current Average" to batteryCurrentAverage.toString()
    )
}

@Composable
fun InfoToComposable(title: String, info: Map<String, String>) {
    Card(Modifier.padding(16.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        info.forEach { (key, value) ->
            Text(text = "$key: $value")
        }
    }
}


@Composable
@Preview(showBackground = true)
fun DeviceInfo(navController: NavController = rememberNavController(),
               view: View? = null) {
    EmptyComingSoon()
    return
    LazyColumn{

        item {
            InfoToComposable("CertificateChain", getCertificateChain())
            InfoToComposable("DeviceInfo", deviceInfo())
            InfoToComposable("Battery", getBattery())
            InfoToComposable("Bootloader", mapOf("Bootloader" to getBootloaderState()))

        }
    }
}