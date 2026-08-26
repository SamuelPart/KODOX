package com.walipet.engine.pet

import com.walipet.engine.model.PetState

/**
 * Motor de experiencia (XP) y niveles.
 *
 * Curva progresiva: para subir del nivel N al N+1 se necesitan `100 * N` XP.
 *  - Nivel 1 → 2 :  100 XP
 *  - Nivel 2 → 3 :  200 XP
 *  - Nivel 3 → 4 :  300 XP
 *  - ...
 *  - Nivel 19 → 20: 1900 XP
 *
 * Cada mensaje leído en voz alta otorga [XP_PER_READING] XP (+ [FIRST_READING_OF_DAY_BONUS]
 * si es la primera lectura del día, para premiar la constancia diaria).
 * Las respuestas automáticas no dan XP.
 */
object XpEngine {

    const val XP_PER_READING = 15
    const val FIRST_READING_OF_DAY_BONUS = 10

    /** XP necesarios para subir de [level] a level+1 (0 si ya es el máximo). */
    fun xpToNextLevel(level: Int): Int {
        require(level >= 1) { "El nivel mínimo es 1" }
        return if (level >= PetState.MAX_LEVEL) 0 else 100 * level
    }

    /** XP totales acumulados necesarios para alcanzar [level] desde el nivel 1. */
    fun totalXpForLevel(level: Int): Int = (1 until level).sumOf { xpToNextLevel(it) }

    data class XpResult(
        val newXp: Int,
        val newLevel: Int,
        val levelsGained: Int,
    ) {
        val leveledUp: Boolean get() = levelsGained > 0
    }

    /**
     * Suma [amount] XP al estado actual y aplica las subidas de nivel
     * correspondientes (pueden ser varias de golpe).
     */
    fun addXp(currentLevel: Int, currentXp: Int, amount: Int): XpResult {
        require(currentLevel in 1..PetState.MAX_LEVEL) { "Nivel fuera de rango: $currentLevel" }
        require(amount >= 0) { "El XP añadido no puede ser negativo" }

        var level = currentLevel
        var xp = currentXp + amount
        var gained = 0

        while (level < PetState.MAX_LEVEL && xp >= xpToNextLevel(level)) {
            xp -= xpToNextLevel(level)
            level += 1
            gained += 1
        }

        // En el nivel máximo se muestra la barra llena.
        if (level == PetState.MAX_LEVEL) xp = 0

        return XpResult(newXp = xp, newLevel = level, levelsGained = gained)
    }

    /** XP de por vida: lo ya invertido en niveles más el progreso del nivel actual. */
    fun lifetimeXp(level: Int, xp: Int): Int = totalXpForLevel(level) + xp

    /**
     * Puntos de experiencia que otorga una lectura en voz alta, teniendo en
     * cuenta si es la primera del día (bonus de constancia).
     */
    fun xpForReading(isFirstReadingOfDay: Boolean): Int =
        XP_PER_READING + if (isFirstReadingOfDay) FIRST_READING_OF_DAY_BONUS else 0
}
