package com.walipet.service

import android.content.Context
import android.media.AudioAttributes
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import com.walipet.engine.voice.VoiceLine
import com.walipet.engine.voice.VoiceOverPlanner
import java.util.LinkedHashMap
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

/**
 * Lee **un** mensaje a la vez: solo lo que manda la otra persona.
 * No repite, no clona el historial, no lee la respuesta automática.
 */
class VoiceOver(context: Context) {

    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private val utteranceSeq = AtomicInteger(0)

    private var tts: TextToSpeech? = null
    @Volatile private var ready = false
    private val pending = ArrayDeque<String>()
    private val spoken = LinkedHashMap<String, Long>(64, 0.75f, false)

    init {
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
                    synchronized(pending) {
                        while (pending.isNotEmpty()) speakNow(pending.removeFirst())
                    }
                }
            }
        }
    }

    /**
     * Encola las líneas nuevas y devuelve las que realmente se van a leer
     * (sin duplicados ni respuestas propias). El caller usa esa lista para
     * alimentar el huevo y la racha.
     */
    fun speakLines(lines: List<VoiceLine>): List<VoiceLine> {
        if (lines.isEmpty()) return emptyList()
        val now = System.currentTimeMillis()
        prune(now)

        val fresh = mutableListOf<VoiceLine>()
        val texts = mutableListOf<String>()
        for (line in lines) {
            val text = VoiceOverPlanner.speechText(line).trim()
            if (text.isEmpty()) continue
            if (ReplyGuard.wasSent(text)) continue
            val key = text.lowercase()
            val accepted = synchronized(spoken) {
                if (spoken.containsKey(key)) {
                    false
                } else {
                    spoken[key] = now
                    true
                }
            }
            if (accepted) {
                fresh += line
                texts += text
            }
        }
        if (fresh.isEmpty()) return emptyList()

        if (!ready) {
            synchronized(pending) { pending.addAll(texts) }
            return fresh
        }
        mainHandler.post {
            texts.forEach { speakNow(it) }
        }
        return fresh
    }

    fun stop() {
        synchronized(pending) { pending.clear() }
        mainHandler.post { runCatching { tts?.stop() } }
    }

    private fun speakNow(text: String) {
        val t = tts ?: return
        val id = "walipet-voz-${utteranceSeq.incrementAndGet()}"
        t.speak(text, TextToSpeech.QUEUE_ADD, null, id)
    }

    private fun prune(now: Long) {
        val cutoff = now - WINDOW_MS
        synchronized(spoken) {
            spoken.entries.removeAll { it.value < cutoff }
        }
    }

    private companion object {
        const val WINDOW_MS = 60L * 60L * 1000L
    }
}
