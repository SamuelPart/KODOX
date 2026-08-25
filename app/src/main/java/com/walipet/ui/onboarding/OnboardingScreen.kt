package com.walipet.ui.onboarding

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.walipet.R
import com.walipet.engine.model.EvolutionStage
import com.walipet.engine.model.PetMood
import com.walipet.service.WaliPetNotificationListener
import com.walipet.ui.components.PixelArtPet
import com.walipet.ui.components.PixelCard
import com.walipet.ui.theme.ArcadePrimary
import com.walipet.ui.theme.ArcadeSecondary
import com.walipet.ui.theme.ArcadeStreak
import kotlinx.coroutines.delay

/**
 * Onboarding por pasos: explica y guía los permisos especiales que necesita
 * WaliPet (acceso a notificaciones, notificaciones de la app y —opcional—
 * exención del ahorro agresivo de batería).
 */
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
) {
    val context = LocalContext.current
    var step by remember { mutableIntStateOf(0) }
    val totalSteps = 5

    var listenerEnabled by remember {
        mutableStateOf(WaliPetNotificationListener.isEnabled(context))
    }

    // Sondeo del permiso especial mientras el usuario está en Ajustes.
    LaunchedEffect(Unit) {
        while (true) {
            listenerEnabled = WaliPetNotificationListener.isEnabled(context)
            delay(1_500)
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    fun openNotificationListenerSettings() {
        runCatching {
            context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
    }

    fun openBatteryOptimizationSettings() {
        runCatching {
            val intent = Intent(
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                Uri.parse("package:${context.packageName}"),
            )
            context.startActivity(intent)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        // Indicador de pasos.
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(totalSteps) { index ->
                Box(
                    modifier = Modifier
                        .size(width = 22.dp, height = 8.dp)
                        .background(
                            if (index <= step) ArcadeSecondary else MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.shapes.extraSmall,
                        ),
                )
            }
        }

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            when (step) {
                0 -> WelcomeStep()
                1 -> ListenerStep(enabled = listenerEnabled, onOpenSettings = ::openNotificationListenerSettings)
                2 -> NotificationsStep(onRequest = { notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) })
                3 -> BatteryStep(onRequest = ::openBatteryOptimizationSettings)
                else -> FinishStep()
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (step < totalSteps - 1) {
                Button(
                    onClick = { step += 1 },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ArcadePrimary, contentColor = MaterialTheme.colorScheme.onPrimary),
                ) {
                    Text(
                        text = stringResource(R.string.onb_next),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(vertical = 6.dp),
                    )
                }
                if (step > 0) {
                    OutlinedButton(onClick = { step -= 1 }, modifier = Modifier.fillMaxWidth()) {
                        Text(text = stringResource(R.string.onb_back), style = MaterialTheme.typography.labelLarge)
                    }
                }
            } else {
                Button(
                    onClick = onFinished,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ArcadeSecondary, contentColor = MaterialTheme.colorScheme.onSecondary),
                ) {
                    Text(
                        text = stringResource(R.string.onb_start),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(vertical = 6.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun StepScaffold(title: String, body: String, content: @Composable () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        content()
        Text(text = title, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun WelcomeStep() {
    StepScaffold(
        title = stringResource(R.string.onb_welcome_title),
        body = stringResource(R.string.onb_welcome_body),
    ) {
        PixelArtPet(
            stage = EvolutionStage.BABY,
            mood = PetMood.HAPPY,
            modifier = Modifier.size(160.dp),
        )
    }
}

@Composable
private fun ListenerStep(enabled: Boolean, onOpenSettings: () -> Unit) {
    StepScaffold(
        title = stringResource(R.string.onb_listener_title),
        body = stringResource(R.string.onb_listener_body),
    ) {
        PixelArtPet(stage = EvolutionStage.EGG, mood = PetMood.CONTENT, modifier = Modifier.size(120.dp))
        if (enabled) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = ArcadePrimary,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = stringResource(R.string.onb_listener_done),
                    style = MaterialTheme.typography.labelLarge,
                    color = ArcadePrimary,
                )
            }
        } else {
            Button(onClick = onOpenSettings, colors = ButtonDefaults.buttonColors(containerColor = ArcadePrimary)) {
                Text(text = stringResource(R.string.onb_listener_action), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun NotificationsStep(onRequest: () -> Unit) {
    StepScaffold(
        title = stringResource(R.string.onb_notifications_title),
        body = stringResource(R.string.onb_notifications_body),
    ) {
        PixelArtPet(stage = EvolutionStage.YOUNG, mood = PetMood.HAPPY, modifier = Modifier.size(120.dp))
        if (Build.VERSION.SDK_INT >= 33) {
            Button(onClick = onRequest, colors = ButtonDefaults.buttonColors(containerColor = ArcadePrimary)) {
                Text(text = stringResource(R.string.onb_notifications_action), style = MaterialTheme.typography.labelLarge)
            }
        } else {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = ArcadePrimary,
                modifier = Modifier.size(40.dp),
            )
        }
    }
}

@Composable
private fun BatteryStep(onRequest: () -> Unit) {
    StepScaffold(
        title = stringResource(R.string.onb_battery_title),
        body = stringResource(R.string.onb_battery_body),
    ) {
        PixelArtPet(stage = EvolutionStage.EPIC, mood = PetMood.CONTENT, modifier = Modifier.size(120.dp))
        Button(onClick = onRequest, colors = ButtonDefaults.buttonColors(containerColor = ArcadeStreak)) {
            Text(text = stringResource(R.string.onb_battery_action), style = MaterialTheme.typography.labelLarge)
        }
        Text(
            text = stringResource(R.string.onb_battery_skip),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun FinishStep() {
    PixelCard(modifier = Modifier.fillMaxWidth()) {
        StepScaffold(
            title = stringResource(R.string.onb_finish_title),
            body = stringResource(R.string.onb_finish_body),
        ) {
            PixelArtPet(stage = EvolutionStage.BABY, mood = PetMood.HAPPY, modifier = Modifier.size(140.dp))
        }
    }
}
