package com.jetpackduba.gitnuro.theme

import androidx.compose.ui.graphics.Color

/**
 * Caldera-inspired color system.
 *
 * Light theme: warm concrete canvas with cream card surfaces, near-black ink text,
 * and Citra Orange (#FC5000) as the single interactive accent.
 *
 * Dark theme: deep warm charcoal with raised slate surfaces, cream-white text,
 * same accent colors carried over for consistency.
 */

// ── Caldera palette tokens ──────────────────────────────────────────────────
private val ConcreteCanvas = Color(0xFFC4BEB4)   // light bg — warm stone concrete
private val ParchmentCard  = Color(0xFFF0ECE4)   // light surface — warm parchment
private val Ink            = Color(0xFF1A1918)    // warm ink text
private val PaperWhite     = Color(0xFFFFFFFF)    // text on dark / accent surfaces
private val BurntOrange    = Color(0xFFE04000)    // primary action / accent
private val DeepIndigo     = Color(0xFF4438D0)    // decorative / secondary branches
private val AmberGlow      = Color(0xFFB89868)    // gradient end — deep warm amber

// ── Dark mode accent (brighter orange for dark backgrounds) ────────────────
private val CitraOrange    = Color(0xFFFC5000)    // brighter orange for dark mode

// ── Dark mode surface tones (more contrast between layers) ──────────────────
private val CharcoalBase   = Color(0xFF121214)    // dark bg — deepest layer
private val SlateCard      = Color(0xFF1C1C20)    // dark surface — panels, sidebar
private val SlateSecondary = Color(0xFF262630)    // dark secondary — toolbar, elevated
private val SlateTertiary  = Color(0xFF2E2822)    // dark tertiary — warm orange-tinted accent
private val DarkSelected   = Color(0xFF3D2C15)    // dark selection — warm orange hint

// ── Shared semantic colors ──────────────────────────────────────────────────
private val FileAdded      = Color(0xFF2E8C48)    // git: added file — forest green
private val FileDeleted    = Color(0xFFD03030)    // git: deleted file — bold red
private val FileConflict   = Color(0xFFD88020)    // git: conflicting file — deep amber
private val ErrorRed       = Color(0xFFD03030)    // error state


val lightTheme = ColorsScheme(
    primary = BurntOrange,
    primaryVariant = BurntOrange,
    onPrimary = PaperWhite,
    secondary = DeepIndigo,
    onSecondary = PaperWhite,
    onBackground = Ink,
    onBackgroundSecondary = Color(0xFF5C5850),     // warm dark grey — legible on parchment
    error = ErrorRed,
    onError = PaperWhite,
    background = ConcreteCanvas,
    backgroundSelected = Color(0xFFF0B878),        // amber glow selection
    surface = ParchmentCard,
    secondarySurface = Color(0xFFE0DAD0),          // sandstone headers
    tertiarySurface = Color(0xFFD0C9C0),           // toolbar concrete
    addFile = FileAdded,
    deletedFile = FileDeleted,
    modifiedFile = BurntOrange,
    conflictingFile = FileConflict,
    dialogOverlay = Color(0xAA000000),
    normalScrollbar = Color(0xFFA8A498),           // visible on concrete
    hoverScrollbar = BurntOrange,
    diffLineAdded = Color(0xFFCCE8C0),             // saturated mint on parchment
    diffContentAdded = Color(0x6650C040),           // vivid green inline
    diffLineRemoved = Color(0xFFF0CCC4),           // warm terracotta blush
    diffContentRemoved = Color(0x66D84040),         // vivid red inline
    diffKeyword = Color(0xFF1840A0),               // deep navy on cream
    diffAnnotation = Color(0xFFB85800),             // burnt amber numbers
    diffComment = Color(0xFF086828),               // deep forest
    backgroundGradientEnd = AmberGlow,
    isLight = true,
)


val darkTheme = ColorsScheme(
    primary = CitraOrange,
    primaryVariant = Color(0xFFFF8A50),             // lighter orange for text on dark bg
    onPrimary = PaperWhite,
    secondary = Color(0xFF7B74FF),                  // lighter violet for dark mode
    onSecondary = PaperWhite,
    onBackground = Color(0xFFF0EFEB),               // cream-white text
    onBackgroundSecondary = Color(0xFFB0AEA8),      // muted cream
    error = ErrorRed,
    onError = PaperWhite,
    background = CharcoalBase,
    backgroundSelected = DarkSelected,
    surface = SlateCard,
    secondarySurface = SlateSecondary,
    tertiarySurface = SlateTertiary,
    addFile = FileAdded,
    deletedFile = FileDeleted,
    modifiedFile = CitraOrange,
    conflictingFile = FileConflict,
    dialogOverlay = Color(0xAA000000),
    normalScrollbar = Color(0xFF555550),            // warm dark scrollbar
    hoverScrollbar = CitraOrange,
    diffLineAdded = Color(0xAA3A5038),              // dark warm green
    diffContentAdded = Color(0x4530A030),
    diffLineRemoved = Color(0xAA5A3838),            // dark warm red
    diffContentRemoved = Color(0x45C03030),
    diffKeyword = Color(0xFFA0CAF0),                // soft blue keywords
    diffAnnotation = Color(0xFFD0CC60),             // warm annotation yellow
    diffComment = Color(0xFF70C290),                // soft green comments
    backgroundGradientEnd = CharcoalBase,           // no gradient in dark mode
    isLight = false,
)
