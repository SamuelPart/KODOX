package com.walipet.engine.voice

import com.walipet.engine.model.NotificationRawData
import com.walipet.engine.rule.WhatsAppMessageParser

/**
 * Clasifica una notificación de WhatsApp para la voz en off y el auto-reply:
 *
 *  - [WhatsAppNotificationClass.Ignored] — llamadas, WhatsApp Web, copias de
 *    seguridad, estados, avisos del sistema… **no se leen ni se responden**.
 *  - [WhatsAppNotificationClass.Summary] — resumen agrupado tipo
 *    «5 mensajes de 3 chats».
 *  - [WhatsAppNotificationClass.ChatMessages] — mensajes de chat reales,
 *    con el texto tal cual llegó.
 */
object WhatsAppNotificationClassifier {

    private val WHATSAPP_PACKAGES = setOf("com.whatsapp", "com.whatsapp.w4b")

    private val SUMMARY_REGEXES = listOf(
        Regex("""(\d+)\s+mensajes?\s+(?:nuevos?\s+)?de\s+(\d+)\s+(?:chats?|conversaciones?)""", RegexOption.IGNORE_CASE),
        Regex("""(\d+)\s+nuevos?\s+mensajes?\s+de\s+(\d+)\s+(?:chats?|conversaciones?)""", RegexOption.IGNORE_CASE),
        Regex("""(\d+)\s+messages?\s+from\s+(\d+)\s+chats?""", RegexOption.IGNORE_CASE),
        Regex("""(\d+)\s+new\s+messages?\s+from\s+(\d+)\s+chats?""", RegexOption.IGNORE_CASE),
        Regex("""(\d+)\s+mensagens?\s+de\s+(\d+)\s+conversas?""", RegexOption.IGNORE_CASE),
    )

    /** Resumen de un solo chat: «3 mensajes nuevos» / «3 new messages». */
    private val SINGLE_CHAT_SUMMARY_REGEXES = listOf(
        Regex("""^(\d+)\s+(?:nuevos?\s+)?mensajes?(?:\s+nuevos?)?$""", RegexOption.IGNORE_CASE),
        Regex("""^(\d+)\s+new\s+messages?$""", RegexOption.IGNORE_CASE),
        Regex("""^(\d+)\s+mensajes?\s+nuevos?$""", RegexOption.IGNORE_CASE),
    )

    /**
     * Títulos que WhatsApp usa para avisos que NO son un chat. Se comparan
     * en minúsculas, por igualdad o prefijo.
     */
    private val SYSTEM_TITLES = listOf(
        "whatsapp web",
        "whatsapp business",
        "dispositivos vinculados",
        "linked devices",
        "copia de seguridad",
        "backup",
        "estados",
        "status",
        "llamadas",
        "calls",
        "pagos",
        "payments",
        "comunidades",
        "communities",
        "canales",
        "channels",
    )

    /**
     * Fragmentos de cuerpo que, junto a un título genérico («WhatsApp»),
     * delatan un aviso de sistema y no un mensaje de chat.
     */
    private val SYSTEM_BODIES = listOf(
        "whatsapp web",
        "checking for new messages",
        "buscando mensajes",
        "checking for messages",
        "copia de seguridad",
        "backup",
        "google storage",
        "google drive",
        "restoring",
        "restaurando",
        "deleting messages",
        "eliminando mensajes",
        "you may have new notifications",
        "tienes notificaciones nuevas",
        "download",
        "descargando",
        "logged in",
        "sesión iniciada",
        "end-to-end encrypted",
        "cifrado de extremo a extremo",
        "tap to",
        "toca para",
        "update whatsapp",
        "actualiza whatsapp",
        "new version",
        "versión nueva",
    )

    private val CALL_MARKERS = listOf(
        "llamada de voz",
        "llamada de vídeo",
        "llamada de video",
        "videollamada",
        "voice call",
        "video call",
        "missed call",
        "llamada perdida",
        "incoming call",
        "llamada entrante",
        "ongoing voice",
        "ongoing video",
        "llamada en curso",
        "ringing",
        "sonando",
        "calling",
        "llamando",
        "call ended",
        "llamada finalizada",
    )

