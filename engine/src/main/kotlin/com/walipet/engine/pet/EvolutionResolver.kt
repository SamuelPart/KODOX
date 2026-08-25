package com.walipet.engine.pet

import com.walipet.engine.model.EvolutionStage
import com.walipet.engine.model.PetState

/**
 * Resuelve la etapa de evolución visual según el nivel de la mascota.
 * Cada etapa está asociada a un sprite pixel art distinto en la UI.
 */
object EvolutionResolver {

    fun stageFor(pet: PetState): EvolutionStage =
        EvolutionStage.fromLevel(pet.level)

    /** Fracción 0..1 de progreso dentro de la etapa actual. */
    fun progressWithinStage(pet: PetState): Float {
        val stage = stageFor(pet)
        if (stage == EvolutionStage.EGG) return 0f
        val span = (stage.maxLevel - stage.minLevel + 1).toFloat()
        val current = (pet.level - stage.minLevel + 1).toFloat()
        return (current / span).coerceIn(0f, 1f)
    }
}
