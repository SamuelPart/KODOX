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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import com.walipet.engine.model.EvolutionStage
import com.walipet.engine.model.PetMood
import com.walipet.ui.theme.ArcadeOnSurface
import com.walipet.ui.theme.ArcadeSecondary
import com.walipet.ui.theme.ArcadeStreak
import com.walipet.ui.theme.ArcadeTertiary
import kotlin.math.PI
import kotlin.math.sin

/**
 * El WaliPet: el mismo zorro del icono, dibujado con forma clara
 * (orejas puntiagudas, hocico, cola) para que se reconozca al instante.
 */
@Composable
fun PixelArtPet(
    stage: EvolutionStage,
    mood: PetMood,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "pet")

    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bob",
    )

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
        val unit = size.minDimension
        val bobOffset = sin(bob * 2 * PI).toFloat() * unit * 0.025f
        val sleepDrift = if (mood == PetMood.SLEEPING) {
            sin(effectPhase * 2 * PI).toFloat() * unit * 0.012f
        } else {
            0f
        }
        val yShift = bobOffset + sleepDrift

        drawOval(
            color = Color.Black.copy(alpha = 0.18f),
            topLeft = Offset(size.width * 0.22f, size.height * 0.86f),
            size = Size(size.width * 0.56f, unit * 0.08f),
        )

        translate(top = yShift) {
            when (stage) {
                EvolutionStage.EGG -> drawFoxEgg(unit)
                EvolutionStage.BABY -> drawFox(unit, FoxLook.KIT, eyesClosed)
                EvolutionStage.YOUNG -> drawFox(unit, FoxLook.YOUNG, eyesClosed)
                EvolutionStage.EPIC -> drawFox(unit, FoxLook.EPIC, eyesClosed)
            }
        }

        when (mood) {
            PetMood.HAPPY -> {
                drawHeart(effectPhase, offsetX = unit * 0.16f, baseY = unit * 0.14f, color = ArcadeSecondary)
                drawHeart((effectPhase + 0.5f).mod(1f), offsetX = unit * 0.78f, baseY = unit * 0.20f, color = ArcadeStreak)
            }
            PetMood.SAD -> drawSweat(effectPhase, color = ArcadeTertiary)
            PetMood.SLEEPING -> {
                drawZ(effectPhase, offset = 1f, color = ArcadeTertiary)
                drawZ((effectPhase + 0.4f).mod(1f), offset = 2f, color = ArcadeOnSurface)
            }
            PetMood.CONTENT -> Unit
        }
    }
}

private enum class FoxLook { KIT, YOUNG, EPIC }

private val FurOrange = Color(0xFFE56B1F)
private val FurDeep = Color(0xFFC45312)
private val FurCream = Color(0xFFFFF4E4)
private val FurWhite = Color(0xFFFFFFF6)
private val InnerEar = Color(0xFFFFB197)
private val EyeDark = Color(0xFF1C140E)
private val NoseDark = Color(0xFF2A2118)
private val EggShell = Color(0xFFF6EED8)
private val EggShade = Color(0xFFE4D4B0)
private val EggSpot = Color(0xFF7EC4F0)
private val CheckTeal = Color(0xFF14C8B8)

private fun DrawScope.drawFoxEgg(unit: Float) {
    val cx = size.width / 2f
    val cy = size.height * 0.56f
    val eggW = unit * 0.46f
    val eggH = unit * 0.58f

    // Orejas del zorro asomando: se entiende qué hay dentro.
    val earH = unit * 0.16f
    val earW = unit * 0.13f
    drawEar(cx - unit * 0.11f, cy - eggH * 0.52f, earW, earH, lean = -12f)
    drawEar(cx + unit * 0.11f, cy - eggH * 0.52f, earW, earH, lean = 12f)

    drawOval(
        color = EggShade,
        topLeft = Offset(cx - eggW * 0.50f, cy - eggH * 0.42f),
        size = Size(eggW, eggH),
    )
    drawOval(
        color = EggShell,
        topLeft = Offset(cx - eggW * 0.46f, cy - eggH * 0.46f),
        size = Size(eggW * 0.92f, eggH * 0.92f),
    )
    drawCircle(EggSpot, unit * 0.035f, Offset(cx - unit * 0.08f, cy + unit * 0.02f))
    drawCircle(EggSpot, unit * 0.025f, Offset(cx + unit * 0.10f, cy + unit * 0.10f))
    drawCircle(EggSpot.copy(alpha = 0.7f), unit * 0.02f, Offset(cx + unit * 0.02f, cy - unit * 0.08f))
}

