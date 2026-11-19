package com.athlens.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.athlens.app.data.local.entity.GrowthMeasurementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrowthMeasurementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(entity: GrowthMeasurementEntity): Long

    @Query("SELECT * FROM growth_measurements WHERE athleteId = :athleteId ORDER BY recordedAt ASC")
    fun getMeasurementsForAthlete(athleteId: String): Flow<List<GrowthMeasurementEntity>>

    @Query("SELECT COUNT(*) FROM growth_measurements WHERE athleteId = :athleteId")
    suspend fun getMeasurementCount(athleteId: String): Int
}
