package com.walipet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.walipet.ui.theme.ArcadeOutline
import com.walipet.ui.theme.ArcadePrimary
import com.walipet.ui.theme.ArcadeSecondary
import com.walipet.ui.theme.ArcadeStreak
import com.walipet.ui.theme.ArcadeSurface
import com.walipet.ui.theme.ArcadeSurfaceVariant

/** Tarjeta base con bordes “pixel” (esquinas pequeñas + borde). */
@Composable
fun PixelCard(
    modifier: Modifier = Modifier,
    borderColor: Color = ArcadeOutline,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ArcadeSurface)
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
            .padding(14.dp),
        content = { content() },
    )
}

/** Barra de progreso segmentada estilo RPG (20 bloques). */
@Composable
fun PixelProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = ArcadePrimary,
    segments: Int = 20,
) {
    val filled = (progress.coerceIn(0f, 1f) * segments).toInt()
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        repeat(segments) { index ->
            val isFilled = index < filled
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isFilled) color else ArcadeSurfaceVariant),
            )
        }
    }
}

/** Barra vertical de mini-estadística (energía / felicidad). */
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
        PixelProgressBar(progress = value / 100f, segments = 12, color = color)
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
            .clip(RoundedCornerShape(6.dp))
            .background(ArcadeSurfaceVariant)
            .border(1.dp, ArcadeOutline, RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
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

/** Insignia de nivel. */
@Composable
fun LevelBadge(level: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(ArcadeSurfaceVariant)
            .border(1.dp, ArcadeStreak, RoundedCornerShape(4.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            tint = ArcadeStreak,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = "NIVEL $level",
            style = MaterialTheme.typography.labelLarge,
            color = ArcadeStreak,
        )
    }
}

/** Espaciador vertical reutilizable. */
@Composable
fun PixelSpacer(height: Dp) = Box(modifier = Modifier.height(height))
