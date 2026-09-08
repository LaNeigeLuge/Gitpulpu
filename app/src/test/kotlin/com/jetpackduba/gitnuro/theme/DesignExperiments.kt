package com.jetpackduba.gitnuro.theme

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetpackduba.gitnuro.domain.models.ui.Theme
import org.jetbrains.skia.EncodedImageFormat
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Design capability experiments. Each one converts a guessed index in
 * Projects/Gitpulpu/Design-Capability-Map.md into a measured fact.
 *
 * These are look-at-it probes, not assertions: they render real Compose through Skia's
 * CPU backend to PNGs in app/build/shots/. They prove a capability works and what it
 * looks like. They prove nothing about frame rate.
 */
private val shotsDir = File("build/shots")
private const val DENSITY = 2f

private fun shot(
    name: String,
    widthDp: Int,
    heightDp: Int,
    theme: Theme = Theme.CalderaNight,
    atNanos: Long = 0L,
    content: @Composable () -> Unit,
) {
    shotsDir.mkdirs()
    val scene = ImageComposeScene(
        width = (widthDp * DENSITY).toInt(),
        height = (heightDp * DENSITY).toInt(),
        density = Density(DENSITY),
    ) {
        AppTheme(selectedTheme = theme) {
            Box(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) { content() }
        }
    }
    try {
        val data = scene.render(atNanos).encodeToData(EncodedImageFormat.PNG)
            ?: error("Skia returned no PNG for $name")
        File(shotsDir, "$name.png").writeBytes(data.bytes)
    } finally {
        scene.close()
    }
}

// ── E1 · variable font axes ────────────────────────────────────────────────
// Does Font(File, ..., FontVariation.Settings) actually instance an axis on the
// desktop loader, or silently render the default master?
private val VF = File(
    "/tmp/claude-1000/-home-julienrose-Documents-Gitpulpu/" +
        "b0970b01-01f0-4f75-ad60-c0f967bc1b93/scratchpad/expfonts/Figtree-VF.ttf"
)

private fun vfAt(weight: Int) = FontFamily(
    Font(
        VF,
        FontWeight(weight),
        FontStyle.Normal,
        FontVariation.Settings(FontVariation.weight(weight)),
    )
)

// ── E3 · colour-matrix duotone ─────────────────────────────────────────────
/** Maps luminance onto a two-colour ramp. The fix for noisy remote avatars. */
private fun duotone(dark: Color, light: Color): ColorFilter {
    fun row(d: Float, l: Float) = floatArrayOf(
        0.2126f * (l - d), 0.7152f * (l - d), 0.0722f * (l - d), 0f, d * 255f
    )
    return ColorFilter.colorMatrix(
        ColorMatrix(
            row(dark.red, light.red) +
                row(dark.green, light.green) +
                row(dark.blue, light.blue) +
                floatArrayOf(0f, 0f, 0f, 1f, 0f)
        )
    )
}

/** Stand-in for a Gravatar identicon: busy, high-chroma, fights any palette. */
private fun noisyAvatar(): ImageBitmap {
    val size = 60
    val bmp = ImageBitmap(size, size)
    val canvas = androidx.compose.ui.graphics.Canvas(bmp)
    val cells = 5
    val cell = size.toFloat() / cells
    val palette = listOf(
        Color(0xFF2F6FD0), Color(0xFF34A853), Color(0xFFEA4335),
        Color(0xFFFBBC05), Color(0xFF7B3FA0),
    )
    var n = 7
    for (y in 0 until cells) for (x in 0 until cells) {
        n = (n * 1103515245 + 12345) and 0x7FFFFFFF
        val paint = Paint().apply { color = palette[(n shr 8) % palette.size] }
        canvas.drawRect(x * cell, y * cell, (x + 1) * cell, (y + 1) * cell, paint)
    }
    return bmp
}

private fun DrawScope.avatar(bmp: ImageBitmap, at: Offset, filter: ColorFilter?) {
    drawImage(image = bmp, dstOffset = androidx.compose.ui.unit.IntOffset(at.x.toInt(), at.y.toInt()), colorFilter = filter)
}

class DesignExperiments {

