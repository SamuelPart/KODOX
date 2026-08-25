package com.walipet.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationManagerCompat
import com.walipet.di.ServiceLocator
import com.walipet.engine.model.ParsedWhatsAppMessage
import com.walipet.engine.rule.WhatsAppMessageParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * El oyente de notificaciones: el sistema Android lo mantiene vivo mientras
 * la app tenga "Acceso a notificaciones" (permiso especial que el usuario
 * concede desde Ajustes; lo guiamos en el onboarding).
 *
 * Filtra SOLO paquetes de WhatsApp y delega en [AutomatedReplier]. Todo el
 * procesamiento ocurre localmente en el dispositivo.
 */
class WaliPetNotificationListener : NotificationListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val serviceLocator by lazy { ServiceLocator.getInstance(applicationContext) }
    private val replier: AutomatedReplier by lazy { serviceLocator.automatedReplier }
    private val voiceOver: VoiceOver by lazy { serviceLocator.voiceOver }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // Solo nos interesan las notificaciones de WhatsApp.
        if (sbn.packageName != WHATSAPP_PACKAGE) return

        val raw = NotificationExtractor.toRaw(sbn) ?: return
        val actions = sbn.notification?.actions?.toList().orEmpty()

        // Cuando la notificación trae varios mensajes (p. ej. llegan varios
        // seguidos), se extrae CADA mensaje distinto.
        val candidates: List<ParsedWhatsAppMessage> =
            if (raw.messages.isNotEmpty()) {
                raw.messages.mapNotNull { (sender, body) ->
                    WhatsAppMessageParser.parseMessage(raw, sender, body)
                }
            } else {
                listOfNotNull(WhatsAppMessageParser.parse(raw))
            }
        if (candidates.isEmpty()) return

        // Voz en off: leer en voz alta los mensajes nuevos, con la pantalla
        // apagada o mientras usas otras apps (si el usuario lo activó).
        if (serviceLocator.settingsRepository.voiceOverEnabled) {
            voiceOver.speakNew(candidates)
        }

        // Auto-respuesta: solo si la notificación expone el botón Responder.
        if (!raw.hasReplyAction) return
        for (parsed in candidates) {
            // ReplyGuard evita responder dos veces al mismo mensaje y rompe los
            // bucles con nuestra propia respuesta.
            if (ReplyGuard.shouldReply(parsed.sender, parsed.text)) {
                scope.launch { replier.process(parsed, actions) }
            }
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        // Si el acceso acaba de concederse, quitamos el recordatorio.
        ServiceLocator.getInstance(applicationContext).notificationHelper.cancelListenerDisabled()
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

        /** `true` si la app está en la lista blanca de oyentes de notificaciones. */
        fun isEnabled(context: android.content.Context): Boolean =
            NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
    }
}
