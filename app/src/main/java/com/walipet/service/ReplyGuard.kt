package com.walipet.service

import java.util.LinkedHashMap

/**
 * Evita responder varias veces al mismo mensaje y rompe los bucles de
 * retroalimentación.
 *
 * Cuando llegan varios mensajes seguidos (o Android re-emite varias veces la
 * misma notificación), la app debe responder UNA vez por mensaje distinto, no
 * una por cada re-emisión. Además, al enviar una respuesta WhatsApp actualiza
 * la notificación incluyendo el mensaje que ya enviamos; sin protección eso
 * provocaría respuestas en cascada (p. ej. 3 mensajes → 7 respuestas).
 *
 * Reglas:
 *  - Un (remitente, texto) concreto solo se responde una vez dentro de una
 *    ventana de tiempo.
 *  - Nunca se responde a un texto que nosotros acabamos de enviar (eco de
 *    nuestra propia respuesta).
 */
object ReplyGuard {

    private const val WINDOW_MS = 60_000L
    private const val SENT_WINDOW_MS = 60L * 60L * 1000L

    private data class Sig(val sender: String, val text: String)

    // Firmas (remitente, texto) que ya procesamos -> última marca de tiempo.
    private val replied = LinkedHashMap<Sig, Long>(64, 0.75f, false)

    // Textos de respuestas que acabamos de enviar -> última marca de tiempo.
    private val sentTexts = LinkedHashMap<String, Long>(32, 0.75f, false)

    /**
     * Devuelve `true` si este mensaje debe responderse (no es un duplicado ni
     * un eco de nuestra propia respuesta). Si devuelve `true`, lo marca como
     * respondido de forma optimista para que un re-emisión posterior se ignore.
     */
    @Synchronized
    fun shouldReply(sender: String, text: String): Boolean {
        val now = System.currentTimeMillis()
        prune(now)

        val t = text.trim()
        if (t.isEmpty()) return false
        if (sentTexts.containsKey(t)) return false // es un eco de nuestra propia respuesta

        val sig = Sig(sender.trim(), t)
        if (replied.containsKey(sig)) return false // ya respondimos a este mensaje

        replied[sig] = now
        return true
    }

    /** `true` si este texto es una respuesta que nosotros acabamos de enviar. */
    @Synchronized
    fun wasSent(replyText: String): Boolean {
        prune(System.currentTimeMillis())
        return sentTexts.containsKey(replyText.trim())
    }

    /** Registra una respuesta que acabamos de enviar para no responderle de nuevo. */
    @Synchronized
    fun markSent(replyText: String) {
        sentTexts[replyText.trim()] = System.currentTimeMillis()
    }

    @Synchronized
    fun reset() {
        replied.clear()
        sentTexts.clear()
    }

    private fun prune(now: Long) {
        replied.entries.removeAll { it.value < now - WINDOW_MS }
        sentTexts.entries.removeAll { it.value < now - SENT_WINDOW_MS }
    }
}
