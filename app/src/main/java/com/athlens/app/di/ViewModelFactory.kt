package com.athlens.app.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.athlens.app.ui.coach.CameraCoachViewModel
import com.athlens.app.ui.dashboard.DashboardViewModel
import com.athlens.app.ui.onboarding.OnboardingViewModel

class ViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> {
                OnboardingViewModel(appContainer.athleteRepository) as T
            }
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(
                    appContainer.athleteRepository,
                    appContainer.trainingRepository,
                    appContainer.passportCryptoManager,
                    appContainer.createNearbyExchangeManager(),
                    appContainer.context
                ) as T
            }
            modelClass.isAssignableFrom(CameraCoachViewModel::class.java) -> {
                CameraCoachViewModel(
                    appContainer.athleteRepository,
                    appContainer.trainingRepository,
                    appContainer.createVoiceCoachManager()
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
