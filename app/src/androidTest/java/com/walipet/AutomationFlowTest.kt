package com.walipet

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.walipet.engine.model.ChatType
import com.walipet.engine.model.MatchMode
import com.walipet.engine.model.ParsedWhatsAppMessage
import com.walipet.engine.model.Rule
import com.walipet.engine.rule.RuleEngine
import com.walipet.engine.rule.RuleMatch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertIs
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba del flujo completo del motor en dispositivo: regla → evaluación →
 * coincidencia. El envío real por RemoteInput requiere una notificación de
 * WhatsApp de verdad (no simulable en instrumentación).
 */
@RunWith(AndroidJUnit4::class)
class AutomationFlowTest {

    @Test
    fun mensajeConPrecioDisparaLaReglaDePrecio() {
        val rules = listOf(
            Rule(
                id = 1,
                name = "Precio",
                trigger = "precio",
                response = "Hola, te enviamos el precio en un momento.",
                matchMode = MatchMode.CONTAINS,
            ),
        )
        val message = ParsedWhatsAppMessage(
            sender = "Cliente",
            text = "Hola, ¿cuál es el precio del combo?",
            chatType = ChatType.PRIVATE,
            hasReplyAction = true,
        )
        val match = RuleEngine.evaluate(message, rules)
        val hit = assertIs<RuleMatch.Hit>(match)
        assertEquals("Hola, te enviamos el precio en un momento.", hit.rule.response)
    }
}
