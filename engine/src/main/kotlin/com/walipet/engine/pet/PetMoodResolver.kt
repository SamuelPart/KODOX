package com.walipet.engine.pet

import com.walipet.engine.model.PetMood
import com.walipet.engine.model.PetState
import java.time.LocalDate

/**
 * Resuelve el estado de ánimo de la mascota a partir de su estado y la fecha
 * actual. La UI lo usa para elegir animaciones y efectos.
 */
object PetMoodResolver {

    fun mood(pet: PetState, today: LocalDate): PetMood {
        val missed = StreakEngine.missedDays(pet.lastAutomationDate, today)
        return when {
            missed >= 3 -> PetMood.SLEEPING
            missed >= 1 -> PetMood.SAD
            pet.streakDays >= 3 -> PetMood.HAPPY
            else -> PetMood.CONTENT
        }
    }
}
