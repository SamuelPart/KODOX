package com.walipet.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Vigilante: tras un reinicio del dispositivo comprueba que el acceso a
 * notificaciones sigue activo; si no, avisa al usuario con una alerta
 * persistente para que lo reactive.
 */
class WatchdogReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val helper = com.walipet.di.ServiceLocator.getInstance(context).notificationHelper
        if (WaliPetNotificationListener.isEnabled(context)) {
            helper.cancelListenerDisabled()
        } else {
            helper.notifyListenerDisabled()
        }
    }
}
