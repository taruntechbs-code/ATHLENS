package com.athlens.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.athlens.app.data.repository.AthleteRepository
import com.athlens.app.data.repository.TrainingRepository
import com.athlens.app.domain.maturity.MaturityAssessmentEngine
import com.athlens.app.domain.model.GrowthMeasurement
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
import java.util.UUID

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
                    trainingRepository.getAverageScore(profile.id),
                    athleteRepository.getGrowthMeasurements(profile.id)
                ) { args ->
                    val tab = args[0] as DashboardTab
                    val manualLang = args[1] as? Language
                    @Suppress("UNCHECKED_CAST")
                    val sessions = args[2] as List<com.athlens.app.domain.model.TrainingSession>
                    val count = args[3] as Int
                    val avgScore = args[4] as? Double
                    @Suppress("UNCHECKED_CAST")
                    val measurements = args[5] as List<GrowthMeasurement>

                    val resolvedLang = manualLang
                        ?: Language.fromCode(profile.languageCode)

                    val maturityReport = MaturityAssessmentEngine.assess(
                        dateOfBirthMillis = profile.dateOfBirth,
                        measurements = measurements,
                        sessions = sessions
                    )

                    DashboardUiState(
                        athleteProfile = profile,
                        currentTab = tab,
                        isLoading = false,
                        activeLanguage = resolvedLang,
                        recentSessions = sessions,
                        totalSessions = count,
                        averageFormScore = avgScore,
                        growthMeasurements = measurements,
                        maturityReport = maturityReport
                    )
                }
            } else {
                combine(_selectedTab, _manualLanguage) { tab, manualLang ->
                    DashboardUiState(
                        athleteProfile = null,
                        currentTab = tab,
                        isLoading = false,
                        activeLanguage = manualLang ?: Language.ENGLISH
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
        val next = when (current) {
            Language.ENGLISH -> Language.TAMIL
            Language.TAMIL -> Language.HINDI
            Language.HINDI -> Language.ENGLISH
        }
        _manualLanguage.update { next }

        viewModelScope.launch {
            athleteRepository.updateLanguage(next.code)
        }
    }

    fun logGrowthMeasurement(heightCm: Float, weightKg: Float) {
        val profile = uiState.value.athleteProfile ?: return
        viewModelScope.launch {
            athleteRepository.recordGrowthMeasurement(
                GrowthMeasurement(
                    id = UUID.randomUUID().toString(),
                    athleteId = profile.id,
                    heightCm = heightCm,
                    weightKg = weightKg,
                    recordedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
