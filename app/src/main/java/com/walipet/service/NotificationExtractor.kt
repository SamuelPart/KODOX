package com.walipet.service

import android.app.Notification
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.walipet.engine.model.NotificationRawData

/**
 * Puente entre el framework de Android y el parser puro del motor:
 * convierte un [StatusBarNotification] en un [NotificationRawData] sin lógica.
 *
 * Todo el análisis de texto vive en `engine` (WhatsAppMessageParser /
 * WhatsAppNotificationClassifier), que además se puede probar de forma
 * unitaria sin emulador.
 */
object NotificationExtractor {

    fun toRaw(sbn: StatusBarNotification): NotificationRawData? {
        val notification = sbn.notification ?: return null
        val extras = notification.extras ?: return null

        val title = extras.getCharSequence(Notification.EXTRA_TITLE)
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)

        val messages = mutableListOf<Pair<CharSequence?, CharSequence?>>()
        val timestamps = mutableListOf<Long>()

        // Mensajes estructurados (MessagingStyle) — los usa WhatsApp en Android 7+.
        try {
            val style = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(notification)
            if (style != null) {
                for (message in style.messages) {
                    messages += message.sender to message.text
                    timestamps += message.timestamp
                }
            }
        } catch (_: Throwable) {
            // Algunas ROMs devuelven null; lo ignoramos y caemos al texto plano.
        }

        // Mensajes del Wearable.Extender: en realidad NotificationCompat.WearableExtender
        // NO expone un getter `messages` (solo getPages()/getActions()), así que este
        // bloque no compilaba. Los mensajes estructurados del chat ya se capturan arriba
        // con MessagingStyle, que es la fuente correcta en Android 7+.

        val hasReplyAction = notification.actions?.any { action ->
            action.remoteInputs?.isNotEmpty() == true
        } == true

        val postedAt = when {
            notification.`when` > 0L -> notification.`when`
            sbn.postTime > 0L -> sbn.postTime
            else -> 0L
        }

        return NotificationRawData(
            packageName = sbn.packageName,
            title = title,
            text = text,
            messages = messages,
            messageTimestamps = timestamps,
            hasReplyAction = hasReplyAction,
            postedAtEpochMilli = postedAt,
            category = notification.category,
            bigText = bigText,
            subText = subText,
        )
    }
}
