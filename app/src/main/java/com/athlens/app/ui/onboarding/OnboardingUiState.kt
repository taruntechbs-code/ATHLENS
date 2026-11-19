package com.athlens.app.ui.onboarding

import com.athlens.app.domain.model.Language
import com.athlens.app.domain.model.Role
import com.athlens.app.domain.model.Sport

data class OnboardingUiState(
    val currentStep: Int = 1,
    val language: Language = Language.ENGLISH,
    val selectedRole: Role = Role.ATHLETE,
    val fullName: String = "",
    val dateOfBirthMillis: Long = 0L,
    val district: String = "Coimbatore",
    val selectedSport: Sport = Sport.KABADDI,
    val selectedPosition: String = "RAIDER",
    val isMinor: Boolean = false,
    val calculatedAge: Int = 0,
    val guardianName: String = "",
    val guardianConsentGiven: Boolean = false,
    val isSaving: Boolean = false,
    val isProfileSaved: Boolean = false,
    val errorMessage: String? = null
) {
    val isStep1Valid: Boolean
        get() = true

    val isStep2Valid: Boolean
        get() = fullName.isNotBlank() && dateOfBirthMillis > 0L && district.isNotBlank()

    val isStep3Valid: Boolean
        get() = if (isMinor) {
            guardianName.isNotBlank() && guardianConsentGiven
        } else {
            true
        }
}
