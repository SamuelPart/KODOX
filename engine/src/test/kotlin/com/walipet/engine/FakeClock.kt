package com.walipet.engine

import com.walipet.engine.time.Clock
import java.time.Instant
import java.time.LocalDate

/** Reloj de prueba con fecha/hora fijas (inyectable en los tests). */
class FakeClock(
    var date: LocalDate = LocalDate.of(2026, 8, 25),
    var instant: Instant = Instant.parse("2026-08-25T12:00:00Z"),
) : Clock {
    override fun today(): LocalDate = date
    override fun now(): Instant = instant
}
