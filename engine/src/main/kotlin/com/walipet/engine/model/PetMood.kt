package com.walipet.engine.model

/**
 * Estado de ánimo de la mascota, derivado de la actividad reciente.
 * Controla la animación y los efectos visuales en el Dashboard.
 */
enum class PetMood {
    /** Actividad reciente y racha ≥ 3 días: corazones y brillos. */
    HAPPY,

    /** Actividad reciente pero racha corta. */
    CONTENT,

    /** Se perdió al menos un día completo: gota de sudor. */
    SAD,

    /** Sin actividad en 3+ días: la mascota se duerme. */
    SLEEPING,
}
