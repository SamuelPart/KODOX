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

    private val _voiceOverEnabled = MutableStateFlow(prefs.getBoolean(KEY_VOICE_OVER, true))
    val voiceOverEnabledFlow: StateFlow<Boolean> = _voiceOverEnabled.asStateFlow()

    /**
     * Voz en off: lee el texto exacto de los mensajes de chat de la última
     * hora y resume el resto. No lee avisos de WhatsApp que no sean de chat.
     */
    var voiceOverEnabled: Boolean
        get() = _voiceOverEnabled.value
        set(value) {
            prefs.edit().putBoolean(KEY_VOICE_OVER, value).apply()
            _voiceOverEnabled.value = value
        }

    private companion object {
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        const val KEY_VOICE_OVER = "voice_over_enabled"
    }
}
