package com.walipet.engine.pet

import com.walipet.engine.model.PetState
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Sistema de rachas (streaks): premia automatizar al menos una vez al día.
 *
 * Reglas:
 *  - Primera automatización de la historia     → racha = 1.
 *  - Segunda automatización el mismo día       → la racha no cambia.
 *  - Automatización al día siguiente           → racha + 1.
 *  - Si pasó más de un día completo sin actuar → la racha se reinicia a 1
 *    (se considera "rota").
 */
object StreakEngine {

    data class StreakResult(
        val streakDays: Int,
        /** `true` si es la primera automatización del día (otorga bonus de XP). */
        val isFirstAutomationOfDay: Boolean,
        /** `true` si la racha anterior se perdió por inactividad. */
        val streakBroken: Boolean,
    )

    /**
     * Calcula el nuevo valor de racha tras una automatización exitosa hoy.
     *
     * @param currentStreak racha actual almacenada.
     * @param lastAutomationDate fecha de la última automatización (null si nunca).
     * @param today fecha de hoy (inyectada vía Clock para poder probar).
     */
    fun applyAutomation(currentStreak: Int, lastAutomationDate: LocalDate?, today: LocalDate): StreakResult {
        return when {
            lastAutomationDate == null -> StreakResult(1, true, currentStreak > 0)
            lastAutomationDate == today -> StreakResult(maxOf(currentStreak, 1), false, false)
            lastAutomationDate == today.minusDays(1) -> StreakResult(currentStreak + 1, true, false)
            else -> StreakResult(1, true, true)
        }
    }

    /** Días completos transcurridos desde la última automatización (0 si fue hoy). */
    fun missedDays(lastAutomationDate: LocalDate?, today: LocalDate): Int {
        if (lastAutomationDate == null) return 0
        return ChronoUnit.DAYS.between(lastAutomationDate, today).toInt().coerceAtLeast(0)
    }

    /**
     * `true` si la racha está en peligro: hay racha activa y se perdió al
     * menos un día completo sin automatizar.
     */
    fun isStreakInDanger(lastAutomationDate: LocalDate?, today: LocalDate): Boolean {
        if (lastAutomationDate == null) return false
        return missedDays(lastAutomationDate, today) >= 2
    }

    /** Energía derivada (0..100): decae 10 puntos por día completo sin actividad. */
    fun energyFor(missedDays: Int): Int =
        (PetState.MAX_ENERGY - missedDays * 10).coerceIn(0, PetState.MAX_ENERGY)

    /** Felicidad derivada (0..100): racha larga suma, días perdidos restan. */
    fun happinessFor(streakDays: Int, missedDays: Int): Int {
        var value = PetState.MAX_HAPPINESS
        value += streakDays.coerceAtMost(10) * 2   // hasta +20 por racha
        value -= missedDays * 15                    // -15 por cada día perdido
        return value.coerceIn(0, PetState.MAX_HAPPINESS)
    }
}
