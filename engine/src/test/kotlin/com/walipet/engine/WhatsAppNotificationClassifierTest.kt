package com.walipet.engine

import com.walipet.engine.model.NotificationRawData
import com.walipet.engine.voice.WhatsAppNotificationClass
import com.walipet.engine.voice.WhatsAppNotificationClassifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class WhatsAppNotificationClassifierTest {

    private fun raw(
        title: String? = null,
        text: String? = null,
        messages: List<Pair<CharSequence?, CharSequence?>> = emptyList(),
        messageTimestamps: List<Long> = emptyList(),
        hasReplyAction: Boolean = true,
        postedAt: Long = 1_000L,
        category: String? = null,
        bigText: String? = null,
        packageName: String = "com.whatsapp",
    ) = NotificationRawData(
        packageName = packageName,
        title = title,
        text = text,
        messages = messages,
        messageTimestamps = messageTimestamps,
        hasReplyAction = hasReplyAction,
        postedAtEpochMilli = postedAt,
        category = category,
        bigText = bigText,
    )

    @Test
    fun `chat privado se clasifica como mensaje`() {
        val result = WhatsAppNotificationClassifier.classify(
            raw(title = "María", text = "¿Me pasas el precio?"),
        )
        val chats = assertIs<WhatsAppNotificationClass.ChatMessages>(result)
        assertEquals("María", chats.messages.single().sender)
        assertEquals("¿Me pasas el precio?", chats.messages.single().text)
    }

    @Test
    fun `whatsapp web no se lee`() {
        val result = WhatsAppNotificationClassifier.classify(
            raw(title = "WhatsApp Web", text = "WhatsApp Web está activo"),
        )
        assertEquals(WhatsAppNotificationClass.Ignored, result)
    }

    @Test
    fun `aviso generico buscando mensajes no se lee`() {
        val result = WhatsAppNotificationClassifier.classify(
            raw(title = "WhatsApp", text = "Checking for new messages"),
        )
        assertEquals(WhatsAppNotificationClass.Ignored, result)
    }

    @Test
    fun `copia de seguridad no se lee`() {
        val result = WhatsAppNotificationClassifier.classify(
            raw(title = "Copia de seguridad", text = "Copia de seguridad de Google Drive finalizada"),
        )
        assertEquals(WhatsAppNotificationClass.Ignored, result)
    }

    @Test
    fun `llamada de voz no se lee`() {
        val result = WhatsAppNotificationClassifier.classify(
            raw(title = "María", text = "Llamada de voz perdida", category = "call", hasReplyAction = false),
        )
        assertEquals(WhatsAppNotificationClass.Ignored, result)
    }

    @Test
    fun `videollamada no se lee`() {
        val result = WhatsAppNotificationClassifier.classify(
            raw(title = "Carlos", text = "Videollamada en curso", hasReplyAction = false),
        )
        assertEquals(WhatsAppNotificationClass.Ignored, result)
    }

    @Test
    fun `estados de whatsapp no se leen`() {
        val result = WhatsAppNotificationClassifier.classify(
            raw(title = "Estados", text = "Ana publicó un estado", hasReplyAction = false),
        )
        assertEquals(WhatsAppNotificationClass.Ignored, result)
    }

    @Test
    fun `resumen agrupado en espanol`() {
        val result = WhatsAppNotificationClassifier.classify(
            raw(title = "WhatsApp", text = "5 mensajes de 3 chats", hasReplyAction = false),
        )
        val summary = assertIs<WhatsAppNotificationClass.Summary>(result)
        assertEquals(5, summary.messageCount)
        assertEquals(3, summary.chatCount)
    }

    @Test
    fun `resumen agrupado en ingles`() {
        val result = WhatsAppNotificationClassifier.classify(
            raw(title = "WhatsApp", text = "5 messages from 3 chats", hasReplyAction = false),
        )
        val summary = assertIs<WhatsAppNotificationClass.Summary>(result)
        assertEquals(5, summary.messageCount)
        assertEquals(3, summary.chatCount)
    }

    @Test
    fun `resumen de un solo chat`() {
        val result = WhatsAppNotificationClassifier.classify(
            raw(title = "Luis", text = "3 mensajes nuevos", hasReplyAction = false),
        )
        val summary = assertIs<WhatsAppNotificationClass.Summary>(result)
        assertEquals(3, summary.messageCount)
        assertEquals(1, summary.chatCount)
    }

    @Test
    fun `mensajes estructurados conservan el texto exacto`() {
        val result = WhatsAppNotificationClassifier.classify(
            raw(
                title = "Equipo",
                text = "2 messages",
                messages = listOf("Ana" to "hola equipo", "Luis" to "¿precio del combo?"),
                messageTimestamps = listOf(10L, 20L),
            ),
        )
        val chats = assertIs<WhatsAppNotificationClass.ChatMessages>(result)
        assertEquals(listOf("hola equipo", "¿precio del combo?"), chats.messages.map { it.text })
        assertEquals(listOf(10L, 20L), chats.messages.map { it.postedAtEpochMilli })
    }

    @Test
    fun `mensajes estructurados ganan al texto de resumen`() {
        val result = WhatsAppNotificationClassifier.classify(
            raw(
                title = "WhatsApp",
                text = "5 mensajes de 3 chats",
                messages = listOf("Ana" to "estoy en camino"),
                messageTimestamps = listOf(50L),
            ),
        )
        val chats = assertIs<WhatsAppNotificationClass.ChatMessages>(result)
        assertEquals("estoy en camino", chats.messages.single().text)
    }

    @Test
    fun `paquete que no es whatsapp se ignora`() {
        val result = WhatsAppNotificationClassifier.classify(
            raw(title = "María", text = "hola", packageName = "com.telegram"),
        )
        assertEquals(WhatsAppNotificationClass.Ignored, result)
    }

    @Test
    fun `shouldIgnore es true para avisos de sistema`() {
        assertTrue(
            WhatsAppNotificationClassifier.shouldIgnore(
                raw(title = "WhatsApp", text = "Buscando mensajes nuevos", hasReplyAction = false),
            ),
        )
    }

    @Test
    fun `mensaje de chat cuyo cuerpo menciona backup no se ignora`() {
        val result = WhatsAppNotificationClassifier.classify(
            raw(title = "Informática", text = "Haz un backup del servidor porfa"),
        )
        val chats = assertIs<WhatsAppNotificationClass.ChatMessages>(result)
        assertEquals("Haz un backup del servidor porfa", chats.messages.single().text)
    }
}
