package com.walipet.engine.rule

import com.walipet.engine.model.ChatScope
import com.walipet.engine.model.MatchMode
import com.walipet.engine.model.ParsedWhatsAppMessage
import com.walipet.engine.model.Rule

/** Resultado de evaluar un mensaje contra el conjunto de reglas activas. */
sealed interface RuleMatch {
    /** Una regla activa coincidió; debe enviarse [RuleMatch.Hit.rule.response]. */
    data class Hit(val rule: Rule, val matchedBy: MatchMode) : RuleMatch

    /** Ninguna regla coincidió (o el mensaje estaba vacío). */
    data object NoMatch : RuleMatch

    /**
     * La regla coincide textualmente pero su regex es inválida; no se responde
     * y se marca para avisar al usuario en la UI.
     */
    data class InvalidRegex(val rule: Rule) : RuleMatch
}

/**
 * Motor de reglas: evalúa un [ParsedWhatsAppMessage] contra la lista de
 * [Rule] activas y devuelve la primera coincidencia (las reglas se evalúan
 * en el orden en que aparecen en la lista, que es el orden de creación).
 *
 * 100 % puro: no toca Android, red ni base de datos.
 */
object RuleEngine {

    fun evaluate(message: ParsedWhatsAppMessage, rules: List<Rule>): RuleMatch {
        val text = message.text.trim()
        if (text.isEmpty()) return RuleMatch.NoMatch

        for (rule in rules) {
            if (!rule.isActive) continue
            if (!chatScopeAllows(rule.chatScope, message.chatType)) continue

            // Si la regla es regex pero el patrón no compila, no se puede evaluar:
            // se informa a la UI en lugar de ejecutarla silenciosamente.
            if (rule.matchMode == MatchMode.REGEX && !MessageMatcher.isRegexValid(rule.trigger)) {
                return RuleMatch.InvalidRegex(rule)
            }

            val matches = MessageMatcher.matches(text, rule.trigger, rule.matchMode, rule.caseSensitive)
            if (!matches) continue

            return RuleMatch.Hit(rule, rule.matchMode)
        }
        return RuleMatch.NoMatch
    }

    private fun chatScopeAllows(scope: ChatScope, chatType: com.walipet.engine.model.ChatType): Boolean =
        when (scope) {
            ChatScope.ALL -> true
            ChatScope.PRIVATE_ONLY -> chatType == com.walipet.engine.model.ChatType.PRIVATE
            ChatScope.GROUPS_ONLY -> chatType == com.walipet.engine.model.ChatType.GROUP
        }
}
