package com.walipet.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Paleta moderna estilo iOS ───────────────────────────────────────────
// Colores del sistema iOS (System colors) aplicados a un tema claro y limpio.

val ArcadeBackground = Color(0xFFF2F2F7)        // systemGroupedBackground
val ArcadeSurface = Color(0xFFFFFFFF)           // tarjetas / superficies
val ArcadeSurfaceVariant = Color(0xFFE9E9EE)    // rellenos secundarios
val ArcadePrimary = Color(0xFF007AFF)           // systemBlue (acento)
val ArcadeSecondary = Color(0xFF34C759)         // systemGreen (acento secundario)
val ArcadeTertiary = Color(0xFF5AC8FA)          // lightBlue (detalles)
val ArcadeStreak = Color(0xFFFF9500)            // systemOrange (racha / gráfico)
val ArcadeError = Color(0xFFFF3B30)             // systemRed
val ArcadeOnSurface = Color(0xFF1C1C1E)         // texto principal
val ArcadeOnSurfaceDim = Color(0xFF8E8E93)      // texto secundario
val ArcadeOutline = Color(0xFFC7C7CC)           // separadores / bordes

// Formas redondeadas modernas (iOS usa esquinas grandes y suaves).
val PixelShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(22.dp),
)

// Tipografía limpia tipo San Francisco: sans-serif, semibold en títulos.
val PixelTypography = Typography(
    displaySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 34.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 24.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 12.sp),
)

private val iOSColorScheme = lightColorScheme(
    primary = ArcadePrimary,
    onPrimary = Color(0xFFFFFFFF),
    secondary = ArcadeSecondary,
    onSecondary = Color(0xFFFFFFFF),
    tertiary = ArcadeTertiary,
    background = ArcadeBackground,
    onBackground = ArcadeOnSurface,
    surface = ArcadeSurface,
    onSurface = ArcadeOnSurface,
    surfaceVariant = ArcadeSurfaceVariant,
    onSurfaceVariant = ArcadeOnSurfaceDim,
    error = ArcadeError,
    onError = Color(0xFFFFFFFF),
    outline = ArcadeOutline,
)

@Composable
fun WaliPetTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = iOSColorScheme,
        typography = PixelTypography,
        shapes = PixelShapes,
        content = content,
    )
}
