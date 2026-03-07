package luph.vulcanizerv3.updates.ui.components

import android.R.attr.value
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composeuisuite.ohteepee.OhTeePeeDefaults
import com.composeuisuite.ohteepee.OhTeePeeInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import luph.vulcanizerv3.updates.MainActivity
import java.security.MessageDigest
import java.util.Base64
import java.util.regex.Pattern
import java.time.Instant
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha

fun hashToAlphanumeric(inputString: String): String {
    val hashObject = MessageDigest.getInstance("SHA-256").digest(inputString.toByteArray())
    val base64Encoded = Base64.getUrlEncoder().encodeToString(hashObject)

    val alphanumeric = base64Encoded.replace(Regex("[^a-zA-Z0-9]"), "")

    return alphanumeric.take(6)
}

fun generateCodeAndValidity(username: String, floor: Long): String {
    val currentTime = Instant.now().epochSecond
    val roundedTime = currentTime.floorDiv(floor) * floor
    Log.e("Rounded Time", roundedTime.toString())
    val code = hashToAlphanumeric(username.lowercase() + roundedTime.toString())
    return code
}


@Composable
fun TelegramVerification(username: MutableState<String>, onCompleted: () -> Unit) {
    var hasRequestedCode by remember { mutableStateOf(true) }
    val hasRequestedCodeVisibility by animateFloatAsState(if (hasRequestedCode) 1f else 0f)

    val url = "https://t.me/VulcanROM_helper_bot?start=Auth"
    val context = MainActivity.applicationContext()
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
            if (hasRequestedCodeVisibility != 1f) {
                Button(
                    onClick = {
                        hasRequestedCode = true
                        context.startActivity(intent)
                    },
                    modifier = Modifier.alpha(1 - hasRequestedCodeVisibility)
                ) {
                    Text("Request Verification Code")
                }
            }
            if (hasRequestedCodeVisibility != 0f) {
                var otpValue = remember { mutableStateOf("") }
                var isOtpValid by remember { mutableStateOf(true) }

                val defaultCellConfig = OhTeePeeDefaults.cellConfiguration(
                    borderColor = Color.LightGray,
                    borderWidth = 1.dp,
                    shape = RoundedCornerShape(16.dp),

                    )
                Column {
                    OhTeePeeInput(
                        value = otpValue.value,
                        onValueChange = { newValue, isValid ->
                            isOtpValid = newValue != ""

                            otpValue.value = newValue

                            if (isValid) {
                                val generatedCode = generateCodeAndValidity(username.value, 3600)
                                Log.e("Generated Code", generatedCode)
                                if (otpValue.value == generatedCode) {
                                    isOtpValid = true
                                    onCompleted()
                                } else {
                                    isOtpValid = false
                                }
                            }
                        },
                        isValueInvalid = !isOtpValid,
                        configurations = OhTeePeeDefaults.inputConfiguration(
                            cellsCount = 6,
                            emptyCellConfig = defaultCellConfig,
                            cellModifier = Modifier.size(48.dp),
                        ),
                        keyboardType = KeyboardType.Text,
                        modifier = Modifier.alpha(hasRequestedCodeVisibility)
                    )
                    TextButton(
                        onClick = {
                            context.startActivity(intent)
                        },
                        modifier = Modifier.alpha(hasRequestedCodeVisibility)
                            .align(Alignment.End)
                    ) {
                        Text("Request New Code")
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun TelegramVerificationPreview(){
    TelegramVerification(remember { mutableStateOf("") }){}
}