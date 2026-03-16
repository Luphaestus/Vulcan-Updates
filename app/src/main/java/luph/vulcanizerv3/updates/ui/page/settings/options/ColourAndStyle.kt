package luph.vulcanizerv3.updates.ui.page.settings.options


import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import luph.vulcanizerv3.updates.MainActivity
import luph.vulcanizerv3.updates.R
import luph.vulcanizerv3.updates.data.ThemeManager
import luph.vulcanizerv3.updates.data.Themes
import luph.vulcanizerv3.updates.ui.components.CircleStore
import luph.vulcanizerv3.updates.ui.components.ClickableOverlay
import luph.vulcanizerv3.updates.ui.components.MultipleExpandingCircleAnimations
import luph.vulcanizerv3.updates.ui.components.PageNAv
import luph.vulcanizerv3.updates.ui.components.SettingsElementBase
import luph.vulcanizerv3.updates.ui.page.showNavigation
import luph.vulcanizerv3.updates.ui.theme.getColourScheme
import luph.vulcanizerv3.updates.utils.getAnimationScale

data class TonalPalettes(val a1: Color, val a2: Color, val a3: Color)

val LocalTonalPalettes = compositionLocalOf { TonalPalettes(Color.Red, Color.Green, Color.Blue) }


@Composable
fun ColorButtonRow(
    animation: MutableList<CircleStore>,
    isDynamic: Boolean,
    lastSelectected: MutableState<String?> = mutableStateOf(
        ThemeManager.theme ?: "yellow"
    )
) {
    val selectedThemeIndex = Themes.keys.toList().indexOf(lastSelectected.value)

    val initialPage = if (selectedThemeIndex >= 0) selectedThemeIndex / 4 else 0
    val pagerState = rememberPagerState(initialPage) { (Themes.size + 3) / 4 }

    val isSystemInDarkTheme = isSystemInDarkTheme()

    AnimatedVisibility(
        visible = !isDynamic,
        enter = expandVertically(
            animationSpec = tween(durationMillis = (300*getAnimationScale()).toInt())
        ),
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = (300*getAnimationScale()).toInt())
        )
    ) {
        Column {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) { page ->
                val startIndex = page * 4
                val endIndex = (startIndex + 4).coerceAtMost(Themes.size)
                val itemsOnPage = Themes.keys.toList().subList(startIndex, endIndex)
                Row {
                    itemsOnPage.forEachIndexed { index, tonalPalettes ->
                        ClickableOverlay(Modifier.padding(4.dp), onClick = { offset: Offset ->
                            animation.add(
                                CircleStore(
                                    offset = offset,
                                    currentColour = getColourScheme(
                                        ThemeManager.darkTheme ?: isSystemInDarkTheme,
                                        ThemeManager.getThemeTheme(),
                                        ThemeManager.contrast
                                    ).background,
                                    newColour = getColourScheme(
                                        ThemeManager.darkTheme ?: isSystemInDarkTheme,
                                        Themes[tonalPalettes],
                                        ThemeManager.contrast
                                    ).background
                                )
                            )
                            ThemeManager.theme = tonalPalettes
                            lastSelectected.value = tonalPalettes
                        }) {
                            ColorButtonImpl(
                                isSelected = { tonalPalettes == ThemeManager.theme },
                                tonalPalettes = TonalPalettes(
                                    a1 = getColourScheme(
                                        true,
                                        Themes[tonalPalettes],
                                        ThemeManager.contrast
                                    ).primary,
                                    a2 = getColourScheme(
                                        false,
                                        Themes[tonalPalettes],
                                        ThemeManager.contrast
                                    ).background,
                                    a3 = getColourScheme(
                                        true,
                                        Themes[tonalPalettes],
                                        ThemeManager.contrast
                                    ).inversePrimary,
                                ),
                            )
                        }
                    }
                }
            }

            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceColorAtElevation(
                            16.dp
                        )
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Message(message: String, drawable: Int? = null) =
    if (drawable != null) {
        Row(Modifier) {
            Image(
                painter = painterResource(id = drawable),
                contentDescription = "icon",
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically)
            )
            Box(
                Modifier
                    .padding(start = 8.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    } else {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Box(
                Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.tertiary)
            ) {
                Text(
                    message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }

@Composable
fun ColorButtonImpl(
    modifier: Modifier = Modifier,
    isSelected: () -> Boolean = { false },
    tonalPalettes: TonalPalettes,
    cardColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
) {
    val containerSize by animateDpAsState(
        targetValue = if (isSelected.invoke()) 28.dp else 0.dp,
        label = ""
    )
    val iconSize by animateDpAsState(
        targetValue = if (isSelected.invoke()) 16.dp else 0.dp,
        label = ""
    )

    Surface(
        modifier = modifier
            .sizeIn(maxHeight = 74.dp, maxWidth = 80.dp, minHeight = 74.dp, minWidth = 64.dp)
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        color = cardColor,
    ) {
        CompositionLocalProvider(LocalTonalPalettes provides tonalPalettes) {
            val color1 = LocalTonalPalettes.current.a1
            val color2 = LocalTonalPalettes.current.a2
            val color3 = LocalTonalPalettes.current.a3
            Box(Modifier.fillMaxSize()) {
                Box(
                    modifier = modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .drawBehind { drawCircle(color1) }
                        .align(Alignment.Center)
                ) {
                    Surface(
                        color = color2,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .size(24.dp),
                    ) {}
                    Surface(
                        color = color3,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp),
                    ) {}
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .size(containerSize)
                            .drawBehind { drawCircle(containerColor) }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            modifier = Modifier
                                .size(iconSize)
                                .align(Alignment.Center),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ColorAndStyleOption(
    navController: NavController = NavController(MainActivity.applicationContext()),
    view: View = MainActivity.instance!!.window.decorView
) {
    showNavigation.show = false

    val displayAnimations = remember { mutableStateListOf<CircleStore>() }

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val isDynamic = remember { mutableStateOf(ThemeManager.theme == null) }
    val isDark = remember { mutableStateOf(ThemeManager.darkTheme ?: isSystemInDarkTheme) }
    val isSystemDark = remember { mutableStateOf(ThemeManager.darkTheme == null) }
    val lastSelectectedColour = remember { mutableStateOf(ThemeManager.theme) }




    MultipleExpandingCircleAnimations(displayAnimations)
    Column(Modifier.padding(start = 16.dp, end=16.dp, bottom=16.dp)) {
        PageNAv(stringResource(R.string.color_and_style), navController)


        Box(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
                .fillMaxWidth()
                .background(Color.Transparent)
        ) {
            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(
                        stringResource(R.string.theme_preview),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                    )
                }
                Column(Modifier.padding(16.dp)) {
                    Message(stringResource(R.string.me_msg_1))
                    Spacer(modifier = Modifier.height(8.dp))
                    Message(stringResource(R.string.me_msg_2))
                    Spacer(modifier = Modifier.height(16.dp))
                    Message(message = stringResource(R.string.mez_msg_1), drawable = R.drawable.mez)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        text = stringResource(R.string.oskar_ahh_convo),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 5.sp),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 20.dp)
                    )
                }
            }
        }

        Row(Modifier.fillMaxWidth()) {
            val buttonSelected = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            val buttonDeselected =
                ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp))
            val textSelected = MaterialTheme.colorScheme.onPrimary
            val textDeselected = MaterialTheme.colorScheme.onSurface

            ClickableOverlay(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                onClick = { offset: Offset ->
                    if (isDynamic.value) return@ClickableOverlay
                    val currentColour = getColourScheme(
                        if (isSystemDark.value) isSystemInDarkTheme else isDark.value,
                        ThemeManager.getThemeTheme(),
                        ThemeManager.contrast
                    ).background
                    isDynamic.value = true
                    ThemeManager.theme = null
                    displayAnimations.add(
                        CircleStore(
                            offset = offset,
                            currentColour = currentColour,
                            newColour = getColourScheme(
                                if (isSystemDark.value) isSystemInDarkTheme else isDark.value,
                                null,
                                ThemeManager.contrast
                            ).background,
                        )
                    )
                }
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDynamic.value) buttonSelected.containerColor else buttonDeselected.containerColor)
                        .indication(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current
                        )
                        .animateContentSize(animationSpec = tween(durationMillis = (300 * getAnimationScale()).toInt()))
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    Text(
                        stringResource(R.string.dynamic_colours_option),
                        Modifier
                            .padding(vertical = 8.dp)
                            .align(Alignment.Center),
                        color = animateColorAsState(
                            if (isDynamic.value) textSelected else textDeselected,
                            animationSpec = tween(durationMillis = (300 * getAnimationScale()).toInt()),
                            label = ""
                        ).value
                    )
                }
            }
            ClickableOverlay(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                onClick = { offset: Offset ->
                    if (!isDynamic.value) return@ClickableOverlay
                    val currentColour = getColourScheme(
                        if (isSystemDark.value) isSystemInDarkTheme else isDark.value,
                        ThemeManager.getThemeTheme(),
                        ThemeManager.contrast
                    ).background
                    isDynamic.value = false
                    ThemeManager.theme = lastSelectectedColour.value
                    displayAnimations.add(
                        CircleStore(
                            offset = offset,
                            currentColour = currentColour,
                            newColour = getColourScheme(
                                if (isSystemDark.value) isSystemInDarkTheme else isDark.value,
                                Themes[lastSelectectedColour.value],
                                ThemeManager.contrast
                            ).background,
                        )
                    )
                }
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isDynamic.value) buttonSelected.containerColor else buttonDeselected.containerColor)
                        .indication(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current
                        )
                        .animateContentSize(animationSpec = tween(durationMillis = (300 * getAnimationScale()).toInt()))
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    Text(
                        text = stringResource(R.string.static_colours_option),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .align(Alignment.Center),
                        color = animateColorAsState(
                            if (!isDynamic.value) textSelected else textDeselected,
                            animationSpec = tween(durationMillis = (300 * getAnimationScale()).toInt()),
                            label = ""
                        ).value
                    )
                }
            }
        }

        ColorButtonRow(displayAnimations, isDynamic.value, lastSelectectedColour)

        SettingsElementBase(
            title = stringResource(R.string.system_dark_theme_title),
            desc = stringResource(R.string.system_dark_theme_desc),
            icon = Icons.Outlined.DarkMode,
        )
        {
            ClickableOverlay(
                onClick = { offset: Offset ->
                    isSystemDark.value = !isSystemDark.value
                    ThemeManager.darkTheme =
                        if (isSystemDark.value) isSystemInDarkTheme else isDark.value

                    val circle = CircleStore(
                        offset = offset,
                        currentColour = getColourScheme(
                            if (isSystemDark.value) isDark.value else isSystemInDarkTheme,
                            ThemeManager.getThemeTheme(),
                            ThemeManager.contrast
                        ).background,
                        newColour = getColourScheme(
                            if (isSystemDark.value) isSystemInDarkTheme else isDark.value,
                            ThemeManager.getThemeTheme(),
                            ThemeManager.contrast
                        ).background,
                    )

                    displayAnimations.add(
                        circle
                    )
                }
            ) {
                Switch(
                    checked = isSystemDark.value,
                    onCheckedChange = null
                )
            }

        }
        AnimatedVisibility(
            visible = !isSystemDark.value,
            enter = expandVertically(
                animationSpec = tween(durationMillis = (300 * getAnimationScale()).toInt())

            ),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = (300 * getAnimationScale()).toInt()),
            )
        ) {
            SettingsElementBase(
                title = stringResource(R.string.dark_theme_title),
                desc = if (isDynamic.value) {
                    stringResource(R.string.apply_dark_theme_based_on_wallpaper)
                } else stringResource(R.string.apply_dark_theme, ThemeManager.theme ?: ""),
                icon = Icons.Outlined.DarkMode,
            )
            {
                ClickableOverlay(
                    onClick = { offset: Offset ->
                        if (isSystemDark.value) return@ClickableOverlay
                        isDark.value = !isDark.value
                        ThemeManager.darkTheme = isDark.value
                        val circle = CircleStore(
                            offset = offset,
                            currentColour = getColourScheme(
                                !isDark.value,
                                ThemeManager.getThemeTheme(),
                                ThemeManager.contrast
                            ).background,
                            newColour = getColourScheme(
                                isDark.value,
                                ThemeManager.getThemeTheme(),
                                ThemeManager.contrast
                            ).background,
                        )

                        displayAnimations.add(
                            circle
                        )
                    }
                ) {
                    Switch(
                        enabled = !isSystemDark.value,
                        checked = isDark.value,
                        onCheckedChange = null
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = !isDynamic.value,
            enter = expandVertically(
                animationSpec = tween(durationMillis = (300 * getAnimationScale()).toInt())
            ),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = (300 * getAnimationScale()).toInt()),
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .padding(end = 8.dp, start = 24.dp),
            ) {
                Row {
                    Text(
                        text = stringResource(
                            R.string.contrast, when (ThemeManager.contrast ?: 0f) {
                                in 0f..0.33f -> stringResource(R.string.low_contrast)
                                in 0.34f..0.66f -> stringResource(R.string.medium_contrast)
                                in 0.67f..1f -> stringResource(R.string.high_contrast)
                                else -> "err"
                            }
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Slider(
                    value = ThemeManager.contrast ?: .0f,
                    onValueChange = { ThemeManager.contrast = it },
                    valueRange = 0f..1f,
                    steps = 1,
                )
            }
        }
    }
}
