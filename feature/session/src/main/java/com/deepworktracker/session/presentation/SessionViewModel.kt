package com.deepworktracker.session.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.common.result.Result
import com.deepworktracker.session.domain.usecase.EndSessionUseCase
import com.deepworktracker.session.domain.usecase.GetActiveSessionUseCase
import com.deepworktracker.session.domain.usecase.GetRecentCategoriesUseCase
import com.deepworktracker.session.domain.usecase.GetRecentGoalsUseCase
import com.deepworktracker.session.domain.usecase.GetRecentTagsUseCase
import com.deepworktracker.session.domain.usecase.StartSessionUseCase
import com.deepworktracker.domain.model.CategoryRule
import com.deepworktracker.domain.repository.CategoryRuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val startSessionUseCase: StartSessionUseCase,
    private val endSessionUseCase: EndSessionUseCase,
    private val getActiveSessionUseCase: GetActiveSessionUseCase,
    private val getRecentGoalsUseCase: GetRecentGoalsUseCase,
    private val getRecentCategoriesUseCase: GetRecentCategoriesUseCase,
    private val getRecentTagsUseCase: GetRecentTagsUseCase,
    private val categoryRuleRepository: CategoryRuleRepository,
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()
    
    private var timerJob: Job? = null
    private var rulesJob: Job? = null
    private var rules: List<CategoryRule> = emptyList()
    
    init {
        observeActiveSession()
        loadRecents()
        observeRules()
    }
    
    fun startSession(goal: String, category: String?, tag: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val nowHour = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .hour
            val rule = pickRuleMatch(rules, goal, nowHour)
            val finalCategory = category ?: rule?.category
            val finalTag = tag ?: rule?.tag
            
            when (val result = startSessionUseCase(goal, finalCategory, finalTag)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            session = result.data,
                            isTracking = true,
                            isLoading = false
                        )
                    }
                    startTimer()
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            error = result.exception,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun loadRecents(){
        viewModelScope.launch {
            val recentGoals = getRecentGoalsUseCase()
            val recentCategories = getRecentCategoriesUseCase()
            val recentTags = getRecentTagsUseCase()
            _uiState.update {
                it.copy(
                    recentSession = recentGoals,
                    recentCategories = recentCategories,
                    recentTags = recentTags
                )
            }
    }}

    private fun observeRules() {
        rulesJob?.cancel()
        rulesJob = viewModelScope.launch {
            categoryRuleRepository.observeRules().collect { list ->
                rules = list
                // keep suggestions stable; they will be recomputed on goal updates
            }
        }
    }

    fun suggestForGoal(goal: String) {
        val trimmed = goal.trim()
        if (trimmed.isEmpty()) {
            _uiState.update { it.copy(suggestedCategory = null, suggestedTag = null) }
            return
        }

        val nowHour = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .hour

        val fromRules = pickRuleMatch(rules, trimmed, nowHour)
        val suggestedCategory = fromRules?.category ?: _uiState.value.recentCategories.firstOrNull()
        val suggestedTag = fromRules?.tag ?: _uiState.value.recentTags.firstOrNull()

        _uiState.update { it.copy(suggestedCategory = suggestedCategory, suggestedTag = suggestedTag) }
    }
    
    fun endSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = endSessionUseCase()) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            session = result.data,
                            isTracking = false,
                            isLoading = false
                        )
                    }
                    stopTimer()
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            error = result.exception,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
    
    private fun observeActiveSession() {
        viewModelScope.launch {
            getActiveSessionUseCase().collect { session ->
                _uiState.update { it.copy(session = session) }
                if (session != null && !_uiState.value.isTracking) {
                    _uiState.update { it.copy(isTracking = true) }
                    startTimer()
                } else if (session == null) {
                    _uiState.update { it.copy(isTracking = false) }
                    stopTimer()
                }
            }
        }
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isTracking) {
                val session = _uiState.value.session
                if (session != null) {
                    val elapsed = Clock.System.now() - session.startTime
                    _uiState.update { it.copy(elapsedTime = elapsed) }
                }
                delay(1000) // Update every second
            }
        }
    }
    
    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        rulesJob?.cancel()
    }
}

private fun pickRuleMatch(rules: List<CategoryRule>, goal: String, hour: Int): CategoryRule? {
    val lower = goal.lowercase()
    fun matchesHour(r: CategoryRule): Boolean {
        val start = r.startHour
        val end = r.endHour
        if (start == null || end == null) return true
        if (start == end) return hour == start
        return if (start < end) {
            hour in start..end
        } else {
            // wrap-around, e.g. 22..2
            hour >= start || hour <= end
        }
    }

    fun keywordScore(r: CategoryRule): Int {
        val k = r.keyword?.trim()?.lowercase().orEmpty()
        if (k.isEmpty()) return 0
        return if (lower.contains(k)) 2 else -1
    }

    return rules
        .asSequence()
        .filter { matchesHour(it) }
        .mapNotNull { r ->
            val ks = keywordScore(r)
            if (ks < 0) null else Triple(r, ks, r.priority)
        }
        .sortedWith(compareByDescending<Triple<CategoryRule, Int, Int>> { it.second }
            .thenByDescending { it.third })
        .map { it.first }
        .firstOrNull()
}
