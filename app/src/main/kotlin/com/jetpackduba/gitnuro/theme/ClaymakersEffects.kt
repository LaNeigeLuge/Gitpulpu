package com.jetpackduba.gitnuro.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * "Claymakers" — paper tooth.
 *
 * One static full-window SkSL pass reproducing the brief's cold-press paper grain
 * (`feTurbulence fractalNoise` multiplied over everything at low opacity). No clock, so the
 * pass is free when idle — it redraws only when the UI does.
 *
 * The first version multiplied by fBm directly, which averages 0.5 with full 0..1 variance
 * and turned paper into grey noise. The CSS multiplies by a *mostly mid-grey* texture, i.e. a
 * multiplier hovering near 1.0 — hence the centring below. Verified against a Chrome render of
 * the brief at strengths 0.06 / 0.12 / 0.24.
 *
 * ponytail: [GRAIN_STRENGTH] is a constant, not a setting. Edit it here to retune; wire it to
 * AppSettings only if it turns out to need per-user control.
 */
private const val GRAIN_STRENGTH = 0.06f

/** Noise cell size in device pixels. Lower = coarser tooth. */
private const val GRAIN_SCALE = 0.5f

private const val PAPER_GRAIN_SKSL = """
uniform shader content;
uniform float2 resolution;
uniform float strength;
uniform float scale;

float hash(float2 p) {
    p = fract(p * float2(123.34, 456.21));
    p += dot(p, p + 34.56);
    return fract(p.x * p.y);
}

// Smoothed value noise — floor()'d hash alone gives 1px static, not paper.
float valueNoise(float2 p) {
    float2 i = floor(p);
    float2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash(i);
    float b = hash(i + float2(1.0, 0.0));
    float c = hash(i + float2(0.0, 1.0));
    float d = hash(i + float2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float fbm(float2 p) {
    float v = 0.0;
    float a = 0.5;
    float norm = 0.0;
    for (int i = 0; i < 3; i++) {
        v += a * valueNoise(p);
        norm += a;
        p *= 2.0;
        a *= 0.5;
    }
    return v / norm;
}

half4 main(float2 coord) {
    half4 src = content.eval(coord);
    float n = fbm(coord * scale);
    // multiply, centred on 1.0 so the grade is tooth rather than a wash of grey
    float m = 1.0 + strength * (n - 0.5);
    return half4(half3(clamp(float3(src.rgb) * m, 0.0, 1.0)), src.a);
}
"""

@Composable
fun ClaymakersEffects(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ShaderEffect(
        sksl = PAPER_GRAIN_SKSL,
        animated = false,
        modifier = modifier,
        setUniforms = { size, _ ->
            uniform("resolution", size.width, size.height)
            uniform("strength", GRAIN_STRENGTH)
            uniform("scale", GRAIN_SCALE)
        },
        content = content,
    )
}
