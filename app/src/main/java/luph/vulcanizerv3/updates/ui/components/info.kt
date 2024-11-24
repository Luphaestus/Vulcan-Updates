package luph.vulcanizerv3.updates.ui.components

import APKUpdateStatus
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.ketch.Status
import getAPKUpdateStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.DETAILFILE
import luph.vulcanizerv3.updates.data.ModDetails
import luph.vulcanizerv3.updates.data.ModType
import luph.vulcanizerv3.updates.ui.page.RouteParams
import luph.vulcanizerv3.updates.ui.page.showNavigation
import luph.vulcanizerv3.updates.utils.apkmanager.installAPK
import luph.vulcanizerv3.updates.utils.apkmanager.openAPK
import luph.vulcanizerv3.updates.utils.apkmanager.uninstallAPK
import luph.vulcanizerv3.updates.utils.download.getDownloadSize


enum class UpdateStatus {
    NOT_INSTALLED, INSTALLED, UPDATE_AVAILABLE, UPDATING
}

object UpdateFirstButtonStrings {
    private val buttonStrings = mapOf(
        UpdateStatus.NOT_INSTALLED to "",
        UpdateStatus.INSTALLED to MainActivity.applicationContext().getString(R.string.uninstall),
        UpdateStatus.UPDATE_AVAILABLE to MainActivity.applicationContext().getString(R.string.uninstall),
        UpdateStatus.UPDATING to MainActivity.applicationContext().getString(R.string.cancel)
    )

    fun getButtonString(updateStatus: UpdateStatus): String {
        return buttonStrings[updateStatus] ?: "Unknown"
    }
}

object UpdateSecondButtonStrings {
    private val buttonStrings = mapOf(
        UpdateStatus.NOT_INSTALLED to MainActivity.applicationContext().getString(R.string.install),
        UpdateStatus.INSTALLED to MainActivity.applicationContext().getString(R.string.open),
        UpdateStatus.UPDATE_AVAILABLE to MainActivity.applicationContext().getString(R.string.update),
        UpdateStatus.UPDATING to MainActivity.applicationContext().getString(R.string.open),
    )

    fun getButtonString(updateStatus: UpdateStatus): String {
        return buttonStrings[updateStatus] ?: "Unknown"
    }
}

