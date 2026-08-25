package com.walipet.data.repository

import android.content.Context

/** Ajustes ligeros de la app (SharedPreferences). */
class SettingsRepository(context: Context) {

    private val prefs = context.getSharedPreferences("walipet_settings", Context.MODE_PRIVATE)

    /** `true` cuando el usuario completó el onboarding de permisos. */
    var onboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, value).apply()

    private companion object {
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
