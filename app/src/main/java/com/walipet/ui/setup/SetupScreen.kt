package com.walipet.ui.setup

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.walipet.R
import com.walipet.service.SetupChecker
import com.walipet.service.SetupStatus
import com.walipet.ui.components.PixelCard
import com.walipet.ui.components.PixelSpacer
import com.walipet.ui.theme.ArcadeError
import com.walipet.ui.theme.ArcadePrimary
import com.walipet.ui.theme.ArcadeSecondary
import kotlinx.coroutines.delay

/**
 * "Estado del sistema": revisa en vivo los permisos que necesita WaliPet y, con
 * un toque, abre el ajuste correcto. Pensado para que cualquiera pueda dejar la
 * app lista sin pelearse con los ajustes del teléfono.
 */
@Composable
fun SetupScreen(
    onFinished: () -> Unit,
) {
    val context = LocalContext.current
    var status by remember { mutableStateOf(SetupChecker.status(context)) }
    var refreshTick by remember { mutableIntStateOf(0) }

    // Re-sondeo automático: al volver de Ajustes el estado se actualiza solo.
    LaunchedEffect(Unit) {
        while (true) {
            status = SetupChecker.status(context)
            delay(1_500)
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { refreshTick++ }

    LaunchedEffect(refreshTick) {
        status = SetupChecker.status(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(text = stringResource(R.string.setup_title), style = MaterialTheme.typography.headlineMedium)
        PixelSpacer(4.dp)
        Text(
            text = stringResource(R.string.setup_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        PixelSpacer(16.dp)

        SetupRow(
            title = stringResource(R.string.setup_listener_title),
            body = stringResource(R.string.setup_listener_body),
            ok = status.listenerEnabled,
            actionLabel = stringResource(R.string.setup_action_open),
            onAction = { SetupChecker.openNotificationListenerSettings(context) },
        )

        if (Build.VERSION.SDK_INT >= 33) {
            SetupRow(
                title = stringResource(R.string.setup_notifications_title),
                body = stringResource(R.string.setup_notifications_body),
                ok = status.notificationsGranted,
                actionLabel = stringResource(R.string.setup_action_open),
                onAction = { notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
            )
        }

        SetupRow(
            title = stringResource(R.string.setup_battery_title),
            body = stringResource(R.string.setup_battery_body),
            ok = status.batteryExempt,
            actionLabel = stringResource(R.string.setup_action_exempt),
            onAction = { SetupChecker.requestBatteryExemption(context) },
        )

        PixelSpacer(8.dp)

        if (status.allGood) {
            PixelCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ArcadePrimary,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = ArcadePrimary,
                        modifier = Modifier.size(44.dp),
                    )
                    Text(
                        text = stringResource(R.string.setup_done_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = ArcadePrimary,
                    )
                    Text(
                        text = stringResource(R.string.setup_done_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
        } else {
            PixelCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ArcadeError,
            ) {
                Text(
                    text = stringResource(R.string.setup_incomplete),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ArcadeError,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        PixelSpacer(12.dp)

        Button(
            onClick = onFinished,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ArcadeSecondary, contentColor = MaterialTheme.colorScheme.onSecondary),
        ) {
            Text(
                text = stringResource(R.string.setup_finish),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(vertical = 6.dp),
            )
        }
    }
}

@Composable
private fun SetupRow(
    title: String,
    body: String,
    ok: Boolean,
    actionLabel: String,
    onAction: () -> Unit,
) {
    PixelCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        borderColor = if (ok) ArcadePrimary else ArcadeError,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (ok) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                contentDescription = null,
                tint = if (ok) ArcadePrimary else ArcadeError,
                modifier = Modifier.size(24.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (ok) ArcadePrimary else ArcadeError,
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (!ok) {
                OutlinedButton(onClick = onAction, modifier = Modifier.padding(start = 4.dp)) {
                    Text(text = actionLabel, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
