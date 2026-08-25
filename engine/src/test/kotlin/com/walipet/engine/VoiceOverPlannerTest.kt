package com.walipet.engine

import com.walipet.engine.model.AutomationLog
import com.walipet.engine.model.ChatType
import com.walipet.engine.voice.ChatLine
import com.walipet.engine.voice.VoiceLine
import com.walipet.engine.voice.VoiceOverPlanner
import com.walipet.engine.voice.WhatsAppNotificationClass
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class VoiceOverPlannerTest {

    private val now = 10_000_000L
    private val hour = VoiceOverPlanner.LAST_HOUR_MILLIS

    private fun chat(
        text: String,
        sender: String = "María",
        chatKey: String = sender,
        postedAt: Long,
    ) = WhatsAppNotificationClass.ChatMessages(
        listOf(ChatLine(sender = sender, text = text, chatKey = chatKey, postedAtEpochMilli = postedAt)),
    )

    @Test
    fun `ultima hora se lee exactamente`() {
        val lines = VoiceOverPlanner.plan(
            listOf(chat("¿Me pasas el precio?", postedAt = now - 5_000)),
            nowEpochMilli = now,
        )
        val exact = assertIs<VoiceLine.ExactMessage>(lines.single())
        assertEquals("¿Me pasas el precio?", exact.text)
        assertEquals("¿Me pasas el precio?", VoiceOverPlanner.speechText(exact))
    }

    @Test
    fun `fuera de la ultima hora se resume`() {
        val lines = VoiceOverPlanner.plan(
            listOf(
                chat("hola", sender = "Ana", postedAt = now - hour - 1_000),
                chat("qué tal", sender = "Luis", postedAt = now - hour - 5_000),
            ),
            nowEpochMilli = now,
        )
        val summary = assertIs<VoiceLine.ChatSummary>(lines.single())
        assertEquals(2, summary.messageCount)
        assertEquals(2, summary.chatCount)
        assertEquals("2 mensajes de 2 chats", VoiceOverPlanner.speechText(summary))
    }

    @Test
    fun `mezcla ultima hora exacta y el resto resumido`() {
        val lines = VoiceOverPlanner.plan(
            listOf(
                chat("estoy en camino", sender = "Ana", postedAt = now - 1_000),
                chat("viejo 1", sender = "Luis", postedAt = now - hour - 1),
                chat("viejo 2", sender = "Pedro", postedAt = now - hour - 2),
            ),
            nowEpochMilli = now,
        )
        assertEquals(2, lines.size)
        val exact = assertIs<VoiceLine.ExactMessage>(lines[0])
        assertEquals("estoy en camino", VoiceOverPlanner.speechText(exact))
        val summary = assertIs<VoiceLine.ChatSummary>(lines[1])
        assertEquals("2 mensajes de 2 chats", VoiceOverPlanner.speechText(summary))
    }

    @Test
    fun `notificaciones que no son de chat no se leen`() {
        val lines = VoiceOverPlanner.plan(
            listOf(WhatsAppNotificationClass.Ignored),
            nowEpochMilli = now,
        )
        assertTrue(lines.isEmpty())
    }

    @Test
    fun `resumen agrupado se dice como recuento`() {
        val lines = VoiceOverPlanner.plan(
            listOf(WhatsAppNotificationClass.Summary(5, 3, postedAtEpochMilli = now)),
            nowEpochMilli = now,
        )
        val summary = assertIs<VoiceLine.ChatSummary>(lines.single())
        assertEquals("5 mensajes de 3 chats", VoiceOverPlanner.speechText(summary))
    }

    @Test
    fun `un mensaje de un chat usa singular`() {
        assertEquals("1 mensaje de 1 chat", VoiceOverPlanner.formatSummary(1, 1))
    }

    @Test
    fun `sin marca de tiempo se trata como reciente y se lee exacto`() {
        val lines = VoiceOverPlanner.plan(
            listOf(chat("texto literal", postedAt = 0L)),
            nowEpochMilli = now,
        )
        assertEquals("texto literal", VoiceOverPlanner.speechText(lines.single()))
    }

    @Test
    fun `mensajes enviados de la ultima hora se leen tal cual`() {
        val logs = listOf(
            AutomationLog(
                ruleId = 1,
                ruleName = "precio",
                sender = "María",
                chatType = ChatType.PRIVATE,
                messageSnippet = "precio?",
                responseText = "Hola, te enviaremos la información en un momento.",
                xpGained = 25,
                occurredAt = Instant.ofEpochMilli(now - 30_000),
            ),
            AutomationLog(
                ruleId = 1,
                ruleName = "precio",
                sender = "Luis",
                chatType = ChatType.PRIVATE,
                messageSnippet = "hola",
                responseText = "Estoy ocupado, te escribo luego.",
                xpGained = 15,
                occurredAt = Instant.ofEpochMilli(now - hour - 10_000),
            ),
        )
        val lines = VoiceOverPlanner.planSentReplies(logs, nowEpochMilli = now)
        assertEquals(2, lines.size)
        assertEquals(
            "Hola, te enviaremos la información en un momento.",
            VoiceOverPlanner.speechText(lines[0]),
        )
        assertEquals("1 mensaje de 1 chat", VoiceOverPlanner.speechText(lines[1]))
    }

    @Test
    fun `planFromRaw ignora avisos de sistema y lee el chat reciente`() {
        val now = 50_000L
        val lines = VoiceOverPlanner.planFromRaw(
            rawItems = listOf(
                com.walipet.engine.model.NotificationRawData(
                    packageName = "com.whatsapp",
                    title = "WhatsApp Web",
                    text = "WhatsApp Web está activo",
                    postedAtEpochMilli = now,
                ),
                com.walipet.engine.model.NotificationRawData(
                    packageName = "com.whatsapp",
                    title = "María",
                    text = "¿Sigue disponible?",
                    hasReplyAction = true,
                    postedAtEpochMilli = now - 1_000,
                ),
            ),
            nowEpochMilli = now,
        )
        assertEquals(listOf("¿Sigue disponible?"), lines.map { VoiceOverPlanner.speechText(it) })
    }

    @Test
    fun `mas de MAX_EXACT_LINES se resume el resto`() {
        val items = (1..15).map { i ->
            chat("msg $i", sender = "S$i", postedAt = now - 1_000)
        }
        val lines = VoiceOverPlanner.plan(items, nowEpochMilli = now)
        val exact = lines.filterIsInstance<VoiceLine.ExactMessage>()
        val summaries = lines.filterIsInstance<VoiceLine.ChatSummary>()
        assertEquals(VoiceOverPlanner.MAX_EXACT_LINES, exact.size)
        assertEquals(1, summaries.size)
        assertEquals(15 - VoiceOverPlanner.MAX_EXACT_LINES, summaries.single().messageCount)
    }
}
