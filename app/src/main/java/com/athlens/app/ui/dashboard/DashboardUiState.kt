package com.athlens.app.ui.dashboard

import com.athlens.app.domain.maturity.MaturityContextReport
import com.athlens.app.domain.model.AthleteProfile
import com.athlens.app.domain.model.GrowthMeasurement
import com.athlens.app.domain.model.Language
import com.athlens.app.domain.model.TrainingSession

data class DashboardUiState(
    val athleteProfile: AthleteProfile? = null,
    val currentTab: DashboardTab = DashboardTab.TRAIN,
    val isLoading: Boolean = true,
    val activeLanguage: Language = Language.ENGLISH,
    val recentSessions: List<TrainingSession> = emptyList(),
    val totalSessions: Int = 0,
    val averageFormScore: Double? = null,
    val growthMeasurements: List<GrowthMeasurement> = emptyList(),
    val maturityReport: MaturityContextReport? = null
)
