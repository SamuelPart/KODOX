package com.walipet.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.walipet.R

/** Canales de notificación y construcción de las notificaciones propias. */
object NotificationChannels {

    const val CHANNEL_AUTOMATIONS = "automations"
    const val CHANNEL_ALERTS = "alerts"

    fun create(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val automations = NotificationChannel(
            CHANNEL_AUTOMATIONS,
            context.getString(R.string.channel_automations_name),
            NotificationManager.IMPORTANCE_LOW,
        ).apply { description = context.getString(R.string.channel_automations_name) }

        val alerts = NotificationChannel(
            CHANNEL_ALERTS,
            context.getString(R.string.channel_alerts_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply { description = context.getString(R.string.channel_alerts_name) }

        manager.createNotificationChannel(automations)
        manager.createNotificationChannel(alerts)
    }
}

/** Helper para las notificaciones que emite WaliPet (no las de WhatsApp). */
class NotificationHelper(private val context: Context) {

    private val notifier = NotificationManagerCompat.from(context)

    /** Aviso sutil cada vez que WaliPet responde (y, si aplica, sube de nivel). */
    fun notifyReplySent(rule: com.walipet.engine.model.Rule, xpGained: Int, leveledUp: Boolean, newLevel: Int) {
        val levelUpText = if (leveledUp) "\n" + context.getString(R.string.notif_levelup_text, newLevel) else ""
        val notification = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_AUTOMATIONS)
            .setSmallIcon(R.drawable.ic_stat_walipet)
            .setContentTitle(context.getString(R.string.notif_reply_title))
            .setContentText(context.getString(R.string.notif_reply_text, rule.name, xpGained) + levelUpText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notif_reply_text, rule.name, xpGained) + levelUpText))
            .setAutoCancel(true)
            .build()
        runCatching { notifier.notify(NOTIFICATION_ID_REPLY, notification) }
    }

    /** Alerta persistente cuando el acceso a notificaciones está desactivado. */
    fun notifyListenerDisabled() {
        val openSettings = android.app.PendingIntent.getActivity(
            context,
            0,
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS),
            android.app.PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_ALERTS)
            .setSmallIcon(R.drawable.ic_stat_walipet)
            .setContentTitle(context.getString(R.string.notif_listener_disabled_title))
            .setContentText(context.getString(R.string.notif_listener_disabled_text))
            .setContentIntent(openSettings)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        runCatching { notifier.notify(NOTIFICATION_ID_LISTENER_DISABLED, notification) }
    }

    fun cancelListenerDisabled() {
        runCatching { notifier.cancel(NOTIFICATION_ID_LISTENER_DISABLED) }
    }

    private companion object {
        const val NOTIFICATION_ID_REPLY = 1001
        const val NOTIFICATION_ID_LISTENER_DISABLED = 1002
    }
}
