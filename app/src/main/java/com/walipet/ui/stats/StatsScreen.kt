package com.walipet.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.walipet.R
import com.walipet.ui.components.PixelCard
import com.walipet.ui.components.PixelSpacer
import com.walipet.ui.components.StatChip
import com.walipet.ui.theme.ArcadeOnSurfaceDim
import com.walipet.ui.theme.ArcadePrimary
import com.walipet.ui.theme.ArcadeSecondary
import com.walipet.ui.theme.ArcadeStreak
import com.walipet.ui.theme.ArcadeSurfaceVariant
import com.walipet.util.DayLabel
import com.walipet.util.DurationFormat
import com.walipet.util.TimeAgo

/** Panel de estadísticas: métricas de impacto, gráfico semanal y feed. */
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel(factory = StatsViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val stats = state.stats

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(text = stringResource(R.string.stats_title), style = MaterialTheme.typography.headlineMedium)
        PixelSpacer(12.dp)

        // Métricas principales.
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatChip(
                value = stats.todayCount.toString(),
                label = stringResource(R.string.stats_today),
                modifier = Modifier.weight(1f),
            )
            StatChip(
                value = stats.weekCount.toString(),
                label = stringResource(R.string.stats_week),
                modifier = Modifier.weight(1f),
                accent = ArcadeSecondary,
            )
            StatChip(
                value = stats.monthCount.toString(),
                label = stringResource(R.string.stats_month),
                modifier = Modifier.weight(1f),
                accent = ArcadeStreak,
            )
        }
        PixelSpacer(8.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatChip(
                value = stats.totalCount.toString(),
                label = stringResource(R.string.stats_total),
                modifier = Modifier.weight(1f),
            )
            StatChip(
                value = stats.totalXp.toString(),
                label = stringResource(R.string.stats_xp_total),
                modifier = Modifier.weight(1f),
                accent = ArcadeSecondary,
            )
            StatChip(
                value = DurationFormat.fromSeconds(stats.timeSavedSeconds),
                label = stringResource(R.string.stats_time_saved),
                modifier = Modifier.weight(1f),
                accent = ArcadeStreak,
            )
        }

        PixelSpacer(16.dp)

        // Gráfico de los últimos 7 días.
        Text(text = stringResource(R.string.stats_chart_title), style = MaterialTheme.typography.titleMedium)
        PixelSpacer(8.dp)
        PixelCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                val max = (state.dailyCounts.maxOfOrNull { it.second } ?: 0).coerceAtLeast(1)
                state.dailyCounts.forEach { (date, count) ->
                    val isToday = date == state.dailyCounts.last().first
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                    ) {
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height((if (count > 0) 22f + 78f * count / max else 4f).dp)
                                .background(
                                    if (isToday) ArcadeStreak else ArcadePrimary,
                                    MaterialTheme.shapes.extraSmall,
                                ),
                        )
                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (count > 0) MaterialTheme.colorScheme.onSurface else ArcadeOnSurfaceDim,
                        )
                        Text(
                            text = DayLabel.short(date),
                            style = MaterialTheme.typography.labelMedium,
                            color = ArcadeOnSurfaceDim,
                        )
                    }
                }
            }
        }

        PixelSpacer(16.dp)

        // Feed de actividad.
        Text(text = stringResource(R.string.stats_feed_title), style = MaterialTheme.typography.titleMedium)
        PixelSpacer(8.dp)
        if (state.feed.isEmpty()) {
            PixelCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.stats_feed_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            state.feed.forEach { log ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = "▸",
                        style = MaterialTheme.typography.titleMedium,
                        color = ArcadeSecondary,
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${log.sender} · ${TimeAgo.format(log.occurredAt)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = stringResource(R.string.stats_feed_row, log.messageSnippet, log.ruleName, log.responseText),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                        )
                        if (log.xpGained > 0) {
                            Text(
                                text = stringResource(R.string.stats_xp_gained, log.xpGained),
                                style = MaterialTheme.typography.labelMedium,
                                color = ArcadePrimary,
                            )
                        }
                    }
                }
            }
        }
        PixelSpacer(16.dp)
    }
}
