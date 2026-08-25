package com.walipet.engine.model

import java.time.Instant

/**
 * Regla lógica de automatización, tal y como la configura el usuario:
 * "Si el mensaje contiene X → responde Y".
 *
 * Es un modelo puro (sin dependencias de Android) para poder persistirse,
 * evaluarse y probarse de forma aislada.
 */
data class Rule(
    val id: Long = 0L,
    val name: String,
    /** Texto disparador (palabra clave, frase o patrón regex). */
    val trigger: String,
    /** Respuesta automática que se enviará por RemoteInput. */
    val response: String,
    val matchMode: MatchMode = MatchMode.CONTAINS,
    val chatScope: ChatScope = ChatScope.ALL,
    val caseSensitive: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Instant = Instant.EPOCH,
    val timesTriggered: Int = 0,
    val lastTriggeredAt: Instant? = null,
) {
    /** Devuelve una copia de la regla con una ejecución exitosa registrada. */
    fun withTriggeredAt(now: Instant): Rule =
        copy(timesTriggered = timesTriggered + 1, lastTriggeredAt = now)

    companion object {
        /** Regla vacía usada como plantilla para el editor. */
        fun blank() = Rule(
            id = 0L,
            name = "",
            trigger = "",
            response = "",
        )
    }
}
