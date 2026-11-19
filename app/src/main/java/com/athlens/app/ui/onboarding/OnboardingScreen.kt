package com.athlens.app.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.athlens.app.domain.model.Language
import com.athlens.app.ui.onboarding.components.Step1LanguageRole
import com.athlens.app.ui.onboarding.components.Step2ProfileDetails
import com.athlens.app.ui.onboarding.components.Step3MinorConsent
import com.athlens.app.ui.theme.BorderDark
import com.athlens.app.ui.theme.CardNavy
import com.athlens.app.ui.theme.DeepNavy
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextWhite
import com.athlens.app.ui.util.BilingualStrings

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.isProfileSaved) {
        if (uiState.isProfileSaved) {
            onOnboardingComplete()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DeepNavy,
        topBar = {
            OnboardingHeader(
                currentStep = uiState.currentStep,
                totalSteps = 3,
                language = uiState.language
            )
        },
        bottomBar = {
            OnboardingBottomBar(
                currentStep = uiState.currentStep,
                language = uiState.language,
                isNextEnabled = when (uiState.currentStep) {
                    1 -> uiState.isStep1Valid
                    2 -> uiState.isStep2Valid
                    3 -> uiState.isStep3Valid
                    else -> false
                },
                isSaving = uiState.isSaving,
                onBack = { viewModel.goToPreviousStep() },
                onNext = {
                    if (uiState.currentStep < 3) {
                        viewModel.goToNextStep()
                    } else {
                        viewModel.submitProfile()
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            when (uiState.currentStep) {
                1 -> Step1LanguageRole(
                    selectedLanguage = uiState.language,
                    selectedRole = uiState.selectedRole,
                    onLanguageSelected = { viewModel.setLanguage(it) },
                    onRoleSelected = { viewModel.setRole(it) }
                )
                2 -> Step2ProfileDetails(
                    language = uiState.language,
                    fullName = uiState.fullName,
                    dateOfBirthMillis = uiState.dateOfBirthMillis,
                    calculatedAge = uiState.calculatedAge,
                    isMinor = uiState.isMinor,
                    district = uiState.district,
                    selectedSport = uiState.selectedSport,
                    selectedPosition = uiState.selectedPosition,
                    onFullNameChanged = { viewModel.setFullName(it) },
                    onDateOfBirthChanged = { viewModel.setDateOfBirth(it) },
                    onDistrictChanged = { viewModel.setDistrict(it) },
                    onSportChanged = { viewModel.setSport(it) },
                    onPositionChanged = { viewModel.setPosition(it) }
                )
                3 -> Step3MinorConsent(
                    language = uiState.language,
                    isMinor = uiState.isMinor,
                    calculatedAge = uiState.calculatedAge,
                    guardianName = uiState.guardianName,
                    guardianConsentGiven = uiState.guardianConsentGiven,
                    onGuardianNameChanged = { viewModel.setGuardianName(it) },
                    onGuardianConsentChanged = { viewModel.setGuardianConsent(it) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OnboardingHeader(
    currentStep: Int,
    totalSteps: Int,
    language: Language
) {
    Surface(
        color = DeepNavy,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ATHLENS",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ElectricEmerald
                )
                Text(
                    text = when (language) {
                        Language.ENGLISH -> "Step $currentStep of $totalSteps"
                        Language.TAMIL -> "படி $currentStep / $totalSteps"
                        Language.HINDI -> "चरण $currentStep / $totalSteps"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = TextGray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Step Progress Indicator
            LinearProgressIndicator(
                progress = { currentStep.toFloat() / totalSteps.toFloat() },
                color = ElectricEmerald,
                trackColor = CardNavy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
private fun OnboardingBottomBar(
    currentStep: Int,
    language: Language,
    isNextEnabled: Boolean,
    isSaving: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Surface(
        color = CardNavy,
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep > 1) {
                OutlinedButton(
                    onClick = onBack,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextWhite
                    ),
                    border = BorderStroke(1.dp, BorderDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.heightIn(min = 44.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(BilingualStrings.getBackButton(language))
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Button(
                onClick = onNext,
                enabled = isNextEnabled && !isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricEmerald,
                    contentColor = DeepNavy,
                    disabledContainerColor = BorderDark,
                    disabledContentColor = TextGray
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.heightIn(min = 44.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = DeepNavy,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    val label = if (currentStep == 3) {
                        BilingualStrings.getCreateProfileButton(language)
                    } else {
                        BilingualStrings.getNextButton(language)
                    }
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (currentStep == 3) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
