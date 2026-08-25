package com.walipet.ui.stats

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.walipet.data.repository.AutomationLogRepository
import com.walipet.data.repository.RuleRepository
import com.walipet.di.ServiceLocator
import com.walipet.engine.model.AutomationLog
import com.walipet.engine.model.AutomationStats
import com.walipet.engine.stats.StatsCalculator
import com.walipet.engine.time.Clock
import java.time.LocalDate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** Estado del panel de estadísticas: métricas agregadas + gráfico + feed. */
class StatsViewModel(
    logRepository: AutomationLogRepository,
    ruleRepository: RuleRepository,
) : ViewModel() {

    data class UiState(
        val stats: AutomationStats = AutomationStats(),
        val dailyCounts: List<Pair<LocalDate, Int>> = emptyList(),
        val feed: List<AutomationLog> = emptyList(),
    )

    val uiState: StateFlow<UiState> = combine(
        logRepository.observeAll(),
        ruleRepository.observeAll(),
    ) { logs, rules ->
        val today = Clock.SYSTEM.today()
        UiState(
            stats = StatsCalculator.compute(logs, rules, today),
            dailyCounts = StatsCalculator.dailyCounts(logs, today, days = 7),
            feed = logs.take(40),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val sl = ServiceLocator.getInstance(app)
                StatsViewModel(sl.automationLogRepository, sl.ruleRepository)
            }
        }
    }
}
