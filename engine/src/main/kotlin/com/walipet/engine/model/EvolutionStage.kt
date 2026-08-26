package com.walipet.engine.model

/**
 * Etapas de evolución visual del WaliPet, según el nivel alcanzado:
 *
 *  - [EGG]    Huevo de zorro     (Niveles 1-2)
 *  - [BABY]   Zorrito            (Niveles 3-5)
 *  - [YOUNG]  Zorro joven        (Niveles 6-9)
 *  - [EPIC]   Zorro épico        (Niveles 10+)
 *
 * La UI mapea estas etapas a los sprites pixel art correspondientes.
 */
enum class EvolutionStage(
    val minLevel: Int,
    val maxLevel: Int,
    /** Clave estable usada por la UI para elegir el sprite. */
    val key: String,
) {
    EGG(1, 2, "egg"),
    BABY(3, 5, "baby"),
    YOUNG(6, 9, "young"),
    EPIC(10, PetState.MAX_LEVEL, "epic");

    val isMax: Boolean get() = this == EPIC

    companion object {
        fun fromLevel(level: Int): EvolutionStage =
            entries.firstOrNull { level in it.minLevel..it.maxLevel } ?: EGG

        fun fromKey(key: String): EvolutionStage? =
            entries.firstOrNull { it.key == key }
    }
}
