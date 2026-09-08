package com.jetpackduba.gitnuro.theme

import androidx.compose.foundation.background
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skia.EncodedImageFormat
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Claymakers spec probes P1–P5. Each converts an unproven row in
 * Projects/Gitpulpu/Design-Spec-Claymakers.md into a fact, and each carries a control.
 * Compare the output against the Chrome reference renders in scratchpad/brief/.
 */
private val out = File("build/shots")
private const val D = 2f

// ── palette, verbatim from the brief's style.css ───────────────────────────
private object Clay {
    val green = Color(0xFF9BC400)
    val lilac = Color(0xFF8076A3)
    val pink = Color(0xFFF9C5BD)
    val plum = Color(0xFF7C677F)
    val greenDeep = Color(0xFF6D8C10)
    val greenPale = Color(0xFFE4EEC1)
    val lilacPale = Color(0xFFD3CCE2)
    val lilacMist = Color(0xFFEBE7F2)
    val pinkPale = Color(0xFFFCE6E2)
    val pinkMist = Color(0xFFFDF1EE)
    val paper = Color(0xFFFDF8F5)
    val paperEdge = Color(0xFFF3ECE7)
    val ink = Color(0xFF33284A)
    val inkSoft = Color(0xFF6B6180)

    // derived — not in the brief
    val clay = Color(0xFFC4706A)
    val clayPale = Color(0xFFF5DCD9)
    val ochre = Color(0xFFBD8B3C)
    val ochrePale = Color(0xFFF2E4C6)

    // AA text tier — same hue + saturation, darkened until legible on its wash
    val addText = Color(0xFF58710D)
    val delText = Color(0xFFA44841)
    val modText = Color(0xFF826029)
    val kwText = Color(0xFF776C9C)
}

// ── the wash shadows, translated 1:1 from CSS box-shadow ──────────────────
private val washSm = Shadow(radius = 14.dp, color = Clay.ink, spread = (-8).dp, offset = DpOffset(0.dp, 6.dp), alpha = 0.35f)
private val washMd = Shadow(radius = 30.dp, color = Clay.ink, spread = (-18).dp, offset = DpOffset(0.dp, 16.dp), alpha = 0.38f)
private val washLg = Shadow(radius = 60.dp, color = Clay.ink, spread = (-32).dp, offset = DpOffset(0.dp, 34.dp), alpha = 0.45f)

private val JOST = File(
    "/tmp/claude-1000/-home-julienrose-Documents-Gitpulpu/" +
        "b0970b01-01f0-4f75-ad60-c0f967bc1b93/scratchpad/expfonts/Jost-VF.ttf"
)

private fun jost(w: Int) = FontFamily(Font(JOST, FontWeight(w), FontStyle.Normal))

/** feTurbulence fractalNoise, baseFrequency 0.9, numOctaves 3, multiply-blended. */
/**
 * Paper tooth. The first attempt multiplied by fbm directly, which averages 0.5 and has full
 * 0..1 variance — that turned paper into grey noise. The CSS multiplies by a mostly-mid-grey
 * turbulence at 0.32 opacity, i.e. a multiplier hovering close to 1.0. So: centre on 1.0,
 * keep the amplitude small, and drop the frequency so the cells read as tooth not static.
 */
private const val GRAIN_SKSL = """
uniform shader content;
uniform float2 resolution;
uniform float strength;
uniform float scale;

float hash(float2 p) {
    p = fract(p * float2(123.34, 456.21));
    p += dot(p, p + 34.56);
    return fract(p.x * p.y);
}
float valueNoise(float2 p) {
    float2 i = floor(p); float2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash(i), b = hash(i + float2(1.0, 0.0));
    float c = hash(i + float2(0.0, 1.0)), d = hash(i + float2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}
float fbm(float2 p) {
    float v = 0.0; float a = 0.5; float norm = 0.0;
    for (int i = 0; i < 3; i++) { v += a * valueNoise(p); norm += a; p *= 2.0; a *= 0.5; }
    return v / norm;
}
half4 main(float2 coord) {
    half4 src = content.eval(coord);
    float n = fbm(coord * scale);
    float m = 1.0 + strength * (n - 0.5);
    return half4(half3(clamp(float3(src.rgb) * m, 0.0, 1.0)), src.a);
}
"""

