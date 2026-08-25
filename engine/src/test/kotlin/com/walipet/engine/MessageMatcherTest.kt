package com.walipet.engine

import com.walipet.engine.model.MatchMode
import com.walipet.engine.rule.MessageMatcher
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MessageMatcherTest {

    // --- EXACT ---

    @Test
    fun `exact match ignora espacios alrededor y mayusculas por defecto`() {
        assertTrue(MessageMatcher.matches("  Hola  ", "hola", MatchMode.EXACT, caseSensitive = false))
        assertTrue(MessageMatcher.matches("PRECIO", "precio", MatchMode.EXACT, caseSensitive = false))
    }

    @Test
    fun `exact match respeta mayusculas si es case sensitive`() {
        assertFalse(MessageMatcher.matches("Precio", "precio", MatchMode.EXACT, caseSensitive = true))
        assertTrue(MessageMatcher.matches("Precio", "Precio", MatchMode.EXACT, caseSensitive = true))
    }

    @Test
    fun `exact match falla si hay texto extra`() {
        assertFalse(MessageMatcher.matches("hola mundo", "hola", MatchMode.EXACT, caseSensitive = false))
    }

    // --- CONTAINS ---

    @Test
    fun `contains encuentra la palabra en medio del mensaje`() {
        assertTrue(MessageMatcher.matches("Me pasas el precio del combo?", "precio", MatchMode.CONTAINS, caseSensitive = false))
        assertTrue(MessageMatcher.matches("CATÁLOGO", "catálogo", MatchMode.CONTAINS, caseSensitive = false))
    }

    @Test
    fun `contains no matchea si la palabra no aparece`() {
        assertFalse(MessageMatcher.matches("Buenos días", "precio", MatchMode.CONTAINS, caseSensitive = false))
    }

    @Test
    fun `contains es sensible a mayusculas si se pide`() {
        assertFalse(MessageMatcher.matches("Hola PRECIO", "precio", MatchMode.CONTAINS, caseSensitive = true))
        assertTrue(MessageMatcher.matches("Hola precio", "precio", MatchMode.CONTAINS, caseSensitive = true))
    }

    // --- STARTS_WITH ---

    @Test
    fun `starts_with matchea por prefijo ignorando espacios iniciales`() {
        assertTrue(MessageMatcher.matches("  Hola, qué tal?", "hola", MatchMode.STARTS_WITH, caseSensitive = false))
        assertFalse(MessageMatcher.matches("Dile hola a Juan", "hola", MatchMode.STARTS_WITH, caseSensitive = false))
    }

    // --- REGEX ---

    @Test
    fun `regex matchea patrones basicos`() {
        assertTrue(MessageMatcher.matches("Mi número es 612345678", """\d{9}""", MatchMode.REGEX, caseSensitive = false))
        assertTrue(MessageMatcher.matches("EL PRECIO ES 10", """precio""", MatchMode.REGEX, caseSensitive = false))
    }

    @Test
    fun `regex invalida no lanza excepcion y devuelve false`() {
        assertFalse(MessageMatcher.matches("hola", "([unclosed", MatchMode.REGEX, caseSensitive = false))
    }

    // --- Casos límite ---

    @Test
    fun `mensaje o trigger vacio nunca matchea`() {
        assertFalse(MessageMatcher.matches("", "hola", MatchMode.CONTAINS, caseSensitive = false))
        assertFalse(MessageMatcher.matches("hola", "", MatchMode.CONTAINS, caseSensitive = false))
        assertFalse(MessageMatcher.matches("   ", "hola", MatchMode.CONTAINS, caseSensitive = false))
    }

    @Test
    fun `isRegexValid distingue patrones validos de invalidos`() {
        assertTrue(MessageMatcher.isRegexValid("""\d+"""))
        assertFalse(MessageMatcher.isRegexValid("([unclosed"))
    }
}
