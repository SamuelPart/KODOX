package com.walipet.service

import android.app.Notification
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.walipet.data.repository.AutomationLogRepository
import com.walipet.data.repository.PetRepository
import com.walipet.data.repository.RuleRepository
import com.walipet.engine.model.AutomationLog
import com.walipet.engine.model.ParsedWhatsAppMessage
import com.walipet.engine.model.Rule
import com.walipet.engine.rule.RuleEngine
import com.walipet.engine.rule.RuleMatch
import com.walipet.engine.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Orquesta la automatización completa de un mensaje entrante:
 *
 *  1. Evalúa el mensaje contra las reglas activas (motor puro).
 *  2. Si hay coincidencia, inyecta la respuesta vía [RemoteInput] sobre la
 *     acción de la notificación de WhatsApp (funciona con pantalla apagada).
 *  3. Persiste el resultado: XP/racha de la mascota, contador de la regla
 *     y entrada en el historial.
 *
 * [sendReply] usa la API pública `RemoteInput.addResultsToIntent` + el
 * `PendingIntent` de la acción de la notificación, el mismo mecanismo que
 * usa el botón "Responder" de Android — no se inyecta texto en la UI de
 * WhatsApp ni se usa accesibilidad.
 */
class AutomatedReplier(
    private val context: Context,
    private val petRepository: PetRepository,
    private val ruleRepository: RuleRepository,
    private val logRepository: AutomationLogRepository,
    private val notificationHelper: NotificationHelper,
    private val clock: Clock = Clock.SYSTEM,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    sealed interface Outcome {
        data class Replied(val rule: Rule, val xpGained: Int, val leveledUp: Boolean, val newLevel: Int) : Outcome
        data class SendFailed(val rule: Rule) : Outcome
        data object NoMatch : Outcome
        data object NoReplyAction : Outcome
    }

    /** Procesa un mensaje parseado (llamado desde el NotificationListenerService). */
    suspend fun process(message: ParsedWhatsAppMessage, actions: List<Notification.Action>): Outcome {
        val rules = ruleRepository.getActive()
        return when (val match = RuleEngine.evaluate(message, rules)) {
            is RuleMatch.Hit -> {
                val replyAction = actions.firstOrNull { it.remoteInputs.isNotEmpty() }
                    ?: return Outcome.NoReplyAction

                if (!sendReply(replyAction, match.rule.response)) {
                    return Outcome.SendFailed(match.rule)
                }
                // Registrar la respuesta enviada: WhatsApp la re-emitirá dentro de
                // la notificación y ReplyGuard debe ignorarla para no repetirse.
                ReplyGuard.markSent(match.rule.response)

                val now = clock.now()
                val today = clock.today()
                val automation = petRepository.recordAutomation(today, now)
                ruleRepository.recordTriggered(match.rule.id, now.toEpochMilli())
                logRepository.add(
                    AutomationLog(
                        ruleId = match.rule.id,
                        ruleName = match.rule.name,
                        sender = message.sender,
                        chatType = message.chatType,
                        messageSnippet = message.text.take(60),
                        responseText = match.rule.response,
                        xpGained = automation.xpGained,
                        occurredAt = now,
                    ),
                )
                notificationHelper.notifyReplySent(
                    rule = match.rule,
                    xpGained = automation.xpGained,
                    leveledUp = automation.leveledUp,
                    newLevel = automation.pet.level,
                )
                Outcome.Replied(
                    rule = match.rule,
                    xpGained = automation.xpGained,
                    leveledUp = automation.leveledUp,
                    newLevel = automation.pet.level,
                )
            }
            is RuleMatch.InvalidRegex, is RuleMatch.NoMatch -> Outcome.NoMatch
        }
    }

    /** Inyección silenciosa de la respuesta en la notificación de WhatsApp. */
    private fun sendReply(action: Notification.Action, replyText: String): Boolean {
        return try {
            val remoteInput = action.remoteInputs.firstOrNull() ?: return false
            val intent = Intent()
            val results = Bundle().apply { putString(remoteInput.resultKey, replyText) }
            RemoteInput.addResultsToIntent(action.remoteInputs, intent, results)
            action.actionIntent.send(context, 0, intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    /** Permite lanzar el proceso en segundo plano desde el listener. */
    fun processAsync(message: ParsedWhatsAppMessage, actions: List<Notification.Action>) {
        scope.launch { process(message, actions) }
    }
}
