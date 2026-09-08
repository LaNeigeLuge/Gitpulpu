package com.jetpackduba.gitnuro.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.jetpackduba.gitnuro.domain.models.ui.Theme
import com.jetpackduba.gitnuro.ui.components.AdjustableOutlinedTextField
import com.jetpackduba.gitnuro.ui.components.PrimaryButton
import org.jetbrains.skia.EncodedImageFormat
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Tier-1 verification of the shipped theme. Renders REAL components through the REAL
 * AppTheme, so palette, Jost loading, AppShapes radii and the grain pass are all exercised
 * the way the app exercises them. Compare against scratchpad/brief/ref-sheet.png.
 */
class ClaymakersVerify {

    private fun render(name: String, theme: Theme, grain: Boolean, w: Int, h: Int) {
        val dir = File("build/shots").apply { mkdirs() }
        val scene = ImageComposeScene((w * 2f).toInt(), (h * 2f).toInt(), Density(2f)) {
            AppTheme(selectedTheme = theme) {
                val body: @Composable () -> Unit = {
                    Box(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                            Text("Claymakers", style = MaterialTheme.typography.h1)
                            Text("Explore our clay", style = MaterialTheme.typography.h4)
                            Text("Body text at 13sp through the real theme.", style = MaterialTheme.typography.body2)
                            Text("Quiet secondary line", style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onBackgroundSecondary)
                            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                                PrimaryButton(text = "Commit", onClick = {})
                                PrimaryButton(
                                    text = "Amend",
                                    backgroundColor = MaterialTheme.colors.secondary,
                                    textColor = MaterialTheme.colors.onSecondary,
                                    onClick = {},
                                )
                            }
                            AdjustableOutlinedTextField(
                                value = "",
                                hint = "Search for branches, tags & more",
                                onValueChange = {},
                                modifier = Modifier.width(320.dp).height(44.dp),
                                singleLine = true,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                                listOf(
                                    MaterialTheme.colors.surface,
                                    MaterialTheme.colors.secondarySurface,
                                    MaterialTheme.colors.tertiarySurface,
                                    MaterialTheme.colors.backgroundSelected,
                                    MaterialTheme.colors.diffLineAdded,
                                    MaterialTheme.colors.diffLineRemoved,
                                    MaterialTheme.colors.primary,
                                ).forEach {
                                    Box(Modifier.size(46.dp, 34.dp).background(it, AppShapes.small))
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                listOf(
                                    "+" to MaterialTheme.colors.addFile,
                                    "~" to MaterialTheme.colors.modifyFile,
                                    "−" to MaterialTheme.colors.deleteFile,
                                    "!" to MaterialTheme.colors.conflictFile,
                                ).forEach { (g, c) ->
                                    Text("$g status", style = MaterialTheme.typography.body2, color = c)
                                }
                            }
                        }
                    }
                }
                if (grain) ClaymakersEffects(Modifier.fillMaxSize(), body) else body()
            }
        }
        try {
            File(dir, "$name.png").writeBytes(scene.render(0L).encodeToData(EncodedImageFormat.PNG)!!.bytes)
        } finally {
            scene.close()
        }
    }

    @Test
    fun `claymakers with and without grain, plus caldera control`() {
        render("v-claymakers-grain", Theme.Claymakers, grain = true, w = 460, h = 360)
        render("v-claymakers-night", Theme.ClaymakersNight, grain = true, w = 460, h = 360)
        render("v-caldera-control", Theme.CalderaNight, grain = false, w = 460, h = 360)
    }
}
