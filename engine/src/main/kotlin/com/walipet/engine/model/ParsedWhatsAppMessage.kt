package com.walipet.engine.model

import java.time.Instant

/**
 * Mensaje de WhatsApp ya extraído y normalizado a partir de la notificación.
 * El parser (puro) lo construye; el resto del motor solo trabaja con esto.
 */
data class ParsedWhatsAppMessage(
    /** Nombre del remitente (contacto o miembro del grupo). */
    val sender: String,
    /** Cuerpo del mensaje, sin el prefijo "Remitente: ". */
    val text: String,
    val chatType: ChatType,
    /** Nombre del grupo si [chatType] es GROUP. */
    val groupName: String? = null,
    /** `true` si la notificación exponía una acción de respuesta (RemoteInput). */
    val hasReplyAction: Boolean = false,
)
