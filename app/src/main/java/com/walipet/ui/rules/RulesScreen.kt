package com.walipet.ui.rules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.walipet.R
import com.walipet.engine.model.ChatScope
import com.walipet.engine.model.MatchMode
import com.walipet.engine.model.Rule
import com.walipet.ui.components.PixelCard
import com.walipet.ui.components.PixelSpacer
import com.walipet.ui.theme.ArcadeError
import com.walipet.ui.theme.ArcadePrimary
import com.walipet.ui.theme.ArcadeSecondary

/** Gestor de reglas: lista de tarjetas con switch, edición y borrado. */
@Composable
fun RulesScreen(
    onNewRule: () -> Unit,
    onEditRule: (Rule) -> Unit,
    viewModel: RulesViewModel = viewModel(factory = RulesViewModel.Factory),
) {
    val rules by viewModel.rules.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(text = stringResource(R.string.rules_title), style = MaterialTheme.typography.headlineMedium)
        PixelSpacer(12.dp)

        if (rules.isEmpty()) {
            PixelCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.rules_empty_title) + "\n" + stringResource(R.string.rules_empty_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            rules.forEach { rule ->
                RuleCard(
                    rule = rule,
                    onToggle = { active -> viewModel.toggleActive(rule, active) },
                    onClick = { onEditRule(rule) },
                    onDelete = { viewModel.delete(rule) },
                )
                PixelSpacer(10.dp)
            }
        }
        PixelSpacer(80.dp) // espacio para el FAB
    }
}

@Composable
private fun RuleCard(
    rule: Rule,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    PixelCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        borderColor = if (rule.isActive) ArcadePrimary else MaterialTheme.colorScheme.outline,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rule.name.ifBlank { rule.trigger },
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(R.string.rule_times_triggered, rule.timesTriggered),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = rule.isActive,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = ArcadePrimary,
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                )
            }

            Text(
                text = "${modeLabel(rule.matchMode)} “${rule.trigger}”",
                style = MaterialTheme.typography.labelLarge,
                color = ArcadeSecondary,
            )
            Text(
                text = "→ ${rule.response}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = scopeLabel(rule.chatScope),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = stringResource(R.string.editor_delete),
                        tint = ArcadeError,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun modeLabel(mode: MatchMode): String = when (mode) {
    MatchMode.EXACT -> stringResource(R.string.mode_exact)
    MatchMode.CONTAINS -> stringResource(R.string.mode_contains)
    MatchMode.STARTS_WITH -> stringResource(R.string.mode_starts_with)
    MatchMode.REGEX -> stringResource(R.string.mode_regex)
}

@Composable
private fun scopeLabel(scope: ChatScope): String = when (scope) {
    ChatScope.ALL -> stringResource(R.string.rule_scope_all)
    ChatScope.PRIVATE_ONLY -> stringResource(R.string.rule_scope_private)
    ChatScope.GROUPS_ONLY -> stringResource(R.string.rule_scope_groups)
}
