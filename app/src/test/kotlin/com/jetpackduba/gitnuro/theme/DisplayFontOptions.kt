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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skia.EncodedImageFormat
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Display-face options paired against Jost for body. Julien asked to "play a little bit more
 * with the font to use different font". Rendered rather than argued.
 * All candidates OFL, all static instances (P2c: variable axes do not work here).
 */
class DisplayFontOptions {

    private val dir = File(
        "/tmp/claude-1000/-home-julienrose-Documents-Gitpulpu/" +
            "b0970b01-01f0-4f75-ad60-c0f967bc1b93/scratchpad/expfonts"
    )
    private fun one(name: String, w: Int) = FontFamily(Font(File(dir, name), FontWeight(w), FontStyle.Normal))
    private val jostBody = FontFamily(
        Font(File(dir, "Jost-Regular.ttf"), FontWeight.Normal, FontStyle.Normal),
        Font(File(dir, "Jost-Medium.ttf"), FontWeight.Medium, FontStyle.Normal),
        Font(File(dir, "Jost-SemiBold.ttf"), FontWeight.SemiBold, FontStyle.Normal),
    )

    private val ground = Color(0xFFF7E4DC)
    private val paper = Color(0xFFFDF1EE)
    private val ink = Color(0xFF33284A)
    private val inkSoft = Color(0xFF564E66)
    private val washAdd = Color(0xFFD8E6A8)
    private val washMod = Color(0xFFEED9A8)
    private val addInk = Color(0xFF546B0C)
    private val modInk = Color(0xFF7A5A27)

    @Test
    fun `display font options`() {
        val options = listOf(
            "Jost ExtraBold — current" to one("Jost-ExtraBold.ttf", 800),
            "Fraunces 700 — soft wonky serif" to one("Fraunces-Display.ttf", 700),
            "Bricolage Grotesque 700" to one("Bricolage-Display.ttf", 700),
            "Instrument Serif 400" to one("InstrumentSerif-Regular.ttf", 400),
        )
        options.forEachIndexed { i, (label, display) ->
            val out = File("build/shots").apply { mkdirs() }
            val scene = ImageComposeScene(940, 460, Density(2f)) {
                Box(Modifier.fillMaxSize().background(ground).padding(14.dp)) {
                    Column(
                        Modifier.fillMaxSize().background(paper, RoundedCornerShape(26.dp)).padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(label, fontFamily = jostBody, fontSize = 9.sp, color = modInk)
                        Text("Claymakers", fontFamily = display, fontSize = 38.sp, color = ink)
                        Text("Changes", fontFamily = display, fontSize = 22.sp, color = ink)
                        Text(
                            "Body stays Jost at 15sp — the display face only carries titles.",
                            fontFamily = jostBody, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = ink,
                        )
                        Text(
                            "Secondary line, darker ink",
                            fontFamily = jostBody, fontSize = 12.sp, color = inkSoft,
                        )
                        listOf("+" to washAdd, "~" to washMod).forEach { (g, wash) ->
                            Row(
                                Modifier.fillMaxWidth().height(42.dp)
                                    .background(wash, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(11.dp),
                            ) {
                                Text(g, fontFamily = jostBody, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                                    color = if (g == "+") addInk else modInk)
                                Text("theme/ClaymakersColors.kt", fontFamily = jostBody,
                                    fontWeight = FontWeight.Medium, fontSize = 15.sp, color = ink)
                            }
                        }
                    }
                }
            }
            try {
                File(out, "f-option-$i.png").writeBytes(scene.render(0L).encodeToData(EncodedImageFormat.PNG)!!.bytes)
            } finally {
                scene.close()
            }
        }
    }
}
