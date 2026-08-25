package com.walipet.engine.model

/**
 * Datos crudos de una notificación (independientes de Android), listos para
 * ser parseados por [com.walipet.engine.rule.WhatsAppMessageParser] y
 * clasificados por el planificador de voz en off.
 *
 * La capa Android construye esta estructura a partir de los extras del
 * `StatusBarNotification`; el parser puro no conoce el framework.
 */
data class NotificationRawData(
    val packageName: String,
    val title: CharSequence? = null,
    val text: CharSequence? = null,
    /** Mensajes estructurados (MessagingStyle/Wearable): pares (remitente, texto). */
    val messages: List<Pair<CharSequence?, CharSequence?>> = emptyList(),
    /**
     * Marca de tiempo de cada mensaje estructurado (epoch millis), alineada
     * por índice con [messages]. `0` si el origen no la expone.
     */
    val messageTimestamps: List<Long> = emptyList(),
    val hasReplyAction: Boolean = false,
    /** `Notification.when` / `postTime` de la notificación. */
    val postedAtEpochMilli: Long = 0L,
    /** Categoría Android (`msg`, `call`, …) o null si no viene. */
    val category: String? = null,
    val bigText: CharSequence? = null,
    val subText: CharSequence? = null,
)
