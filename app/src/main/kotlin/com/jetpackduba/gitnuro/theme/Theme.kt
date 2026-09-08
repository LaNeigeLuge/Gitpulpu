@file:Suppress("unused")

package com.jetpackduba.gitnuro.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jetpackduba.gitnuro.domain.models.ui.LinesHeightType
import com.jetpackduba.gitnuro.domain.models.ui.Theme
import com.jetpackduba.gitnuro.ui.dropdowns.DropDownOption
import kotlinx.coroutines.flow.MutableStateFlow

private val defaultAppTheme: ColorsScheme = calderaNightTheme
private var appTheme: MutableStateFlow<ColorsScheme> = MutableStateFlow(defaultAppTheme)
internal val LocalLinesHeight = compositionLocalOf { spacedLineHeight }
internal val LocalGraphColors = compositionLocalOf { calderaGraphColors }

/** Side-panel watermark. Needs to be *darker* than the ground to read, so it is per-theme. */
internal val LocalWatermarkTint = compositionLocalOf { Color(0x14FFFFFF) }

/**
 * Alpha for per-status file-row washes. Claymakers carries status in the row colour with ink
 * text on top; every other theme leaves rows flat, so 0f is the default and nothing changes.
 */
internal val LocalRowWashAlpha = compositionLocalOf { 0f }

class LinesHeight internal constructor(
    val fileHeight: Dp,
    val logCommitHeight: Dp,
    val sidePanelItemHeight: Dp,
)

val spacedLineHeight = LinesHeight(
    fileHeight = 46.dp,
    logCommitHeight = 46.dp,
    sidePanelItemHeight = 44.dp
)

val compactLineHeight = LinesHeight(
    fileHeight = 42.dp,
    logCommitHeight = 42.dp,
    sidePanelItemHeight = 40.dp
)

@Composable
fun AppTheme(
    selectedTheme: Theme = Theme.CalderaNight,
    linesHeightType: LinesHeightType = LinesHeightType.COMPACT,
    customTheme: ColorsScheme? = null,
    content: @Composable () -> Unit,
) {
    val theme = when (selectedTheme) {
        Theme.CalderaNight -> calderaNightTheme
        Theme.Claymakers -> claymakersTheme
        Theme.ClaymakersNight -> claymakersNightTheme
        Theme.Custom -> customTheme ?: defaultAppTheme
    }

    val lineHeight = when (linesHeightType) {
        LinesHeightType.SPACED -> spacedLineHeight
        LinesHeightType.COMPACT -> compactLineHeight
    }

    appTheme.value = theme

    val composeColors = theme.toComposeColors()
    val isClay = selectedTheme == Theme.Claymakers || selectedTheme == Theme.ClaymakersNight
    val bodyFamily = if (isClay) jostFontFamily else interFontFamily
    val laneColors = if (isClay) claymakersGraphColors else calderaGraphColors
    // salmon, and dark enough to actually show on a light ground
    // the watermark has to sit on the opposite side of its ground to read at all
    val watermark = when (selectedTheme) {
        Theme.Claymakers -> Color(0x2EC97B69)        // salmon, darker than paper
        Theme.ClaymakersNight -> Color(0x3DE8968E)   // salmon, lighter than the night ground
        else -> Color(0x0AFFFFFF)
    }
    val rowWash = if (isClay) 0.22f else 0f
    val compositionValues = arrayOf(
        LocalLinesHeight provides lineHeight,
        LocalBodyFontFamily provides bodyFamily,
        LocalGraphColors provides laneColors,
        LocalWatermarkTint provides watermark,
        LocalRowWashAlpha provides rowWash,
    )

    val shapes = Shapes(
        small = AppShapes.small,
        medium = AppShapes.medium,
        large = AppShapes.large,
    )

    CompositionLocalProvider(values = compositionValues) {
        MaterialTheme(
            colors = composeColors,
            shapes = shapes,
            content = content,
            typography = typography(composeColors, selectedTheme),
        )
    }

}

val MaterialTheme.linesHeight: LinesHeight
    @Composable
    @ReadOnlyComposable
    get() = LocalLinesHeight.current

/** Commit-graph lane sequence for the active theme. */
val MaterialTheme.graphColors: List<Color>
    @Composable
    @ReadOnlyComposable
    get() = LocalGraphColors.current

/** Tint for the side-panel watermark. */
val MaterialTheme.watermarkTint: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalWatermarkTint.current

/** Per-status file-row wash alpha. 0f means flat rows. */
val MaterialTheme.rowWashAlpha: Float
    @Composable
    @ReadOnlyComposable
    get() = LocalRowWashAlpha.current


private val theme: ColorsScheme
    @Composable
    get() = appTheme.collectAsState().value

val Colors.backgroundSelected: Color
    @Composable
    get() = theme.backgroundSelected

val Colors.backgroundGradientEnd: Color
    @Composable
    get() = theme.backgroundGradientEnd

val Colors.onBackgroundSecondary: Color
    @Composable
    get() = theme.onBackgroundSecondary

val Colors.secondarySurface: Color
    @Composable
    get() = theme.secondarySurface

val Colors.tertiarySurface: Color
    @Composable
    get() = theme.tertiarySurface

val Colors.addFile: Color
    @Composable
    get() = theme.addFile

val Colors.deleteFile: Color
    @Composable
    get() = theme.deletedFile

val Colors.modifyFile: Color
    @Composable
    get() = theme.modifiedFile

val Colors.conflictFile: Color
    @Composable
    get() = theme.conflictingFile

val Colors.abortButton: Color
    @Composable
    get() = theme.error

val Colors.scrollbarNormal: Color
    @Composable
    get() = theme.normalScrollbar

val Colors.scrollbarHover: Color
    @Composable
    get() = theme.hoverScrollbar

val Colors.dialogOverlay: Color
    @Composable
    get() = theme.dialogOverlay

val Colors.diffLineAdded: Color
    @Composable
    get() = theme.diffLineAdded


val Colors.diffContentAdded: Color
    @Composable
    get() = theme.diffContentAdded

val Colors.diffLineRemoved: Color
    @Composable
    get() = theme.diffLineRemoved

val Colors.diffContentRemoved: Color
    @Composable
    get() = theme.diffContentRemoved

val Colors.diffKeyword: Color
    @Composable
    get() = theme.diffKeyword

val Colors.diffAnnotation: Color
    @Composable
    get() = theme.diffAnnotation

val Colors.diffComment: Color
    @Composable
    get() = theme.diffComment

val Colors.isDark: Boolean
    get() = !this.isLight


// TODO Do not hardcode theme here and use proper string resource
val themeLists = listOf(
    DropDownOption(Theme.CalderaNight, "Caldera Night"),
    DropDownOption(Theme.Claymakers, "Claymakers"),
    DropDownOption(Theme.ClaymakersNight, "Claymakers Night"),
    DropDownOption(Theme.Custom, "Custom"),
)