private fun DrawScope.drawFox(unit: Float, look: FoxLook, eyesClosed: Boolean) {
    val cx = size.width / 2f
    val bodyScale = when (look) {
        FoxLook.KIT -> 0.82f
        FoxLook.YOUNG -> 0.94f
        FoxLook.EPIC -> 1f
    }
    val headR = unit * 0.22f * bodyScale
    val headCy = size.height * when (look) {
        FoxLook.KIT -> 0.42f
        else -> 0.38f
    }
    val bodyTop = headCy + headR * 0.55f

    drawTail(cx + headR * 0.95f, bodyTop + unit * 0.10f, unit * bodyScale, look)
    drawBody(cx, bodyTop, unit * bodyScale, look)
    drawHead(cx, headCy, headR, look, eyesClosed)

    if (look == FoxLook.EPIC) {
        drawCheckBubble(cx + headR * 0.95f, headCy + headR * 0.55f, unit * 0.13f)
    }
}

private fun DrawScope.drawTail(x: Float, y: Float, unit: Float, look: FoxLook) {
    val tail = Path().apply {
        moveTo(x - unit * 0.04f, y)
        cubicTo(
            x + unit * 0.22f, y - unit * 0.04f,
            x + unit * 0.34f, y + unit * 0.10f,
            x + unit * 0.18f, y + unit * 0.28f,
        )
        cubicTo(
            x + unit * 0.08f, y + unit * 0.36f,
            x - unit * 0.02f, y + unit * 0.18f,
            x - unit * 0.04f, y,
        )
        close()
    }
    drawPath(tail, FurDeep)
    val tip = Path().apply {
        moveTo(x + unit * 0.16f, y + unit * 0.16f)
        quadraticBezierTo(x + unit * 0.30f, y + unit * 0.18f, x + unit * 0.18f, y + unit * 0.28f)
        quadraticBezierTo(x + unit * 0.08f, y + unit * 0.30f, x + unit * 0.16f, y + unit * 0.16f)
        close()
    }
    drawPath(tip, FurWhite)
    if (look == FoxLook.KIT) return
}

private fun DrawScope.drawBody(cx: Float, top: Float, unit: Float, look: FoxLook) {
    val bodyW = unit * if (look == FoxLook.KIT) 0.34f else 0.38f
    val bodyH = unit * if (look == FoxLook.KIT) 0.28f else 0.32f
    drawOval(
        color = FurOrange,
        topLeft = Offset(cx - bodyW / 2f, top),
        size = Size(bodyW, bodyH),
    )
    // Pecho crema: marca de zorro.
    drawOval(
        color = FurCream,
        topLeft = Offset(cx - bodyW * 0.28f, top + bodyH * 0.12f),
        size = Size(bodyW * 0.56f, bodyH * 0.70f),
    )
    val pawY = top + bodyH * 0.78f
    val pawR = unit * 0.045f
    drawCircle(FurDeep, pawR, Offset(cx - bodyW * 0.22f, pawY))
    drawCircle(FurDeep, pawR, Offset(cx + bodyW * 0.22f, pawY))
}

private fun DrawScope.drawHead(
    cx: Float,
    cy: Float,
    r: Float,
    look: FoxLook,
    eyesClosed: Boolean,
) {
    val earW = r * 0.78f
    val earH = r * 1.05f
    drawEar(cx - r * 0.62f, cy - r * 0.55f, earW, earH, lean = -16f)
    drawEar(cx + r * 0.62f, cy - r * 0.55f, earW, earH, lean = 16f)

    // Cabeza: óvalo ancho, como el icono.
    drawOval(
        color = FurOrange,
        topLeft = Offset(cx - r * 1.05f, cy - r * 0.92f),
        size = Size(r * 2.10f, r * 1.95f),
    )

    // Mejillas / hocico crema (la “máscara” del zorro).
    val muzzle = Path().apply {
        moveTo(cx, cy + r * 0.82f)
        cubicTo(
            cx - r * 0.95f, cy + r * 0.55f,
            cx - r * 0.88f, cy - r * 0.05f,
            cx - r * 0.22f, cy + r * 0.05f,
        )
        lineTo(cx, cy + r * 0.18f)
        lineTo(cx + r * 0.22f, cy + r * 0.05f)
        cubicTo(
            cx + r * 0.88f, cy - r * 0.05f,
            cx + r * 0.95f, cy + r * 0.55f,
            cx, cy + r * 0.82f,
        )
        close()
    }
    drawPath(muzzle, FurCream)

    drawEyes(cx, cy, r, look, eyesClosed)

    // Nariz triangular, apuntando abajo.
    val nose = Path().apply {
        moveTo(cx, cy + r * 0.42f)
        lineTo(cx - r * 0.11f, cy + r * 0.30f)
        lineTo(cx + r * 0.11f, cy + r * 0.30f)
        close()
    }
    drawPath(nose, NoseDark)

    // Boca suave en V.
    val mouth = Path().apply {
        moveTo(cx, cy + r * 0.44f)
        quadraticBezierTo(cx - r * 0.10f, cy + r * 0.56f, cx - r * 0.20f, cy + r * 0.50f)
        moveTo(cx, cy + r * 0.44f)
        quadraticBezierTo(cx + r * 0.10f, cy + r * 0.56f, cx + r * 0.20f, cy + r * 0.50f)
    }
    drawPath(mouth, NoseDark, style = Stroke(width = r * 0.06f, cap = StrokeCap.Round))
}