private fun shot(name: String, w: Int, h: Int, content: @Composable () -> Unit) {
    out.mkdirs()
    val scene = ImageComposeScene((w * D).toInt(), (h * D).toInt(), Density(D)) {
        Box(Modifier.fillMaxSize().background(Clay.pinkMist)) { content() }
    }
    try {
        File(out, "$name.png").writeBytes(scene.render(0L).encodeToData(EncodedImageFormat.PNG)!!.bytes)
    } finally {
        scene.close()
    }
}

class ClaymakersProbes {

    /** P1 — do the CSS wash shadows port exactly? Control: elevation-shadow and none. */
    @Test
    fun `p1 wash shadows`() {
        shot("p1-shadows", 620, 300) {
            Column(Modifier.padding(26.dp), verticalArrangement = Arrangement.spacedBy(22.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    listOf("sm" to washSm, "md" to washMd, "lg" to washLg).forEach { (label, sh) ->
                        Box(
                            Modifier
                                .dropShadow(RoundedCornerShape(26.dp), sh)
                                .size(160.dp, 90.dp)
                                .background(Clay.paper, RoundedCornerShape(26.dp)),
                            contentAlignment = Alignment.Center,
                        ) { Text("wash-$label", color = Clay.inkSoft, fontFamily = jost(400), fontSize = 12.sp) }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    // controls
                    Box(
                        Modifier.shadow(6.dp, RoundedCornerShape(26.dp)).size(160.dp, 90.dp)
                            .background(Clay.paper, RoundedCornerShape(26.dp)),
                        contentAlignment = Alignment.Center,
                    ) { Text("elevation 6dp", color = Clay.inkSoft, fontFamily = jost(400), fontSize = 12.sp) }
                    Box(
                        Modifier.size(160.dp, 90.dp).background(Clay.paper, RoundedCornerShape(26.dp)),
                        contentAlignment = Alignment.Center,
                    ) { Text("no shadow", color = Clay.inkSoft, fontFamily = jost(400), fontSize = 12.sp) }
                }
            }
        }
    }

    /** P2 — Jost variable weights. Control: the same sizes in the fallback face. */
    @Test
    fun `p2 jost weights`() {
        require(JOST.exists()) { "Jost missing at ${JOST.path}" }
        shot("p2-jost", 560, 340) {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Home", fontFamily = jost(800), fontSize = 34.sp, letterSpacing = (-0.033).em(), color = Clay.ink)
                Text("EXPLORE OUR CLAY", fontFamily = jost(600), fontSize = 11.sp, letterSpacing = 0.143.em(), color = Clay.inkSoft)
                Text("CLAYMAKERS", fontFamily = jost(500), fontSize = 15.sp, letterSpacing = 0.2.em(), color = Clay.ink)
                Text("Regular 400 — default UI text at 13sp", fontFamily = jost(400), fontSize = 13.sp, color = Clay.ink)
                Text("Light 300 — quiet secondary text at 13sp", fontFamily = jost(300), fontSize = 13.sp, color = Clay.inkSoft)
                Text("Thin 100 / Black 900 — full axis range", fontFamily = jost(100), fontSize = 15.sp, color = Clay.ink)
                Text("Thin 100 / Black 900 — full axis range", fontFamily = jost(900), fontSize = 15.sp, color = Clay.ink)
                Box(Modifier.height(10.dp))
                Text("CONTROL — fallback face, no Jost", fontSize = 13.sp, color = Clay.plum)
                Text("Regular 400 — default UI text at 13sp", fontSize = 13.sp, color = Clay.ink)
            }
        }
    }

