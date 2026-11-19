package com.athlens.app.data.repository

import com.athlens.app.domain.model.TrainingSession
import kotlinx.coroutines.flow.Flow

interface TrainingRepository {
    fun getSessionsForAthlete(athleteId: String): Flow<List<TrainingSession>>
    fun getSessionCount(athleteId: String): Flow<Int>
    fun getAverageScore(athleteId: String): Flow<Double?>
    suspend fun recordSession(session: TrainingSession): Result<Unit>
}
