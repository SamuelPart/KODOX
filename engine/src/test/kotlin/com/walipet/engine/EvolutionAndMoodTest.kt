package com.walipet.engine

import com.walipet.engine.model.EvolutionStage
import com.walipet.engine.model.PetMood
import com.walipet.engine.model.PetState
import com.walipet.engine.pet.EvolutionResolver
import com.walipet.engine.pet.PetMoodResolver
import kotlin.test.Test
import kotlin.test.assertEquals
import java.time.LocalDate

class EvolutionAndMoodTest {

    private val today = LocalDate.of(2026, 8, 25)

    @Test
    fun `etapas segun nivel`() {
        assertEquals(EvolutionStage.EGG, EvolutionStage.fromLevel(1))
        assertEquals(EvolutionStage.EGG, EvolutionStage.fromLevel(2))
        assertEquals(EvolutionStage.BABY, EvolutionStage.fromLevel(3))
        assertEquals(EvolutionStage.BABY, EvolutionStage.fromLevel(5))
        assertEquals(EvolutionStage.YOUNG, EvolutionStage.fromLevel(6))
        assertEquals(EvolutionStage.YOUNG, EvolutionStage.fromLevel(9))
        assertEquals(EvolutionStage.EPIC, EvolutionStage.fromLevel(10))
        assertEquals(EvolutionStage.EPIC, EvolutionStage.fromLevel(PetState.MAX_LEVEL))
    }

    @Test
    fun `nivel fuera de rango cae en huevo`() {
        assertEquals(EvolutionStage.EGG, EvolutionStage.fromLevel(0))
    }

    @Test
    fun `fromKey mapea claves estables`() {
        assertEquals(EvolutionStage.BABY, EvolutionStage.fromKey("baby"))
        assertEquals(null, EvolutionStage.fromKey("nope"))
    }

    @Test
    fun `estado animico feliz con racha larga y actividad reciente`() {
        val pet = PetState(streakDays = 5, lastAutomationDate = today)
        assertEquals(PetMood.HAPPY, PetMoodResolver.mood(pet, today))
    }

    @Test
    fun `estado animico triste si se perdio un dia`() {
        val pet = PetState(streakDays = 5, lastAutomationDate = today.minusDays(2))
        assertEquals(PetMood.SAD, PetMoodResolver.mood(pet, today))
    }

    @Test
    fun `estado animico dormido sin actividad durante dias`() {
        val pet = PetState(lastAutomationDate = today.minusDays(4))
        assertEquals(PetMood.SLEEPING, PetMoodResolver.mood(pet, today))
    }

    @Test
    fun `estado animico contento con racha corta`() {
        val pet = PetState(streakDays = 1, lastAutomationDate = today)
        assertEquals(PetMood.CONTENT, PetMoodResolver.mood(pet, today))
    }
}
