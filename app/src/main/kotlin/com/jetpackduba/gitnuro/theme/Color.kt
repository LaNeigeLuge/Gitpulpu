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


// ── "Radioactive Dreams" palette ────────────────────────────────────────────
// A screencapture from a lost PS1 game rendered in Bryce 3D: a toxic swamp world
// drowned in nuclear haze. Phosphor-green UI glowing off a green-black CRT void,
// with chromatic-aberration magenta + cyan bleeding at the edges like a glitched
// PSX framebuffer. Low-bit murk, dithered depth, radioactive dream logic.
private val VoidMurk      = Color(0xFF050A07)    // deepest bg — green-black CRT void
private val SwampSurface  = Color(0xFF0B1610)    // panels, sidebar — submerged murk
private val SwampElevated = Color(0xFF11211A)    // toolbar, elevated surfaces
private val ReactorTint   = Color(0xFF16281D)    // green-lit accent surface
private val OozeSelection = Color(0xFF2A1030)    // selection — glitch magenta-purple bleed
private val ToxicHaze     = Color(0xFF0A1E16)    // gradient end — radioactive sky haze

private val ReactorCore   = Color(0xFF2BFF88)    // nuclear green — primary phosphor accent
private val ReactorGlow   = Color(0xFF7CFFB0)    // lighter green — accent text on dark
private val UraniumAcid   = Color(0xFFB6FF3C)    // acid yellow-green — modified files
private val GlitchMagenta = Color(0xFFFF2D95)    // chromatic magenta — deleted / secondary
private val GlitchRed     = Color(0xFFFF2D5A)    // hot glitch red — error / abort
private val PsxCyan       = Color(0xFF23E0FF)    // chromatic cyan — keywords
private val HazmatAmber   = Color(0xFFFFC53D)    // caution yellow — conflicts

private val PhosphorText  = Color(0xFFC8FFD8)    // phosphorescent screen text
private val PhosphorDim   = Color(0xFF6E9E80)    // muted swamp-green secondary text

val radioactiveDreamsTheme = ColorsScheme(
    primary = ReactorCore,
    primaryVariant = ReactorGlow,
    onPrimary = VoidMurk,                          // dark text on bright nuclear green
    secondary = GlitchMagenta,
    onSecondary = VoidMurk,
    onBackground = PhosphorText,
    onBackgroundSecondary = PhosphorDim,
    error = GlitchRed,
    onError = VoidMurk,
    background = VoidMurk,
    backgroundSelected = OozeSelection,
    surface = SwampSurface,
    secondarySurface = SwampElevated,
    tertiarySurface = ReactorTint,
    addFile = ReactorCore,
    deletedFile = GlitchMagenta,
    modifiedFile = UraniumAcid,
    conflictingFile = HazmatAmber,
    dialogOverlay = Color(0xCC02060A),             // deep toxic dim
    normalScrollbar = Color(0xFF1E3A2A),           // dark swamp scrollbar
    hoverScrollbar = ReactorCore,
    diffLineAdded = Color(0xAA103322),             // dark radioactive-green wash
    diffContentAdded = Color(0x552BFF88),          // nuclear green inline
    diffLineRemoved = Color(0xAA331028),           // dark glitch-magenta wash
    diffContentRemoved = Color(0x55FF2D95),        // magenta inline
    diffKeyword = PsxCyan,                          // chromatic cyan keywords
    diffAnnotation = HazmatAmber,                   // hazard-yellow numbers
    diffComment = PhosphorDim,                       // dim swamp comments
    backgroundGradientEnd = ToxicHaze,              // vertical radioactive haze
    isLight = false,
)


// ── "Gen X Soft Club" palette ────────────────────────────────────────────────
// A late-night 1990s nightclub shot on expired 35mm: a dim warm-smoke room, velvet
// and chrome surfaces, soft diffused neon in dusty teal, mauve and amber bleeding
// through hazy air. Low contrast, desaturated, faded warmth — wistful glamour, not
// bright modern club energy. Softness over sharpness; nothing is neon-hard here.
private val SmokeRoom     = Color(0xFF191620)    // dim warm-smoke bg — the dark room
private val VelvetPanel   = Color(0xFF221E2A)    // panels, sidebar — worn velvet
private val ChromeDusk    = Color(0xFF2A2532)    // toolbar, elevated — dusty chrome
private val AmberBooth    = Color(0xFF2C2822)    // amber-lit accent surface
private val NeonHalo      = Color(0xFF2E2838)    // selection — soft mauve glow
private val FloorHaze     = Color(0xFF1F2329)    // gradient end — cool neon floor-haze

private val FadedTeal     = Color(0xFF6DB3AB)    // dusty teal neon — primary accent
private val TealBloom     = Color(0xFF8FC9C1)    // lighter teal — accent text on dark
private val DustyMauve    = Color(0xFFBC8FB2)    // dusty rose/mauve neon — secondary
private val MutedAmber    = Color(0xFFD6A868)    // muted amber neon — modified/highlight
private val SageTeal      = Color(0xFF83B8A0)    // soft sage-teal — added
private val FadedRose     = Color(0xFFCF8794)    // faded rose — deleted / error
private val DuskyOrange   = Color(0xFFCE9463)    // dusky orange — conflict
private val LavenderInk   = Color(0xFFAF9BC9)    // soft lavender — keywords

private val FadedCream    = Color(0xFFDED4C6)    // warm faded-film text (low contrast)
private val SmokeGray     = Color(0xFF928A94)    // dusty mauve-gray — secondary text

val genXSoftClubTheme = ColorsScheme(
    primary = FadedTeal,
    primaryVariant = TealBloom,
    onPrimary = SmokeRoom,                         // dark text on soft teal
    secondary = DustyMauve,
    onSecondary = SmokeRoom,
    onBackground = FadedCream,
    onBackgroundSecondary = SmokeGray,
    error = FadedRose,
    onError = SmokeRoom,
    background = SmokeRoom,
    backgroundSelected = NeonHalo,
    surface = VelvetPanel,
    secondarySurface = ChromeDusk,
    tertiarySurface = AmberBooth,
    addFile = SageTeal,
    deletedFile = FadedRose,
    modifiedFile = MutedAmber,
    conflictingFile = DuskyOrange,
    dialogOverlay = Color(0xAA141019),             // warm smoke haze, not hard black
    normalScrollbar = Color(0xFF3A3440),           // dusty velvet scrollbar
    hoverScrollbar = FadedTeal,
    diffLineAdded = Color(0x552E4A46),             // soft dark-teal wash
    diffContentAdded = Color(0x4483B8A0),          // sage-teal inline
    diffLineRemoved = Color(0x554A3236),           // soft dark-rose wash
    diffContentRemoved = Color(0x44CF8794),        // faded-rose inline
    diffKeyword = LavenderInk,                      // soft lavender keywords
    diffAnnotation = Color(0xFFC9A56B),             // muted amber numbers
    diffComment = Color(0xFF7E8A82),                 // quiet smoke-green comments
    backgroundGradientEnd = FloorHaze,              // gentle vertical neon haze
    isLight = false,
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