fun isInstallTwoButtons(updateStatus: UpdateStatus): Boolean {
    return updateStatus == UpdateStatus.INSTALLED || updateStatus == UpdateStatus.UPDATE_AVAILABLE || updateStatus == UpdateStatus.UPDATING
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModInfo(navController: NavController, view: View) {
    val initialModDetails: ModDetails = RouteParams.pop(ModDetails::class.java)
        ?: ModDetails()
    val modDetails = remember { initialModDetails }
    val downloadId = remember { mutableIntStateOf(0) }
    val downloadProgressPercentage = remember { mutableIntStateOf(0) }
    val showDescription = remember { mutableStateOf(false) }
    val showVersion = remember { mutableStateOf(false) }
    val infoState = remember { mutableStateOf(UpdateStatus.NOT_INSTALLED) }
    val firstButtonVisible = remember { mutableStateOf(false) }
    val doToggleButtonVisibility = remember { mutableStateOf(false) }
    val buttonAnimWeightValue = remember { mutableFloatStateOf(0.00000001f) }
    val canCancel = remember { mutableStateOf(true) }

    val fileSize = remember { mutableFloatStateOf(-1f) }

    showNavigation.show = false

    fun changeUpdateType(updateStatus: UpdateStatus) {
        infoState.value = updateStatus
        canCancel.value = true
        if (isInstallTwoButtons(updateStatus)) {
            Log.e("buttonVisible", "status: $updateStatus")
            firstButtonVisible.value = true
            buttonAnimWeightValue.floatValue = 1f
        } else {
            Log.e("buttonNotVisible", "status: $updateStatus")
            doToggleButtonVisibility.value = true
            buttonAnimWeightValue.floatValue = 0.00000001f
        }
    }

    LaunchedEffect(Unit) {
        if (modDetails.updateType == ModType.APK) {
            val apkUpdateStatus = getAPKUpdateStatus(modDetails.packageName, modDetails.version)
            if (apkUpdateStatus == APKUpdateStatus.NO_UPDATE_NEEDED) {
                changeUpdateType(UpdateStatus.INSTALLED)
            } else if (apkUpdateStatus == APKUpdateStatus.UPDATE_NEEDED) {
                changeUpdateType(UpdateStatus.UPDATE_AVAILABLE)
            }
        }

        launch(Dispatchers.IO) {
            fileSize.floatValue = getDownloadSize(modDetails.url + DETAILFILE.FILE.type) / 1048576f
        }
    }

    val buttonAnimWeight by animateFloatAsState(
        targetValue = buttonAnimWeightValue.floatValue,
        animationSpec = tween(durationMillis = 1000),
        finishedListener = {
            if (doToggleButtonVisibility.value) {
                firstButtonVisible.value = false
                doToggleButtonVisibility.value = false
            }
        }
    )

    LaunchedEffect(downloadId.intValue) {
        MainActivity.getKetch().observeDownloadById(downloadId.intValue)
            .flowOn(Dispatchers.IO)
            .collect { downloadModel ->
                Log.e("downloadStatus", downloadModel.toString())
                downloadProgressPercentage.intValue = downloadModel.progress
                downloadModel.status.let {
                    var success = false
                    if (it == Status.SUCCESS) {
                        if (modDetails.updateType == ModType.APK) {
                            canCancel.value = false
                            GlobalScope.launch(Dispatchers.IO) {
                                success = installAPK(downloadModel.path + "/${modDetails.name}")
                                canCancel.value = true
                                if (success) changeUpdateType(UpdateStatus.INSTALLED)
                                else changeUpdateType(UpdateStatus.NOT_INSTALLED)
                            }
                        }
                        if (success) {
                            changeUpdateType(UpdateStatus.INSTALLED)
                        }
                    } else if (it in listOf(Status.FAILED, Status.CANCELLED, Status.PAUSED)) {
                        changeUpdateType(UpdateStatus.NOT_INSTALLED)
                        MainActivity.getKetch().clearDb(downloadId.intValue)
                    } else {
                        changeUpdateType(UpdateStatus.UPDATING)
                    }
                }
            }
    }


    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()
            .padding(16.dp)
    ) {

        PageNAv(stringResource(R.string.mod_info_title), navController)

        Spacer(
            modifier = Modifier
                .height(16.dp)
                .background(MaterialTheme.colorScheme.surface)
        )
        LazyColumn(Modifier.background(MaterialTheme.colorScheme.surface)) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                            shape = ShapeDefaults.Large
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(124.dp)
                            .padding(8.dp)
                    ) {

                        val targetSize =
                            if (infoState.value == UpdateStatus.UPDATING) 60.dp else 124.dp
                        val animatedSize by animateDpAsState(
                            targetValue = targetSize,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioHighBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                        Image(
                            painter = rememberImagePainter(data = modDetails.url + DETAILFILE.ICON.type),
                            contentDescription = "Mod Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(animatedSize)
                                .clip(RoundedCornerShape(22.dp))
                        )
                        if (infoState.value == UpdateStatus.UPDATING) {
                            Log.e(
                                "downloadProgress",
                                downloadProgressPercentage.intValue.toString()
                            )
                            Log.e(
                                "determinate",
                                (downloadProgressPercentage.intValue != 0 && downloadProgressPercentage.intValue != 100).toString()
                            )
                            if (downloadProgressPercentage.intValue != 0 && downloadProgressPercentage.intValue != 100) {
                                val animatedProgress by animateFloatAsState(
                                    targetValue = downloadProgressPercentage.intValue / 100f,
                                    animationSpec = tween(
                                        durationMillis = 1500,
                                        easing = CubicBezierEasing(0.2f, 0f, 0.8f, 1f)
                                    )
                                )
                                CircularProgressIndicator(
                                    modifier = Modifier.size(180.dp),
                                    progress = animatedProgress
                                )
                            } else {
                                CircularProgressIndicator(Modifier.size(180.dp))
                            }
                        }
                    }
                    Text(
                        text = modDetails.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = buildAnnotatedString {
                            append("By ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                                append(modDetails.author)
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { showDescription.value = true },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Version", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = modDetails.version,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Box(
                            modifier = Modifier
                                .height(48.dp)
                                .width(2.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(30.dp)
                                )
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = stringResource(R.string.mod_size), style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${"%.3g".format(fileSize.floatValue)}mb",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Box(
                            modifier = Modifier
                                .height(48.dp)
                                .width(2.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(30.dp)
                                )
                        )
                        ElevatedButton(
                            onClick = {
                                val context = view.context
                                val intent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse(modDetails.srcLink))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(4.dp)
                                .weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = stringResource(R.string.mod_source_code_link_button),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = stringResource(R.string.mod_source_code_link_button),
                                    Modifier.fillMaxWidth(),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,

                                                                   )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    if (firstButtonVisible.value) {
                        OutlinedButton(
                            onClick = {
                                if (infoState.value == UpdateStatus.UPDATING) {
                                    MainActivity.getKetch().cancel(downloadId.intValue)
                                } else if (infoState.value == UpdateStatus.INSTALLED || infoState.value == UpdateStatus.UPDATE_AVAILABLE) {
                                    var success = false
                                    if (modDetails.updateType == ModType.APK) {
                                        success = uninstallAPK(modDetails.packageName)
                                    }
                                    if (success)
                                        changeUpdateType(UpdateStatus.NOT_INSTALLED)
                                }
                            },
                            modifier = Modifier
                                .weight(buttonAnimWeight)
                                .padding(end = if (firstButtonVisible.value) 8.dp else 0.dp)
                                .height(42.dp),
                            enabled = canCancel.value
                        ) {
                            Text(text = UpdateFirstButtonStrings.getButtonString(infoState.value))
                        }
                    }
                    Button(
                        onClick = {
                            if (infoState.value == UpdateStatus.NOT_INSTALLED || infoState.value == UpdateStatus.UPDATE_AVAILABLE) {
                                changeUpdateType(UpdateStatus.UPDATING)
                                downloadId.intValue = MainActivity.getKetch().download(
                                    modDetails.url + DETAILFILE.FILE.type,
                                    MainActivity.applicationContext().cacheDir.absolutePath,
                                    modDetails.name
                                )
                            }
                            if (infoState.value == UpdateStatus.INSTALLED) {
                                if (modDetails.updateType == ModType.APK) {
                                    if (modDetails.updateType == ModType.APK) {
                                        openAPK(modDetails.openName)
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = if (firstButtonVisible.value) 8.dp else 0.dp)
                            .height(42.dp),
                        enabled = infoState.value != UpdateStatus.UPDATING && (infoState.value != UpdateStatus.INSTALLED || modDetails.updateType == ModType.APK)
                    ) {
                        Text(text = UpdateSecondButtonStrings.getButtonString(infoState.value))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ImageCarousel(
                    (1..modDetails.images).map { index -> "${modDetails.url}$index.jpg" },
                    modifier = Modifier
                        .height(168.dp)
                        .padding(start = 8.dp, bottom = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                            shape = ShapeDefaults.Large
                        )
                        .clickable { showDescription.value = true }
                ) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            append(
                                modDetails.READMEsummary.substring(
                                    0,
                                    modDetails.READMEsummary.length - 3
                                )
                            )
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.5f
                                    )
                                )
                            ) {
                                append(modDetails.READMEsummary[modDetails.READMEsummary.length - 3])
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.4f
                                    )
                                )
                            ) {
                                append(modDetails.READMEsummary[modDetails.READMEsummary.length - 2])
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.2f
                                    )
                                )
                            ) {
                                append(modDetails.READMEsummary[modDetails.READMEsummary.length - 1])
                            }
                            append("...more")
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(
                            start = 16.dp,
                            top = 8.dp,
                            bottom = 16.dp,
                            end = 16.dp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                            shape = ShapeDefaults.Large
                        )
                        .clickable { showVersion.value = true }
                ) {
                    Text(
                        text = "Change Log",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            append(
                                modDetails.changeLogSummary.substring(
                                    0,
                                    modDetails.changeLogSummary.length - 3
                                )
                            )
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.5f
                                    )
                                )
                            ) {
                                append(modDetails.changeLogSummary[modDetails.changeLogSummary.length - 3])
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.4f
                                    )
                                )
                            ) {
                                append(modDetails.changeLogSummary[modDetails.changeLogSummary.length - 2])
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.2f
                                    )
                                )
                            ) {
                                append(modDetails.changeLogSummary[modDetails.changeLogSummary.length - 1])
                            }
                            append("...more")
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(
                            start = 16.dp,
                            top = 8.dp,
                            bottom = 16.dp,
                            end = 16.dp
                        )
                    )


                    if (showDescription.value || showVersion.value) {
                        ModalBottomSheet(onDismissRequest = {
                            showDescription.value = false; showVersion.value = false
                        }) {
                            LazyColumn(modifier = Modifier.padding(16.dp)) {
                                if (showDescription.value)
                                    item { MarkdownGenerator(markdown = modDetails.README) }
                                else if (showVersion.value)
                                    item {
                                        val versionHtml = """
<h2 style="text-align:center;">Version ${modDetails.version}</h2>
<hr>
<br>
""" + modDetails.changeLog
                                        MarkdownGenerator(markdown = versionHtml)
                                    }
                            }
                        }

                    }
                }
            }
        }
    }
}

