package luph.vulcanizerv3.updates.ui.page.misc.options

import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.starry.file_utils.FileUtils
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.ui.components.ImageCarousel
import luph.vulcanizerv3.updates.ui.components.PageNAv
import luph.vulcanizerv3.updates.ui.components.Tinder
import luph.vulcanizerv3.updates.ui.components.info.UpdateAlert
import luph.vulcanizerv3.updates.ui.page.settings.options.slightlyDeemphasizedAlpha
import luph.vulcanizerv3.updates.ui.page.showNavigation
import luph.vulcanizerv3.updates.utils.download.getRemoteFile
import luph.vulcanizerv3.updates.utils.download.getRemoteText
import luph.vulcanizerv3.updates.utils.download.unzip
import luph.vulcanizerv3.updates.utils.download.zip
import luph.vulcanizerv3.updates.utils.installTwrpModule
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand
import luph.vulcanizerv3.updates.utils.root.runShellCommand
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import androidx.compose.runtime.setValue
import luph.vulcanizerv3.updates.ui.components.noNetworkAlert
import luph.vulcanizerv3.updates.ui.components.rootRequiredAlert

fun changePage(
    newPageIndex: Int,
    maxIndex: Int,
    pageIndex: MutableState<Int>,
    selectedItems: MutableMap<String, Int>,
    selectedImageURIS: MutableState<Map<String, String>>,
    currentCardIndex: MutableState<Int>,
    imageURLS: List<MutableList<String>>,
    categories: List<String>
) {
    if (newPageIndex < 0 || newPageIndex > maxIndex) return


    if (pageIndex.value < categories.size) {
        selectedItems.put(
            categories[pageIndex.value],
            currentCardIndex.value % imageURLS[pageIndex.value].size
        )

        val selectedImageURI = imageURLS[pageIndex.value][selectedItems.get(categories[pageIndex.value]) ?: 0]
        if (selectedImageURI.isNotEmpty()) {
            selectedImageURIS.value = selectedImageURIS.value.toMutableMap().apply {
                put(categories[pageIndex.value], selectedImageURI)
            }
        }
    }

    pageIndex.value = newPageIndex

    if (pageIndex.value < categories.size)
        currentCardIndex.value = selectedItems.get(categories[pageIndex.value]) ?: 0
}


