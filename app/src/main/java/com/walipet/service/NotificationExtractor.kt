package com.walipet.service

import android.app.Notification
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.walipet.engine.model.NotificationRawData

/**
 * Puente entre el framework de Android y el parser puro del motor:
 * convierte un [StatusBarNotification] en un [NotificationRawData] sin lógica.
 *
 * Todo el análisis de texto vive en `engine` (WhatsAppMessageParser), que
 * además se puede probar de forma unitaria sin emulador.
 */
object NotificationExtractor {

    fun toRaw(sbn: StatusBarNotification): NotificationRawData? {
        val notification = sbn.notification ?: return null
        val extras = notification.extras ?: return null

        val title = extras.getCharSequence(Notification.EXTRA_TITLE)
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)

        // Mensajes estructurados (MessagingStyle) — los usa WhatsApp en Android 7+.
        val messages = mutableListOf<Pair<CharSequence?, CharSequence?>>()
        try {
            val style = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(notification)
            if (style != null) {
                for (message in style.messages) {
                    messages += message.sender to message.text
                }
            }
        } catch (_: Throwable) {
            // Algunas ROMs devuelven null; lo ignoramos y caemos al texto plano.
        }

        // Mensajes del Wearable.Extender: en realidad NotificationCompat.WearableExtender
        // NO expone un getter `messages` (solo getPages()/getActions()), así que este
        // bloque no compilaba. Los mensajes estructurados del chat ya se capturan arriba
        // con MessagingStyle, que es la fuente correcta en Android 7+.

        val hasReplyAction = notification.actions?.any { it.remoteInputs.isNotEmpty() } == true

        return NotificationRawData(
            packageName = sbn.packageName,
            title = title,
            text = text,
            messages = messages,
            hasReplyAction = hasReplyAction,
        )
    }
}
