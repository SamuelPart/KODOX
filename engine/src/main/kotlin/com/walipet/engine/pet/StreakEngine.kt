package com.walipet.engine.pet

import com.walipet.engine.model.PetState
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Sistema de rachas (streaks): premia leer al menos un mensaje en voz alta al día.
 *
 * Reglas:
 *  - Primera lectura de la historia            → racha = 1.
 *  - Segunda lectura el mismo día              → la racha no cambia.
 *  - Lectura al día siguiente                  → racha + 1.
 *  - Si pasó más de un día completo sin leer   → la racha se reinicia a 1
 *    (se considera "rota").
 * Las respuestas automáticas no cuentan.
 */
object StreakEngine {

    data class StreakResult(
        val streakDays: Int,
        /** `true` si es la primera lectura del día (otorga bonus de XP). */
        val isFirstReadingOfDay: Boolean,
        /** `true` si la racha anterior se perdió por inactividad. */
        val streakBroken: Boolean,
    )

    /**
     * Calcula el nuevo valor de racha tras una lectura en voz alta hoy.
     *
     * @param currentStreak racha actual almacenada.
     * @param lastAutomationDate fecha de la última lectura (null si nunca).
     * @param today fecha de hoy (inyectada vía Clock para poder probar).
     */
    fun applyReading(currentStreak: Int, lastAutomationDate: LocalDate?, today: LocalDate): StreakResult {
        return when {
            lastAutomationDate == null -> StreakResult(1, true, currentStreak > 0)
            lastAutomationDate == today -> StreakResult(maxOf(currentStreak, 1), false, false)
            lastAutomationDate == today.minusDays(1) -> StreakResult(currentStreak + 1, true, false)
            else -> StreakResult(1, true, true)
        }
    }

    /** Días completos transcurridos desde la última lectura (0 si fue hoy). */
    fun missedDays(lastAutomationDate: LocalDate?, today: LocalDate): Int {
        if (lastAutomationDate == null) return 0
        return ChronoUnit.DAYS.between(lastAutomationDate, today).toInt().coerceAtLeast(0)
    }

    /**
     * `true` si la racha está en peligro: hay racha activa y se perdió al
     * menos un día completo sin leer en voz alta.
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
