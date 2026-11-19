package com.athlens.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.athlens.app.data.repository.AthleteRepository
import com.athlens.app.domain.model.AthleteProfile
import com.athlens.app.domain.model.Language
import com.athlens.app.domain.model.PlayingPosition
import com.athlens.app.domain.model.Role
import com.athlens.app.domain.model.Sport
import com.athlens.app.ui.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class OnboardingViewModel(
    private val athleteRepository: AthleteRepository
) : ViewModel() {

    private val defaultDob = DateUtils.defaultGrassrootsDobMillis()

    private val _uiState = MutableStateFlow(
        OnboardingUiState(
            dateOfBirthMillis = defaultDob,
            calculatedAge = DateUtils.calculateAge(defaultDob),
            isMinor = DateUtils.isMinor(defaultDob)
        )
    )
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun setLanguage(language: Language) {
        _uiState.update { it.copy(language = language) }
    }

    fun setRole(role: Role) {
        _uiState.update { it.copy(selectedRole = role) }
    }

    fun setFullName(name: String) {
        _uiState.update { it.copy(fullName = name, errorMessage = null) }
    }

    fun setDateOfBirth(millis: Long) {
        val age = DateUtils.calculateAge(millis)
        val minor = DateUtils.isMinor(millis)
        _uiState.update {
            it.copy(
                dateOfBirthMillis = millis,
                calculatedAge = age,
                isMinor = minor,
                guardianConsentGiven = if (!minor) false else it.guardianConsentGiven,
                errorMessage = null
            )
        }
    }

    fun setDistrict(district: String) {
        _uiState.update { it.copy(district = district, errorMessage = null) }
    }

    fun setSport(sport: Sport) {
        val defaultPosition = PlayingPosition.positionsFor(sport).first().id
        _uiState.update {
            it.copy(
                selectedSport = sport,
                selectedPosition = defaultPosition
            )
        }
    }

    fun setPosition(positionId: String) {
        _uiState.update { it.copy(selectedPosition = positionId) }
    }

    fun setGuardianName(name: String) {
        _uiState.update { it.copy(guardianName = name, errorMessage = null) }
    }

    fun setGuardianConsent(consent: Boolean) {
        _uiState.update { it.copy(guardianConsentGiven = consent, errorMessage = null) }
    }

    fun goToNextStep() {
        _uiState.update {
            when (it.currentStep) {
                1 -> it.copy(currentStep = 2)
                2 -> it.copy(currentStep = 3)
                else -> it
            }
        }
    }

    fun goToPreviousStep() {
        _uiState.update {
            when (it.currentStep) {
                3 -> it.copy(currentStep = 2)
                2 -> it.copy(currentStep = 1)
                else -> it
            }
        }
    }

    fun submitProfile() {
        val current = _uiState.value
        if (!current.isStep2Valid) {
            _uiState.update { it.copy(errorMessage = "Please complete all profile fields", currentStep = 2) }
            return
        }
        if (!current.isStep3Valid) {
            _uiState.update { it.copy(errorMessage = "Guardian name and consent are required for minor athletes") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val profile = AthleteProfile(
                id = UUID.randomUUID().toString(),
                role = current.selectedRole,
                fullName = current.fullName.trim(),
                dateOfBirth = current.dateOfBirthMillis,
                district = current.district,
                sport = current.selectedSport,
                playingPosition = current.selectedPosition,
                languageCode = current.language.code,
                isMinor = current.isMinor,
                guardianName = if (current.isMinor) current.guardianName.trim() else null,
                guardianConsentGiven = if (current.isMinor) current.guardianConsentGiven else false,
                createdAt = System.currentTimeMillis()
            )

            athleteRepository.saveProfile(profile)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, isProfileSaved = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.localizedMessage ?: "Failed to save profile to database"
                        )
                    }
                }
        }
    }
}
