package com.walipet.engine.voice

import com.walipet.engine.model.AutomationLog
import com.walipet.engine.model.NotificationRawData

/**
 * Decide QUÉ lee la voz en off:
 *
 *  - Mensajes de chat de la **última hora** → el texto **exacto**.
 *  - El resto (más antiguos o resúmenes agrupados) → «N mensajes de M chats».
 *  - Notificaciones que no son de chat → silencio.
 *
 * Puro y determinista: no habla, solo produce [VoiceLine].
 */
object VoiceOverPlanner {

    const val LAST_HOUR_MILLIS: Long = 60L * 60L * 1000L

    /** Tope de lecturas exactas seguidas para no recitar un muro de mensajes. */
    const val MAX_EXACT_LINES: Int = 12

    fun plan(
        classifications: List<WhatsAppNotificationClass>,
        nowEpochMilli: Long,
        lastHourMillis: Long = LAST_HOUR_MILLIS,
    ): List<VoiceLine> {
        val cutoff = nowEpochMilli - lastHourMillis
        val recentExact = mutableListOf<VoiceLine.ExactMessage>()
        val olderMessages = mutableListOf<ChatLine>()
        val overflow = mutableListOf<ChatLine>()
        var extraSummaryMessages = 0
        var extraSummaryChats = 0

        fun considerChat(line: ChatLine) {
            val text = line.text.trim()
            if (text.isEmpty()) return
            val recent = line.postedAtEpochMilli == 0L || line.postedAtEpochMilli >= cutoff
            if (recent) {
                if (recentExact.size < MAX_EXACT_LINES) {
                    recentExact += VoiceLine.ExactMessage(sender = line.sender, text = text)
                } else {
                    overflow += line
                }
            } else {
                olderMessages += line
            }
        }

        for (item in classifications) {
            when (item) {
                WhatsAppNotificationClass.Ignored -> Unit
                is WhatsAppNotificationClass.ChatMessages -> item.messages.forEach(::considerChat)
                is WhatsAppNotificationClass.Summary -> {
                    extraSummaryMessages += item.messageCount
                    extraSummaryChats += item.chatCount
                }
            }
        }

        val lines = mutableListOf<VoiceLine>()
        lines += recentExact

        val leftoverMessages = olderMessages.size + overflow.size + extraSummaryMessages
        if (leftoverMessages > 0) {
            val leftoverChats = (
                olderMessages.map { it.chatKey.ifBlank { it.sender } } +
                    overflow.map { it.chatKey.ifBlank { it.sender } }
                ).toSet().size + extraSummaryChats
            lines += VoiceLine.ChatSummary(
                messageCount = leftoverMessages,
                chatCount = leftoverChats.coerceAtLeast(1),
            )
        }

        return lines
    }

    /**
     * Una sola notificación del **chat del momento**:
     *  - si el último mensaje lo mandó la otra persona y es de la última hora →
     *    se lee ese texto, una vez, sin el historial;
     *  - si es más viejo o es un resumen agrupado → «N mensajes de M chats»;
     *  - si el último es nuestra respuesta automática → silencio.
     */
    fun planLatestIncoming(
        classified: WhatsAppNotificationClass,
        nowEpochMilli: Long,
        isOwnReply: (String) -> Boolean = { false },
        lastHourMillis: Long = LAST_HOUR_MILLIS,
    ): List<VoiceLine> {
        return when (classified) {
            WhatsAppNotificationClass.Ignored -> emptyList()
            is WhatsAppNotificationClass.Summary -> listOf(
                VoiceLine.ChatSummary(classified.messageCount, classified.chatCount),
            )
            is WhatsAppNotificationClass.ChatMessages -> {
                val incoming = classified.messages.filter { line ->
                    val text = line.text.trim()
                    text.isNotEmpty() &&
                        !isOwnReply(text) &&
                        !isSelfSender(line.sender)
                }
                if (incoming.isEmpty()) return emptyList()

                val lastRaw = classified.messages.lastOrNull { it.text.isNotBlank() }
                if (lastRaw != null &&
                    (isOwnReply(lastRaw.text.trim()) || isSelfSender(lastRaw.sender))
                ) {
                    return emptyList()
                }

                val newest = incoming.last()
                val recent = newest.postedAtEpochMilli == 0L ||
                    newest.postedAtEpochMilli >= nowEpochMilli - lastHourMillis
                if (recent) {
                    listOf(VoiceLine.ExactMessage(sender = newest.sender, text = newest.text.trim()))
                } else {
                    val chats = incoming.map { it.chatKey.ifBlank { it.sender } }.toSet().size
                    listOf(VoiceLine.ChatSummary(incoming.size, chats.coerceAtLeast(1)))
                }
            }
        }
    }

    fun isSelfSender(sender: String): Boolean {
        val n = sender.trim().lowercase()
        return n == "tú" || n == "tu" || n == "you" || n == "yo" || n == "usted" || n == "me"
    }

    fun planFromRaw(
        rawItems: List<NotificationRawData>,
        nowEpochMilli: Long,
        lastHourMillis: Long = LAST_HOUR_MILLIS,
    ): List<VoiceLine> = plan(
        classifications = rawItems.map(WhatsAppNotificationClassifier::classify),
        nowEpochMilli = nowEpochMilli,
        lastHourMillis = lastHourMillis,
    )

    /**
     * Plan a partir de logs de automatización. La voz en off de la app
     * **no** lo usa: las respuestas automáticas no se leen.
     */
    fun planSentReplies(
        logs: List<AutomationLog>,
        nowEpochMilli: Long,
        lastHourMillis: Long = LAST_HOUR_MILLIS,
    ): List<VoiceLine> {
        val classifications = logs.map { log ->
            WhatsAppNotificationClass.ChatMessages(
                listOf(
                    ChatLine(
                        sender = log.sender,
                        text = log.responseText,
                        chatKey = log.sender,
                        postedAtEpochMilli = log.occurredAt.toEpochMilli(),
                    ),
                ),
            )
        }
        return plan(classifications, nowEpochMilli, lastHourMillis)
    }

    /** Texto que debe pronunciar el TTS para una línea. */
    fun speechText(line: VoiceLine): String = when (line) {
        is VoiceLine.ExactMessage -> line.text
        is VoiceLine.ChatSummary -> formatSummary(line.messageCount, line.chatCount)
    }

    fun formatSummary(messageCount: Int, chatCount: Int): String {
        val msgWord = if (messageCount == 1) "mensaje" else "mensajes"
        val chatWord = if (chatCount == 1) "chat" else "chats"
        return "$messageCount $msgWord de $chatCount $chatWord"
    }
}

sealed class VoiceLine {
    /** Lectura literal del cuerpo del mensaje (última hora). */
    data class ExactMessage(val sender: String, val text: String) : VoiceLine()

    /** Recuento de lo que queda fuera de la última hora. */
    data class ChatSummary(val messageCount: Int, val chatCount: Int) : VoiceLine()
}
