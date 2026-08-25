package com.walipet.engine.model

/**
 * Datos crudos de una notificación (independientes de Android), listos para
 * ser parseados por [com.walipet.engine.rule.WhatsAppMessageParser].
 *
 * La capa Android construye esta estructura a partir de los extras del
 * `StatusBarNotification`; el parser puro no conoce el framework.
 */
data class NotificationRawData(
    val packageName: String,
    val title: CharSequence?,
    val text: CharSequence?,
    /** Mensajes estructurados (MessagingStyle/Wearable): pares (remitente, texto). */
    val messages: List<Pair<CharSequence?, CharSequence?>> = emptyList(),
    val hasReplyAction: Boolean = false,
)