    fun classify(raw: NotificationRawData): WhatsAppNotificationClass {
        if (raw.packageName !in WHATSAPP_PACKAGES) return WhatsAppNotificationClass.Ignored

        val title = raw.title?.toString()?.trim().orEmpty()
        val text = firstNonBlank(raw.text, raw.bigText, raw.subText)

        if (isCall(raw.category, title, text)) return WhatsAppNotificationClass.Ignored
        if (isSystemNotification(title, text)) return WhatsAppNotificationClass.Ignored

        // Si hay mensajes estructurados, se leen tal cual (prioridad sobre el resumen).
        val structured = structuredChatLines(raw, title)
        if (structured.isNotEmpty()) {
            return WhatsAppNotificationClass.ChatMessages(structured)
        }

        parseSummary(title, text, raw.postedAtEpochMilli)?.let { return it }

        val parsed = WhatsAppMessageParser.parse(raw) ?: return WhatsAppNotificationClass.Ignored
        if (parsed.text.isBlank()) return WhatsAppNotificationClass.Ignored
        if (isSystemNotification(parsed.sender, parsed.text)) return WhatsAppNotificationClass.Ignored

        return WhatsAppNotificationClass.ChatMessages(
            listOf(
                ChatLine(
                    sender = parsed.sender,
                    text = parsed.text,
                    chatKey = parsed.groupName?.ifBlank { null } ?: parsed.sender,
                    postedAtEpochMilli = raw.postedAtEpochMilli,
                ),
            ),
        )
    }

    /** `true` si la notificación se debe silenciar (no es un mensaje de chat). */
    fun shouldIgnore(raw: NotificationRawData): Boolean =
        classify(raw) is WhatsAppNotificationClass.Ignored

    private fun structuredChatLines(raw: NotificationRawData, title: String): List<ChatLine> {
        if (raw.messages.isEmpty()) return emptyList()
        val chatKey = title.ifBlank { "WhatsApp" }
        return raw.messages.mapIndexedNotNull { index, (sender, body) ->
            val bodyText = body?.toString()?.trim().orEmpty()
            if (bodyText.isEmpty()) return@mapIndexedNotNull null
            val senderName = sender?.toString()?.trim().orEmpty()
                .ifEmpty { title.ifEmpty { "WhatsApp" } }
            val timestamp = raw.messageTimestamps.getOrNull(index)
                ?.takeIf { it > 0L }
                ?: raw.postedAtEpochMilli
            ChatLine(
                sender = senderName,
                text = bodyText,
                chatKey = chatKey.ifBlank { senderName },
                postedAtEpochMilli = timestamp,
            )
        }
    }

    private fun parseSummary(title: String, text: String, postedAt: Long): WhatsAppNotificationClass.Summary? {
        val haystack = listOf(text, title).filter { it.isNotBlank() }
        for (candidate in haystack) {
            for (regex in SUMMARY_REGEXES) {
                val match = regex.find(candidate) ?: continue
                val messages = match.groupValues[1].toIntOrNull() ?: continue
                val chats = match.groupValues[2].toIntOrNull() ?: continue
                if (messages <= 0 || chats <= 0) continue
                return WhatsAppNotificationClass.Summary(
                    messageCount = messages,
                    chatCount = chats,
                    postedAtEpochMilli = postedAt,
                )
            }
            for (regex in SINGLE_CHAT_SUMMARY_REGEXES) {
                val match = regex.find(candidate.trim()) ?: continue
                val messages = match.groupValues[1].toIntOrNull() ?: continue
                if (messages <= 0) continue
                return WhatsAppNotificationClass.Summary(
                    messageCount = messages,
                    chatCount = 1,
                    postedAtEpochMilli = postedAt,
                )
            }
        }
        return null
    }

    private fun isCall(category: String?, title: String, text: String): Boolean {
        if (category.equals("call", ignoreCase = true)) return true
        val blob = "$title $text".lowercase()
        return CALL_MARKERS.any { blob.contains(it) }
    }

    private fun isSystemNotification(title: String, text: String): Boolean {
        val t = title.lowercase().trim()
        if (t.isNotEmpty()) {
            for (marker in SYSTEM_TITLES) {
                if (t == marker || t.startsWith("$marker ") || t.startsWith("$marker:")) return true
            }
        }
        val genericTitle = t.isEmpty() || t == "whatsapp" || t == "whatsapp business"
        if (!genericTitle) return false
        val b = text.lowercase()
        if (b.isEmpty()) return true
        return SYSTEM_BODIES.any { b.contains(it) }
    }

    private fun firstNonBlank(vararg values: CharSequence?): String {
        for (value in values) {
            val s = value?.toString()?.trim().orEmpty()
            if (s.isNotEmpty()) return s
        }
        return ""
    }
}

/** Un mensaje de chat ya extraído, listo para decidir si se lee o se resume. */
data class ChatLine(
    val sender: String,
    val text: String,
    val chatKey: String,
    val postedAtEpochMilli: Long,
)

sealed class WhatsAppNotificationClass {
    /** Llamadas, WhatsApp Web, backups, estados… no se leen. */
    data object Ignored : WhatsAppNotificationClass()

    /** Mensajes de chat reales (uno o varios, p. ej. MessagingStyle). */
    data class ChatMessages(val messages: List<ChatLine>) : WhatsAppNotificationClass()

    /** Resumen agrupado («N mensajes de M chats»). */
    data class Summary(
        val messageCount: Int,
        val chatCount: Int,
        val postedAtEpochMilli: Long,
    ) : WhatsAppNotificationClass()
}
