package com.walipet.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.walipet.ui.theme.ArcadeOutline
import com.walipet.ui.theme.ArcadePrimary
import com.walipet.ui.theme.ArcadeSecondary
import com.walipet.ui.theme.ArcadeSurface
import com.walipet.ui.theme.ArcadeSurfaceVariant

/** Tarjeta base moderna: fondo claro, esquinas redondeadas y borde sutil. */
@Composable
fun PixelCard(
    modifier: Modifier = Modifier,
    borderColor: Color = ArcadeOutline.copy(alpha = 0.4f),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(ArcadeSurface)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(18.dp))
            .padding(16.dp),
        content = { content() },
    )
}

/** Barra de progreso: el relleno gana color conforme avanza. */
@Composable
fun PixelProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = ArcadePrimary,
    segments: Int = 20,
) {
    val target = if (progress.isNaN()) 0f else progress.coerceIn(0f, 1f)
    val animated = remember { Animatable(0f) }
    LaunchedEffect(target) {
        animated.animateTo(
            targetValue = target,
            animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        )
    }
    val fraction = animated.value
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ArcadeSurfaceVariant),
    ) {
        if (fraction > 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .fillMaxWidth(fraction.coerceIn(0.03f, 1f))
                    .clip(RoundedCornerShape(8.dp))
                    .background(color),
            )
        }
    }
}

/** Barra de mini-estadística (energía / felicidad). */
@Composable
fun PixelMiniBar(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
    color: Color = ArcadeSecondary,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        PixelProgressBar(progress = value / 100f, color = color)
    }
}

/** Chip compacto para métricas del Dashboard/Stats. */
@Composable
fun StatChip(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    accent: Color = ArcadePrimary,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(ArcadeSurface)
            .border(1.dp, ArcadeOutline.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = accent,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Insignia de nivel: píldora rellena estilo iOS. */
@Composable
fun LevelBadge(level: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(ArcadePrimary)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = "NIVEL $level",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
        )
    }
}

/** Espaciador vertical reutilizable. */
@Composable
fun PixelSpacer(height: Dp) = Box(modifier = Modifier.height(height))