    /** P3 — the layered page washes. CSS uses ELLIPTICAL radials; Compose only does circular. */
    @Test
    fun `p3 radial washes`() {
        shot("p3-washes", 560, 300) {
            Box(
                Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawRect(Clay.pinkMist)
                        drawRect(
                            Brush.radialGradient(
                                0f to Clay.pink, 0.6f to Clay.pink.copy(alpha = 0f),
                                center = Offset(size.width * 0.12f, 0f), radius = size.width * 0.70f,
                            )
                        )
                        drawRect(
                            Brush.radialGradient(
                                0f to Clay.lilacPale, 0.62f to Clay.lilacPale.copy(alpha = 0f),
                                center = Offset(size.width * 0.92f, size.height * 0.12f), radius = size.width * 0.60f,
                            )
                        )
                        drawRect(
                            Brush.radialGradient(
                                0f to Clay.greenPale, 0.58f to Clay.greenPale.copy(alpha = 0f),
                                center = Offset(size.width * 0.78f, size.height), radius = size.width * 0.80f,
                            )
                        )
                    }
            ) {
                Box(
                    Modifier.padding(24.dp).fillMaxSize()
                        .dropShadow(RoundedCornerShape(26.dp), washLg)
                        .background(Clay.paper, RoundedCornerShape(26.dp)),
                    contentAlignment = Alignment.Center,
                ) { Text("paper over three washes", color = Clay.inkSoft, fontFamily = jost(400), fontSize = 13.sp) }
            }
        }
    }

    /** P4 — paper grain, strength sweep. Control: shader off. Pick against ref-sheet.png. */
    @Test
    fun `p4 paper grain`() {
        listOf(0f to "off", 0.06f to "s06", 0.12f to "s12", 0.24f to "s24").forEach { (strength, label) ->
            shot("p4-grain-$label", 400, 170) {
                val body: @Composable () -> Unit = {
                    Box(Modifier.fillMaxSize().background(Clay.paper)) {
                        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                            Text("Paper grain $label", fontFamily = jost(800), fontSize = 22.sp, color = Clay.ink)
                            Text(
                                "Body text at 13sp — squint test.",
                                fontFamily = jost(400), fontSize = 13.sp, color = Clay.ink,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                                listOf(Clay.greenPale, Clay.lilacMist, Clay.pinkPale, Clay.green).forEach {
                                    Box(Modifier.size(58.dp, 30.dp).background(it, RoundedCornerShape(10.dp)))
                                }
                            }
                        }
                    }
                }
                if (strength == 0f) body() else ShaderEffect(
                    sksl = GRAIN_SKSL,
                    animated = false,
                    modifier = Modifier.fillMaxSize(),
                    setUniforms = { size, _ ->
                        uniform("resolution", size.width, size.height)
                        uniform("strength", strength)
                        uniform("scale", 0.5f)
                    },
                    content = body,
                )
            }
        }
    }

    /** P5 — the whole palette in Compose, to diff against ref-sheet.png. */
    @Test
    fun `p5 palette sheet`() {
        val given = listOf(
            "green" to Clay.green, "lilac" to Clay.lilac, "pink" to Clay.pink, "plum" to Clay.plum,
            "green-deep" to Clay.greenDeep, "green-pale" to Clay.greenPale,
            "lilac-pale" to Clay.lilacPale, "lilac-mist" to Clay.lilacMist,
            "pink-pale" to Clay.pinkPale, "pink-mist" to Clay.pinkMist,
            "paper" to Clay.paper, "paper-edge" to Clay.paperEdge,
            "ink" to Clay.ink, "ink-soft" to Clay.inkSoft,
        )
        val derived = listOf(
            "clay" to Clay.clay, "clay-pale" to Clay.clayPale,
            "ochre" to Clay.ochre, "ochre-pale" to Clay.ochrePale,
            "add-t" to Clay.addText, "del-t" to Clay.delText,
            "mod-t" to Clay.modText, "kw-t" to Clay.kwText,
        )
        shot("p5-palette", 640, 380) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                listOf("GIVEN" to given, "DERIVED" to derived).forEach { (label, set) ->
                    Text(label, fontFamily = jost(600), fontSize = 10.sp, letterSpacing = 0.2.em(), color = Clay.inkSoft)
                    set.chunked(7).forEach { rowItems ->
                        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            rowItems.forEach { (n, c) ->
                                Column(Modifier.width(82.dp)) {
                                    Box(Modifier.fillMaxWidth().height(52.dp).background(c, RoundedCornerShape(10.dp)))
                                    Text(n, fontFamily = jost(400), fontSize = 8.sp, color = Clay.inkSoft)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun Double.em() = androidx.compose.ui.unit.TextUnit(
    this.toFloat(), androidx.compose.ui.unit.TextUnitType.Em
)

/**
 * P2b — P2 showed Jost 100 and 900 rendering identically, contradicting E1 where Figtree's
 * weights clearly differed. Three ways of asking for the same weights, side by side, plus an
 * unmistakable serif control. Whichever column varies is the mechanism that actually works.
 */
class JostWeightProbe {

    private fun oneFontFamily(w: Int) =
        FontFamily(Font(JOST, FontWeight(w), FontStyle.Normal))

    private fun withAxis(w: Int) = FontFamily(
        Font(
            JOST, FontWeight(w), FontStyle.Normal,
            androidx.compose.ui.text.font.FontVariation.Settings(
                androidx.compose.ui.text.font.FontVariation.weight(w)
            ),
        )
    )

    /** All declared weights in ONE family, so Compose has to pick by requested weight. */
    private val allInOne = FontFamily(
        listOf(100, 200, 300, 400, 500, 600, 700, 800, 900).map {
            Font(JOST, FontWeight(it), FontStyle.Normal)
        }
    )

    @Test
    fun `p2b jost weight mechanisms`() {
        val weights = listOf(100, 300, 500, 700, 900)
        shot("p2b-jost-mechanisms", 760, 300) {
            Row(Modifier.padding(18.dp), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                data class Col(val label: String, val fam: (Int) -> FontFamily, val useWeight: Boolean)
                listOf(
                    Col("one-font family", { oneFontFamily(it) }, false),
                    Col("+ variationSettings", { withAxis(it) }, false),
                    Col("all-in-one family", { allInOne }, true),
                    Col("SERIF control", { FontFamily.Serif }, true),
                ).forEach { col ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(col.label, fontSize = 9.sp, color = Clay.plum, fontFamily = FontFamily.SansSerif)
                        weights.forEach { w ->
                            Text(
                                "Aa $w",
                                fontFamily = col.fam(w),
                                fontWeight = if (col.useWeight) FontWeight(w) else null,
                                fontSize = 22.sp,
                                color = Clay.ink,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * P2c — the settling probe. Static instances (fontTools) in one FontFamily, against the
 * variable file as control. If statics vary and the VF column does not, the rule is:
 * Compose Desktop's file loader does NOT instance variable axes — bundle statics.
 */
class JostStaticProbe {

    private val dir = JOST.parentFile
    private fun f(name: String, w: Int) = Font(File(dir, name), FontWeight(w), FontStyle.Normal)

    private val statics = FontFamily(
        f("Jost-Light.ttf", 300), f("Jost-Regular.ttf", 400), f("Jost-Medium.ttf", 500),
        f("Jost-SemiBold.ttf", 600), f("Jost-ExtraBold.ttf", 800),
    )
    private val variable = FontFamily(
        listOf(300, 400, 500, 600, 800).map { Font(JOST, FontWeight(it), FontStyle.Normal) }
    )

    @Test
    fun `p2c statics versus variable`() {
        val weights = listOf(300, 400, 500, 600, 800)
        shot("p2c-jost-statics", 620, 290) {
            Row(Modifier.padding(18.dp), horizontalArrangement = Arrangement.spacedBy(30.dp)) {
                listOf("STATIC instances" to statics, "VARIABLE file (control)" to variable).forEach { (label, fam) ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(label, fontSize = 9.sp, color = Clay.plum, fontFamily = FontFamily.SansSerif)
                        weights.forEach { w ->
                            Text(
                                "Claymakers $w",
                                fontFamily = fam,
                                fontWeight = FontWeight(w),
                                fontSize = 21.sp,
                                color = Clay.ink,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * P6 — density. Julien chose "somewhere between" the brief's dashboard scale and Gitpulpu's
 * current 13sp/34dp. Same content at three densities so the choice is made from a picture.
 * Uses Jost statics, the Claymakers palette, and answer B (ink text, colour in the wash).
 */
class ClaymakersDensityProbe {

    private val dir = JOST.parentFile
    private fun f(n: String, w: Int) = Font(File(dir, n), FontWeight(w), FontStyle.Normal)
    private val jostFamily = FontFamily(
        f("Jost-Light.ttf", 300), f("Jost-Regular.ttf", 400), f("Jost-Medium.ttf", 500),
        f("Jost-SemiBold.ttf", 600), f("Jost-ExtraBold.ttf", 800),
    )

    private data class Density3(val label: String, val body: Int, val row: Int, val small: Int)

    @Composable
    private fun pane(d: Density3) {
        val files = listOf(
            Triple("+", Clay.addText, "theme/ClaymakersColors.kt") to Clay.greenPale,
            Triple("~", Clay.modText, "theme/Typography.kt") to Clay.ochrePale,
            Triple("−", Clay.delText, "theme/OldGradients.kt") to Clay.clayPale,
            Triple("!", Clay.plum, "ui/log/Log.kt") to Clay.lilacMist,
        )
        val commits = listOf(
            "main" to "new [KT]: Claymakers palette + paper grain",
            null to "chg [KT]: instance Jost statics, drop the VF",
            "origin/main" to "fix [KT]: dropShadow ports the wash shadows",
            null to "chg [KT]: calibrate grain strength to 0.12",
        )
        Column(
            Modifier
                .dropShadow(RoundedCornerShape(26.dp), washSm)
                .width(400.dp)
                .background(Clay.paper, RoundedCornerShape(26.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                d.label, fontFamily = jostFamily, fontWeight = FontWeight(800),
                fontSize = (d.body + 9).sp, color = Clay.ink,
            )
            Text(
                "CHANGES", fontFamily = jostFamily, fontWeight = FontWeight(600),
                fontSize = d.small.sp, letterSpacing = 0.2.em(), color = Clay.inkSoft,
                modifier = Modifier.padding(top = 6.dp, bottom = 3.dp),
            )
            files.forEach { (trip, wash) ->
                val (glyph, glyphColor, path) = trip
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(d.row.dp)
                        .background(wash, RoundedCornerShape(10.dp))
                        .padding(horizontal = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        glyph, fontFamily = jostFamily, fontWeight = FontWeight(600),
                        fontSize = d.body.sp, color = glyphColor, modifier = Modifier.width(11.dp),
                    )
                    Text(path, fontFamily = jostFamily, fontWeight = FontWeight(400), fontSize = d.body.sp, color = Clay.ink)
                }
            }
            Text(
                "HISTORY", fontFamily = jostFamily, fontWeight = FontWeight(600),
                fontSize = d.small.sp, letterSpacing = 0.2.em(), color = Clay.inkSoft,
                modifier = Modifier.padding(top = 10.dp, bottom = 3.dp),
            )
            commits.forEach { (ref, msg) ->
                Row(
                    Modifier.fillMaxWidth().height(d.row.dp).padding(horizontal = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(Modifier.size((d.body - 3).dp).background(Clay.lilac, RoundedCornerShape(50)))
                    if (ref != null) {
                        Box(
                            Modifier
                                .background(Clay.greenPale, RoundedCornerShape(40.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                ref, fontFamily = jostFamily, fontWeight = FontWeight(600),
                                fontSize = (d.small).sp, letterSpacing = 0.1.em(), color = Clay.addText,
                            )
                        }
                    }
                    Text(msg, fontFamily = jostFamily, fontWeight = FontWeight(400), fontSize = d.body.sp, color = Clay.ink, maxLines = 1)
                }
            }
        }
    }

    @Test
    fun `p6 density options`() {
        val opts = listOf(
            Density3("13sp / 34dp — current", 13, 34, 9),
            Density3("14sp / 38dp — between", 14, 38, 10),
            Density3("15sp / 42dp — toward brief", 15, 42, 11),
        )
        opts.forEachIndexed { i, d ->
            shot("p6-density-$i", 440, 400) {
                Box(Modifier.fillMaxSize().padding(14.dp)) { pane(d) }
            }
        }
    }
}
