package com.walipet.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Paleta retro / arcade ────────────────────────────────────────────────

val ArcadeBackground = Color(0xFF0D0A1F)
val ArcadeSurface = Color(0xFF1B1538)
val ArcadeSurfaceVariant = Color(0xFF2A2250)
val ArcadePrimary = Color(0xFF3DFC9E)
val ArcadeSecondary = Color(0xFFFF4FD8)
val ArcadeTertiary = Color(0xFF4FD8FF)
val ArcadeStreak = Color(0xFFFFD54A)
val ArcadeError = Color(0xFFFF6B6B)
val ArcadeOnSurface = Color(0xFFF2EFFF)
val ArcadeOnSurfaceDim = Color(0xFFB8B0D9)
val ArcadeOutline = Color(0xFF4A4178)

// Formas cuadradas “pixel” para todos los componentes.
val PixelShapes = androidx.compose.material3.Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(6.dp),
    large = RoundedCornerShape(10.dp),
    extraLarge = RoundedCornerShape(14.dp),
)

// Tipografías: monoespaciada (estilo arcade) para títulos y números.
val PixelTypography = Typography(
    displaySmall = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 34.sp, letterSpacing = 2.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 24.sp, letterSpacing = 1.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 20.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.Default, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, letterSpacing = 0.5.sp),
)

private val ArcadeColorScheme = darkColorScheme(
    primary = ArcadePrimary,
    onPrimary = Color(0xFF062112),
    secondary = ArcadeSecondary,
    onSecondary = Color(0xFF2A0A20),
    tertiary = ArcadeTertiary,
    background = ArcadeBackground,
    onBackground = ArcadeOnSurface,
    surface = ArcadeSurface,
    onSurface = ArcadeOnSurface,
    surfaceVariant = ArcadeSurfaceVariant,
    onSurfaceVariant = ArcadeOnSurfaceDim,
    error = ArcadeError,
    onError = Color(0xFF2A0505),
    outline = ArcadeOutline,
)

@Composable
fun WaliPetTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ArcadeColorScheme,
        typography = PixelTypography,
        shapes = PixelShapes,
        content = content,
    )
}
