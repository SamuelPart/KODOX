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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Resultado de registrar una lectura en voz alta. */
data class ProgressResult(
    val pet: PetState,
    val xpGained: Int,
    val leveledUp: Boolean,
    val levelsGained: Int,
    val isFirstReadingOfDay: Boolean,
)

/**
 * Repositorio del estado de la mascota. Única puerta de escritura del
 * progreso: aplica el motor (XP + rachas) y persiste en Room.
 *
 * El huevo y la racha crecen solo con lecturas en voz alta, nunca con
 * respuestas automáticas.
 */
class PetRepository(
    private val dao: PetDao,
    private val clock: Clock = Clock.SYSTEM,
) {

    private val writeLock = Mutex()

    fun observe(): Flow<PetState> =
        dao.observe().map { it?.toDomain() ?: PetState() }

    suspend fun getState(): PetState = dao.get()?.toDomain() ?: PetState()

    /** Crea la fila de la mascota en el primer arranque. */
    suspend fun ensureSeeded() {
        if (dao.get() == null) dao.upsert(PetEntity())
    }

    /**
     * Registra un mensaje leído en voz alta:
     * actualiza XP/nivel, racha, energía/felicidad y contadores.
     */
    suspend fun recordSpokenMessage(
        today: LocalDate = clock.today(),
        now: Instant = clock.now(),
    ): ProgressResult = writeLock.withLock {
        val current = getState()

        val streak = StreakEngine.applyReading(current.streakDays, current.lastAutomationDate, today)
        val xpAmount = XpEngine.xpForReading(streak.isFirstReadingOfDay)
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

        ProgressResult(
            pet = updated,
            xpGained = xpAmount,
            leveledUp = xp.leveledUp,
            levelsGained = xp.levelsGained,
            isFirstReadingOfDay = streak.isFirstReadingOfDay,
        )
    }
}
