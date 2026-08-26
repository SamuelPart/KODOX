package com.walipet.engine.model

import java.time.Instant
import java.time.LocalDate

/**
 * Estado completo de la mascota (WaliPet).
 *
 * Todo el progreso que se ve en el Dashboard (nivel, XP, racha, energía,
 * felicidad, lecturas totales) vive aquí. Los campos `energy` y
 * `happiness` son derivados cada vez que se consulta el estado, a partir
 * de la recencia de lecturas en voz alta ([lastAutomationDate]) y la racha.
 */
data class PetState(
    val level: Int = 1,
    val xp: Int = 0,
    /** Racha actual en días consecutivos con al menos una lectura en voz alta. */
    val streakDays: Int = 0,
    /** Racha máxima histórica. */
    val longestStreak: Int = 0,
    /** Total de mensajes leídos en voz alta (el huevo crece con esto, no con auto-respuestas). */
    val totalAutomations: Long = 0,
    /** Energía (0..100), derivada del tiempo sin actividad. */
    val energy: Int = PetState.MAX_ENERGY,
    /** Felicidad (0..100), derivada de racha y actividad reciente. */
    val happiness: Int = PetState.MAX_HAPPINESS,
    val lastAutomationDate: LocalDate? = null,
    val lastAutomationAt: Instant? = null,
    val createdAt: Instant = Instant.EPOCH,
) {
    companion object {
        const val MAX_LEVEL = 20
        const val MAX_ENERGY = 100
        const val MAX_HAPPINESS = 100
    }
}
