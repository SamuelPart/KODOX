package com.walipet.service

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat

/**
 * Estado actual de los permisos/ajustes que WaliPet necesita para funcionar,
 * y acciones de un solo toque para activarlos. La UI lo usa para guiar a
 * usuarios que no están familiarizados con los ajustes del teléfono.
 */
data class SetupStatus(
    /** Acceso a notificaciones (imprescindible para leer WhatsApp en segundo plano). */
    val listenerEnabled: Boolean,
    /** Permiso de notificaciones de la propia app (Android 13+). */
    val notificationsGranted: Boolean,
    /** Exención de la optimización agresiva de batería (reduce la demora). */
    val batteryExempt: Boolean,
) {
    val allGood: Boolean get() = listenerEnabled && notificationsGranted
}

object SetupChecker {

    fun status(context: Context): SetupStatus = SetupStatus(
        listenerEnabled = WaliPetNotificationListener.isEnabled(context),
        notificationsGranted = notificationsGranted(context),
        batteryExempt = isBatteryExempt(context),
    )

    fun notificationsGranted(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 33) return true
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    }

    fun isBatteryExempt(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager ?: return false
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }

    /** Abre la lista de apps con "acceso a notificaciones". */
    fun openNotificationListenerSettings(context: Context) {
        runCatching { context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
    }

    /** Abre los ajustes de notificación de la app. */
    fun openNotificationPermissionSettings(context: Context) {
        runCatching {
            context.startActivity(
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName),
            )
        }
    }

    /** Pide la exención de optimización de batería. */
    fun requestBatteryExemption(context: Context) {
        runCatching {
            context.startActivity(
                Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.parse("package:${context.packageName}"),
                ),
            )
        }
    }
}
