package com.walipet.engine.pet

import com.walipet.engine.model.Achievement
import com.walipet.engine.model.AchievementContext
import com.walipet.engine.model.PetState

/**
 * Catálogo de logros/medallas de WaliPet. Cada logro declara su condición
 * como predicado puro sobre [AchievementContext], por lo que es 100 % testeable.
 *
 * La UI resuelve `title`/`description` desde strings usando el [Achievement.id].
 */
object AchievementChecker {

    val ACHIEVEMENTS: List<Achievement> = listOf(
        Achievement("first_reply", "robot", { it.stats.totalCount >= 1 }),
        Achievement("replies_10", "bolt", { it.stats.totalCount >= 10 }),
        Achievement("replies_100", "hundred", { it.stats.totalCount >= 100 }),
        Achievement("replies_500", "rocket", { it.stats.totalCount >= 500 }),
        Achievement("streak_3", "flame", { it.pet.streakDays >= 3 }),
        Achievement("streak_7", "flame", { it.pet.streakDays >= 7 }),
        Achievement("streak_30", "trophy", { it.pet.streakDays >= 30 }),
        Achievement("level_3", "egg", { it.pet.level >= 3 }),
        Achievement("level_6", "shield", { it.pet.level >= 6 }),
        Achievement("level_10", "star", { it.pet.level >= 10 }),
        Achievement("xp_1000", "sparkle", { XpEngine.lifetimeXp(it.pet.level, it.pet.xp) >= 1_000 }),
        Achievement("hour_saved", "timer", { it.stats.timeSavedSeconds >= 3_600 }),
    )

    /** Logros desbloqueados para un contexto dado. */
    fun unlocked(context: AchievementContext): List<Achievement> =
        ACHIEVEMENTS.filter { it.condition(context) }

    /** Fracción de logros desbloqueados (0..1) para la barra del Codex. */
    fun unlockProgress(context: AchievementContext): Float {
        if (ACHIEVEMENTS.isEmpty()) return 0f
        return unlocked(context).size.toFloat() / ACHIEVEMENTS.size.toFloat()
    }
}
