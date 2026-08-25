package com.walipet.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.walipet.engine.model.EvolutionStage
import com.walipet.engine.model.PetMood
import com.walipet.ui.theme.ArcadeOnSurface
import com.walipet.ui.theme.ArcadePrimary
import com.walipet.ui.theme.ArcadeSecondary
import com.walipet.ui.theme.ArcadeStreak
import com.walipet.ui.theme.ArcadeTertiary
import kotlin.math.PI
import kotlin.math.sin

/**
 * Renderiza al WaliPet como pixel art puro (sin assets de imagen): cada
 * sprite es una rejilla de 16×16 caracteres mapeada a colores, dibujada con
 * Canvas y animada (bote, parpadeo y efectos según el estado de ánimo).
 *
 * Los sprites están diseñados a mano en [Sprite.rows] (16 columnas exactas).
 */
@Composable
fun PixelArtPet(
    stage: EvolutionStage,
    mood: PetMood,
    modifier: Modifier = Modifier,
) {
    val sprite = Sprite.forStage(stage)
    val transition = rememberInfiniteTransition(label = "pet")

    // Bote suave arriba/abajo.
    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bob",
    )

    // Parpadeo: mantiene los ojos abiertos ~2 s y parpadea 200 ms.
    val blink by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2600
                0f at 0
                0f at 2200
                1f at 2400
                0f at 2600
            },
        ),
        label = "blink",
    )

    // Fase para corazones / gota / Z (efectos flotantes).
    val effectPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
        ),
        label = "effect",
    )

    val eyesClosed = mood == PetMood.SLEEPING || blink > 0.5f

    Canvas(modifier = modifier) {
        val grid = 16
        val cell = size.minDimension / grid

        // Sombra en el suelo.
        val shadowW = cell * 12f
        drawOval(
            color = Color.Black.copy(alpha = 0.35f),
            topLeft = Offset((size.width - shadowW) / 2f, size.height - cell * 1.2f),
            size = Size(shadowW, cell * 0.9f),
        )

        // Desplazamiento vertical por el bote (y por el ánimo).
        val bobOffset = sin(bob * 2 * PI).toFloat() * cell * 0.35f
        val sleepDrift = if (mood == PetMood.SLEEPING) sin(effectPhase * 2 * PI).toFloat() * cell * 0.15f else 0f
        val yShift = bobOffset + sleepDrift

        val startX = (size.width - cell * grid) / 2f
        val startY = (size.height - cell * grid) / 2f + yShift

        // Píxeles del sprite (los ojos cerrados se pintan del color del cuerpo).
        sprite.rows.forEachIndexed { row, line ->
            line.forEachIndexed { col, ch ->
                val color = sprite.palette[ch] ?: return@forEachIndexed
                val drawColor = if (eyesClosed && ch in sprite.eyeChars) sprite.bodyColor else color
                drawRect(
                    color = drawColor,
                    topLeft = Offset(startX + col * cell, startY + row * cell),
                    size = Size(cell, cell),
                )
            }
        }

        // Efectos según el ánimo.
        when (mood) {
            PetMood.HAPPY -> {
                drawHeart(startX, startY, effectPhase, offsetCols = 2f, baseRow = 1f, color = ArcadeSecondary)
                drawHeart(startX, startY, (effectPhase + 0.5f).mod(1f), offsetCols = 12f, baseRow = 2f, color = ArcadeStreak)
            }
            PetMood.SAD -> {
                drawSweat(startX, startY, effectPhase, color = ArcadeTertiary)
            }
            PetMood.SLEEPING -> {
                drawZ(startX, startY, effectPhase, offset = 1f, color = ArcadeTertiary)
                drawZ(startX, startY, (effectPhase + 0.4f).mod(1f), offset = 2f, color = ArcadeOnSurface)
            }
            PetMood.CONTENT -> Unit
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHeart(
    startX: Float,
    startY: Float,
    phase: Float,
    offsetCols: Float,
    baseRow: Float,
    color: Color,
) {
    val cell = size.minDimension / 16
    val rise = phase * cell * 1.6f
    val sway = sin(phase * 2 * PI).toFloat() * cell * 0.3f
    val x = startX + offsetCols * cell + sway
    val y = startY + baseRow * cell - rise
    // Corazón de 3×3 píxeles.
    drawRect(color, Offset(x + cell, y), Size(cell, cell))
    drawRect(color, Offset(x + 2 * cell, y), Size(cell, cell))
    drawRect(color, Offset(x, y + cell), Size(cell, cell))
    drawRect(color, Offset(x + cell, y + cell), Size(cell, cell))
    drawRect(color, Offset(x + 2 * cell, y + cell), Size(cell, cell))
    drawRect(color, Offset(x + cell, y + 2 * cell), Size(cell, cell))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSweat(
    startX: Float,
    startY: Float,
    phase: Float,
    color: Color,
) {
    val cell = size.minDimension / 16
    val x = startX + 13f * cell + sin(phase * 2 * PI).toFloat() * cell * 0.2f
    val y = startY + 3f * cell + phase * cell * 0.8f
    drawRect(color, Offset(x + cell, y), Size(cell, cell))
    drawRect(color, Offset(x, y + cell), Size(cell, cell))
    drawRect(color, Offset(x + cell, y + cell), Size(cell, cell))
    drawRect(color, Offset(x + cell, y + 2 * cell), Size(cell, cell))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawZ(
    startX: Float,
    startY: Float,
    phase: Float,
    offset: Float,
    color: Color,
) {
    val cell = size.minDimension / 16
    val rise = phase * cell * (2f + offset)
    val x = startX + (12f + offset * 2f) * cell
    val y = startY + (3f - offset) * cell - rise
    drawRect(color, Offset(x, y), Size(cell * 2, cell))
    drawRect(color, Offset(x + cell, y + cell), Size(cell, cell))
    drawRect(color, Offset(x, y + 2 * cell), Size(cell * 2, cell))
}

/** Sprite pixel art: filas de 16 caracteres + paleta. */
private class Sprite(
    val rows: List<String>,
    val palette: Map<Char, Color>,
    /** Caracteres que representan los ojos (se "cierran" al parpadear/dormir). */
    val eyeChars: Set<Char> = setOf('W', 'B'),
) {
    /** Color dominante del cuerpo (se usa al cerrar los ojos). */
    val bodyColor: Color = palette[dominantBodyChar] ?: ArcadePrimary

    private val dominantBodyChar: Char
        get() = rows.flatMap { it.toList() }
            .filter { it != '.' }
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
            ?: 'C'

    companion object {
        fun forStage(stage: EvolutionStage): Sprite = when (stage) {
            EvolutionStage.EGG -> egg()
            EvolutionStage.BABY -> baby()
            EvolutionStage.YOUNG -> young()
            EvolutionStage.EPIC -> epic()
        }

        private fun egg(): Sprite {
            val c = Color(0xFFF5F0DC) // cáscara crema
            val s = Color(0xFF9BD8F5) // motas
            return Sprite(
                rows = listOf(
                    "................",
                    ".....CCCCCC.....",
                    "....CCCCCCCC....",
                    "...CCCCCCCCCC...",
                    "..CCCCCCCCCCCC..",
                    "..CCCCCCCCCCCC..",
                    ".CCCCCCCCCCCCCC.",
                    ".CCCCCCCCCCCCCC.",
                    ".CCCCCCCCCCCCCC.",
                    "..CCCCCCCCCCCC..",
                    ".CCSCCCCCCCCCCC.",
                    ".CCCCCCCCCCCSCC.",
                    "...CCCCCCCCCC...",
                    ".....CCCCCC.....",
                ),
                palette = mapOf('C' to c, 'S' to s),
            )
        }

        private fun baby(): Sprite {
            val g = ArcadePrimary
            val d = Color(0xFF0D0A1F)
            val w = Color(0xFFF2EFFF)
            val b = Color(0xFF0D0A1F)
            val p = ArcadeSecondary
            return Sprite(
                rows = listOf(
                    "................",
                    ".....GGGGGG.....",
                    "....GGGGGGGG....",
                    "...GGGGGGGGGG...",
                    "..GGGGGGGGGGGG..",
                    "..GGWGGGGGGWGG..",
                    "..GWBBGGGGBBWG..",
                    "..GGWGGGGGGWGG..",
                    "..GGPGGGGGGPGG..",
                    "..GGGGGDDGGGGG..",
                    "...GGGGGGGGGG...",
                    "....GGGGGGGG....",
                ),
                palette = mapOf('G' to g, 'D' to d, 'W' to w, 'B' to b, 'P' to p),
            )
        }

        private fun young(): Sprite {
            val g = ArcadePrimary
            val d = Color(0xFF0D0A1F)
            val w = Color(0xFFF2EFFF)
            val b = Color(0xFF0D0A1F)
            val p = ArcadeSecondary
            val a = ArcadeTertiary // antena
            val y = ArcadeStreak  // orbe
            return Sprite(
                rows = listOf(
                    "......AA........",
                    "......AA........",
                    "......AY........",
                    ".....GGGGGG.....",
                    "....GGGGGGGG....",
                    "...GGGGGGGGGG...",
                    "..GGGGGGGGGGGG..",
                    ".GGGGGGGGGGGGGG.",
                    ".GGWGGGGGGGGWGG.",
                    ".GWBBGGGGGGBBWG.",
                    ".GGWGGGGGGGGWGG.",
                    ".GGPGGGGGGGGPGG.",
                    ".GGGGGDDGGGGGGG.",
                    "...GGGGGGGGGG...",
                ),
                palette = mapOf('G' to g, 'D' to d, 'W' to w, 'B' to b, 'P' to p, 'A' to a, 'Y' to y),
            )
        }

        private fun epic(): Sprite {
            val m = Color(0xFF9AA7C0) // metal
            val v = ArcadeTertiary  // visor
            val e = Color(0xFFE8FBFF) // brillo del visor
            val g = ArcadePrimary
            val d = Color(0xFF0D0A1F)
            val y = ArcadeStreak
            val r = ArcadeSecondary // núcleo
            return Sprite(
                rows = listOf(
                    "..Y..........Y..",
                    "..D..........D..",
                    "..DDDDDDDDDDDD..",
                    ".DMMMMMMMMMMMMD.",
                    ".DMMMMMMMMMMMMD.",
                    ".DVVVVVVVVVVVVD.",
                    ".DVVEEVVVVEEVVD.",
                    ".DVVVVVVVVVVVVD.",
                    ".DMMMMMMMMMMMMD.",
                    ".DMMDMMMMMMDMMD.",
                    ".DDDDDDDDDDDDDD.",
                    "..GGGGGGGGGGGG..",
                    "..GMDDDDDDDDMG..",
                    "..GMDDRRRRDDMG..",
                    "..GMDDDDDDDDMG..",
                    "..GGGGGGGGGGGG..",
                ),
                palette = mapOf('M' to m, 'V' to v, 'E' to e, 'G' to g, 'D' to d, 'Y' to y, 'R' to r),
                eyeChars = setOf('E'),
            )
        }
    }
}
