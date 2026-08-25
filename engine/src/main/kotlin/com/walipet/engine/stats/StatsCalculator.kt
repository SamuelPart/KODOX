package com.walipet.engine.stats

import com.walipet.engine.model.AutomationLog
import com.walipet.engine.model.AutomationStats
import com.walipet.engine.model.Rule
import java.time.LocalDate
import java.time.ZoneId

/**
 * Calcula las métricas de impacto a partir del historial de automatizaciones:
 *  - Respuestas de hoy / últimos 7 días / este mes / totales.
 *  - XP total ganado.
 *  - Tiempo ahorrado (estimado): cada respuesta automática ahorra
 *    [TIME_SAVED_PER_REPLY_SECONDS] segundos de escritura manual.
 */
object StatsCalculator {

    /** Estimación conservadora: ~45 segundos por respuesta manual evitada. */
    const val TIME_SAVED_PER_REPLY_SECONDS = 45L

    fun compute(
        logs: List<AutomationLog>,
        rules: List<Rule>,
        today: LocalDate,
        zone: ZoneId = ZoneId.systemDefault(),
    ): AutomationStats {
        val totalCount = logs.size.toLong()
        val todayCount = logs.count { it.occurredAt.atZone(zone).toLocalDate() == today }
        val weekCount = logs.count { it.occurredAt.atZone(zone).toLocalDate() >= today.minusDays(6) }
        val monthCount = logs.count { it.occurredAt.atZone(zone).toLocalDate() >= today.withDayOfMonth(1) }

        return AutomationStats(
            totalCount = totalCount,
            todayCount = todayCount,
            weekCount = weekCount,
            monthCount = monthCount,
            totalXp = logs.sumOf { it.xpGained.toLong() },
            timeSavedSeconds = totalCount * TIME_SAVED_PER_REPLY_SECONDS,
            rulesActive = rules.count { it.isActive },
            rulesTotal = rules.size,
        )
    }

    /** Cuenta de automatizaciones por día para los últimos [days] días (gráfico). */
    fun dailyCounts(
        logs: List<AutomationLog>,
        today: LocalDate,
        days: Int,
        zone: ZoneId = ZoneId.systemDefault(),
    ): List<Pair<LocalDate, Int>> {
        val start = today.minusDays((days - 1).toLong())
        val counts = mutableMapOf<LocalDate, Int>()
        for (log in logs) {
            val day = log.occurredAt.atZone(zone).toLocalDate()
            if (day >= start && day <= today) counts[day] = (counts[day] ?: 0) + 1
        }
        return (0 until days).map { offset -> start.plusDays(offset.toLong()) to (counts[start.plusDays(offset.toLong())] ?: 0) }
    }
}
