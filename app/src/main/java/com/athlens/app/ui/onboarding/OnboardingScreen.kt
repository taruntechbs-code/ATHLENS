package com.athlens.app.ui.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.athlens.app.domain.model.Language
import com.athlens.app.ui.onboarding.components.Step1LanguageRole
import com.athlens.app.ui.onboarding.components.Step2ProfileDetails
import com.athlens.app.ui.onboarding.components.Step3MinorConsent
import com.athlens.app.ui.theme.AmbientGlassBackground
import com.athlens.app.ui.theme.DeepNavy
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.GlassBorderStandard
import com.athlens.app.ui.theme.LiquidGlassButton
import com.athlens.app.ui.theme.OceanTeal
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextMuted
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

    AmbientGlassBackground(modifier = modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent,
            topBar = {
                OnboardingGlassHeader(
                    currentStep = uiState.currentStep,
                    totalSteps = 3,
                    language = uiState.language
                )
            },
            bottomBar = {
                OnboardingGlassBottomBar(
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
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
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

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

/**
 * Translucent Glass Onboarding Header with Apple-Style Capsule Progress Bar
 */
@Composable
private fun OnboardingGlassHeader(
    currentStep: Int,
    totalSteps: Int,
    language: Language
) {
    val headerShape = RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = OceanTeal.copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    0.0f to Color.White.copy(alpha = 0.12f),
                    0.5f to Color(0x280B132B),
                    1.0f to Color.White.copy(alpha = 0.03f)
                )
            )
            .border(1.2.dp, GlassBorderStandard, RoundedCornerShape(24.dp))
            .drawBehind {
                drawLine(
                    brush = Brush.horizontalGradient(
                        0.0f to Color.Transparent,
                        0.3f to Color.White.copy(alpha = 0.22f),
                        0.7f to Color.White.copy(alpha = 0.22f),
                        1.0f to Color.Transparent
                    ),
                    start = Offset(24f, 1.dp.toPx()),
                    end = Offset(size.width - 24f, 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ATHLENS",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        ),
                        color = ElectricEmerald
                    )
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(ElectricEmerald)
                    )
                }

                Text(
                    text = when (language) {
                        Language.ENGLISH -> "Step $currentStep of $totalSteps"
                        Language.TAMIL -> "படி $currentStep / $totalSteps"
                        Language.HINDI -> "चरण $currentStep / $totalSteps"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = TextGray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sleek Translucent Glass Capsule Progress Bar
            val targetProgress = currentStep.toFloat() / totalSteps.toFloat()
            val animatedProgress by animateFloatAsState(
                targetValue = targetProgress,
                label = "onboarding_progress"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(0.8.dp, Color.White.copy(alpha = 0.15f), CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(ElectricEmerald, OceanTeal)
                            )
                        )
                )
            }
        }
    }
}

/**
 * Floating Liquid Glass Onboarding Bottom Action Bar
 */
@Composable
private fun OnboardingGlassBottomBar(
    currentStep: Int,
    language: Language,
    isNextEnabled: Boolean,
    isSaving: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val barShape = RoundedCornerShape(28.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .shadow(
                elevation = 14.dp,
                shape = barShape,
                ambientColor = Color.Black.copy(alpha = 0.45f),
                spotColor = OceanTeal.copy(alpha = 0.30f)
            )
            .clip(barShape)
            .background(
                Brush.linearGradient(
                    0.0f to Color.White.copy(alpha = 0.12f),
                    0.5f to Color(0x330B132B),
                    1.0f to Color.White.copy(alpha = 0.04f)
                )
            )
            .border(1.2.dp, GlassBorderStandard, barShape)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep > 1) {
                LiquidGlassButton(
                    onClick = onBack,
                    isPrimary = false,
                    modifier = Modifier.heightIn(min = 44.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = BilingualStrings.getBackButton(language),
                        color = TextWhite,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            LiquidGlassButton(
                onClick = onNext,
                isPrimary = true,
                enabled = isNextEnabled && !isSaving,
                modifier = Modifier.heightIn(min = 44.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = DeepNavy,
                        modifier = Modifier.size(18.dp),
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
                        style = MaterialTheme.typography.labelMedium,
                        color = DeepNavy,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (currentStep == 3) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = DeepNavy,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
