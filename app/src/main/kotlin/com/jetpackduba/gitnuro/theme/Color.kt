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
private val ConcreteCanvas = Color(0xFFE2E2DF)   // light bg — warm stone gray
private val CreamCard      = Color(0xFFF7F6F2)   // light surface — one step brighter
private val Ink            = Color(0xFF070607)    // near-black text / borders
private val PaperWhite     = Color(0xFFFFFFFF)    // text on dark / accent surfaces
private val CitraOrange    = Color(0xFFFC5000)    // primary action / accent
private val IonViolet      = Color(0xFF524AE9)    // decorative / secondary branches
private val HazardYellow   = Color(0xFFF5F28E)    // warnings, tags, highlights

// ── Dark mode surface tones (more contrast between layers) ──────────────────
private val CharcoalBase   = Color(0xFF121214)    // dark bg — deepest layer
private val SlateCard      = Color(0xFF1C1C20)    // dark surface — panels, sidebar
private val SlateSecondary = Color(0xFF262630)    // dark secondary — toolbar, elevated
private val SlateTertiary  = Color(0xFF2E2822)    // dark tertiary — warm orange-tinted accent
private val DarkSelected   = Color(0xFF3D2C15)    // dark selection — warm orange hint

// ── Shared semantic colors ──────────────────────────────────────────────────
private val FileAdded      = Color(0xFF32A852)    // git: added file
private val FileDeleted    = Color(0xFFC93838)    // git: deleted file
private val FileConflict   = Color(0xFFE8A530)    // git: conflicting file
private val ErrorRed       = Color(0xFFC93838)    // error state


val lightTheme = ColorsScheme(
    primary = CitraOrange,
    primaryVariant = CitraOrange,
    onPrimary = PaperWhite,
    secondary = IonViolet,
    onSecondary = PaperWhite,
    onBackground = Ink,
    onBackgroundSecondary = Color(0xFF6B6A68),
    error = ErrorRed,
    onError = PaperWhite,
    background = ConcreteCanvas,
    backgroundSelected = Color(0xC0E8D4C0),       // warm peach selection on concrete
    surface = CreamCard,
    secondarySurface = Color(0xFFE8E6E0),          // slightly darker cream
    tertiarySurface = Color(0xFFDBD8D0),           // warm taupe
    addFile = FileAdded,
    deletedFile = FileDeleted,
    modifiedFile = CitraOrange,
    conflictingFile = FileConflict,
    dialogOverlay = Color(0xAA000000),
    normalScrollbar = Color(0xFFC4C3BF),           // warm gray scrollbar
    hoverScrollbar = CitraOrange,
    diffLineAdded = Color(0xAAD5E8CD),             // warm green line bg
    diffContentAdded = Color(0x4560C060),           // green highlight
    diffLineRemoved = Color(0xAAF0D4D0),           // warm red line bg
    diffContentRemoved = Color(0x45E04040),         // red highlight
    diffKeyword = Color(0xFF3070B8),               // keyword blue on cream
    diffAnnotation = Color(0xFF8F8520),             // annotation olive
    diffComment = Color(0xFF0D8040),               // comment green
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
    isLight = false,
)