private fun DrawScope.drawEar(cx: Float, baseY: Float, w: Float, h: Float, lean: Float) {
    rotate(lean, pivot = Offset(cx, baseY + h * 0.15f)) {
        val outer = Path().apply {
            moveTo(cx, baseY - h)
            lineTo(cx - w * 0.55f, baseY + h * 0.15f)
            quadraticBezierTo(cx, baseY + h * 0.28f, cx + w * 0.55f, baseY + h * 0.15f)
            close()
        }
        drawPath(outer, FurOrange)
        val inner = Path().apply {
            moveTo(cx, baseY - h * 0.72f)
            lineTo(cx - w * 0.22f, baseY + h * 0.02f)
            lineTo(cx + w * 0.22f, baseY + h * 0.02f)
            close()
        }
        drawPath(inner, InnerEar)
    }
}

private fun DrawScope.drawEyes(
    cx: Float,
    cy: Float,
    r: Float,
    look: FoxLook,
    eyesClosed: Boolean,
) {
    val eyeY = cy - r * 0.08f
    val eyeDx = r * 0.38f
    val eyeW = r * if (look == FoxLook.KIT) 0.28f else 0.24f
    val eyeH = r * if (look == FoxLook.KIT) 0.32f else 0.28f

    fun one(x: Float) {
        if (eyesClosed) {
            val lid = Path().apply {
                moveTo(x - eyeW, eyeY)
                quadraticBezierTo(x, eyeY + eyeH * 0.35f, x + eyeW, eyeY)
            }
            drawPath(lid, EyeDark, style = Stroke(width = r * 0.07f, cap = StrokeCap.Round))
        } else {
            drawOval(
                color = EyeDark,
                topLeft = Offset(x - eyeW, eyeY - eyeH),
                size = Size(eyeW * 2f, eyeH * 2f),
            )
            drawCircle(
                color = FurWhite,
                radius = eyeW * 0.28f,
                center = Offset(x - eyeW * 0.25f, eyeY - eyeH * 0.30f),
            )
        }
    }
    one(cx - eyeDx)
    one(cx + eyeDx)
}

private fun DrawScope.drawCheckBubble(cx: Float, cy: Float, r: Float) {
    drawCircle(CheckTeal, r, Offset(cx, cy))
    drawCircle(FurWhite, r * 0.86f, Offset(cx, cy))
    val tail = Path().apply {
        moveTo(cx - r * 0.15f, cy + r * 0.72f)
        lineTo(cx - r * 0.55f, cy + r * 1.25f)
        lineTo(cx + r * 0.22f, cy + r * 0.78f)
        close()
    }
    drawPath(tail, FurWhite)
    val check = Path().apply {
        moveTo(cx - r * 0.38f, cy)
        lineTo(cx - r * 0.08f, cy + r * 0.32f)
        lineTo(cx + r * 0.42f, cy - r * 0.28f)
    }
    drawPath(check, CheckTeal, style = Stroke(width = r * 0.18f, cap = StrokeCap.Round))
}

private fun DrawScope.drawHeart(phase: Float, offsetX: Float, baseY: Float, color: Color) {
    val cell = size.minDimension / 16f
    val rise = phase * cell * 1.6f
    val sway = sin(phase * 2 * PI).toFloat() * cell * 0.3f
    val x = offsetX + sway
    val y = baseY - rise
    drawRect(color, Offset(x + cell, y), Size(cell, cell))
    drawRect(color, Offset(x + 2 * cell, y), Size(cell, cell))
    drawRect(color, Offset(x, y + cell), Size(cell, cell))
    drawRect(color, Offset(x + cell, y + cell), Size(cell, cell))
    drawRect(color, Offset(x + 2 * cell, y + cell), Size(cell, cell))
    drawRect(color, Offset(x + cell, y + 2 * cell), Size(cell, cell))
}

private fun DrawScope.drawSweat(phase: Float, color: Color) {
    val cell = size.minDimension / 16f
    val x = size.minDimension * 0.78f + sin(phase * 2 * PI).toFloat() * cell * 0.2f
    val y = size.minDimension * 0.22f + phase * cell * 0.8f
    drawRect(color, Offset(x + cell, y), Size(cell, cell))
    drawRect(color, Offset(x, y + cell), Size(cell, cell))
    drawRect(color, Offset(x + cell, y + cell), Size(cell, cell))
    drawRect(color, Offset(x + cell, y + 2 * cell), Size(cell, cell))
}

private fun DrawScope.drawZ(phase: Float, offset: Float, color: Color) {
    val cell = size.minDimension / 16f
    val rise = phase * cell * (2f + offset)
    val x = size.minDimension * 0.72f + offset * cell * 2f
    val y = size.minDimension * 0.16f - offset * cell - rise
    drawRect(color, Offset(x, y), Size(cell * 2, cell))
    drawRect(color, Offset(x + cell, y + cell), Size(cell, cell))
    drawRect(color, Offset(x, y + 2 * cell), Size(cell * 2, cell))
}
