package com.walipet.ui.rules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.walipet.R
import com.walipet.engine.model.ChatScope
import com.walipet.engine.model.MatchMode
import com.walipet.engine.model.Rule
import com.walipet.engine.rule.MessageMatcher
import com.walipet.ui.components.PixelSpacer
import com.walipet.ui.theme.ArcadeBackground
import com.walipet.ui.theme.ArcadeError
import com.walipet.ui.theme.ArcadePrimary
import com.walipet.ui.theme.ArcadeSecondary
import com.walipet.ui.theme.ArcadeSurfaceVariant
import com.walipet.ui.theme.ArcadeOnSurfaceDim

/**
 * Editor de reglas (pantalla completa): “Si el mensaje [modo] «disparador»
 * → responde «respuesta»”, con ámbito de chats y validación en vivo.
 */
@Composable
fun RuleEditorScreen(
    rule: Rule,
    onSave: (Rule) -> Unit,
    onDelete: (Rule) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember(rule.id) { mutableStateOf(rule.name) }
    var trigger by remember(rule.id) { mutableStateOf(rule.trigger) }
    var response by remember(rule.id) { mutableStateOf(rule.response) }
    var matchMode by remember(rule.id) { mutableStateOf(rule.matchMode) }
    var chatScope by remember(rule.id) { mutableStateOf(rule.chatScope) }
    var caseSensitive by remember(rule.id) { mutableStateOf(rule.caseSensitive) }

    var errorName by remember(rule.id) { mutableStateOf(false) }
    var errorTrigger by remember(rule.id) { mutableStateOf(false) }
    var errorResponse by remember(rule.id) { mutableStateOf(false) }
    var errorRegex by remember(rule.id) { mutableStateOf(false) }

    val isNew = rule.id == 0L

    fun validateAndSave() {
        errorName = name.isBlank()
        errorTrigger = trigger.isBlank()
        errorResponse = response.isBlank()
        errorRegex = matchMode == MatchMode.REGEX && !MessageMatcher.isRegexValid(trigger.trim())
        if (errorName || errorTrigger || errorResponse || errorRegex) return

        onSave(
            rule.copy(
                name = name.trim(),
                trigger = trigger.trim(),
                response = response.trim(),
                matchMode = matchMode,
                chatScope = chatScope,
                caseSensitive = caseSensitive,
            ),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ArcadeBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(if (isNew) R.string.editor_title_new else R.string.editor_title_edit),
                style = MaterialTheme.typography.headlineMedium,
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        PixelSpacer(12.dp)

        EditorField(
            label = stringResource(R.string.editor_name),
            value = name,
            onValueChange = { name = it },
            placeholder = stringResource(R.string.editor_name_hint),
            error = errorName,
            errorText = stringResource(R.string.editor_error_name),
        )
        PixelSpacer(12.dp)

        EditorField(
            label = stringResource(R.string.editor_trigger),
            value = trigger,
            onValueChange = { trigger = it },
            placeholder = stringResource(R.string.editor_trigger_hint),
            error = errorTrigger || errorRegex,
            errorText = if (errorRegex) stringResource(R.string.editor_error_regex) else stringResource(R.string.editor_error_trigger),
        )
        PixelSpacer(12.dp)

        // Modo de coincidencia.
        Text(text = stringResource(R.string.editor_mode), style = MaterialTheme.typography.labelLarge)
        PixelSpacer(6.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            MatchMode.entries.forEach { mode ->
                FilterChip(
                    selected = matchMode == mode,
                    onClick = { matchMode = mode },
                    label = { Text(modeLabel(mode), style = MaterialTheme.typography.labelMedium) },
                )
            }
        }
        PixelSpacer(10.dp)

        // Ámbito de chats.
        Text(text = stringResource(R.string.editor_scope), style = MaterialTheme.typography.labelLarge)
        PixelSpacer(6.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ChatScope.entries.forEach { scope ->
                FilterChip(
                    selected = chatScope == scope,
                    onClick = { chatScope = scope },
                    label = { Text(scopeLabel(scope), style = MaterialTheme.typography.labelMedium) },
                )
            }
        }
        PixelSpacer(10.dp)

        // Mayúsculas/minúsculas.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.editor_case_sensitive),
                style = MaterialTheme.typography.bodyMedium,
            )
            Switch(
                checked = caseSensitive,
                onCheckedChange = { caseSensitive = it },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = ArcadeSecondary,
                    checkedThumbColor = MaterialTheme.colorScheme.onSecondary,
                    uncheckedTrackColor = ArcadeSurfaceVariant,
                ),
            )
        }
        PixelSpacer(12.dp)

        EditorField(
            label = stringResource(R.string.editor_response),
            value = response,
            onValueChange = { response = it },
            placeholder = stringResource(R.string.editor_response_hint),
            error = errorResponse,
            errorText = stringResource(R.string.editor_error_response),
            minLines = 3,
        )
        PixelSpacer(20.dp)

        Button(
            onClick = { validateAndSave() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ArcadePrimary, contentColor = MaterialTheme.colorScheme.onPrimary),
        ) {
            Text(
                text = stringResource(R.string.editor_save),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(vertical = 6.dp),
            )
        }

        if (!isNew) {
            PixelSpacer(8.dp)
            TextButton(
                onClick = { onDelete(rule) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.editor_delete),
                    color = ArcadeError,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
        PixelSpacer(24.dp)
    }
}

@Composable
private fun EditorField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: Boolean,
    errorText: String,
    minLines: Int = 1,
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        PixelSpacer(4.dp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ArcadeOnSurfaceDim,
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            minLines = minLines,
            isError = error,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error) ArcadeError else ArcadePrimary,
                unfocusedBorderColor = ArcadeOnSurfaceDim,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = ArcadePrimary,
            ),
        )
        if (error) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.labelMedium,
                color = ArcadeError,
            )
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
