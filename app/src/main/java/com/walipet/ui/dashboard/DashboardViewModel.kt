package com.walipet.ui.dashboard

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.walipet.data.repository.AutomationLogRepository
import com.walipet.data.repository.PetRepository
import com.walipet.data.repository.RuleRepository
import com.walipet.data.repository.SettingsRepository
import com.walipet.di.ServiceLocator
import com.walipet.engine.model.AutomationLog
import com.walipet.engine.model.PetMood
import com.walipet.engine.model.PetState
import com.walipet.engine.pet.PetMoodResolver
import com.walipet.engine.pet.StreakEngine
import com.walipet.engine.time.Clock
import com.walipet.engine.voice.VoiceOverPlanner
import com.walipet.service.VoiceOverSpeaker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** Estado del Dashboard: mascota + ánimo + métricas derivadas + últimos logs. */
class DashboardViewModel(
    petRepository: PetRepository,
    logRepository: AutomationLogRepository,
    ruleRepository: RuleRepository,
    private val settingsRepository: SettingsRepository,
    private val voiceOverSpeaker: VoiceOverSpeaker,
) : ViewModel() {

    data class UiState(
        val pet: PetState = PetState(),
        val mood: PetMood = PetMood.CONTENT,
        val energy: Int = PetState.MAX_ENERGY,
        val happiness: Int = PetState.MAX_HAPPINESS,
        val activeRuleCount: Int = 0,
        val recentLogs: List<AutomationLog> = emptyList(),
        val voiceOverEnabled: Boolean = true,
    )

    val uiState: StateFlow<UiState> = combine(
        petRepository.observe(),
        logRepository.observeRecent(limit = 40),
        ruleRepository.observeAll(),
        settingsRepository.voiceOverEnabledFlow,
    ) { pet, logs, rules, voiceOver ->
        val today = Clock.SYSTEM.today()
        val missed = StreakEngine.missedDays(pet.lastAutomationDate, today)
        UiState(
            pet = pet,
            mood = PetMoodResolver.mood(pet, today),
            energy = StreakEngine.energyFor(missed),
            happiness = StreakEngine.happinessFor(pet.streakDays, missed),
            activeRuleCount = rules.count { it.isActive },
            recentLogs = logs,
            voiceOverEnabled = voiceOver,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    fun toggleVoiceOver() {
        val enabled = !settingsRepository.voiceOverEnabled
        settingsRepository.voiceOverEnabled = enabled
        if (!enabled) {
            voiceOverSpeaker.stop()
        } else {
            speakSentReplies()
        }
    }

    /**
     * Lee en voz alta los mensajes **enviados** (respuestas automáticas):
     * texto exacto si son de la última hora; el resto, «N mensajes de M chats».
     */
    fun speakSentReplies() {
        if (!settingsRepository.voiceOverEnabled) return
        val lines = VoiceOverPlanner.planSentReplies(
            logs = uiState.value.recentLogs,
            nowEpochMilli = System.currentTimeMillis(),
        )
        voiceOverSpeaker.speak(lines, dedupKey = "sent-replies")
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val sl = ServiceLocator.getInstance(app)
                DashboardViewModel(
                    petRepository = sl.petRepository,
                    logRepository = sl.automationLogRepository,
                    ruleRepository = sl.ruleRepository,
                    settingsRepository = sl.settingsRepository,
                    voiceOverSpeaker = sl.voiceOverSpeaker,
                )
            }
        }
    }
}
