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

/**
 * Voz en off: lee **solo los mensajes que te envían** (nunca la respuesta
 * automática de WaliPet). Última hora al pie de la letra; el resto,
 * «N mensajes de M chats».
 */
class VoiceOver(context: Context) {

    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())

    private var tts: TextToSpeech? = null
    @Volatile private var ready = false
    private val pending = ArrayDeque<String>()
    private val spoken = LinkedHashMap<String, Long>(32, 0.75f, false)

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
                        while (pending.isNotEmpty()) flush(listOf(pending.removeFirst()))
                    }
                }
            }
        }
    }

    /** Pronuncia las líneas ya planificadas (texto exacto o resumen). */
    fun speakLines(lines: List<VoiceLine>, dedupKey: String? = null) {
        if (lines.isEmpty()) return
        val texts = lines.map { VoiceOverPlanner.speechText(it) }.filter { it.isNotBlank() }
        if (texts.isEmpty()) return

        val now = System.currentTimeMillis()
        prune(now)
        val fingerprint = dedupKey?.let { "$it|${texts.joinToString("|")}" }
        if (fingerprint != null) {
            if (spoken.containsKey(fingerprint)) return
            spoken[fingerprint] = now
        }

        if (!ready) {
            synchronized(pending) { pending.addAll(texts) }
            return
        }
        mainHandler.post { flush(texts) }
    }

    fun markSpoken(dedupKey: String, lines: List<VoiceLine>) {
        val texts = lines.map { VoiceOverPlanner.speechText(it) }
        spoken["$dedupKey|${texts.joinToString("|")}"] = System.currentTimeMillis()
    }

    fun stop() {
        synchronized(pending) { pending.clear() }
        mainHandler.post { runCatching { tts?.stop() } }
    }

    private fun flush(texts: List<String>) {
        val t = tts ?: return
        texts.forEachIndexed { index, text ->
            val mode = if (index == 0) TextToSpeech.QUEUE_ADD else TextToSpeech.QUEUE_ADD
            t.speak(text, mode, null, "walipet-voz-$index")
        }
    }

    private fun prune(now: Long) {
        val cutoff = now - WINDOW_MS
        spoken.entries.removeAll { it.value < cutoff }
    }

    private companion object {
        const val WINDOW_MS = 60_000L
    }
}
