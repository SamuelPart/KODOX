package com.walipet.ui.rules

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.walipet.data.repository.RuleRepository
import com.walipet.di.ServiceLocator
import com.walipet.engine.model.Rule
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Gestión de reglas lógicas (lista, activación, guardado y borrado). */
class RulesViewModel(
    private val ruleRepository: RuleRepository,
) : ViewModel() {

    val rules: StateFlow<List<Rule>> = ruleRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggleActive(rule: Rule, active: Boolean) {
        viewModelScope.launch { ruleRepository.setActive(rule.id, active) }
    }

    fun save(rule: Rule) {
        viewModelScope.launch { ruleRepository.save(rule) }
    }

    fun delete(rule: Rule) {
        viewModelScope.launch { ruleRepository.delete(rule) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                RulesViewModel(ServiceLocator.getInstance(app).ruleRepository)
            }
        }
    }
}
