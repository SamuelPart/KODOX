package com.walipet.engine

import com.walipet.engine.pet.StreakEngine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import java.time.LocalDate

class StreakEngineTest {

    private val today = LocalDate.of(2026, 8, 25)

    @Test
    fun `primera lectura inicia racha en 1`() {
        val result = StreakEngine.applyReading(currentStreak = 0, lastAutomationDate = null, today = today)
        assertEquals(1, result.streakDays)
        assertTrue(result.isFirstReadingOfDay)
        assertFalse(result.streakBroken)
    }

    @Test
    fun `leer el mismo dia no aumenta la racha`() {
        val result = StreakEngine.applyReading(currentStreak = 5, lastAutomationDate = today, today = today)
        assertEquals(5, result.streakDays)
        assertFalse(result.isFirstReadingOfDay)
        assertFalse(result.streakBroken)
    }

    @Test
    fun `leer al dia siguiente aumenta la racha`() {
        val result = StreakEngine.applyReading(currentStreak = 5, lastAutomationDate = today.minusDays(1), today = today)
        assertEquals(6, result.streakDays)
        assertTrue(result.isFirstReadingOfDay)
        assertFalse(result.streakBroken)
    }

    @Test
    fun `un hueco de mas de un dia rompe la racha`() {
        val result = StreakEngine.applyReading(currentStreak = 12, lastAutomationDate = today.minusDays(3), today = today)
        assertEquals(1, result.streakDays)
        assertTrue(result.isFirstReadingOfDay)
        assertTrue(result.streakBroken)
    }

    @Test
    fun `missedDays cuenta los dias completos sin actividad`() {
        assertEquals(0, StreakEngine.missedDays(today, today))
        assertEquals(1, StreakEngine.missedDays(today.minusDays(1), today))
        assertEquals(2, StreakEngine.missedDays(today.minusDays(2), today))
        assertEquals(0, StreakEngine.missedDays(null, today))
    }

    @Test
    fun `la racha esta en peligro cuando se pierde un dia completo`() {
        assertFalse(StreakEngine.isStreakInDanger(today, today))
        assertFalse(StreakEngine.isStreakInDanger(today.minusDays(1), today))
        assertTrue(StreakEngine.isStreakInDanger(today.minusDays(2), today))
        assertFalse(StreakEngine.isStreakInDanger(null, today))
    }

    @Test
    fun `la energia decae con los dias perdidos`() {
        assertEquals(100, StreakEngine.energyFor(0))
        assertEquals(90, StreakEngine.energyFor(1))
        assertEquals(50, StreakEngine.energyFor(5))
        assertEquals(0, StreakEngine.energyFor(99))
    }

    @Test
    fun `la felicidad sube con la racha y baja con los dias perdidos`() {
        assertEquals(100, StreakEngine.happinessFor(streakDays = 0, missedDays = 0))
        assertEquals(120.coerceIn(0, 100), StreakEngine.happinessFor(streakDays = 10, missedDays = 0))
        assertEquals(100, StreakEngine.happinessFor(streakDays = 10, missedDays = 0)) // tope en 100
        assertEquals(85, StreakEngine.happinessFor(streakDays = 0, missedDays = 1))
        assertEquals(0, StreakEngine.happinessFor(streakDays = 0, missedDays = 99))
    }
}
