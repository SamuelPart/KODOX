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
        Achievement("first_reply", "🤖", { it.stats.totalCount >= 1 }),
        Achievement("replies_10", "⚡", { it.stats.totalCount >= 10 }),
        Achievement("replies_100", "💯", { it.stats.totalCount >= 100 }),
        Achievement("replies_500", "🚀", { it.stats.totalCount >= 500 }),
        Achievement("streak_3", "🔥", { it.pet.streakDays >= 3 }),
        Achievement("streak_7", "🔥", { it.pet.streakDays >= 7 }),
        Achievement("streak_30", "🌋", { it.pet.streakDays >= 30 }),
        Achievement("level_3", "🐣", { it.pet.level >= 3 }),
        Achievement("level_6", "🛡️", { it.pet.level >= 6 }),
        Achievement("level_10", "👑", { it.pet.level >= 10 }),
        Achievement("xp_1000", "✨", { it.stats.totalXp >= 1_000 }),
        Achievement("hour_saved", "⏱️", { it.stats.timeSavedSeconds >= 3_600 }),
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
