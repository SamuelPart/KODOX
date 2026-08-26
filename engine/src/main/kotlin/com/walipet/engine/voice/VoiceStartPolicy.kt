package com.walipet.engine.voice

/**
 * Tope de demora para **empezar** a leer un mensaje (no para terminarlo).
 * Si no se puede arrancar en este plazo, se descarta: el usuario ya habrá
 * abierto WhatsApp y una lectura tardía no sirve.
 */
object VoiceStartPolicy {

    const val MAX_START_DELAY_MS: Long = 2_000L

    fun isOnTime(enqueuedAtEpochMilli: Long, nowEpochMilli: Long): Boolean {
        if (nowEpochMilli < enqueuedAtEpochMilli) return true
        return nowEpochMilli - enqueuedAtEpochMilli <= MAX_START_DELAY_MS
    }
}
