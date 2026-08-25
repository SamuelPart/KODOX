package com.walipet.engine.rule

import com.walipet.engine.model.ChatType
import com.walipet.engine.model.NotificationRawData
import com.walipet.engine.model.ParsedWhatsAppMessage

/**
 * Parser de notificaciones de WhatsApp → [ParsedWhatsAppMessage].
 *
 * Puro (sin Android): recibe [NotificationRawData] y devuelve el mensaje
 * normalizado. La capa Android solo convierte los extras de la notificación
 * en esa estructura.
 *
 * Heurísticas aplicadas (los empaquetados de WhatsApp cambian con el tiempo;
 * ver README → "Retos técnicos"):
 *
 *  1. Si hay mensajes estructurados (MessagingStyle/Wearable), se usa el
 *     primero de ellos; más de un mensaje ⇒ grupo.
 *  2. Si el título contiene el emoji de grupo 👥 ⇒ grupo.
 *  3. Si el texto tiene forma "Remitente: mensaje" y el remitente no coincide
 *     con el título (chat privado) ⇒ grupo.
 *  4. En caso contrario ⇒ chat privado, remitente = título.
 */
object WhatsAppMessageParser {

    private const val GROUP_EMOJI = "\uD83D\uDC65" // 👥

    /** Longitud máxima razonable del prefijo "Nombre: " en mensajes de grupo. */
    private const val MAX_SENDER_PREFIX_LENGTH = 24

    /** Máximo de palabras del prefijo "Nombre: " (nombres compuestos incluidos). */
    private const val MAX_SENDER_WORDS = 2

    fun parse(raw: NotificationRawData): ParsedWhatsAppMessage? {
        val title = raw.title?.toString()?.trim().orEmpty()
        val text = raw.text?.toString()?.trim().orEmpty()

        if (text.isEmpty() && raw.messages.isEmpty()) return null

        // 1) Mensajes estructurados: la fuente más fiable.
        if (raw.messages.isNotEmpty()) {
            val first = raw.messages.first()
            val sender = first.first?.toString()?.trim()
                ?.ifEmpty { title.ifEmpty { "WhatsApp" } }
                ?: title.ifEmpty { "WhatsApp" }
            val body = first.second?.toString()?.trim().orEmpty()
            val isGroup = raw.messages.size > 1 || title.contains(GROUP_EMOJI)
            return ParsedWhatsAppMessage(
                sender = sender,
                text = body,
                chatType = if (isGroup) ChatType.GROUP else ChatType.PRIVATE,
                groupName = title.takeIf { isGroup },
                hasReplyAction = raw.hasReplyAction,
            )
        }

        // 2) Título de grupo (WhatsApp añade 👥 a las notificaciones de grupo).
        if (title.contains(GROUP_EMOJI)) {
            return ParsedWhatsAppMessage(
                sender = parseSenderFromText(text, title),
                text = parseBodyFromText(text, title),
                chatType = ChatType.GROUP,
                groupName = title.removeSuffix(GROUP_EMOJI).trim().ifEmpty { null },
                hasReplyAction = raw.hasReplyAction,
            )
        }

        // 3) Texto con prefijo "Remitente: " ⇒ grupo, solo cuando el prefijo
        //    tiene pinta de nombre (corto, ≤2 palabras, distinto del título).
        //    Así, un chat privado cuyo mensaje contiene "12: 30" no se confunde.
        val colon = text.indexOf(": ")
        if (colon > 0) {
            val prefix = text.substring(0, colon).trim()
            val body = text.substring(colon + 2).trim()
            val looksLikeSender =
                prefix.isNotBlank() &&
                    body.isNotBlank() &&
                    prefix != title &&
                    prefix.length <= MAX_SENDER_PREFIX_LENGTH &&
                    prefix.split(" ").size <= MAX_SENDER_WORDS
            if (looksLikeSender) {
                return ParsedWhatsAppMessage(
                    sender = prefix,
                    text = body,
                    chatType = ChatType.GROUP,
                    groupName = title.ifEmpty { null },
                    hasReplyAction = raw.hasReplyAction,
                )
            }
        }

        // 4) Chat privado: el título es el contacto; a veces viene "WhatsApp".
        val cleanTitle = title.removePrefix("WhatsApp").trim()
        return ParsedWhatsAppMessage(
            sender = cleanTitle.ifEmpty { "WhatsApp" },
            text = text,
            chatType = ChatType.PRIVATE,
            hasReplyAction = raw.hasReplyAction,
        )
    }

    private fun parseSenderFromText(text: String, title: String): String {
        val colon = text.indexOf(": ")
        return if (colon > 0) text.substring(0, colon).trim() else title.ifEmpty { "WhatsApp" }
    }

    private fun parseBodyFromText(text: String, title: String): String {
        val colon = text.indexOf(": ")
        return if (colon > 0) text.substring(colon + 2).trim() else text
    }
}
