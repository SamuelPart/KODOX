package com.walipet.engine.time

import java.time.Instant
import java.time.LocalDate

/**
 * Abstracción del tiempo para poder probar la lógica con fechas fijas.
 * En producción se usa [Clock.SYSTEM].
 */
interface Clock {
    fun today(): LocalDate
    fun now(): Instant

    companion object {
        val SYSTEM: Clock = object : Clock {
            override fun today(): LocalDate = LocalDate.now()
            override fun now(): Instant = Instant.now()
        }
    }
}