@Composable
fun BootAnimationShowcase(
    item: String,
    dimensions: Pair<Int, Int>?
) {

    val painter = rememberImagePainter(item)
    val aspectRatio = dimensions?.let { it.first.toFloat() / it.second.toFloat() } ?: 1f

    Box(
        Modifier
            .padding(horizontal = 32.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Image(
            painter = painter,
            contentDescription = "Boot Animation Preview",
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(aspectRatio)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.shapes.large
                )
                .clip(MaterialTheme.shapes.large)
                .background(
                    MaterialTheme.colorScheme
                        .surfaceColorAtElevation(16.dp)
                        .copy(alpha = 0.7f)
                ),
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
@Preview(showBackground = true)
fun ChangeBootOption(
    navController: NavController = rememberNavController(),
    view: View? = null
) {
    showNavigation.show = false

    val baseUrl = stringResource(R.string.github_link)
    var device by remember { mutableStateOf("") }
    var imageCategories by remember { mutableStateOf(listOf<String>()) }
    var OVERVIEW_INDEX by remember { mutableStateOf(0) }
    var END_INDEX by remember { mutableStateOf(0) }

    var pageIndex = remember { mutableStateOf(0) }
    var currentCardIndex = remember { mutableStateOf(0) }
    var selectedImageURIS = remember { mutableStateOf(mapOf<String, String>()) }
    val selectedItems by remember { mutableStateOf(mutableMapOf<String, Int>()) }

    var showInstallModal = remember { mutableStateOf(false) }
    var showUploadModal = remember { mutableStateOf(false) }
    var showUploadErrorModal = remember { mutableStateOf(false) }

    UploadConfirmationDialogParams.showUploadErrorModal = showUploadErrorModal
    UploadConfirmationDialogParams.selectedImageURIS = selectedImageURIS


    var imageDimensions = remember {
        mutableMapOf<String, Pair<Int, Int>>()
    }

    var imageURLS = remember {
        mutableListOf<MutableList<String>>()
    }

    var isLoaded by remember { mutableStateOf(false) }

    val noRoot = rootRequiredAlert(negativeClickLambda = { navController.popBackStack() }, positiveClickLambda = { navController.popBackStack() })
    val noNetwork = noNetworkAlert(navController, view, negativeClickLambda = { navController.popBackStack() }, positiveClickLambda = { navController.popBackStack() })

    if (!noRoot.value || !noNetwork.value) {
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                device = runShellCommand("getprop ro.boot.bootloader").value.first.trim().take(5).ifEmpty { "N986B" }
                imageCategories = (getRemoteText("${baseUrl}animations/$device/list") ?: "")
                    .replace("\\s".toRegex(), "")
                    .split(",")

                OVERVIEW_INDEX = imageCategories.size
                END_INDEX = OVERVIEW_INDEX

                val dimensions =
                    getRemoteFile("${baseUrl}animations/$device/dimens").readText().split("\n")
                        .mapNotNull {
                            val parts = it.split(":").map { part -> part.trim() }
                            if (parts.size == 2) {
                                val dims = parts[1].split("x").map { dim -> dim.toIntOrNull() }
                                if (dims.size == 2 && dims[0] != null && dims[1] != null) {
                                    parts[0] to Pair(dims[0]!!, dims[1]!!)
                                } else null
                            } else null
                        }.toMutableStateMap()

                val urls = imageCategories.map { category ->
                    (getRemoteText("${baseUrl}animations/$device/$category/list") ?: "")
                        .split(",")
                        .toMutableList()
                        .apply {
                            for (i in indices) {
                                this[i] = "${baseUrl}animations/$device/$category/${this[i]}"
                            }
                            add("")
                        }
                }

                withContext(Dispatchers.Main) {
                    imageDimensions = dimensions
                    UploadConfirmationDialogParams.dimensions = imageDimensions
                    imageURLS = urls.toMutableList()
                    if (imageDimensions.isNotEmpty() && imageURLS.isNotEmpty()) {
                        isLoaded = true
                    }
                }
            }
        }



        InstallConfirmationDialog(
            showInstallModal = showInstallModal,
            imageURIS = selectedImageURIS
        )

        UploadConfirmationDialog(showUploadModal)

        UploadErrorDialog(showUploadErrorModal)

        Scaffold(
            bottomBar = {
                if (isLoaded) {
                    NavigationButtons(
                        pageIndex = pageIndex,
                        END_INDEX = END_INDEX,
                        selectedItems = selectedItems,
                        selectedImageURIS = selectedImageURIS,
                        currentCardIndex = currentCardIndex,
                        imageCategories = imageCategories,
                        imageURLS = imageURLS,
                        showInstallModal = showInstallModal
                    )
                }
            }
        ) { padding ->
            Column(
                Modifier
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding() + 8.dp + (10f * 5).dp
                    )
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Box(Modifier.padding(horizontal = 16.dp)) {
                    PageNAv(stringResource(R.string.boot_animation), navController)
                }
                if (!isLoaded) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    MainContent(
                        pageIndex = pageIndex,
                        imageCategories = imageCategories,
                        imageURLS = imageURLS,
                        currentCardIndex = currentCardIndex,
                        selectedImageURIS = selectedImageURIS,
                        OVERVIEW_INDEX = OVERVIEW_INDEX,
                        showUploadModal = showUploadModal,
                        imageDimensions = imageDimensions
                    )
                }
            }
        }
    }
}

@Composable
private fun InstallConfirmationDialog(
    showInstallModal: MutableState<Boolean>,
    imageURIS: MutableState<Map<String, String>>
) {
    UpdateAlert(
        title = stringResource(R.string.are_you_sure_you_want_to_install_this_boot_animation),
        description = stringResource(R.string.this_will_replace_your_current_boot_animation_make_sure_to_backup_your_current_boot_animation_before_proceeding),
        show = showInstallModal,
        positiveClickText = stringResource(R.string.install_and_reboot),
        positiveClick = {
            val up_param_patch_src =
                getRemoteFile("https://github.com/Luphaestus/up_param-Patcher/releases/download/v1/CustomBootAnimation-OTA.zip")
            val unzipped_patcher = unzip(up_param_patch_src)
            up_param_patch_src.delete()

            imageURIS.value.map { (category, uri) ->
                if (uri.isNotEmpty()) {
                    if (File(uri).exists())
                        Files.copy(Paths.get(uri), Paths.get("${unzipped_patcher.path}/images/$category.jpg"), StandardCopyOption.REPLACE_EXISTING)
                    else
                        getRemoteFile(uri, "${unzipped_patcher.path}/images/$category.jpg")
                }
            }

            val zipped_patcher = zip(unzipped_patcher)

//            test code
//            runRootShellCommand("cp ${zipped_patcher.path} /sdcard/")

            installTwrpModule(zipped_patcher.absolutePath)
            runRootShellCommand("reboot recovery")
        },
        negativeClickText = stringResource(R.string.cancel),
    )
}

enum class UploadErrorType {
    NONE,
    INVALID_FILE_TYPE,
    DIMENSION_MISMATCH
}

