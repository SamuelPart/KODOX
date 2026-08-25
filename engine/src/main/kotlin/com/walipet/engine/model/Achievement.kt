package com.walipet.engine.model

import java.time.LocalDate

/**
 * Métricas agregadas de automatización, usadas por el panel de estadísticas
 * y por el sistema de logros.
 */
data class AutomationStats(
    val totalCount: Long = 0L,
    val todayCount: Int = 0,
    val weekCount: Int = 0,
    val monthCount: Int = 0,
    val totalXp: Long = 0L,
    val timeSavedSeconds: Long = 0L,
    val rulesActive: Int = 0,
    val rulesTotal: Int = 0,
) {
    val timeSavedMinutes: Long get() = timeSavedSeconds / 60
}

/**
 * Contexto evaluado por el sistema de logros para decidir si un logro
 * está desbloqueado.
 */
data class AchievementContext(
    val pet: PetState,
    val stats: AutomationStats,
    val today: LocalDate,
)

/**
 * Logro o medalla desbloqueable por hitos de uso.
 * [id] es la clave estable; la UI resuelve títulos/descripciones desde strings.
 */
data class Achievement(
    val id: String,
    /** Clave estable del ícono; la UI la mapea a un ImageVector (sin emojis). */
    val icon: String,
    val condition: (AchievementContext) -> Boolean,
)
