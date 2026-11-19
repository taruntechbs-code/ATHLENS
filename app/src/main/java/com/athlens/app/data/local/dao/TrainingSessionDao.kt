package com.athlens.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.athlens.app.data.local.entity.TrainingSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingSessionDao {
    @Query("SELECT * FROM training_sessions WHERE athleteId = :athleteId ORDER BY timestamp DESC")
    fun getSessionsForAthlete(athleteId: String): Flow<List<TrainingSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TrainingSessionEntity)

    @Query("SELECT COUNT(*) FROM training_sessions WHERE athleteId = :athleteId")
    fun getSessionCount(athleteId: String): Flow<Int>

    @Query("SELECT AVG(formScore) FROM training_sessions WHERE athleteId = :athleteId")
    fun getAverageScore(athleteId: String): Flow<Double?>
}
