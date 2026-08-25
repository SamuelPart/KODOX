package com.walipet.data.repository

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Ajustes ligeros de la app (SharedPreferences). */
class SettingsRepository(context: Context) {

    private val prefs = context.getSharedPreferences("walipet_settings", Context.MODE_PRIVATE)

    /** `true` cuando el usuario completó el onboarding de permisos. */
    var onboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, value).apply()

    /** `true` si WaliPet debe leer en voz alta los mensajes entrantes de WhatsApp. */
    var voiceOverEnabled: Boolean
        get() = prefs.getBoolean(KEY_VOICE_OVER_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_VOICE_OVER_ENABLED, value).apply()

    private companion object {
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        const val KEY_VOICE_OVER_ENABLED = "voice_over_enabled"
    }
}
