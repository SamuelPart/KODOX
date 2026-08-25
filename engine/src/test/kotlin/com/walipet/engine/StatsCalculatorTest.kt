package com.walipet.engine

import com.walipet.engine.model.AutomationLog
import com.walipet.engine.model.ChatType
import com.walipet.engine.model.Rule
import com.walipet.engine.stats.StatsCalculator
import kotlin.test.Test
import kotlin.test.assertEquals
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class StatsCalculatorTest {

    private val today = LocalDate.of(2026, 8, 25)
    private val zone = ZoneOffset.UTC

    private fun log(daysAgo: Long, xp: Int = 15, ruleId: Long = 1) = AutomationLog(
        ruleId = ruleId,
        ruleName = "Regla",
        sender = "Juan",
        chatType = ChatType.PRIVATE,
        messageSnippet = "hola",
        responseText = "ok",
        xpGained = xp,
        occurredAt = today.minusDays(daysAgo).atStartOfDay().toInstant(zone),
    )

    @Test
    fun `calcula conteos de hoy semana y mes`() {
        // Hoy: 3 | Hace 2 días: 2 | Hace 9 días: 1 (fuera de semana, dentro de mes... el mes es agosto)
        val logs = listOf(
            log(0), log(0), log(0),
            log(2), log(2),
            log(9),
        )
        val stats = StatsCalculator.compute(logs, rules = emptyList(), today = today, zone = zone)
        assertEquals(3, stats.todayCount)
        assertEquals(5, stats.weekCount) // hoy + últimos 6 días
        assertEquals(6, stats.monthCount) // agosto completo (día 9 cae en el mismo mes)
        assertEquals(6L, stats.totalCount)
    }

    @Test
    fun `tiempo ahorrado estimado`() {
        val stats = StatsCalculator.compute(List(80) { log(0) }, emptyList(), today, zone)
        assertEquals(80L * 45L, stats.timeSavedSeconds)
    }

    @Test
    fun `xp total acumulado`() {
        val stats = StatsCalculator.compute(listOf(log(0, xp = 15), log(1, xp = 25)), emptyList(), today, zone)
        assertEquals(40L, stats.totalXp)
    }

    @Test
    fun `reglas activas y totales`() {
        val rules = listOf(
            Rule(id = 1, name = "A", trigger = "a", response = "r", isActive = true),
            Rule(id = 2, name = "B", trigger = "b", response = "r", isActive = false),
        )
        val stats = StatsCalculator.compute(emptyList(), rules, today, zone)
        assertEquals(1, stats.rulesActive)
        assertEquals(2, stats.rulesTotal)
    }

    @Test
    fun `conteo diario para el grafico de 7 dias`() {
        val logs = listOf(log(0), log(0), log(3))
        val daily = StatsCalculator.dailyCounts(logs, today, days = 7, zone = zone)
        assertEquals(7, daily.size)
        assertEquals(2, daily.last().second) // hoy
        assertEquals(1, daily[daily.size - 4].second) // hace 3 días
        assertEquals(LocalDate.of(2026, 8, 19), daily.first().first)
    }

    @Test
    fun `stats vacias devuelven ceros`() {
        val stats = StatsCalculator.compute(emptyList(), emptyList(), today, zone)
        assertEquals(0, stats.todayCount)
        assertEquals(0L, stats.totalCount)
        assertEquals(0L, stats.timeSavedSeconds)
    }
}