data object UploadConfirmationDialogParams {
    var selectedImageURIS: MutableState<Map<String, String>> = mutableStateOf(mapOf())
    var category: String = ""
    var dimensions: MutableMap<String, Pair<Int, Int>> = mutableMapOf()
    var showUploadErrorModal: MutableState<Boolean> = mutableStateOf(false)
    var uploadErrorType: MutableState<UploadErrorType> = mutableStateOf(UploadErrorType.NONE)
    var expectedDimensions: MutableState<Pair<Int, Int>> = mutableStateOf(Pair(0, 0))
    var actualDimensions: MutableState<Pair<Int, Int>> = mutableStateOf(Pair(0, 0))}

@Composable
private fun UploadConfirmationDialog(
    showModal: MutableState<Boolean>,
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        val filePath = uri?.let { FileUtils(MainActivity.applicationContext()).getPath(it) } ?: ""
        if (!filePath.endsWith(".jpg", ignoreCase = true) && !filePath.endsWith(".jpeg", ignoreCase = true)) {
            UploadConfirmationDialogParams.uploadErrorType.value = UploadErrorType.INVALID_FILE_TYPE
            UploadConfirmationDialogParams.showUploadErrorModal.value = true
            return@rememberLauncherForActivityResult
        }
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(filePath, options)
        val width = options.outWidth
        val height = options.outHeight
        if (width != UploadConfirmationDialogParams.dimensions[UploadConfirmationDialogParams.category]?.first
            || height != UploadConfirmationDialogParams.dimensions[UploadConfirmationDialogParams.category]?.second
        ) {
            Log.e("realdimentions", "${width}x${height}")
            Log.e("expecteddimentions", UploadConfirmationDialogParams.dimensions.toString())
            UploadConfirmationDialogParams.uploadErrorType.value = UploadErrorType.DIMENSION_MISMATCH
            UploadConfirmationDialogParams.expectedDimensions.value = UploadConfirmationDialogParams.dimensions[UploadConfirmationDialogParams.category] ?: Pair(0, 0)
            UploadConfirmationDialogParams.actualDimensions.value = Pair(width, height)
            UploadConfirmationDialogParams.showUploadErrorModal.value = true
            return@rememberLauncherForActivityResult
        }
        UploadConfirmationDialogParams.selectedImageURIS.value =  UploadConfirmationDialogParams.selectedImageURIS.value.toMutableMap().apply { put( UploadConfirmationDialogParams.category, filePath) }
    }

    UpdateAlert(
        title = stringResource(R.string.upload_warning),
        description = stringResource(R.string.flashing_a_custom_image_may_cause_bootloops_or_brick_your_device_make_sure_you_know_what_you_are_doing_if_you_are_unsure_ask_for_help_in_the_support_group_i_will_not_be_responsible_for_any_damage_caused_by_flashing_custom_images),
        show = showModal,
        positiveClickText = stringResource(R.string.i_understand),
        positiveClick = {
            imagePickerLauncher.launch("image/*")
        },
        negativeClickText = stringResource(R.string.cancel),
    )
}

@Composable
private fun UploadErrorDialog(
    showModal: MutableState<Boolean>,
) {
    UpdateAlert(
        title = if (UploadConfirmationDialogParams.uploadErrorType.value == UploadErrorType.INVALID_FILE_TYPE) stringResource(
            R.string.invalid_file_type
        ) else stringResource(R.string.dimension_mismatch),
        description = if (UploadConfirmationDialogParams.uploadErrorType.value == UploadErrorType.INVALID_FILE_TYPE) stringResource(
            R.string.the_file_you_uploaded_is_not_a_jpeg_file
        ) else stringResource(
            R.string.the_image_you_uploaded_does_not_match_the_expected_dimensions_of_x_the_image_you_uploaded_has_dimensions_of_x,
            UploadConfirmationDialogParams.expectedDimensions.value.first,
            UploadConfirmationDialogParams.expectedDimensions.value.second,
            UploadConfirmationDialogParams.actualDimensions.value.first,
            UploadConfirmationDialogParams.actualDimensions.value.second
        ),
        show = showModal,
        positiveClickText = stringResource(R.string.ok),
        negativeClickText = ""
    )
}