    /** E1 — variable weight axis 300→900 from a single file. */
    @Test
    fun `e1 variable font axis`() {
        require(VF.exists()) { "variable font missing at ${VF.path}" }
        shot("e1-variable-font", widthDp = 460, heightDp = 300) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(300, 400, 500, 600, 700, 800, 900).forEach { w ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "wght $w",
                            fontFamily = vfAt(w),
                            fontSize = 22.sp,
                            color = MaterialTheme.colors.onBackground,
                        )
                    }
                }
            }
        }
    }

    /** E2 — Modifier.shadow. The app is elevation 0 throughout; is depth worth having? */
    @Test
    fun `e2 shadow elevations`() {
        listOf(Theme.CalderaNight to "dark", Theme.Claymakers to "light").forEach { (t, label) ->
            shot("e2-shadow-$label", widthDp = 460, heightDp = 150, theme = t) {
                Row(
                    Modifier.padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    listOf(0, 2, 6, 16).forEach { e ->
                        Box(
                            Modifier
                                .shadow(e.dp, RoundedCornerShape(12.dp))
                                .size(92.dp, 72.dp)
                                .background(MaterialTheme.colors.surface, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("${e}dp", color = MaterialTheme.colors.onBackground, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    /** E3 — duotone vs raw vs flat tint on a synthetic identicon. */
    @Test
    fun `e3 colour matrix duotone`() {
        val bmp = noisyAvatar()
        shot("e3-duotone", widthDp = 380, heightDp = 130) {
            val dark = MaterialTheme.colors.background
            val accent = MaterialTheme.colors.primary
            Box(Modifier.fillMaxSize().drawWithContent {
                drawContent()
                val y = 34f * DENSITY
                avatar(bmp, Offset(30f * DENSITY, y), null)
                avatar(bmp, Offset(140f * DENSITY, y), ColorFilter.tint(accent))
                avatar(bmp, Offset(250f * DENSITY, y), duotone(dark, accent))
            }) {
                Row(Modifier.fillMaxWidth().padding(top = 96.dp), horizontalArrangement = Arrangement.spacedBy(50.dp)) {
                    listOf("raw", "tint", "duotone").forEach {
                        Text(it, Modifier.padding(start = 26.dp), color = MaterialTheme.colors.onBackground, fontSize = 11.sp)
                    }
                }
            }
        }
    }

    /** E4 — drive the real VerticalScrollbar + ScrollbarStyle API, not a fake. */
    @Test
    fun `e4 scrollbar style`() {
        val styles = listOf(
            "stock" to null,
            "thin" to Triple(3.dp, 2.dp, 0),
            "fat-square" to Triple(12.dp, 0.dp, 1),
        )
        shot("e4-scrollbar", widthDp = 330, heightDp = 190) {
            Row(Modifier.fillMaxSize().padding(14.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                styles.forEach { (label, spec) ->
                    val style = if (spec == null) LocalScrollbarStyle.current else ScrollbarStyle(
                        minimalHeight = 16.dp,
                        thickness = spec.first,
                        shape = RoundedCornerShape(spec.second),
                        hoverDurationMillis = 300,
                        unhoverColor = MaterialTheme.colors.primary.copy(alpha = 0.45f),
                        hoverColor = MaterialTheme.colors.primary,
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.height(130.dp).width(70.dp)) {
                            val scrollState = androidx.compose.foundation.rememberScrollState()
                            Column(Modifier.verticalScroll(scrollState)) {
                                repeat(14) {
                                    Text(
                                        "row $it",
                                        color = MaterialTheme.colors.onBackground,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(vertical = 3.dp),
                                    )
                                }
                            }
                            CompositionLocalProvider(LocalScrollbarStyle provides style) {
                                VerticalScrollbar(
                                    adapter = rememberScrollbarAdapter(scrollState),
                                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                                )
                            }
                        }
                        Text(label, color = MaterialTheme.colors.onBackground, fontSize = 10.sp)
                    }
                }
            }
        }
    }

    // ── E7 · icons — MEASURED LIMIT, no test possible ─────────────────────────
    // The compose-resources generated accessors (Res.drawable.*) are NOT on the test
    // compile classpath: `Unresolved reference 'branch'`. Icons therefore cannot be
    // rendered headless at all — any icon work must be verified in the live window.

    /** E5 — the highest-information test: run the existing SkSL pass headless. */
    @Test
    fun `e5 shader gen x soft club`() {
        listOf(0L to "t0", 1_500_000_000L to "t1500ms").forEach { (nanos, label) ->
            shot("e5-shader-$label", widthDp = 420, heightDp = 200, theme = Theme.CalderaNight, atNanos = nanos) {
                CalderaNightEffects(Modifier.fillMaxSize()) {
                    Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Soft club halation", color = MaterialTheme.colors.primary, fontSize = 20.sp)
                        Text(
                            "Body text at 13sp — the squint test for a full-window pass.",
                            color = MaterialTheme.colors.onBackground, fontSize = 13.sp,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                MaterialTheme.colors.primary,
                                MaterialTheme.colors.secondary,
                                MaterialTheme.colors.error,
                                MaterialTheme.colors.surface,
                            ).forEach { c ->
                                Box(Modifier.size(56.dp, 34.dp).background(c, RoundedCornerShape(8.dp)))
                            }
                        }
                    }
                }
            }
        }
    }

    /** E6 — capture animateColorAsState mid-flight by advancing the scene clock. */
    @Test
    fun `e6 motion mid transition`() {
        listOf(0L, 80_000_000L, 160_000_000L, 400_000_000L).forEachIndexed { i, nanos ->
            val target: MutableState<Boolean> = mutableStateOf(i > 0)
            shot("e6-motion-$i", widthDp = 220, heightDp = 90, atNanos = nanos) {
                val c = androidx.compose.animation.animateColorAsState(
                    targetValue = if (target.value) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
                    animationSpec = androidx.compose.animation.core.tween(300),
                )
                Box(
                    Modifier
                        .padding(20.dp)
                        .size(180.dp, 50.dp)
                        .background(c.value, RoundedCornerShape(10.dp))
                        .border(1.dp, MaterialTheme.colors.onBackground.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("${nanos / 1_000_000} ms", color = MaterialTheme.colors.onBackground, fontSize = 12.sp)
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// CORRECTIONS. The first pass produced three overclaims, all from flawed probes
// rather than platform limits. Re-run properly here.
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Renders several frames from ONE scene. The original E5b/E6 built a fresh scene per
 * frame, so every scene saw `start == now` on its only render and elapsed time was
 * structurally zero — the "motion is unverifiable" conclusion was an artefact of that.
 */
private fun shotSeq(
    name: String,
    widthDp: Int,
    heightDp: Int,
    theme: Theme = Theme.CalderaNight,
    frames: List<Long>,
    beforeFrame: (Int) -> Unit = {},
    content: @Composable () -> Unit,
) {
    shotsDir.mkdirs()
    val scene = ImageComposeScene(
        width = (widthDp * DENSITY).toInt(),
        height = (heightDp * DENSITY).toInt(),
        density = Density(DENSITY),
    ) {
        AppTheme(selectedTheme = theme) {
            Box(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) { content() }
        }
    }
    try {
        frames.forEachIndexed { i, nanos ->
            beforeFrame(i)
            val data = scene.render(nanos).encodeToData(EncodedImageFormat.PNG)
                ?: error("no PNG for $name-$i")
            File(shotsDir, "$name-$i.png").writeBytes(data.bytes)
        }
    } finally {
        scene.close()
    }
}

class DesignExperimentCorrections {

    /**
     * C1 — control for E1. Same weights WITHOUT variationSettings. If both columns look
     * identical, the axis did nothing and FontWeight/synthesis was doing the work.
     */
    @Test
    fun `c1 variable font control`() {
        fun plain(w: Int) = FontFamily(Font(VF, FontWeight(w), FontStyle.Normal))
        shot("c1-vf-control", widthDp = 430, heightDp = 250) {
            Row(Modifier.padding(18.dp), horizontalArrangement = Arrangement.spacedBy(30.dp)) {
                listOf("axis set" to true, "no axis" to false).forEach { (label, useAxis) ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(label, color = MaterialTheme.colors.primary, fontSize = 11.sp)
                        listOf(300, 500, 700, 900).forEach { w ->
                            Text(
                                "Weight $w",
                                fontFamily = if (useAxis) vfAt(w) else plain(w),
                                fontSize = 21.sp,
                                color = MaterialTheme.colors.onBackground,
                            )
                        }
                    }
                }
            }
        }
    }

    /** C2 — coloured shadow on a dark ground. E2b only tested the default black. */
    @Test
    fun `c2 coloured shadow on dark`() {
        shot("c2-shadow-coloured", widthDp = 470, heightDp = 160) {
            Row(Modifier.padding(22.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                val accent = MaterialTheme.colors.primary
                listOf<Triple<String, Color, Color>>(
                    Triple("black", Color.Black, Color.Black),
                    Triple("accent", accent, accent),
                    Triple("white", Color.White, Color.White),
                ).forEach { (label, ambient, spot) ->
                    Box(
                        Modifier
                            .shadow(
                                elevation = 10.dp,
                                shape = RoundedCornerShape(12.dp),
                                clip = false,
                                ambientColor = ambient,
                                spotColor = spot,
                            )
                            .size(120.dp, 76.dp)
                            .background(MaterialTheme.colors.surface, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(label, color = MaterialTheme.colors.onBackground, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    /** C3 — shader `time` uniform, one scene, advancing clock. Grain must differ frame to frame. */
    @Test
    fun `c3 shader time advances`() {
        shotSeq(
            "c3-shader-time",
            widthDp = 300, heightDp = 120,
            theme = Theme.CalderaNight,
            frames = listOf(0L, 500_000_000L, 2_000_000_000L),
        ) {
            CalderaNightEffects(Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxSize().padding(14.dp)) {
                    Text("grain sample", color = MaterialTheme.colors.onBackground, fontSize = 15.sp)
                }
            }
        }
    }

    /** C4 — animateColorAsState across a reused scene, state flipped after the first frame. */
    @Test
    fun `c4 motion across frames`() {
        val target = mutableStateOf(false)
        shotSeq(
            "c4-motion",
            widthDp = 200, heightDp = 80,
            frames = listOf(0L, 60_000_000L, 150_000_000L, 240_000_000L, 600_000_000L),
            beforeFrame = { i -> if (i == 1) target.value = true },
        ) {
            val c = androidx.compose.animation.animateColorAsState(
                targetValue = if (target.value) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
                animationSpec = androidx.compose.animation.core.tween(400),
            )
            Box(
                Modifier
                    .padding(16.dp)
                    .size(168.dp, 48.dp)
                    .background(c.value, RoundedCornerShape(10.dp))
                    .border(1.dp, MaterialTheme.colors.onBackground.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            )
        }
    }
}
