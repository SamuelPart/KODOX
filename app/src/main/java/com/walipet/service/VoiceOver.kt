package com.walipet.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import com.walipet.engine.voice.VoiceLine
import com.walipet.engine.voice.VoiceOverPlanner
import com.walipet.engine.voice.VoiceStartPolicy
import java.util.LinkedHashMap
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

/**
 * Lee **un** mensaje a la vez: solo lo que manda la otra persona.
 * Arranca en cuanto llega (tope [VoiceStartPolicy.MAX_START_DELAY_MS]):
 * el motor se precalienta y no se espera a propósito.
 */
class VoiceOver(context: Context) {

    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private val utteranceSeq = AtomicInteger(0)

    private var tts: TextToSpeech? = null
    @Volatile private var ready = false
    @Volatile private var starting = false
    private val pending = ArrayDeque<PendingBatch>()
    private val spoken = LinkedHashMap<String, Long>(64, 0.75f, false)

    fun warmup() {
        ensureEngine()
    }

    /**
     * Encola las líneas nuevas. [onStarted] se llama solo cuando la voz
     * **empieza** de verdad y dentro del tope de 2 s (huevo/racha).
     */
    fun speakLines(
        lines: List<VoiceLine>,
        onStarted: (List<VoiceLine>) -> Unit = {},
    ): List<VoiceLine> {
        if (lines.isEmpty()) return emptyList()
        ensureEngine()

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

        if (ready) {
            speakNow(texts)
            onStarted(fresh)
            return fresh
        }

        synchronized(pending) {
            pending.clear()
            pending.add(PendingBatch(fresh, texts, now, onStarted))
        }
        mainHandler.postDelayed({ dropLatePending() }, VoiceStartPolicy.MAX_START_DELAY_MS + 1L)
        return emptyList()
    }

    fun stop() {
        synchronized(pending) { pending.clear() }
        runOnMain { runCatching { tts?.stop() } }
    }

    private fun ensureEngine() {
        if (ready || tts != null || starting) return
        starting = true
        mainHandler.postAtFrontOfQueue {
            if (tts != null || ready) {
                starting = false
                return@postAtFrontOfQueue
            }
            tts = TextToSpeech(appContext) { status ->
                if (status != TextToSpeech.SUCCESS) {
                    starting = false
                    return@TextToSpeech
                }
                val engine = tts ?: return@TextToSpeech
                engine.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build(),
                )
                val es = engine.setLanguage(Locale("es"))
                if (es == TextToSpeech.LANG_MISSING_DATA || es == TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine.setLanguage(Locale.getDefault())
                }
                engine.playSilentUtterance(1L, TextToSpeech.QUEUE_FLUSH, "walipet-warmup")
                ready = true
                starting = false
                flushPending()
            }
        }
    }

    private fun flushPending() {
        val batch = synchronized(pending) {
            pending.removeFirstOrNull()
        } ?: return
        val now = System.currentTimeMillis()
        if (!VoiceStartPolicy.isOnTime(batch.enqueuedAt, now)) return
        speakNow(batch.texts)
        batch.onStarted(batch.lines)
    }

    private fun dropLatePending() {
        val now = System.currentTimeMillis()
        synchronized(pending) {
            pending.removeAll { !VoiceStartPolicy.isOnTime(it.enqueuedAt, now) }
        }
    }

    private fun speakNow(texts: List<String>) {
        if (texts.isEmpty()) return
        runOnMain {
            val t = tts ?: return@runOnMain
            texts.forEachIndexed { index, text ->
                val id = "walipet-voz-${utteranceSeq.incrementAndGet()}"
                val mode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
                t.speak(text, mode, SPEAK_PARAMS, id)
            }
        }
    }

    private fun runOnMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            mainHandler.postAtFrontOfQueue(block)
        }
    }

    private fun prune(now: Long) {
        val cutoff = now - WINDOW_MS
        synchronized(spoken) {
            spoken.entries.removeAll { it.value < cutoff }
        }
    }

    private data class PendingBatch(
        val lines: List<VoiceLine>,
        val texts: List<String>,
        val enqueuedAt: Long,
        val onStarted: (List<VoiceLine>) -> Unit,
    )

    private companion object {
        const val WINDOW_MS = 60L * 60L * 1000L
        val SPEAK_PARAMS = Bundle().apply {
            putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_NOTIFICATION)
        }
    }
}
