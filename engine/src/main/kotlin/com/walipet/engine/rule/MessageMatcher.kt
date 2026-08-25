package com.walipet.engine.rule

import com.walipet.engine.model.MatchMode

/**
 * Comparador de mensajes: decide si un texto entrante cumple el disparador
 * de una regla según el [MatchMode] configurado.
 *
 * Es una función pura y determinista, ideal para pruebas unitarias.
 */
object MessageMatcher {

    /**
     * Comprueba si [message] cumple el [trigger] según [mode].
     *
     * @param caseSensitive si `false` (por defecto) la comparación ignora
     * mayúsculas/minúsculas (útil para "precio" vs "Precio").
     */
    fun matches(message: String, trigger: String, mode: MatchMode, caseSensitive: Boolean): Boolean {
        if (message.isBlank() || trigger.isBlank()) return false

        return when (mode) {
            MatchMode.EXACT -> compare(message.trim(), trigger.trim(), caseSensitive) { a, b -> a == b }
            MatchMode.CONTAINS -> compare(message, trigger.trim(), caseSensitive) { a, b -> a.contains(b) }
            MatchMode.STARTS_WITH -> compare(message.trimStart(), trigger.trim(), caseSensitive) { a, b -> a.startsWith(b) }
            MatchMode.REGEX -> matchesRegex(message, trigger, caseSensitive)
        }
    }

    /** Devuelve `true` si el disparador es una expresión regular válida. */
    fun isRegexValid(trigger: String): Boolean =
        runCatching { Regex(trigger) }.isSuccess

    private inline fun compare(message: String, trigger: String, caseSensitive: Boolean, op: (String, String) -> Boolean): Boolean {
        return if (caseSensitive) {
            op(message, trigger)
        } else {
            op(message.lowercase(), trigger.lowercase())
        }
    }

    private fun matchesRegex(message: String, trigger: String, caseSensitive: Boolean): Boolean {
        val regex = runCatching {
            Regex(
                trigger,
                if (caseSensitive) emptySet() else setOf(RegexOption.IGNORE_CASE),
            )
        }.getOrNull() ?: return false
        return regex.containsMatchIn(message)
    }
}
