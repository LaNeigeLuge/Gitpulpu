package com.jetpackduba.gitnuro.theme

import androidx.compose.ui.graphics.Color

/**
 * Two themes.
 *
 * **Caldera Night** — deep warm charcoal, raised slate surfaces, cream-white text,
 * Citra Orange as the single interactive accent.
 *
 * **Claymakers** — watercolour light theme: pastel washes on near-white paper,
 * green as the action accent, ink for all text. See Design-Spec-Claymakers in the vault.
 */

// ── Caldera Night palette tokens ────────────────────────────────────────────
private val PaperWhite     = Color(0xFFFFFFFF)    // text on dark / accent surfaces

private val CitraOrange    = Color(0xFFFC5000)    // brighter orange for dark mode

// ── surfaces ────────────────────────────────────────────────────────────────
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

val calderaNightTheme = ColorsScheme(
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

// ── Claymakers palette tokens ───────────────────────────────────────────────
// The brief's four colours, plus tints. Surfaces are deliberately *tinted* rather than
// near-white: at #FDF8F5 paper the screen read as white, so the whole set is pushed a step
// warmer and more saturated, salmon-leaning. Every wash below still holds Ink at 9-12:1.
private val Green      = Color(0xFF9BC400)   // action accent — FILL ONLY, 1.94:1 on paper
private val GreenDeep  = Color(0xFF6D8C10)   // hover / pressed
private val Lilac      = Color(0xFF8076A3)   // decorative secondary
private val Plum       = Color(0xFF7C677F)   // given

// surfaces — warmer and stronger than the brief's, to get colour onto the screen
private val ClayGround = Color(0xFFF7E4DC)   // page ground — salmon paper
private val ClayPaper  = Color(0xFFFDF1EE)   // panel surface
private val ClayVeil   = Color(0xFFE4DEEF)   // rails, headers — lilac veil
private val ClayRaised = Color(0xFFF9D9D1)   // chips, raised — deeper salmon
private val ClayPicked = Color(0xFFCBC1DE)   // selection — lilac, ink still 7.9:1
private val ClayEdge   = Color(0xFFF3ECE7)   // gradient end

// status washes — richer than the brief's pales so rows carry real colour
private val WashAdd    = Color(0xFFD8E6A8)   // ink 10.24:1
private val WashDel    = Color(0xFFF0C9C3)   // ink  8.97:1
private val WashMod    = Color(0xFFEED9A8)   // ink  9.80:1
private val WashConf   = Color(0xFFD9D0EA)   // ink  9.18:1

// Tried stronger diff bands (#B9D75F / #EE9E92) for findability. Reverted: on a newly added
// file every line is an add, so the whole pane became a solid green block. In a diff the wash
// has to stay a wash.

// text
private val InkPlum    = Color(0xFF33284A)   // primary text — 12.31:1 on ClayPaper
private val InkSoft    = Color(0xFF564E66)   // secondary — darkened from #6B6180 so it also
                                             // clears AA on ClayPicked (4.57:1)

// Status/syntax tier, darkened again to hold 4.5:1 on the *richer* washes above.
private val AddInk     = Color(0xFF546B0C)   // 4.53 on WashAdd
private val DelInk     = Color(0xFF94413A)   // 4.50 on WashDel
private val ModInk     = Color(0xFF7A5A27)   // 4.56 on WashMod
private val ConfInk    = Color(0xFF675569)   // 4.60 on WashConf
private val KeywordInk = Color(0xFF736799)   // 4.60 on ClayPaper

/**
 * Claymakers — watercolour light theme.
 *
 * Status is carried by the row wash plus a coloured glyph; body text stays [InkPlum] at
 * 12.91:1 throughout. That is deliberate: a pastel palette on near-white paper cannot hold
 * small coloured text at AA, so colour lives in the background instead of the type.
 */
val claymakersTheme = ColorsScheme(
    primary = Green,
    primaryVariant = AddInk,                        // accent-TEXT role — Green is 1.94:1
    onPrimary = InkPlum,
    secondary = Lilac,
    onSecondary = ClayPaper,
    onBackground = InkPlum,
    onBackgroundSecondary = InkSoft,
    error = DelInk,
    onError = ClayPaper,
    background = ClayGround,
    backgroundSelected = ClayPicked,
    surface = ClayPaper,
    secondarySurface = ClayVeil,
    tertiarySurface = ClayRaised,
    addFile = AddInk,
    deletedFile = DelInk,
    modifiedFile = ModInk,
    conflictingFile = ConfInk,
    dialogOverlay = Color(0x8833284A),
    normalScrollbar = ClayPicked,
    hoverScrollbar = Lilac,
    diffLineAdded = WashAdd,
    diffContentAdded = Color(0x669BC400),
    diffLineRemoved = WashDel,
    diffContentRemoved = Color(0x66C4706A),
    diffKeyword = KeywordInk,
    diffAnnotation = ModInk,
    diffComment = InkSoft,
    backgroundGradientEnd = ClayEdge,
    isLight = true,
)

/**
 * Commit-graph lanes. Separate from [ColorsScheme] because the graph is Canvas-drawn and the
 * palette is a sequence of roles, not a set. Provided per-theme via LocalGraphColors.
 */
val calderaGraphColors = listOf(
    Color(0xFFE06020), Color(0xFF7B8BDD), Color(0xFF5AAA70), Color(0xFFCC9530),
    Color(0xFFD06878), Color(0xFF4AA8B8), Color(0xFF9980CC), Color(0xFFD88840),
)

/**
 * Lane and chip colours — one palette so a branch's graph line and its pill are the same colour.
 *
 * These are the vibrant tier: each clears 4.5:1 with Ink, so a chip can carry dark text like the
 * Stage-all button does. As a hairline on paper they measure 1.5-2.3:1, below WCAG's 3:1 for
 * graphics, which is why the lane stroke is drawn a little thicker on light themes.
 */
val claymakersGraphColors = listOf(
    Color(0xFF9BC400),    // green   ink 6.67:1 — same as the Stage-all button
    Color(0xFFB0A6D4),    // lilac   ink 5.99:1
    Color(0xFFE8968E),    // clay    ink 5.95:1
    Color(0xFFE0B662),    // ochre   ink 7.15:1
    Color(0xFFB49BB7),    // plum    ink 5.39:1
    Color(0xFFA99FC9),    // iris    ink 5.50:1
    Color(0xFF95BCC4),    // slate   ink 6.65:1
    Color(0xFFB3D92E),    // lime    ink 8.35:1
)

// ── Claymakers Night palette tokens ─────────────────────────────────────────
// The light theme inverted rather than re-derived: its ink #33284A becomes the ground, and its
// vibrant lane/chip tier becomes the foreground. Contrast is symmetric, so every colour built
// to hold dark ink also reads as light-on-dark — the vibrant set lands at 6.4-9.9:1 here,
// better than it does on paper.
private val NightGround = Color(0xFF1C1629)   // page ground — deepest plum
private val NightPanel  = Color(0xFF241D35)   // panel surface
private val NightVeil   = Color(0xFF2E2542)   // rails, headers
private val NightRaised = Color(0xFF3A2F52)   // chips, raised
private val NightPicked = Color(0xFF3F3459)   // selection — NOT #463A63: that left clay/plum/
                                              // iris at 4.06-4.49:1. This clears 4.5 for all three.
private val NightEdge   = Color(0xFF161122)   // gradient end

private val NightText   = Color(0xFFFDF1EE)   // primary — 14.59:1 on panel (the light theme's paper)
private val NightSoft   = Color(0xFFA99CC4)   // secondary — 6.33:1

// dark diff bands: kept as washes, same lesson as the light theme — on a newly added file
// every line is an add, so a strong band swallows the pane
private val NightWashAdd = Color(0xFF33421F)  // band 1.49 vs panel, text 9.79:1
private val NightWashDel = Color(0xFF52272F)  // band 1.30 vs panel, text 11.24:1

/**
 * Claymakers Night — the watercolour palette after dark.
 *
 * Same structure as the light theme: status lives in the row wash and a coloured glyph, text
 * stays at high contrast. Depth is genuinely available here in a way it is not on paper —
 * probe C2 showed black shadows are invisible on dark grounds but coloured ones read clearly,
 * so a green shadow sits at 8.6:1 against the ground and works as a glow.
 */
val claymakersNightTheme = ColorsScheme(
    primary = Green,
    primaryVariant = Color(0xFFB3D92E),             // lime — accent text, 9.89:1 on panel
    onPrimary = NightGround,                        // dark text on the vibrant fill, as on light
    secondary = Color(0xFFB0A6D4),                  // lilac
    onSecondary = NightGround,
    onBackground = NightText,
    onBackgroundSecondary = NightSoft,
    error = Color(0xFFE8968E),
    onError = NightGround,
    background = NightGround,
    backgroundSelected = NightPicked,
    surface = NightPanel,
    secondarySurface = NightVeil,
    tertiarySurface = NightRaised,
    addFile = Green,                                // 7.90:1
    deletedFile = Color(0xFFE8968E),                // 7.05:1
    modifiedFile = Color(0xFFE0B662),               // 8.48:1
    conflictingFile = Color(0xFFC9B4CB),            // 8.35:1
    dialogOverlay = Color(0xB3120E1C),
    normalScrollbar = NightRaised,
    hoverScrollbar = Color(0xFFB0A6D4),
    diffLineAdded = NightWashAdd,
    diffContentAdded = Color(0x559BC400),
    diffLineRemoved = NightWashDel,
    diffContentRemoved = Color(0x55E8968E),
    diffKeyword = Color(0xFFB0A6D4),                // 7.10:1
    diffAnnotation = Color(0xFFE0B662),             // 8.48:1
    diffComment = NightSoft,                        // 6.33:1
    backgroundGradientEnd = NightEdge,
    isLight = false,
)
