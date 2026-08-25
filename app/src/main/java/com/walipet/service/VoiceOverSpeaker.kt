package com.walipet.service

import android.content.Context
import android.media.AudioAttributes
import android.os.Bundle
import android.speech.tts.TextToSpeech
import com.walipet.data.repository.SettingsRepository
import com.walipet.engine.voice.VoiceLine
import com.walipet.engine.voice.VoiceOverPlanner
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * TTS de la voz en off. Solo pronuncia lo que decide [VoiceOverPlanner]:
 * texto exacto de la última hora, o «N mensajes de M chats». Nunca lee
 * avisos de WhatsApp que no sean de chat (el filtro ocurre antes).
 */
class VoiceOverSpeaker(
    context: Context,
    private val settings: SettingsRepository,
) : TextToSpeech.OnInitListener {

    private val appContext = context.applicationContext
    private val ready = AtomicBoolean(false)
    private val utteranceSeq = AtomicInteger(0)
    private val pending = ArrayDeque<String>()
    private val spokenKeys = LinkedHashMap<String, Long>()

    private val tts: TextToSpeech = TextToSpeech(appContext, this)

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return
        val spanish = Locale("es", "ES")
        val result = tts.setLanguage(spanish)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts.language = Locale.getDefault()
        }
        tts.setSpeechRate(1.02f)
        tts.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build(),
        )
        ready.set(true)
        synchronized(pending) {
            while (pending.isNotEmpty()) {
                speakRaw(pending.removeFirst())
            }
        }
    }

    /**
     * @param dedupKey clave de la notificación (`StatusBarNotification.key`)
     * para no repetir la misma lectura si WhatsApp republica el aviso.
     */
    fun speak(
        lines: List<VoiceLine>,
        dedupKey: String? = null,
        dedupWindowMs: Long = DEDUP_WINDOW_MS,
    ) {
        if (!settings.voiceOverEnabled) return
        if (lines.isEmpty()) return

        val fingerprint = lines.joinToString("|") { VoiceOverPlanner.speechText(it) }
        if (dedupKey != null && alreadySpoken(dedupKey, fingerprint, dedupWindowMs)) return

        val texts = lines.map { VoiceOverPlanner.speechText(it) }.filter { it.isNotBlank() }
        if (texts.isEmpty()) return

        if (!ready.get()) {
            synchronized(pending) {
                pending.addAll(texts)
            }
            return
        }
        texts.forEach { text -> speakRaw(text) }
    }

    /** Recuerda una lectura para no repetirla si WhatsApp republica el mismo aviso. */
    fun markSpoken(dedupKey: String, lines: List<VoiceLine>, windowMs: Long = DEDUP_WINDOW_MS) {
        val fingerprint = lines.joinToString("|") { VoiceOverPlanner.speechText(it) }
        alreadySpoken(dedupKey, fingerprint, windowMs)
    }

    fun stop() {
        synchronized(pending) { pending.clear() }
        runCatching { tts.stop() }
    }

    fun shutdown() {
        stop()
        runCatching { tts.shutdown() }
        ready.set(false)
    }

    private fun alreadySpoken(key: String, fingerprint: String, windowMs: Long): Boolean {
        val token = "$key::$fingerprint"
        val now = System.currentTimeMillis()
        synchronized(spokenKeys) {
            val last = spokenKeys[token] ?: 0L
            if (now - last < windowMs) return true
            spokenKeys[token] = now
            while (spokenKeys.size > 80) {
                val oldest = spokenKeys.keys.first()
                spokenKeys.remove(oldest)
            }
        }
        return false
    }

    private fun speakRaw(text: String) {
        val params = Bundle()
        val id = "walipet-voice-${utteranceSeq.incrementAndGet()}"
        tts.speak(text, TextToSpeech.QUEUE_ADD, params, id)
    }

    private companion object {
        const val DEDUP_WINDOW_MS = 8_000L
    }
}
