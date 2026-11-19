package com.athlens.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.athlens.app.data.repository.AthleteRepository
import com.athlens.app.data.repository.TrainingRepository
import com.athlens.app.domain.model.Language
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    private val athleteRepository: AthleteRepository,
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(DashboardTab.TRAIN)
    private val _manualLanguage = MutableStateFlow<Language?>(null)

    val uiState: StateFlow<DashboardUiState> = athleteRepository.getActiveProfile()
        .flatMapLatest { profile ->
            if (profile != null) {
                combine(
                    _selectedTab,
                    _manualLanguage,
                    trainingRepository.getSessionsForAthlete(profile.id),
                    trainingRepository.getSessionCount(profile.id),
                    trainingRepository.getAverageScore(profile.id)
                ) { tab, manualLang, sessions, count, avgScore ->
                    val resolvedLang = manualLang
                        ?: Language.fromCode(profile.languageCode)

                    DashboardUiState(
                        athleteProfile = profile,
                        currentTab = tab,
                        isLoading = false,
                        activeLanguage = resolvedLang,
                        recentSessions = sessions,
                        totalSessions = count,
                        averageFormScore = avgScore
                    )
                }
            } else {
                combine(_selectedTab, _manualLanguage) { tab, manualLang ->
                    DashboardUiState(
                        athleteProfile = null,
                        currentTab = tab,
                        isLoading = false,
                        activeLanguage = manualLang ?: Language.TAMIL
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState(isLoading = true)
        )

    fun selectTab(tab: DashboardTab) {
        _selectedTab.update { tab }
    }

    fun toggleLanguage() {
        val current = uiState.value.activeLanguage
        val next = if (current == Language.TAMIL) Language.ENGLISH else Language.TAMIL
        _manualLanguage.update { next }

        viewModelScope.launch {
            athleteRepository.updateLanguage(next.code)
        }
    }
}
