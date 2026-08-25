package com.walipet.ui.codex

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.walipet.data.repository.AutomationLogRepository
import com.walipet.data.repository.PetRepository
import com.walipet.data.repository.RuleRepository
import com.walipet.di.ServiceLocator
import com.walipet.engine.model.Achievement
import com.walipet.engine.model.AchievementContext
import com.walipet.engine.model.PetState
import com.walipet.engine.pet.AchievementChecker
import com.walipet.engine.stats.StatsCalculator
import com.walipet.engine.time.Clock
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** Estado del Codex: evolución (nivel) + logros desbloqueados. */
class CodexViewModel(
    petRepository: PetRepository,
    logRepository: AutomationLogRepository,
    ruleRepository: RuleRepository,
) : ViewModel() {

    data class UiState(
        val pet: PetState = PetState(),
        val achievements: List<Achievement> = AchievementChecker.ACHIEVEMENTS,
        val unlockedIds: Set<String> = emptySet(),
        val unlockedCount: Int = 0,
    )

    val uiState: StateFlow<UiState> = combine(
        petRepository.observe(),
        logRepository.observeAll(),
        ruleRepository.observeAll(),
    ) { pet, logs, rules ->
        val today = Clock.SYSTEM.today()
        val stats = StatsCalculator.compute(logs, rules, today)
        val unlocked = AchievementChecker.unlocked(AchievementContext(pet, stats, today))
        UiState(
            pet = pet,
            achievements = AchievementChecker.ACHIEVEMENTS,
            unlockedIds = unlocked.map { it.id }.toSet(),
            unlockedCount = unlocked.size,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val sl = ServiceLocator.getInstance(app)
                CodexViewModel(sl.petRepository, sl.automationLogRepository, sl.ruleRepository)
            }
        }
    }
}
