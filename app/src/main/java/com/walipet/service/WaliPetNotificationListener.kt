package com.walipet.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationManagerCompat
import com.walipet.di.ServiceLocator
import com.walipet.engine.rule.WhatsAppMessageParser
import com.walipet.engine.voice.VoiceOverPlanner
import com.walipet.engine.voice.WhatsAppNotificationClass
import com.walipet.engine.voice.WhatsAppNotificationClassifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Filtra SOLO paquetes de WhatsApp. Las notificaciones que no son de un chat
 * (llamadas, WhatsApp Web, copias de seguridad, estados…) se descartan: no
 * se leen ni se responden. Los mensajes que te envían de la última hora se
 * leen al pie de la letra; el resto se resume. La respuesta automática
 * **no** se lee. Cada lectura real alimenta el huevo y la racha.
 */
class WaliPetNotificationListener : NotificationListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val serviceLocator by lazy { ServiceLocator.getInstance(applicationContext) }
    private val replier: AutomatedReplier by lazy { serviceLocator.automatedReplier }
    private val voiceOver: VoiceOver by lazy { serviceLocator.voiceOver }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName !in WHATSAPP_PACKAGES) return

        val raw = NotificationExtractor.toRaw(sbn) ?: return
        val classified = WhatsAppNotificationClassifier.classify(raw)

        // Avisos que no son de chat: silencio total (ni lectura ni auto-reply).
        if (classified is WhatsAppNotificationClass.Ignored) return

        if (serviceLocator.settingsRepository.voiceOverEnabled) {
            val lines = VoiceOverPlanner.planLatestIncoming(
                classified = classified,
                nowEpochMilli = System.currentTimeMillis(),
                isOwnReply = ReplyGuard::wasSent,
            )
            val spoken = voiceOver.speakLines(lines)
            if (spoken.isNotEmpty()) {
                scope.launch {
                    spoken.forEach { _ ->
                        val progress = serviceLocator.petRepository.recordSpokenMessage()
                        if (progress.leveledUp) {
                            serviceLocator.notificationHelper.notifyLevelUp(progress.pet.level)
                        }
                    }
                }
            }
        }

        if (classified !is WhatsAppNotificationClass.ChatMessages) return
        if (!raw.hasReplyAction) return

        val actions = sbn.notification?.actions?.toList().orEmpty()
        val candidates = if (raw.messages.isNotEmpty()) {
            raw.messages.mapNotNull { (sender, body) ->
                WhatsAppMessageParser.parseMessage(raw, sender, body)
            }
        } else {
            listOfNotNull(WhatsAppMessageParser.parse(raw))
        }
        for (parsed in candidates) {
            if (ReplyGuard.shouldReply(parsed.sender, parsed.text)) {
                scope.launch { replier.process(parsed, actions) }
            }
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        serviceLocator.notificationHelper.cancelListenerDisabled()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        ServiceLocator.getInstance(applicationContext).notificationHelper.notifyListenerDisabled()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        const val WHATSAPP_PACKAGE = "com.whatsapp"
        val WHATSAPP_PACKAGES = setOf("com.whatsapp", "com.whatsapp.w4b")

        fun isEnabled(context: android.content.Context): Boolean =
            NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
    }
}
