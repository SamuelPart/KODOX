package com.walipet.engine.model

import java.time.Instant

/**
 * Registro de una automatización exitosa (una respuesta enviada por una regla).
 * Es la fuente de verdad del historial y de las estadísticas.
 */
data class AutomationLog(
    val id: Long = 0L,
    val ruleId: Long,
    val ruleName: String,
    val sender: String,
    val chatType: ChatType,
    /** Fragmento del mensaje entrante que disparó la regla. */
    val messageSnippet: String,
    /** Texto de la respuesta enviada automáticamente. */
    val responseText: String,
    /** XP otorgado por esta automatización. */
    val xpGained: Int,
    val occurredAt: Instant,
)
