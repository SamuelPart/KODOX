package com.walipet.engine

import com.walipet.engine.model.ChatScope
import com.walipet.engine.model.ChatType
import com.walipet.engine.model.MatchMode
import com.walipet.engine.model.ParsedWhatsAppMessage
import com.walipet.engine.model.Rule
import com.walipet.engine.rule.RuleEngine
import com.walipet.engine.rule.RuleMatch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class RuleEngineTest {

    private fun message(
        text: String,
        sender: String = "Juan",
        chatType: ChatType = ChatType.PRIVATE,
    ) = ParsedWhatsAppMessage(
        sender = sender,
        text = text,
        chatType = chatType,
        hasReplyAction = true,
    )

    private fun rule(
        trigger: String,
        response: String = "Respuesta",
        mode: MatchMode = MatchMode.CONTAINS,
        scope: ChatScope = ChatScope.ALL,
        active: Boolean = true,
        id: Long = 1L,
    ) = Rule(
        id = id,
        name = "Regla $id",
        trigger = trigger,
        response = response,
        matchMode = mode,
        chatScope = scope,
        isActive = active,
    )

    @Test
    fun `regla contains responde correctamente`() {
        val match = RuleEngine.evaluate(message("¿Cuál es el precio?"), listOf(rule("precio", "Te lo paso en un momento.")))
        val hit = assertIs<RuleMatch.Hit>(match)
        assertEquals("Te lo paso en un momento.", hit.rule.response)
    }

    @Test
    fun `primera regla que coincide gana`() {
        val rules = listOf(
            rule("hola", "Saludo", id = 1),
            rule("hola", "Otro saludo", id = 2),
        )
        val hit = assertIs<RuleMatch.Hit>(RuleEngine.evaluate(message("Hola!"), rules))
        assertEquals(1L, hit.rule.id)
        assertEquals("Saludo", hit.rule.response)
    }

    @Test
    fun `reglas inactivas se ignoran`() {
        val rules = listOf(rule("hola", active = false))
        assertIs<RuleMatch.NoMatch>(RuleEngine.evaluate(message("Hola!"), rules))
    }

    @Test
    fun `el ambito privado solo responde a chats privados`() {
        val rules = listOf(rule("hola", scope = ChatScope.PRIVATE_ONLY))
        assertIs<RuleMatch.Hit>(RuleEngine.evaluate(message("Hola!", chatType = ChatType.PRIVATE), rules))
        assertIs<RuleMatch.NoMatch>(RuleEngine.evaluate(message("Hola!", chatType = ChatType.GROUP), rules))
    }

    @Test
    fun `el ambito de grupos solo responde a grupos`() {
        val rules = listOf(rule("hola", scope = ChatScope.GROUPS_ONLY))
        assertIs<RuleMatch.Hit>(RuleEngine.evaluate(message("Hola!", chatType = ChatType.GROUP), rules))
        assertIs<RuleMatch.NoMatch>(RuleEngine.evaluate(message("Hola!", chatType = ChatType.PRIVATE), rules))
    }

    @Test
    fun `sin coincidencia devuelve NoMatch`() {
        val match = RuleEngine.evaluate(message("Buenas tardes"), listOf(rule("precio")))
        assertIs<RuleMatch.NoMatch>(match)
    }

    @Test
    fun `mensaje vacio no dispara reglas`() {
        assertIs<RuleMatch.NoMatch>(RuleEngine.evaluate(message("   "), listOf(rule("hola"))))
    }

    @Test
    fun `regex invalida devuelve InvalidRegex`() {
        val match = RuleEngine.evaluate(
            message("cualquier cosa"),
            listOf(rule("([unclosed", mode = MatchMode.REGEX)),
        )
        assertIs<RuleMatch.InvalidRegex>(match)
    }

    @Test
    fun `regex valida funciona end-to-end`() {
        val rules = listOf(rule("""\b(hola|buenas)\b""", "¡Hola!", mode = MatchMode.REGEX))
        val hit = assertIs<RuleMatch.Hit>(RuleEngine.evaluate(message("Buenas, ¿estás?"), rules))
        assertEquals("¡Hola!", hit.rule.response)
        assertIs<RuleMatch.NoMatch>(RuleEngine.evaluate(message("Adiós"), rules))
    }

    @Test
    fun `las reglas se evaluan en orden de la lista`() {
        val rules = listOf(
            rule("precio", "Respuesta precio", id = 1),
            rule("catálogo", "Respuesta catálogo", id = 2),
        )
        val hit = assertIs<RuleMatch.Hit>(RuleEngine.evaluate(message("manda el catálogo"), rules))
        assertEquals(2L, hit.rule.id)
        assertTrue(hit.matchedBy == MatchMode.CONTAINS)
    }
}
