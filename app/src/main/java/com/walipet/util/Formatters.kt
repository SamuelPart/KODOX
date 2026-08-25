package com.walipet.util

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Formateadores de texto puros (JVM) para la UI: tiempos relativos,
 * duraciones y fechas. Al ser puros se pueden probar sin emulador.
 */
object TimeAgo {

    fun format(instant: Instant, now: Instant = Instant.now(), zone: ZoneId = ZoneId.systemDefault()): String {
        val elapsed = Duration.between(instant, now).seconds.coerceAtLeast(0)
        return when {
            elapsed < 60 -> "ahora"
            elapsed < 3600 -> "hace ${elapsed / 60} min"
            elapsed < 86_400 -> "hace ${elapsed / 3600} h"
            elapsed < 604_800 -> "hace ${elapsed / 86_400} días"
            else -> {
                val date = instant.atZone(zone).toLocalDate()
                date.format(DateTimeFormatter.ofPattern("d MMM", Locale.getDefault()))
            }
        }
    }
}

object DurationFormat {

    fun fromSeconds(totalSeconds: Long): String {
        if (totalSeconds <= 0) return "0 min"
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        return when {
            hours > 0 && minutes > 0 -> "${hours} h ${minutes} min"
            hours > 0 -> "${hours} h"
            else -> "${minutes} min"
        }
    }
}

object DayLabel {

    /** "LU", "MA", … para las etiquetas del gráfico semanal. */
    fun short(date: LocalDate): String =
        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            .take(2)
            .uppercase(Locale.getDefault())
}
