package com.athlens.app.data.repository

import com.athlens.app.data.local.dao.AthleteProfileDao
import com.athlens.app.data.local.dao.GrowthMeasurementDao
import com.athlens.app.data.local.entity.AthleteProfileEntity
import com.athlens.app.data.local.entity.GrowthMeasurementEntity
import com.athlens.app.domain.model.AthleteProfile
import com.athlens.app.domain.model.GrowthMeasurement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AthleteRepositoryImpl(
    private val dao: AthleteProfileDao,
    private val growthDao: GrowthMeasurementDao
) : AthleteRepository {

    override fun getActiveProfile(): Flow<AthleteProfile?> {
        return dao.getActiveProfile().map { it?.toDomainModel() }
    }

    override suspend fun getActiveProfileOnce(): AthleteProfile? {
        return dao.getActiveProfileOnce()?.toDomainModel()
    }

    override fun hasProfile(): Flow<Boolean> {
        return dao.getProfileCount().map { it > 0 }
    }

    override suspend fun saveProfile(profile: AthleteProfile): Result<Unit> {
        return runCatching {
            dao.insertProfile(AthleteProfileEntity.fromDomainModel(profile))
        }
    }

    override suspend fun updateLanguage(languageCode: String): Result<Unit> {
        return runCatching {
            val current = dao.getActiveProfileOnce()
            if (current != null) {
                dao.updateProfile(current.copy(languageCode = languageCode))
            }
        }
    }

    override suspend fun clearAllProfiles(): Result<Unit> {
        return runCatching {
            dao.deleteAllProfiles()
        }
    }

    override suspend fun recordGrowthMeasurement(measurement: GrowthMeasurement): Result<Unit> {
        return runCatching {
            growthDao.insertMeasurement(
                GrowthMeasurementEntity(
                    id = measurement.id,
                    athleteId = measurement.athleteId,
                    heightCm = measurement.heightCm,
                    weightKg = measurement.weightKg,
                    recordedAt = measurement.recordedAt
                )
            )
        }
    }

    override fun getGrowthMeasurements(athleteId: String): Flow<List<GrowthMeasurement>> {
        return growthDao.getMeasurementsForAthlete(athleteId).map { list ->
            list.map {
                GrowthMeasurement(
                    id = it.id,
                    athleteId = it.athleteId,
                    heightCm = it.heightCm,
                    weightKg = it.weightKg,
                    recordedAt = it.recordedAt
                )
            }
        }
    }
}
