package com.jetpackduba.gitnuro.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// "Gen X Soft Club" — Tier-2 (P1).
// Single full-window SkSL pass over the rendered UI: soft halation (8-tap glow,
// screen-blended on the bright parts only) + desaturate + low-contrast warm grade
// + faint film grain. Softness over sharpness — no scanlines, no glitch.
//
// ponytail: 8-tap glow ≈ cheap pseudo-bloom, not a true gaussian. Upgrade to an
// ImageFilter blur chain in P2 if the halation needs a wider, softer radius.
// ponytail: grain rides the frame clock (animated = true) → continuous redraw.
// That's this theme's signature cost; set animated = false for a static grade.
private const val SOFT_CLUB_SKSL = """
uniform shader content;
uniform float2 resolution;
uniform float time;
uniform float grainAmount;
uniform float bloomAmount;
uniform float contrast;
uniform float saturation;

float hash(float2 p) {
    p = fract(p * float2(123.34, 456.21));
    p += dot(p, p + 34.56);
    return fract(p.x * p.y);
}

half4 main(float2 coord) {
    half4 src = content.eval(coord);
    float3 c = float3(src.rgb);
    float a = float(src.a);

    // soft halation: 8-tap glow, screen-blended on the brighter parts (light sources)
    float r = 2.5;
    float3 g = float3(0.0);
    g += float3(content.eval(coord + float2( r, 0.0)).rgb);
    g += float3(content.eval(coord + float2(-r, 0.0)).rgb);
    g += float3(content.eval(coord + float2(0.0,  r)).rgb);
    g += float3(content.eval(coord + float2(0.0, -r)).rgb);
    g += float3(content.eval(coord + float2( r,  r)).rgb);
    g += float3(content.eval(coord + float2(-r,  r)).rgb);
    g += float3(content.eval(coord + float2( r, -r)).rgb);
    g += float3(content.eval(coord + float2(-r, -r)).rgb);
    g *= 0.125;
    float lum = dot(g, float3(0.299, 0.587, 0.114));
    float3 bloom = g * smoothstep(0.35, 0.9, lum) * bloomAmount;
    c = 1.0 - (1.0 - c) * (1.0 - bloom);

    // desaturate + lower contrast + warm lift (faded expired-film grade)
    float l = dot(c, float3(0.299, 0.587, 0.114));
    c = mix(float3(l), c, saturation);
    c = (c - 0.5) * contrast + 0.5;
    c += float3(0.02, 0.008, -0.02);

    // faint film grain
    float n = hash(coord + float2(time, time * 1.37));
    c += (n - 0.5) * grainAmount;

    return half4(half3(clamp(c, 0.0, 1.0)), half(a));
}
"""

@Composable
fun GenXSoftClubEffects(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ShaderEffect(
        sksl = SOFT_CLUB_SKSL,
        animated = true,
        modifier = modifier,
        setUniforms = { size, time ->
            uniform("resolution", size.width, size.height)
            uniform("time", time)
            uniform("grainAmount", 0.045f)
            uniform("bloomAmount", 0.6f)
            uniform("contrast", 0.88f)
            uniform("saturation", 0.85f)
        },
        content = content,
    )
}
