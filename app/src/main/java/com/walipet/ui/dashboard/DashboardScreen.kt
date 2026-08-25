package com.walipet.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.walipet.R
import com.walipet.engine.model.EvolutionStage
import com.walipet.engine.model.PetMood
import com.walipet.engine.pet.EvolutionResolver
import com.walipet.engine.pet.XpEngine
import com.walipet.ui.components.LevelBadge
import com.walipet.ui.components.PixelCard
import com.walipet.ui.components.PixelArtPet
import com.walipet.ui.components.PixelMiniBar
import com.walipet.ui.components.PixelProgressBar
import com.walipet.ui.components.PixelSpacer
import com.walipet.ui.theme.ArcadeOutline
import com.walipet.ui.theme.ArcadePrimary
import com.walipet.ui.theme.ArcadeStreak
import com.walipet.util.TimeAgo

/** El Hábitat: pantalla principal donde vive y se anima la mascota. */
@Composable
fun DashboardScreen(
    onNewRule: () -> Unit,
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val pet = state.pet
    val stage = EvolutionResolver.stageFor(pet)
    val xpToNext = XpEngine.xpToNextLevel(pet.level)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        // Cabecera: nombre + insignia de nivel.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(R.string.dashboard_title), style = MaterialTheme.typography.headlineMedium)
            LevelBadge(level = pet.level)
        }

        PixelSpacer(8.dp)

        // La mascota.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp),
            contentAlignment = Alignment.Center,
        ) {
            PixelArtPet(
                stage = stage,
                mood = state.mood,
                modifier = Modifier.size(210.dp),
            )
        }

        Text(
            text = stageName(stage),
            style = MaterialTheme.typography.titleLarge,
            color = ArcadePrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Text(
            text = moodText(state.mood),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        PixelSpacer(16.dp)

        // Barra de XP.
        PixelCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_level, pet.level),
                        style = MaterialTheme.typography.labelLarge,
                        color = ArcadePrimary,
                    )
                    Text(
                        text = if (xpToNext > 0) stringResource(R.string.dashboard_xp, pet.xp, xpToNext) else "MÁX",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                PixelProgressBar(
                    progress = if (xpToNext > 0) pet.xp / xpToNext.toFloat() else 1f,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        PixelSpacer(12.dp)

        // Racha + energía/felicidad.
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PixelCard(
                modifier = Modifier.weight(1f),
                borderColor = ArcadeStreak,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = ArcadeStreak,
                        modifier = Modifier.size(28.dp),
                    )
                    Text(
                        text = streakText(pet.streakDays),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        color = ArcadeStreak,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PixelMiniBar(label = stringResource(R.string.dashboard_energy), value = state.energy)
                PixelMiniBar(
                    label = stringResource(R.string.dashboard_happiness),
                    value = state.happiness,
                    color = ArcadeStreak,
                )
            }
        }

        PixelSpacer(20.dp)

        // Actividad reciente.
        Text(text = stringResource(R.string.dashboard_recent), style = MaterialTheme.typography.titleMedium)
        PixelSpacer(8.dp)
        if (state.recentLogs.isEmpty()) {
            PixelCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.dashboard_empty_activity),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            state.recentLogs.take(3).forEach { log ->
                PixelCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    borderColor = ArcadeOutline,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "${log.sender} · ${TimeAgo.format(log.occurredAt)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "“${log.messageSnippet}” → “${log.responseText}”",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                        )
                    }
                }
            }
        }

        PixelSpacer(8.dp)
    }
}

@Composable
private fun stageName(stage: EvolutionStage): String = when (stage) {
    EvolutionStage.EGG -> stringResource(R.string.stage_egg_name)
    EvolutionStage.BABY -> stringResource(R.string.stage_baby_name)
    EvolutionStage.YOUNG -> stringResource(R.string.stage_young_name)
    EvolutionStage.EPIC -> stringResource(R.string.stage_epic_name)
}

@Composable
private fun moodText(mood: PetMood): String = when (mood) {
    PetMood.HAPPY -> stringResource(R.string.mood_happy)
    PetMood.CONTENT -> stringResource(R.string.mood_content)
    PetMood.SAD -> stringResource(R.string.mood_sad)
    PetMood.SLEEPING -> stringResource(R.string.mood_sleeping)
}

@Composable
private fun streakText(streakDays: Int): String = when {
    streakDays > 1 -> stringResource(R.string.dashboard_streak_days, streakDays)
    streakDays == 1 -> stringResource(R.string.dashboard_streak_day)
    else -> stringResource(R.string.dashboard_streak_none)
}
