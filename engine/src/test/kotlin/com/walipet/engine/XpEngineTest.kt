package com.walipet.engine

import com.walipet.engine.model.PetState
import com.walipet.engine.pet.XpEngine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class XpEngineTest {

    @Test
    fun `umbrales progresivos de nivel`() {
        assertEquals(100, XpEngine.xpToNextLevel(1))
        assertEquals(200, XpEngine.xpToNextLevel(2))
        assertEquals(500, XpEngine.xpToNextLevel(5))
        assertEquals(0, XpEngine.xpToNextLevel(PetState.MAX_LEVEL))
    }

    @Test
    fun `xp para subir de nivel se descuenta del acumulado`() {
        // Nivel 1→2 cuesta 100; sobran 150 para el nivel 2 (que necesita 200).
        val result = XpEngine.addXp(currentLevel = 1, currentXp = 0, amount = 250)
        assertEquals(2, result.newLevel)
        assertEquals(150, result.newXp)
        assertEquals(1, result.levelsGained)
        assertTrue(result.leveledUp)
    }

    @Test
    fun `subida multiple de niveles con mucho xp`() {
        val result = XpEngine.addXp(currentLevel = 1, currentXp = 0, amount = 700) // 100+200+300=600 → nivel 4, sobran 100
        assertEquals(4, result.newLevel)
        assertEquals(100, result.newXp)
        assertEquals(3, result.levelsGained)
    }

    @Test
    fun `sin xp suficiente no hay subida`() {
        val result = XpEngine.addXp(currentLevel = 3, currentXp = 50, amount = XpEngine.XP_PER_READING)
        assertEquals(3, result.newLevel)
        assertEquals(65, result.newXp)
        assertFalse(result.leveledUp)
    }

    @Test
    fun `el nivel maximo hace tope y vacia la barra`() {
        val result = XpEngine.addXp(currentLevel = PetState.MAX_LEVEL, currentXp = 0, amount = 100_000)
        assertEquals(PetState.MAX_LEVEL, result.newLevel)
        assertEquals(0, result.newXp)
    }

    @Test
    fun `una lectura normal da 15 xp`() {
        assertEquals(15, XpEngine.xpForReading(isFirstReadingOfDay = false))
    }

    @Test
    fun `la primera lectura del dia da bonus`() {
        assertEquals(25, XpEngine.xpForReading(isFirstReadingOfDay = true))
    }

    @Test
    fun `lifetimeXp suma la curva mas el progreso actual`() {
        assertEquals(0, XpEngine.lifetimeXp(1, 0))
        assertEquals(40, XpEngine.lifetimeXp(1, 40))
        assertEquals(140, XpEngine.lifetimeXp(2, 40))
    }

    @Test
    fun `totalXpForLevel acumula la curva`() {
        assertEquals(0, XpEngine.totalXpForLevel(1))
        assertEquals(100, XpEngine.totalXpForLevel(2))
        assertEquals(600, XpEngine.totalXpForLevel(4))
    }

    @Test
    fun `cantidad negativa lanza excepcion`() {
        runCatching { XpEngine.addXp(1, 0, -5) }.exceptionOrNull()?.let {
            assertTrue(it is IllegalArgumentException)
        }
    }
}
