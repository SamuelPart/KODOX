package com.walipet.data.repository

import com.walipet.data.local.PetDao
import com.walipet.data.local.entity.PetEntity
import com.walipet.engine.model.PetState
import com.walipet.engine.pet.StreakEngine
import com.walipet.engine.pet.XpEngine
import com.walipet.engine.time.Clock
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Resultado de registrar una automatización exitosa. */
data class AutomationResult(
    val pet: PetState,
    val xpGained: Int,
    val leveledUp: Boolean,
    val levelsGained: Int,
    val isFirstAutomationOfDay: Boolean,
)

/**
 * Repositorio del estado de la mascota. Única puerta de escritura del
 * progreso: aplica el motor (XP + rachas) y persiste en Room.
 */
class PetRepository(
    private val dao: PetDao,
    private val clock: Clock = Clock.SYSTEM,
) {

    fun observe(): Flow<PetState> =
        dao.observe().map { it?.toDomain() ?: PetState() }

    suspend fun getState(): PetState = dao.get()?.toDomain() ?: PetState()

    /** Crea la fila de la mascota en el primer arranque. */
    suspend fun ensureSeeded() {
        if (dao.get() == null) dao.upsert(PetEntity())
    }

    /**
     * Registra una respuesta automática enviada con éxito:
     * actualiza XP/nivel, racha, energía/felicidad y contadores.
     */
    suspend fun recordAutomation(today: LocalDate = clock.today(), now: Instant = clock.now()): AutomationResult {
        val current = getState()

        val streak = StreakEngine.applyAutomation(current.streakDays, current.lastAutomationDate, today)
        val xpAmount = XpEngine.xpForAutomation(streak.isFirstAutomationOfDay)
        val xp = XpEngine.addXp(current.level, current.xp, xpAmount)

        val updated = current.copy(
            level = xp.newLevel,
            xp = xp.newXp,
            streakDays = streak.streakDays,
            longestStreak = maxOf(current.longestStreak, streak.streakDays),
            totalAutomations = current.totalAutomations + 1,
            energy = StreakEngine.energyFor(0),
            happiness = StreakEngine.happinessFor(streak.streakDays, 0),
            lastAutomationDate = today,
            lastAutomationAt = now,
        )
        dao.upsert(PetEntity.fromDomain(updated))

        return AutomationResult(
            pet = updated,
            xpGained = xpAmount,
            leveledUp = xp.leveledUp,
            levelsGained = xp.levelsGained,
            isFirstAutomationOfDay = streak.isFirstAutomationOfDay,
        )
    }
}
