package com.athlens.app.ui.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.athlens.app.data.audio.VoiceCoachManager
import com.athlens.app.data.repository.AthleteRepository
import com.athlens.app.data.repository.TrainingRepository
import com.athlens.app.domain.kinematics.DrillType
import com.athlens.app.domain.kinematics.KinematicAnalyzer
import com.athlens.app.domain.kinematics.KinematicFeedback
import com.athlens.app.domain.model.Language
import com.athlens.app.domain.model.TrainingSession
import com.athlens.app.ui.util.BilingualStrings
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.roundToInt

data class CameraCoachUiState(
    val selectedDrill: DrillType = DrillType.KABADDI_RAID_STANCE,
    val isFrontCamera: Boolean = true,
    val isSessionActive: Boolean = false,
    val sessionDurationSeconds: Int = 0,
    val kinematicFeedback: KinematicFeedback = KinematicFeedback(),
    val landmarks: List<NormalizedLandmark>? = null,
    val imageWidth: Int = 0,
    val imageHeight: Int = 0,
    val inferenceTimeMs: Long = 0L,
    val isSavingSession: Boolean = false,
    val sessionSavedSuccessfully: Boolean = false,
    val latestSavedScore: Int = 0,
    val activeLanguage: Language = Language.TAMIL,
    val isVoiceMuted: Boolean = false,
    val athleteName: String = "Athlete",
    val athleteId: String = ""
)

class CameraCoachViewModel(
    private val athleteRepository: AthleteRepository,
    private val trainingRepository: TrainingRepository,
    private val voiceCoachManager: VoiceCoachManager
) : ViewModel() {

    private val kinematicAnalyzer = KinematicAnalyzer()
    private val _uiState = MutableStateFlow(CameraCoachUiState())
    val uiState: StateFlow<CameraCoachUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private val recordedScores = mutableListOf<Int>()
    private val recordedErrors = mutableMapOf<String, Int>()

    init {
        viewModelScope.launch {
            val profile = athleteRepository.getActiveProfile().firstOrNull()
            if (profile != null) {
                val userLang = Language.fromCode(profile.languageCode)
                voiceCoachManager.setLanguage(userLang)
                _uiState.update {
                    it.copy(
                        athleteId = profile.id,
                        athleteName = profile.fullName,
                        activeLanguage = userLang
                    )
                }
            }
        }
    }

    fun setDrill(drill: DrillType) {
        kinematicAnalyzer.resetSmoothing()
        _uiState.update { it.copy(selectedDrill = drill) }
    }

    fun toggleCamera() {
        _uiState.update { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    fun toggleVoiceMute() {
        val newMuted = !_uiState.value.isVoiceMuted
        voiceCoachManager.setMuted(newMuted)
        _uiState.update { it.copy(isVoiceMuted = newMuted) }
    }

    fun setLanguage(language: Language) {
        voiceCoachManager.setLanguage(language)
        _uiState.update { it.copy(activeLanguage = language) }
    }

    fun onPoseDetected(
        landmarks: List<NormalizedLandmark>?,
        imageWidth: Int,
        imageHeight: Int,
        inferenceTimeMs: Long
    ) {
        val currentDrill = _uiState.value.selectedDrill
        val feedback = kinematicAnalyzer.analyze(landmarks, currentDrill)

        if (_uiState.value.isSessionActive && feedback.formScore > 0) {
            recordedScores.add(feedback.formScore)
            feedback.activeErrorCodes.forEach { code ->
                if (code != KinematicFeedback.CODE_GOOD_FORM) {
                    recordedErrors[code] = (recordedErrors[code] ?: 0) + 1
                }
            }
        }

        // Trigger voice coaching cue if primary error code is present
        if (!_uiState.value.isVoiceMuted && feedback.primaryErrorCode.isNotEmpty()) {
            voiceCoachManager.speakCorrection(feedback.primaryErrorCode)
        }

        _uiState.update {
            it.copy(
                landmarks = landmarks,
                imageWidth = imageWidth,
                imageHeight = imageHeight,
                inferenceTimeMs = inferenceTimeMs,
                kinematicFeedback = feedback
            )
        }
    }

    fun startSession() {
        recordedScores.clear()
        recordedErrors.clear()
        kinematicAnalyzer.resetSmoothing()
        _uiState.update {
            it.copy(
                isSessionActive = true,
                sessionDurationSeconds = 0,
                sessionSavedSuccessfully = false
            )
        }

        // Announce session start via Voice Coach
        val startCue = BilingualStrings.getVoiceSessionStarted(_uiState.value.activeLanguage)
        voiceCoachManager.speakMilestone(startCue)

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                _uiState.update { it.copy(sessionDurationSeconds = it.sessionDurationSeconds + 1) }
            }
        }
    }

    fun stopAndSaveSession() {
        timerJob?.cancel()
        timerJob = null

        val current = _uiState.value
        val avgScore = if (recordedScores.isNotEmpty()) {
            recordedScores.average().roundToInt()
        } else {
            current.kinematicFeedback.formScore.coerceAtLeast(60)
        }

        val topErrors = recordedErrors.entries
            .sortedByDescending { it.value }
            .map { it.key }
            .take(3)

        val errorCodesJson = if (topErrors.isEmpty()) {
            "[\"GOOD_FORM\"]"
        } else {
            "[" + topErrors.joinToString(",") { "\"$it\"" } + "]"
        }

        // Announce session completion via Voice Coach
        val stopCue = BilingualStrings.getVoiceSessionStopped(avgScore, current.activeLanguage)
        voiceCoachManager.speakMilestone(stopCue)

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingSession = true, isSessionActive = false) }

            val session = TrainingSession(
                id = UUID.randomUUID().toString(),
                athleteId = current.athleteId.ifEmpty { "default_athlete" },
                drillName = current.selectedDrill.displayNameEn,
                formScore = avgScore,
                errorCodesJson = errorCodesJson,
                timestamp = System.currentTimeMillis(),
                synced = false
            )

            trainingRepository.recordSession(session)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSavingSession = false,
                            sessionSavedSuccessfully = true,
                            latestSavedScore = avgScore
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isSavingSession = false) }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        voiceCoachManager.shutdown()
    }
}
