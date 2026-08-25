package com.walipet.engine

import com.walipet.engine.model.ChatType
import com.walipet.engine.model.NotificationRawData
import com.walipet.engine.rule.WhatsAppMessageParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WhatsAppMessageParserTest {

    private fun raw(
        title: String? = null,
        text: String? = null,
        messages: List<Pair<CharSequence?, CharSequence?>> = emptyList(),
        hasReplyAction: Boolean = true,
    ) = NotificationRawData(
        packageName = "com.whatsapp",
        title = title,
        text = text,
        messages = messages,
        hasReplyAction = hasReplyAction,
    )

    @Test
    fun `chat privado usa titulo como remitente`() {
        val parsed = WhatsAppMessageParser.parse(raw(title = "María", text = "¿Me pasas el precio?"))
        assertEquals("María", parsed?.sender)
        assertEquals("¿Me pasas el precio?", parsed?.text)
        assertEquals(ChatType.PRIVATE, parsed?.chatType)
    }

    @Test
    fun `titulo WhatsApp generico no se usa como remitente`() {
        val parsed = WhatsAppMessageParser.parse(raw(title = "WhatsApp", text = "Juan: hola!"))
        assertEquals("Juan", parsed?.sender)
        assertEquals("hola!", parsed?.text)
        assertEquals(ChatType.GROUP, parsed?.chatType)
    }

    @Test
    fun `mensaje de grupo con emoji de grupo`() {
        val parsed = WhatsAppMessageParser.parse(raw(title = "Equipo Dev 👥", text = "Samuel: ¿listo para el deploy?"))
        assertEquals("Samuel", parsed?.sender)
        assertEquals("¿listo para el deploy?", parsed?.text)
        assertEquals(ChatType.GROUP, parsed?.chatType)
        assertEquals("Equipo Dev", parsed?.groupName)
    }

    @Test
    fun `mensaje de grupo con prefijo remitente en el texto`() {
        val parsed = WhatsAppMessageParser.parse(raw(title = "Ventas", text = "Ana: ¿cuál es el precio?"))
        assertEquals("Ana", parsed?.sender)
        assertEquals("¿cuál es el precio?", parsed?.text)
        assertEquals(ChatType.GROUP, parsed?.chatType)
        assertEquals("Ventas", parsed?.groupName)
    }

    @Test
    fun `mensajes estructurados del wearable se usan primero`() {
        val parsed = WhatsAppMessageParser.parse(
            raw(
                title = "Carlos",
                text = "texto plano",
                messages = listOf("Carlos" to "mensaje estructurado"),
            ),
        )
        assertEquals("Carlos", parsed?.sender)
        assertEquals("mensaje estructurado", parsed?.text)
        assertEquals(ChatType.PRIVATE, parsed?.chatType)
    }

    @Test
    fun `varios mensajes estructurados implican grupo`() {
        val parsed = WhatsAppMessageParser.parse(
            raw(
                title = "Grupo 👥",
                messages = listOf("A" to "uno", "B" to "dos"),
            ),
        )
        assertEquals(ChatType.GROUP, parsed?.chatType)
        assertEquals("A", parsed?.sender)
        assertEquals("uno", parsed?.text)
    }

    @Test
    fun `sin titulo ni texto devuelve null`() {
        assertNull(WhatsAppMessageParser.parse(raw(title = null, text = null)))
    }

    @Test
    fun `hasReplyAction se propaga`() {
        val parsed = WhatsAppMessageParser.parse(raw(title = "María", text = "hola", hasReplyAction = false))
        assertEquals(false, parsed?.hasReplyAction)
    }

    @Test
    fun `chat privado cuyo texto contiene dos puntos no se confunde con grupo`() {
        val parsed = WhatsAppMessageParser.parse(raw(title = "Pedro", text = "Te espero a las 3: 30 en la puerta"))
        assertEquals("Pedro", parsed?.sender)
        assertEquals(ChatType.PRIVATE, parsed?.chatType)
    }
}
