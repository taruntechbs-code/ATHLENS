package com.athlens.app.data.repository

import com.athlens.app.data.local.dao.TrainingSessionDao
import com.athlens.app.data.local.entity.TrainingSessionEntity
import com.athlens.app.domain.model.TrainingSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrainingRepositoryImpl(
    private val dao: TrainingSessionDao
) : TrainingRepository {

    override fun getSessionsForAthlete(athleteId: String): Flow<List<TrainingSession>> {
        return dao.getSessionsForAthlete(athleteId).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun getSessionCount(athleteId: String): Flow<Int> {
        return dao.getSessionCount(athleteId)
    }

    override fun getAverageScore(athleteId: String): Flow<Double?> {
        return dao.getAverageScore(athleteId)
    }

    override suspend fun recordSession(session: TrainingSession): Result<Unit> {
        return runCatching {
            dao.insertSession(TrainingSessionEntity.fromDomainModel(session))
        }
    }

    override fun getUnsyncedSessionCount(): Flow<Int> {
        return dao.getUnsyncedCount()
    }
}
