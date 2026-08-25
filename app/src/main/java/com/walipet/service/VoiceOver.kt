package com.walipet.service

import android.content.Context
import android.media.AudioAttributes
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import com.walipet.engine.model.ParsedWhatsAppMessage
import java.util.LinkedHashMap
import java.util.Locale

/**
 * Voz en off: lee en voz alta los mensajes entrantes de WhatsApp con el motor
 * de texto a voz del sistema.
 *
 * Funciona con la pantalla apagada o mientras usas otra app porque el oyente
 * de notificaciones recibe el aviso en segundo plano. Además:
 *  - Se deduplica: un mismo mensaje re-emitido por Android no se repite.
 *  - Se habla en español (con respaldo al idioma del sistema si no está).
 *  - Se prepara el audio como voz (USAGE_MEDIA / CONTENT_TYPE_SPEECH) para que
 *    suene por el altavoz normal.
 */
class VoiceOver(context: Context) {

    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())

    private var tts: TextToSpeech? = null
    @Volatile private var ready = false
    @Volatile private var pending: List<ParsedWhatsAppMessage>? = null

    // Deduplicación de lecturas: firma (remitente|texto) -> marca de tiempo.
    private val spoken = LinkedHashMap<String, Long>(32, 0.75f, false)

    init {
        // Crear el TextToSpeech en el hilo principal (requisito de Android).
        mainHandler.post {
            tts = TextToSpeech(appContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build(),
                    )
                    val es = tts?.setLanguage(Locale("es"))
                    if (es == TextToSpeech.LANG_MISSING_DATA || es == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts?.setLanguage(Locale.getDefault())
                    }
                    ready = true
                    pending?.let { flush(it) }
                    pending = null
                }
            }
        }
    }

    /** Lee en voz alta los mensajes nuevos (una sola vez cada uno). */
    fun speakNew(messages: List<ParsedWhatsAppMessage>) {
        val now = System.currentTimeMillis()
        prune(now)

        val fresh = messages.filter { (sender, text) ->
            val key = "${sender.trim()}|${text.trim()}"
            if (spoken.containsKey(key)) {
                false
            } else {
                spoken[key] = now
                true
            }
        }
        if (fresh.isEmpty()) return

        if (!ready) {
            pending = (pending ?: emptyList()) + fresh
            return
        }
        mainHandler.post { flush(fresh) }
    }

    private fun flush(messages: List<ParsedWhatsAppMessage>) {
        val t = tts ?: return
        // Combina todos los mensajes nuevos en una sola locución natural.
        val utterance = messages.joinToString(" ") { (sender, text) ->
            "Mensaje de $sender. $text"
        }
        t.speak(utterance, TextToSpeech.QUEUE_FLUSH, null, "walipet-voz")
    }

    private fun prune(now: Long) {
        val cutoff = now - WINDOW_MS
        spoken.entries.removeAll { it.value < cutoff }
    }

    private companion object {
        const val WINDOW_MS = 60_000L
    }
}
