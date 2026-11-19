package com.athlens.app.ui.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.athlens.app.data.local.entity.ReceivedPassportEntity
import com.athlens.app.data.p2p.NearbyExchangeManager
import com.athlens.app.data.repository.AthleteRepository
import com.athlens.app.data.repository.TrainingRepository
import com.athlens.app.data.security.PassportCryptoManager
import com.athlens.app.data.sync.AthlensSyncWorker
import com.athlens.app.domain.maturity.MaturityAssessmentEngine
import com.athlens.app.domain.model.Achievement
import com.athlens.app.domain.model.AchievementLevel
import com.athlens.app.domain.model.GrowthMeasurement
import com.athlens.app.domain.model.Language
import com.athlens.app.domain.model.TrainingSession
import com.athlens.app.domain.passport.PassportPayload
import com.athlens.app.domain.passport.PassportSerializer
import com.athlens.app.domain.passport.SignedTalentPassport
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
import kotlin.math.roundToInt

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    private val athleteRepository: AthleteRepository,
    private val trainingRepository: TrainingRepository,
    private val passportCryptoManager: PassportCryptoManager,
    val nearbyExchangeManager: NearbyExchangeManager,
    private val appContext: Context
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
                    athleteRepository.getGrowthMeasurements(profile.id),
                    athleteRepository.getAchievements(profile.id),
                    athleteRepository.getReceivedPassports(),
                    trainingRepository.getUnsyncedSessionCount()
                ) { args ->
                    val tab = args[0] as DashboardTab
                    val manualLang = args[1] as? Language
                    @Suppress("UNCHECKED_CAST")
                    val sessions = args[2] as List<TrainingSession>
                    val count = args[3] as Int
                    val avgScore = args[4] as? Double
                    @Suppress("UNCHECKED_CAST")
                    val measurements = args[5] as List<GrowthMeasurement>
                    @Suppress("UNCHECKED_CAST")
                    val achievements = args[6] as List<Achievement>
                    @Suppress("UNCHECKED_CAST")
                    val receivedPassports = args[7] as List<ReceivedPassportEntity>
                    val unsyncedSessionCount = args[8] as Int

                    val resolvedLang = manualLang
                        ?: Language.fromCode(profile.languageCode)

                    val maturityReport = MaturityAssessmentEngine.assess(
                        dateOfBirthMillis = profile.dateOfBirth,
                        measurements = measurements,
                        sessions = sessions
                    )

                    // Construct and sign deterministic Talent Passport
                    val verifiedCount = achievements.count { it.isVerified }
                    val avgInt = avgScore?.roundToInt() ?: 0
                    val ageMonths = maturityReport.exactAgeYears * 12 + maturityReport.exactAgeMonths

                    val payload = PassportPayload(
                        version = 1,
                        athleteId = profile.id,
                        fullName = profile.fullName,
                        district = profile.district,
                        sport = profile.sport.id,
                        playingPosition = profile.playingPosition,
                        ageMonths = ageMonths,
                        raeQuarter = maturityReport.raeQuarter,
                        totalSessions = count,
                        avgScore = avgInt,
                        progressionDelta = maturityReport.progressionDeltaPercent,
                        verifiedAchievements = verifiedCount,
                        issuedAtSeconds = System.currentTimeMillis() / 1000
                    )

                    val canonicalJson = PassportSerializer.serializeCanonical(payload)
                    val signature = passportCryptoManager.signPayload(canonicalJson)
                    val pubKey = passportCryptoManager.getPublicKeyBase64()
                    val envelope = PassportSerializer.buildEnvelope(canonicalJson, signature, pubKey)
                    val fingerprint = passportCryptoManager.getPublicKeyFingerprint()

                    val signedPassport = SignedTalentPassport(
                        payload = payload,
                        canonicalPayloadJson = canonicalJson,
                        signatureBase64 = signature,
                        publicKeyBase64 = pubKey,
                        envelopeJson = envelope,
                        keyFingerprint = fingerprint
                    )

                    val unsyncedPassports = receivedPassports.count { !it.synced }
                    val totalUnsynced = unsyncedSessionCount + unsyncedPassports

                    DashboardUiState(
                        athleteProfile = profile,
                        currentTab = tab,
                        isLoading = false,
                        activeLanguage = resolvedLang,
                        recentSessions = sessions,
                        totalSessions = count,
                        averageFormScore = avgScore,
                        growthMeasurements = measurements,
                        maturityReport = maturityReport,
                        achievements = achievements,
                        signedPassport = signedPassport,
                        receivedPassports = receivedPassports,
                        unsyncedCount = totalUnsynced
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

    fun addAchievement(
        title: String,
        level: AchievementLevel,
        issuerName: String,
        isVerified: Boolean
    ) {
        val profile = uiState.value.athleteProfile ?: return
        viewModelScope.launch {
            athleteRepository.recordAchievement(
                Achievement(
                    id = UUID.randomUUID().toString(),
                    athleteId = profile.id,
                    title = title,
                    level = level,
                    issuerName = issuerName,
                    awardedAt = System.currentTimeMillis(),
                    isVerified = isVerified
                )
            )
        }
    }

    fun deleteAchievement(id: String) {
        viewModelScope.launch {
            athleteRepository.deleteAchievement(id)
        }
    }

    fun verifyPassportOffline(
        canonicalPayload: String,
        signatureBase64: String,
        publicKeyBase64: String
    ): Boolean {
        return passportCryptoManager.verifySignature(canonicalPayload, signatureBase64, publicKeyBase64)
    }

    fun simulateTamperedPayload(canonicalPayload: String): String {
        return PassportSerializer.tamperPayload(canonicalPayload)
    }

    // Phase 6: P2P & Scout Inbox Actions
    fun startSharingPassport() {
        val profile = uiState.value.athleteProfile ?: return
        val signedPassport = uiState.value.signedPassport ?: return
        nearbyExchangeManager.startSharingPassport(
            senderName = profile.fullName,
            signedEnvelopeJson = signedPassport.envelopeJson
        )
    }

    fun startDiscovering() {
        val profile = uiState.value.athleteProfile
        val scoutName = profile?.fullName ?: "ATHLENS Scout"
        nearbyExchangeManager.startDiscovering(scoutName) { received ->
            saveReceivedPassport(received)
        }
    }

    fun connectToEndpoint(endpointId: String) {
        val profile = uiState.value.athleteProfile
        val scoutName = profile?.fullName ?: "ATHLENS Scout"
        nearbyExchangeManager.connectToEndpoint(endpointId, scoutName)
    }

    fun stopP2P() {
        nearbyExchangeManager.stopAll()
    }

    fun saveReceivedPassport(entity: ReceivedPassportEntity) {
        viewModelScope.launch {
            athleteRepository.saveReceivedPassport(entity)
        }
    }

    fun deleteReceivedPassport(id: String) {
        viewModelScope.launch {
            athleteRepository.deleteReceivedPassport(id)
        }
    }

    fun triggerCloudSync() {
        AthlensSyncWorker.triggerImmediateSync(appContext)
    }

    override fun onCleared() {
        super.onCleared()
        nearbyExchangeManager.stopAll()
    }
}
