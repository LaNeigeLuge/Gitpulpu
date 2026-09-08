package com.jetpackduba.gitnuro.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.jetpackduba.gitnuro.app.generated.resources.Res
import com.jetpackduba.gitnuro.app.generated.resources.branch
import com.jetpackduba.gitnuro.app.generated.resources.download
import com.jetpackduba.gitnuro.app.generated.resources.stash
import com.jetpackduba.gitnuro.app.generated.resources.upload
import com.jetpackduba.gitnuro.domain.models.ui.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skia.EncodedImageFormat
import org.junit.jupiter.api.Test
import java.io.File

/**
 * C5 — control for E7. The first probe fully-qualified the accessors
 * (`...resources.Res.drawable.branch`), which cannot work: they are extension
 * properties and must be imported individually. `internal` is visible to the
 * test source set. If this renders, 12 more components become headless-testable.
 */
class ResourceProbe {

    @Test
    fun `c5 compose resources in a jvm test`() {
        val dir = File("build/shots").apply { mkdirs() }
        val scene = ImageComposeScene(560, 160, Density(2f)) {
            AppTheme(selectedTheme = Theme.CalderaNight) {
                Box(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
                    Row(
                        Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        listOf(
                            Res.drawable.branch,
                            Res.drawable.download,
                            Res.drawable.upload,
                            Res.drawable.stash,
                        ).forEach {
                            Icon(
                                painter = painterResource(it),
                                contentDescription = null,
                                tint = MaterialTheme.colors.primary,
                                modifier = Modifier.size(32.dp),
                            )
                        }
                    }
                }
            }
        }
        try {
            val data = scene.render(0L).encodeToData(EncodedImageFormat.PNG)!!
            File(dir, "c5-resources.png").writeBytes(data.bytes)
        } finally {
            scene.close()
        }
    }
}