@Composable
private fun NavigationButtons(
    pageIndex: MutableState<Int>,
    END_INDEX: Int,
    selectedItems: MutableMap<String, Int>,
    selectedImageURIS: MutableState<Map<String, String>>,
    currentCardIndex: MutableState<Int>,
    imageCategories: List<String>,
    imageURLS:  MutableList<MutableList<String>>,
    showInstallModal: MutableState<Boolean>
) {

    Column {
        Row {
            OutlinedButton(

                onClick = {
                    changePage(
                        pageIndex.value - 1,
                        END_INDEX,
                        pageIndex,
                        selectedItems,
                        selectedImageURIS,
                        currentCardIndex,
                        imageURLS,
                        imageCategories
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                enabled = pageIndex.value != 0
            ) {
                Text(stringResource(R.string.previous))
            }
            Button(
                onClick = {
                    if (pageIndex.value == END_INDEX) {
                        showInstallModal.value = true
                        return@Button
                    }
                    changePage(
                        pageIndex.value + 1,
                        END_INDEX,
                        pageIndex,
                        selectedItems,
                        selectedImageURIS,
                        currentCardIndex,
                        imageURLS,
                        imageCategories
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
            ) {
                Text(
                    text = when {
                        pageIndex.value == END_INDEX - 1 -> stringResource(R.string.overview)
                        pageIndex.value == END_INDEX -> stringResource(R.string.install)
                        else -> stringResource(R.string.next)
                    },
                    maxLines = 1
                )
            }
        }
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun MainContent(
    pageIndex: MutableState<Int>,
    imageCategories: List<String>,
    imageURLS:  MutableList<MutableList<String>>,
    currentCardIndex: MutableState<Int>,
    selectedImageURIS: MutableState<Map<String, String>>,
    OVERVIEW_INDEX: Int,
    showUploadModal: MutableState<Boolean>,
    imageDimensions: MutableMap<String, Pair<Int, Int>>
) {
    Column{
        val currentURL =  if (pageIndex.value < imageCategories.size) imageURLS[pageIndex.value][currentCardIndex.value%imageURLS[pageIndex.value].size] else ""
        Text(
            if (pageIndex.value < imageCategories.size) "${imageCategories.get(pageIndex.value)} - ${if (currentURL.isEmpty()) stringResource(
                R.string.custom_boot_animation
            ) else  currentURL.split("/").last().split(".")[0]}"
            else if (pageIndex.value == OVERVIEW_INDEX) stringResource(R.string.overview)
            else "Errrrr",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 10.dp)
                .clip(MaterialTheme.shapes.small)
                .background(color = MaterialTheme.colorScheme.inverseOnSurface)
                .padding(20.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha),
        )
        when {
            pageIndex.value < imageCategories.size -> TinderContent(
                category = imageCategories.get(pageIndex.value),
                imageURLS = imageURLS.get(pageIndex.value),
                currentCardIndex = currentCardIndex,
                selectedImageURIS = selectedImageURIS,
                showUploadModal = showUploadModal,
                imageDimensions = imageDimensions
            )

            pageIndex.value == OVERVIEW_INDEX -> OverviewContent(
                selectedImageURIS = selectedImageURIS
            )
        }
    }
}

@Composable
private fun TinderContent(
    category: String,
    imageURLS: MutableList<String>,
    currentCardIndex: MutableState<Int>,
    selectedImageURIS: MutableState<Map<String, String>>,
    showUploadModal: MutableState<Boolean>,
    imageDimensions: MutableMap<String, Pair<Int, Int>>
) {
    Tinder(
        modifier = Modifier
            .background(Color.Transparent)
            .padding(horizontal = 16.dp),
        items = imageURLS,
        currentCardIndex = currentCardIndex
    ) { item, index ->

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (upload) = createRefs()

            BootAnimationShowcase(
                item,
                imageDimensions[category],
            )
            if (item.isEmpty()) {
                UploadSection(
                    category,
                    modifier = Modifier.constrainAs(upload) { centerTo(parent) },
                    selectedImageURIS,
                    showUploadModal
                )
            }
        }
    }
}

@Composable
private fun UploadSection(
    category: String,
    modifier: Modifier,
    selectedImageURIS: MutableState<Map<String, String>>,
    showUploadModal: MutableState<Boolean>
) {

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(horizontal = 32.dp, vertical = 16.dp)
            .background(
                MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp),
                MaterialTheme.shapes.medium
            )
            .padding(16.dp)
            .alpha(0.5f),
    ) {
        Text(
            text = "Upload your $category.jpg",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha)
        )
        Button(
            onClick = {
                showUploadModal.value = true
                UploadConfirmationDialogParams.category = category
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp)
        ) {
            Text(stringResource(R.string.upload))
        }
        Text(
            text = stringResource(R.string.or),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha)
        )
        Button(
            onClick = {
                selectedImageURIS.value =
                    selectedImageURIS.value.toMutableMap().apply { put(category, "") }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp)
        ) {
            Text(stringResource(R.string.don_t_replace))
        }
    }
}

@Composable
private fun OverviewContent(
    selectedImageURIS:  MutableState<Map<String, String>>
) {
    ImageCarousel(
        imageUrls = selectedImageURIS.value.values.toList().filter { it.isNotEmpty() },
        modifier = Modifier
            .padding(end = 8.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary,
                MaterialTheme.shapes.large
            )
            .clip(MaterialTheme.shapes.large),
    )
}