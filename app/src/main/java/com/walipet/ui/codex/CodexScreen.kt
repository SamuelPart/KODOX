package com.walipet.ui.codex

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.walipet.R
import com.walipet.engine.model.Achievement
import com.walipet.engine.model.EvolutionStage
import com.walipet.engine.pet.EvolutionResolver
import com.walipet.ui.components.PixelArtPet
import com.walipet.ui.components.PixelCard
import com.walipet.ui.components.PixelProgressBar
import com.walipet.ui.components.PixelSpacer
import com.walipet.ui.theme.ArcadeOnSurfaceDim
import com.walipet.ui.theme.ArcadeOutline
import com.walipet.ui.theme.ArcadePrimary
import com.walipet.ui.theme.ArcadeSecondary
import com.walipet.ui.theme.ArcadeStreak
import com.walipet.ui.theme.ArcadeSurfaceVariant

/** Codex: línea de evolución + logros/medallas desbloqueables. */
@Composable
fun CodexScreen(
    viewModel: CodexViewModel = viewModel(factory = CodexViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentStage = EvolutionResolver.stageFor(state.pet)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(text = stringResource(R.string.codex_title), style = MaterialTheme.typography.headlineMedium)
        PixelSpacer(14.dp)

        Text(text = stringResource(R.string.codex_evolution_title), style = MaterialTheme.typography.titleMedium)
        PixelSpacer(8.dp)
        EvolutionStage.entries.forEach { stage ->
            StageCard(
                stage = stage,
                isCurrent = stage == currentStage,
                unlocked = state.pet.level >= stage.minLevel,
                petLevel = state.pet.level,
            )
            PixelSpacer(10.dp)
        }

        PixelSpacer(6.dp)

        // Logros.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = stringResource(R.string.codex_achievements_title), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.codex_progress, state.unlockedCount, state.achievements.size),
                style = MaterialTheme.typography.labelMedium,
                color = ArcadeStreak,
            )
        }
        PixelSpacer(8.dp)
        PixelProgressBar(
            progress = if (state.achievements.isEmpty()) 0f else state.unlockedCount.toFloat() / state.achievements.size,
            color = ArcadeSecondary,
            modifier = Modifier.fillMaxWidth(),
        )
        PixelSpacer(12.dp)

        state.achievements.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { achievement ->
                    AchievementCard(
                        achievement = achievement,
                        unlocked = achievement.id in state.unlockedIds,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) Box(modifier = Modifier.weight(1f))
            }
            PixelSpacer(10.dp)
        }
        PixelSpacer(16.dp)
    }
}

@Composable
private fun StageCard(
    stage: EvolutionStage,
    isCurrent: Boolean,
    unlocked: Boolean,
    petLevel: Int,
) {
    PixelCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isCurrent) ArcadeStreak else MaterialTheme.colorScheme.outline,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PixelArtPet(
                stage = stage,
                mood = com.walipet.engine.model.PetMood.CONTENT,
                modifier = Modifier.size(64.dp),
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stageName(stage),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isCurrent) ArcadeStreak else MaterialTheme.colorScheme.onSurface,
                    )
                    if (isCurrent) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = stringResource(R.string.stage_current),
                            tint = ArcadeStreak,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
                Text(
                    text = stageDesc(stage),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (!unlocked) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = ArcadeOnSurfaceDim,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = stringResource(R.string.stage_locked, stage.minLevel),
                        style = MaterialTheme.typography.labelMedium,
                        color = ArcadeOnSurfaceDim,
                    )
                }
            } else if (isCurrent) {
                Text(
                    text = stringResource(R.string.stage_current),
                    style = MaterialTheme.typography.labelMedium,
                    color = ArcadeStreak,
                )
            }
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: Achievement,
    unlocked: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(ArcadeSurfaceVariant, MaterialTheme.shapes.medium)
            .padding(12.dp)
            .then(if (!unlocked) Modifier.alpha(0.45f) else Modifier),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(MaterialTheme.shapes.small)
                .background(if (unlocked) ArcadePrimary else ArcadeOutline),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = achievementIcon(achievement.icon),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }
        Text(
            text = achievementTitle(achievement),
            style = MaterialTheme.typography.labelLarge,
            color = if (unlocked) ArcadePrimary else ArcadeOnSurfaceDim,
            maxLines = 1,
        )
        Text(
            text = achievementDesc(achievement),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            textAlign = TextAlign.Start,
        )
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
private fun stageDesc(stage: EvolutionStage): String = when (stage) {
    EvolutionStage.EGG -> stringResource(R.string.stage_egg_desc)
    EvolutionStage.BABY -> stringResource(R.string.stage_baby_desc)
    EvolutionStage.YOUNG -> stringResource(R.string.stage_young_desc)
    EvolutionStage.EPIC -> stringResource(R.string.stage_epic_desc)
}

@Composable
private fun achievementTitle(a: Achievement): String = stringResource(
    when (a.id) {
        "first_reply" -> R.string.ach_first_reply_title
        "replies_10" -> R.string.ach_replies_10_title
        "replies_100" -> R.string.ach_replies_100_title
        "replies_500" -> R.string.ach_replies_500_title
        "streak_3" -> R.string.ach_streak_3_title
        "streak_7" -> R.string.ach_streak_7_title
        "streak_30" -> R.string.ach_streak_30_title
        "level_3" -> R.string.ach_level_3_title
        "level_6" -> R.string.ach_level_6_title
        "level_10" -> R.string.ach_level_10_title
        "xp_1000" -> R.string.ach_xp_1000_title
        "hour_saved" -> R.string.ach_hour_saved_title
        else -> R.string.app_name
    },
)

@Composable
private fun achievementDesc(a: Achievement): String = stringResource(
    when (a.id) {
        "first_reply" -> R.string.ach_first_reply_desc
        "replies_10" -> R.string.ach_replies_10_desc
        "replies_100" -> R.string.ach_replies_100_desc
        "replies_500" -> R.string.ach_replies_500_desc
        "streak_3" -> R.string.ach_streak_3_desc
        "streak_7" -> R.string.ach_streak_7_desc
        "streak_30" -> R.string.ach_streak_30_desc
        "level_3" -> R.string.ach_level_3_desc
        "level_6" -> R.string.ach_level_6_desc
        "level_10" -> R.string.ach_level_10_desc
        "xp_1000" -> R.string.ach_xp_1000_desc
        "hour_saved" -> R.string.ach_hour_saved_desc
        else -> R.string.app_name
    },
)

/** Traduce la clave de ícono de un logro a un ImageVector (material, sin emojis). */
private fun achievementIcon(key: String): ImageVector = when (key) {
    "robot" -> Icons.Filled.SmartToy
    "bolt" -> Icons.Filled.Bolt
    "hundred" -> Icons.Filled.Bolt
    "rocket" -> Icons.Filled.RocketLaunch
    "flame" -> Icons.Filled.LocalFireDepartment
    "trophy" -> Icons.Filled.EmojiEvents
    "egg" -> Icons.Filled.Egg
    "shield" -> Icons.Filled.Shield
    "star" -> Icons.Filled.Star
    "sparkle" -> Icons.Filled.AutoAwesome
    "timer" -> Icons.Filled.Timer
    else -> Icons.Filled.AutoAwesome
}
