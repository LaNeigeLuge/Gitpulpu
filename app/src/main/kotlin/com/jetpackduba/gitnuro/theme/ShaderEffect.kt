package com.jetpackduba.gitnuro.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder

/**
 * Shared plumbing for full-window SkSL "filter mode" theme effects: compiles the
 * shader once, captures the layer size, and rebuilds the [renderEffect] each time
 * an observed uniform changes. The SkSL must declare `uniform shader content;`.
 *
 * [setUniforms] receives the builder plus the current layer size and elapsed
 * seconds (0 when [animated] is false). Set `resolution` / `time` there as needed.
 *
 * ponytail: [animated] == true runs a frame clock → continuous redraw even when
 * idle. Only turn it on for a theme where motion (grain, flicker) is the signature.
 */
@Composable
internal fun ShaderEffect(
    sksl: String,
    animated: Boolean,
    modifier: Modifier = Modifier,
    setUniforms: RuntimeShaderBuilder.(size: Size, timeSeconds: Float) -> Unit,
    content: @Composable () -> Unit,
) {
    val effect = remember(sksl) { RuntimeEffect.makeForShader(sksl) }
    val builder = remember(effect) { RuntimeShaderBuilder(effect) }
    var size by remember { mutableStateOf(Size.Zero) }
    var time by remember { mutableStateOf(0f) }

    if (animated) {
        LaunchedEffect(Unit) {
            val start = withFrameNanos { it }
            while (true) {
                withFrameNanos { now -> time = (now - start) / 1_000_000_000f }
            }
        }
    }

    Box(
        modifier = modifier
            .onSizeChanged { size = Size(it.width.toFloat(), it.height.toFloat()) }
            .graphicsLayer {
                if (size.width > 0f && size.height > 0f) {
                    builder.setUniforms(size, time)
                    renderEffect = ImageFilter
                        .makeRuntimeShader(builder, "content", null)
                        .asComposeRenderEffect()
                }
            },
    ) {
        content()
    }
}
