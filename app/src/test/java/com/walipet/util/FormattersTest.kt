package com.walipet.util

import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FormattersTest {

    private val now = Instant.parse("2026-08-25T12:00:00Z")

    @Test
    fun `timeAgo segundos y minutos`() {
        assertEquals("ahora", TimeAgo.format(now.minusSeconds(30), now))
        assertEquals("hace 5 min", TimeAgo.format(now.minusSeconds(300), now))
    }

    @Test
    fun `timeAgo horas y dias`() {
        assertEquals("hace 2 h", TimeAgo.format(now.minusSeconds(7200), now))
        assertEquals("hace 3 días", TimeAgo.format(now.minusSeconds(86_400 * 3), now))
    }

    @Test
    fun `timeAgo mayor a una semana muestra la fecha`() {
        // El día es estable entre locales; el mes depende del locale del dispositivo.
        val result = TimeAgo.format(now.minusSeconds(86_400 * 10), now, ZoneOffset.UTC)
        assertTrue(result.startsWith("15 "), "esperaba fecha 15, era: $result")
    }

    @Test
    fun `duracion legible`() {
        assertEquals("0 min", DurationFormat.fromSeconds(0))
        assertEquals("45 min", DurationFormat.fromSeconds(45 * 60))
        assertEquals("1 h", DurationFormat.fromSeconds(3600))
        assertEquals("2 h 15 min", DurationFormat.fromSeconds(8100))
    }

    @Test
    fun `etiqueta corta de dia`() {
        val date = java.time.LocalDate.of(2026, 8, 25) // martes
        assertEquals("MA", DayLabel.short(date))
    }
}
