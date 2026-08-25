package com.walipet.di

import android.content.Context
import com.walipet.data.local.AppDatabase
import com.walipet.data.repository.AutomationLogRepository
import com.walipet.data.repository.PetRepository
import com.walipet.data.repository.RuleRepository
import com.walipet.data.repository.SettingsRepository
import com.walipet.engine.time.Clock
import com.walipet.service.AutomatedReplier
import com.walipet.service.NotificationHelper
import com.walipet.service.VoiceOver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Contenedor de dependencias manual (sin Hilt) — suficiente para una app de
 * este tamaño y mucho más fácil de auditar. Todos los singletons se crean
 * de forma perezosa y se comparten entre la UI y los servicios.
 */
class ServiceLocator private constructor(context: Context) {

    private val appContext = context.applicationContext
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: AppDatabase by lazy { AppDatabase.build(appContext) }

    private val petDao get() = database.petDao()
    private val ruleDao get() = database.ruleDao()
    private val logDao get() = database.automationLogDao()

    val petRepository: PetRepository by lazy { PetRepository(petDao) }
    val ruleRepository: RuleRepository by lazy { RuleRepository(ruleDao) }
    val automationLogRepository: AutomationLogRepository by lazy { AutomationLogRepository(logDao) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(appContext) }

    val notificationHelper: NotificationHelper by lazy { NotificationHelper(appContext) }

    val voiceOverSpeaker: VoiceOverSpeaker by lazy {
        VoiceOverSpeaker(appContext, settingsRepository)
    }

    val automatedReplier: AutomatedReplier by lazy {
        AutomatedReplier(
            context = appContext,
            petRepository = petRepository,
            ruleRepository = ruleRepository,
            logRepository = automationLogRepository,
            notificationHelper = notificationHelper,
            clock = Clock.SYSTEM,
        )
    }

    /** Voz en off: lee en voz alta los mensajes entrantes de WhatsApp. */
    val voiceOver: VoiceOver by lazy { VoiceOver(appContext) }

    /** Siembra los datos iniciales (mascota + reglas de ejemplo) en primer uso. */
    private fun seedIfNeeded() {
        appScope.launch {
            petRepository.ensureSeeded()
            ruleRepository.ensureSeeded()
        }
        // Calienta el TTS para que la primera lectura no se pierda.
        voiceOverSpeaker
    }

    companion object {
        @Volatile
        private var instance: ServiceLocator? = null

        fun getInstance(context: Context): ServiceLocator =
            instance ?: synchronized(this) {
                instance ?: ServiceLocator(context).also {
                    instance = it
                    it.seedIfNeeded()
                }
            }
    }
}
