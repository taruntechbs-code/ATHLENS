package com.athlens.app.data.repository

import com.athlens.app.domain.model.Achievement
import com.athlens.app.domain.model.AthleteProfile
import com.athlens.app.domain.model.GrowthMeasurement
import kotlinx.coroutines.flow.Flow

interface AthleteRepository {
    fun getActiveProfile(): Flow<AthleteProfile?>
    suspend fun getActiveProfileOnce(): AthleteProfile?
    fun hasProfile(): Flow<Boolean>
    suspend fun saveProfile(profile: AthleteProfile): Result<Unit>
    suspend fun updateLanguage(languageCode: String): Result<Unit>
    suspend fun clearAllProfiles(): Result<Unit>
    suspend fun recordGrowthMeasurement(measurement: GrowthMeasurement): Result<Unit>
    fun getGrowthMeasurements(athleteId: String): Flow<List<GrowthMeasurement>>
    suspend fun recordAchievement(achievement: Achievement): Result<Unit>
    suspend fun deleteAchievement(id: String): Result<Unit>
    fun getAchievements(athleteId: String): Flow<List<Achievement>>
    fun getVerifiedAchievementCount(athleteId: String): Flow<Int>
}
