package com.walipet.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationManagerCompat
import com.walipet.di.ServiceLocator
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
    private val replier: AutomatedReplier by lazy {
        ServiceLocator.getInstance(applicationContext).automatedReplier
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // Solo nos interesan las notificaciones de WhatsApp.
        if (sbn.packageName != WHATSAPP_PACKAGE) return

        val raw = NotificationExtractor.toRaw(sbn) ?: return
        val parsed = com.walipet.engine.rule.WhatsAppMessageParser.parse(raw) ?: return

        // Sin acción de respuesta (RemoteInput) no hay nada que inyectar.
        if (!parsed.hasReplyAction) return

        val actions = sbn.notification?.actions?.toList().orEmpty()
        scope.launch { replier.process(parsed, actions) }
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
