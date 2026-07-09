package com.jetpackduba.gitnuro.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// "Caldera Night" (Dark theme) — Tier-2 (P1).
// Restrained, professional-first: an accent-masked warm bloom. Only warm-orange
// "ember" pixels (the Citra Orange accent) glow; everything else is untouched.
// Static (no clock) → idle-free. No aberration, no scanlines, no global glow.
//
// The mask isolates embers by "warmth" = r - 0.5*g - b, which is high only for the
// orange accent and near-zero for cream text, charcoal bg and slate surfaces.
//
// ponytail: 12-tap two-ring gather ≈ a mid-radius glow. Widen via an ImageFilter
// blur chain in P2 if embers need a softer, larger halo.
private const val CALDERA_NIGHT_SKSL = """
uniform shader content;
uniform float2 resolution;
uniform float bloomAmount;

float3 emberAt(float2 p) {
    float3 s = float3(content.eval(p).rgb);
    float warmth = s.r - 0.5 * s.g - s.b;   // high only for warm-orange
    float m = smoothstep(0.30, 0.70, warmth);
    return s * m;
}

half4 main(float2 coord) {
    half4 src = content.eval(coord);
    float3 c = float3(src.rgb);
    float a = float(src.a);

    // gather the masked ember field over two rings for a soft warm halo
    float r1 = 3.0;
    float r2 = 6.0;
    float3 e = float3(0.0);
    e += emberAt(coord + float2( r1, 0.0));
    e += emberAt(coord + float2(-r1, 0.0));
    e += emberAt(coord + float2(0.0,  r1));
    e += emberAt(coord + float2(0.0, -r1));
    e += emberAt(coord + float2( r1, r1));
    e += emberAt(coord + float2(-r1, r1));
    e += emberAt(coord + float2( r1,-r1));
    e += emberAt(coord + float2(-r1,-r1));
    e += emberAt(coord + float2( r2, 0.0));
    e += emberAt(coord + float2(-r2, 0.0));
    e += emberAt(coord + float2(0.0,  r2));
    e += emberAt(coord + float2(0.0, -r2));
    e *= (1.0 / 12.0);

    float3 glow = e * bloomAmount;
    c = 1.0 - (1.0 - c) * (1.0 - glow);   // screen blend — embers add, never darken

    return half4(half3(clamp(c, 0.0, 1.0)), half(a));
}
"""

@Composable
fun CalderaNightEffects(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ShaderEffect(
        sksl = CALDERA_NIGHT_SKSL,
        animated = false,
        modifier = modifier,
        setUniforms = { size, _ ->
            uniform("resolution", size.width, size.height)
            uniform("bloomAmount", 0.5f)
        },
        content = content,
    )
}
