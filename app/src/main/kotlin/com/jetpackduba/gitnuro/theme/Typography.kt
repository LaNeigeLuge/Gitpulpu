package com.jetpackduba.gitnuro.theme

import androidx.compose.material.Colors
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import com.jetpackduba.gitnuro.domain.models.ui.Theme
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.sp

val interFontFamily = FontFamily(
    Font("fonts/Inter/Inter_18pt-Regular.ttf", FontWeight.Normal, FontStyle.Normal),
    Font("fonts/Inter/Inter_18pt-Italic.ttf", FontWeight.Normal, FontStyle.Italic),
    Font("fonts/Inter/Inter_18pt-Medium.ttf", FontWeight.Medium, FontStyle.Normal),
    Font("fonts/Inter/Inter_18pt-MediumItalic.ttf", FontWeight.Medium, FontStyle.Italic),
    Font("fonts/Inter/Inter_18pt-SemiBold.ttf", FontWeight.SemiBold, FontStyle.Normal),
    Font("fonts/Inter/Inter_18pt-SemiBoldItalic.ttf", FontWeight.SemiBold, FontStyle.Italic),
    Font("fonts/Inter/Inter_18pt-Bold.ttf", FontWeight.Bold, FontStyle.Normal),
    Font("fonts/Inter/Inter_18pt-BoldItalic.ttf", FontWeight.Bold, FontStyle.Italic),
)

val notoSansMonoFontFamily = FontFamily(
    Font("fonts/NotoSansMono/NotoSansMono-Regular.ttf", FontWeight.Normal, FontStyle.Normal),
    Font("fonts/NotoSansMono/NotoSansMono-Italic.ttf", FontWeight.Normal, FontStyle.Italic),
    Font("fonts/NotoSansMono/NotoSansMono-Medium.ttf", FontWeight.Medium, FontStyle.Normal),
    Font("fonts/NotoSansMono/NotoSansMono-MediumItalic.ttf", FontWeight.Medium, FontStyle.Italic),
    Font("fonts/NotoSansMono/NotoSansMono-SemiBold.ttf", FontWeight.SemiBold, FontStyle.Normal),
    Font("fonts/NotoSansMono/NotoSansMono-SemiBoldItalic.ttf", FontWeight.SemiBold, FontStyle.Italic),
    Font("fonts/NotoSansMono/NotoSansMono-Bold.ttf", FontWeight.Bold, FontStyle.Normal),
    Font("fonts/NotoSansMono/NotoSansMono-BoldItalic.ttf", FontWeight.Bold, FontStyle.Italic),
)

/**
 * Jost — the Claymakers face. Static instances, not the variable file: probe P2c proved
 * Compose Desktop's file loader ignores variable axes entirely and fakes bold instead.
 * No 700 cut on purpose — Compose resolves FontWeight.Bold to the 800 ExtraBold, which is
 * the brief's display weight.
 */
val jostFontFamily = FontFamily(
    Font("fonts/Jost/Jost-Light.ttf", FontWeight.Light, FontStyle.Normal),
    Font("fonts/Jost/Jost-LightItalic.ttf", FontWeight.Light, FontStyle.Italic),
    Font("fonts/Jost/Jost-Regular.ttf", FontWeight.Normal, FontStyle.Normal),
    Font("fonts/Jost/Jost-Italic.ttf", FontWeight.Normal, FontStyle.Italic),
    Font("fonts/Jost/Jost-Medium.ttf", FontWeight.Medium, FontStyle.Normal),
    Font("fonts/Jost/Jost-MediumItalic.ttf", FontWeight.Medium, FontStyle.Italic),
    Font("fonts/Jost/Jost-SemiBold.ttf", FontWeight.SemiBold, FontStyle.Normal),
    Font("fonts/Jost/Jost-SemiBoldItalic.ttf", FontWeight.SemiBold, FontStyle.Italic),
    Font("fonts/Jost/Jost-ExtraBold.ttf", FontWeight.ExtraBold, FontStyle.Normal),
    Font("fonts/Jost/Jost-ExtraBoldItalic.ttf", FontWeight.ExtraBold, FontStyle.Italic),
)

const val LETTER_SPACING = 0.3
const val HEADING_LETTER_SPACING = 0.5

/** Body face for the active theme. Claymakers is the only one that leaves Inter. */
val LocalBodyFontFamily = compositionLocalOf { interFontFamily }

@Composable
fun typography(composeColors: Colors, selectedTheme: Theme = Theme.CalderaNight) = Typography(
    defaultFontFamily = if (selectedTheme == Theme.Claymakers) jostFontFamily else interFontFamily,
    h1 = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight.ExtraBold,
        color = composeColors.onBackground,
        letterSpacing = HEADING_LETTER_SPACING.sp,
    ),
    h2 = TextStyle(
        fontSize = 27.sp,
        fontWeight = FontWeight.ExtraBold,
        color = composeColors.onBackground,
        letterSpacing = HEADING_LETTER_SPACING.sp,
    ),
    h3 = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = composeColors.onBackground,
        letterSpacing = HEADING_LETTER_SPACING.sp,
    ),
    h4 = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = composeColors.onBackground,
        letterSpacing = LETTER_SPACING.sp,
    ),
    body1 = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = composeColors.onBackground,
        letterSpacing = LETTER_SPACING.sp,
    ),
    body2 = TextStyle(
        fontSize = 15.sp,
        color = composeColors.onBackground,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp,
    ),
    caption = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = composeColors.onBackground,
        letterSpacing = LETTER_SPACING.sp,
    )
)