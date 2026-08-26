package com.walipet.engine

import com.walipet.engine.voice.VoiceStartPolicy
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VoiceStartPolicyTest {

    @Test
    fun `arrancar al instante esta a tiempo`() {
        assertTrue(VoiceStartPolicy.isOnTime(enqueuedAtEpochMilli = 1_000L, nowEpochMilli = 1_000L))
    }

    @Test
    fun `arrancar a los 2 segundos justos esta a tiempo`() {
        assertTrue(VoiceStartPolicy.isOnTime(enqueuedAtEpochMilli = 1_000L, nowEpochMilli = 3_000L))
    }

    @Test
    fun `pasar de 2 segundos es tarde`() {
        assertFalse(VoiceStartPolicy.isOnTime(enqueuedAtEpochMilli = 1_000L, nowEpochMilli = 3_001L))
    }

    @Test
    fun `reloj invertido no bloquea la lectura`() {
        assertTrue(VoiceStartPolicy.isOnTime(enqueuedAtEpochMilli = 5_000L, nowEpochMilli = 4_000L))
    }
}
