package com.walipet.engine

import com.walipet.engine.model.AchievementContext
import com.walipet.engine.model.AutomationStats
import com.walipet.engine.model.PetState
import com.walipet.engine.pet.AchievementChecker
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import java.time.LocalDate

class AchievementCheckerTest {

    private val today = LocalDate.of(2026, 8, 25)

    private fun context(
        pet: PetState = PetState(),
        stats: AutomationStats = AutomationStats(),
    ) = AchievementContext(pet = pet, stats = stats, today = today)

    @Test
    fun `sin actividad no hay logros`() {
        assertEquals(0, AchievementChecker.unlocked(context()).size)
    }

    @Test
    fun `la primera respuesta desbloquea first_reply`() {
        val ctx = context(stats = AutomationStats(totalCount = 1))
        assertTrue(AchievementChecker.unlocked(ctx).any { it.id == "first_reply" })
    }

    @Test
    fun `cien respuestas desbloquean replies_100`() {
        val ctx = context(stats = AutomationStats(totalCount = 100, totalXp = 1_500))
        val ids = AchievementChecker.unlocked(ctx).map { it.id }
        assertTrue("replies_100" in ids)
        assertTrue("xp_1000" in ids)
        assertTrue("replies_500" !in ids)
    }

    @Test
    fun `racha de una semana desbloquea streak_7`() {
        val ctx = context(pet = PetState(streakDays = 7))
        assertTrue(AchievementChecker.unlocked(ctx).any { it.id == "streak_7" })
    }

    @Test
    fun `nivel diez desbloquea logros de evolucion`() {
        val ctx = context(pet = PetState(level = 10))
        val ids = AchievementChecker.unlocked(ctx).map { it.id }
        assertTrue("level_3" in ids && "level_6" in ids && "level_10" in ids)
    }

    @Test
    fun `una hora ahorrada desbloquea hour_saved`() {
        val ctx = context(stats = AutomationStats(timeSavedSeconds = 3_600))
        assertTrue(AchievementChecker.unlocked(ctx).any { it.id == "hour_saved" })
    }

    @Test
    fun `progreso de desbloqueo es proporcion`() {
        val all = AchievementChecker.ACHIEVEMENTS.size
        val ctx = context(pet = PetState(level = 10, streakDays = 30), stats = AutomationStats(totalCount = 500, totalXp = 9_000, timeSavedSeconds = 22_500))
        val unlocked = AchievementChecker.unlocked(ctx).size
        assertEquals(unlocked.toFloat() / all.toFloat(), AchievementChecker.unlockProgress(ctx))
        assertEquals(1f, AchievementChecker.unlockProgress(ctx), absoluteTolerance = 0.0001f)
    }
}
