package luph.vulcanizerv3.updates.ui.page.misc.options


import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import luph.vulcanizerv3.updates.ui.components.PageNAv
import luph.vulcanizerv3.updates.ui.components.info.UpdateAlert
import luph.vulcanizerv3.updates.ui.page.showNavigation
import luph.vulcanizerv3.updates.utils.download.getRemoteFile
import luph.vulcanizerv3.updates.utils.download.getRemoteText
import luph.vulcanizerv3.updates.utils.download.unzip
import luph.vulcanizerv3.updates.utils.download.zip
import luph.vulcanizerv3.updates.utils.getStandardAnimationSpeed
import luph.vulcanizerv3.updates.utils.installTwrpModule
import luph.vulcanizerv3.updates.utils.root.runRootShellCommand
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.ui.components.info.InfoPopup
import luph.vulcanizerv3.updates.ui.components.noNetworkAlert
import luph.vulcanizerv3.updates.ui.components.rootRequiredAlert
import okhttp3.OkHttpClient
import okhttp3.Request

suspend fun urlExists(url: String): Boolean {
    val client = OkHttpClient()
    val request = Request.Builder().url(url).head().build()
    return try {
        client.newCall(request).execute().isSuccessful
    } catch (e: Exception) {
        false
    }
}


@Composable
private fun InstallConfirmationDialog(
    showModal: MutableState<Boolean>,
    bootAnimation: MutableState<String>,
    baseURLLink: String = stringResource(R.string.github_link),
    isInstalling: MutableState<Boolean>,
    showFailModal: MutableState<Boolean>,
) {
    UpdateAlert(
        title = "Install Boot Animation",

        description = "Are you sure you want to install the ${bootAnimation.value} animation?",
        show = showModal,
        positiveClickText = stringResource(R.string.install),
        negativeClickText = stringResource(R.string.cancel),
        positiveClick = {
            isInstalling.value = true
            CoroutineScope(Dispatchers.IO).launch {
                var url = "$baseURLLink/${bootAnimation.value.replace(" ", "_")}/"
                val files =
                    getRemoteText("${url}list")?.replace("\n", "")?.split(",")?.toMutableList()
                if (files == null) return@launch

                if (runRootShellCommand("mount -o rw,remount /").value.second) {
                    for (file in files) {
                        var bootFile = getRemoteFile("$url$file")
                        var result = runRootShellCommand("cp ${bootFile.absolutePath} /system/media/$file").value.second
                        if (result)
                        {
                        } else {
                            showFailModal.value = true
                            bootAnimation.value = ""
                            return@launch
                        }
                    }
                    runRootShellCommand("reboot")
                } else {
                    val bootAnimPatchSrc =
                        getRemoteFile("https://huggingface.co/datasets/Luphaestus/VulcanUpdates3Data/resolve/master/animations/qmg/bootanimflasher.zip")
                    val unzipped_patcher = unzip(bootAnimPatchSrc)
                    bootAnimPatchSrc.delete()

                    for (file in files) {
                        getRemoteFile("$url$file", "${unzipped_patcher.path}/images/$file")
                    }

                    val zipped_patcher = zip(unzipped_patcher)

                    installTwrpModule(zipped_patcher.absolutePath)
                    runRootShellCommand("reboot recovery")
                }
            }
        },
    )
}

@Composable
private fun InstallFailDialog(
    showModal: MutableState<Boolean>,
    bootAnimation: MutableState<String>,
) {
    UpdateAlert(
        title = "FAILED TO INSTALL BOOT ANIMATION",
        description = "The animation ${bootAnimation.value} failed to install, likely due to insufficient space in /system. Please try a different animation or free up space in /system.",
        show = showModal,
        positiveClickText = stringResource(R.string.ok),
        negativeClickText = "",
    )
}

@Composable
private fun DetailedView(
    animationList: List<String>,
    baseURL: String,
    bootAnimation: MutableState<String>,
    showInstallConfirmModal: MutableState<Boolean>,
    currentIndex: MutableState<Int>,
    inDetailedView: MutableState<Boolean> = mutableStateOf(true)
) {
    var animateSwitch = remember { mutableStateOf(false) }

    if (inDetailedView.value) {
        BackHandler {
            inDetailedView.value = false
        }
    }

    Box(Modifier.fillMaxSize().padding(bottom = 16.dp)) {
        val listState = rememberLazyListState()
        val flingBehavior = rememberSnapFlingBehavior(
            lazyListState = listState,
            snapPosition = SnapPosition.Start,

            )

        LazyRow(state = listState, flingBehavior = flingBehavior) {
            items(animationList.size) { animationIndex ->
                val animation = animationList[animationIndex]
                val gifLink = "$baseURL/$animation/showcase.gif"
                val pngLink = "$baseURL/$animation/showcase.png"
                val context = LocalContext.current

                val imageLink = remember { mutableStateOf(gifLink) }

                LaunchedEffect(gifLink) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val exists = urlExists(gifLink)
                        imageLink.value = if (exists) gifLink else pngLink
                    }
                }

                Column(modifier = Modifier.fillParentMaxWidth()) {
                    val imageLoader = ImageLoader.Builder(LocalContext.current)
                        .components {
                            add(GifDecoder.Factory())
                        }
                        .build()

                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context).data(data = imageLink.value)
                                .apply(block = {
                                    size(Size.ORIGINAL)
                                }).build(), imageLoader = imageLoader
                        ),

                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .background(Color.Black, MaterialTheme.shapes.large)
                            .clip(MaterialTheme.shapes.large),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
        }

        var animatedOverlayAlpha = animateFloatAsState(
            targetValue = if (listState.isScrollInProgress) .5f else .8f,
            animationSpec = tween(durationMillis = getStandardAnimationSpeed() * 2)
        )


        ConstraintLayout(Modifier.fillMaxSize().alpha(animatedOverlayAlpha.value)) {
            val (back, forward, install) = createRefs()

            Button(
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                enabled = remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }.value,
                onClick = {
                    if (listState.firstVisibleItemIndex > 0) {
                        animateSwitch.value = true
                        currentIndex.value = listState.firstVisibleItemIndex - 1
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(52.dp)
                    .constrainAs(back) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardDoubleArrowLeft,
                    contentDescription = "Back"
                )
            }

            Button(
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    if (listState.firstVisibleItemIndex < animationList.size - 1) {
                        animateSwitch.value = true
                        currentIndex.value = listState.firstVisibleItemIndex + 1
                    }
                },
                enabled = remember { derivedStateOf { listState.firstVisibleItemIndex } }.value < animationList.size - 1,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(52.dp)
                    .constrainAs(forward) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardDoubleArrowRight,
                    contentDescription = "Forward"
                )
            }

            Button(
                onClick = {
                    bootAnimation.value =
                        animationList[listState.firstVisibleItemIndex]
                    showInstallConfirmModal.value = true
                },
                modifier = Modifier
                    .constrainAs(install) {
                        bottom.linkTo(parent.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Text("Install")
            }
        }

        LaunchedEffect(currentIndex.value) {
            if (animateSwitch.value)
                listState.animateScrollToItem(currentIndex.value)
            else
                listState.scrollToItem(currentIndex.value)
            animateSwitch.value = false
        }
    }
}


@Composable
fun AnimationGridView(animationList: List<String>, baseURL: String, currentIndex: MutableState<Int>, inDetailedView: MutableState<Boolean>, gridScrollState: LazyGridState = rememberLazyGridState()) {

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        modifier = Modifier.fillMaxSize().padding(16.dp),
        state = gridScrollState
    ) {
        items(animationList.size) { index ->
            val animation = animationList[index]
            val gifLink = "$baseURL/$animation/showcase.gif"
            val pngLink = "$baseURL/$animation/showcase.png"
            val imageLink = remember { mutableStateOf(gifLink) }

            LaunchedEffect(gifLink) {
                CoroutineScope(Dispatchers.IO).launch {
                    val exists = urlExists(gifLink)
                    imageLink.value = if (exists) gifLink else pngLink
                }
            }

            val context = LocalContext.current
            Column(
                modifier = Modifier
                    .padding(1.dp)
                    .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
                    .clip(MaterialTheme.shapes.medium)
            ) {


                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context).data(data = imageLink.value)
                            .apply(block = {
                                size(Size.ORIGINAL)
                            }).build()
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(248.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            currentIndex.value = index
                            inDetailedView.value = true
                        }
                            ,
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChangeQMGOption(
    navController: NavController = rememberNavController(),
    view: View? = null
) {
    val baseURL = "${navController.context.getString(R.string.github_link)}animations/qmg"
    showNavigation.show = false

    var isLoading by remember { mutableStateOf(true) }
    var animationList by remember { mutableStateOf(listOf<String>()) }
    var gridScrollState = rememberLazyGridState()
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            animationList =
                getRemoteText("$baseURL/list")?.replace("\n", "")?.split(",")?.toMutableList()
                    ?.sorted()
                    ?: mutableListOf()
            isLoading = false
        }
    }

    var inDetailedView = remember { mutableStateOf(false) }
    var currentIndex = remember { mutableStateOf(0) }

    var showInstallConfirmModal = remember { mutableStateOf(false) }
    var showInstallFailModal = remember { mutableStateOf(false) }
    var bootAnimation = remember { mutableStateOf("") }
    var isInstalling = remember { mutableStateOf(false) }

    rootRequiredAlert(
        negativeClickLambda = { navController.popBackStack() },
        positiveClickLambda = { navController.popBackStack() })
    val noNetwork = noNetworkAlert(
        navController,
        view,
        negativeClickLambda = { navController.popBackStack() },
        positiveClickLambda = { navController.popBackStack() })
    if (!noNetwork.value) {
        InstallConfirmationDialog(
            showInstallConfirmModal,
            bootAnimation,
            baseURL,
            isInstalling,
            showInstallFailModal
        )

        InstallFailDialog(
            showInstallFailModal,
            bootAnimation,
        )

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            if (isInstalling.value) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Box(Modifier.padding(horizontal = 16.dp)) {
                    if (inDetailedView.value) {
                        PageNAv(
                            animationList[currentIndex.value].replace("_", " "),
                            goBack = { inDetailedView.value = false })
                    } else {
                        PageNAv("Change QMG Animation", navController)
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else {
                        Column {
                            AnimatedVisibility(
                                visible = !inDetailedView.value,
                            ) {
                                AnimationGridView(
                                    animationList,
                                    baseURL,
                                    currentIndex,
                                    inDetailedView,
                                    gridScrollState
                                )
                            }

                            DetailedView(
                                animationList,
                                baseURL,
                                bootAnimation,
                                showInstallConfirmModal,
                                currentIndex,
                                inDetailedView
                            )
                        }
                    }
                }
            }
        }
    }
}
