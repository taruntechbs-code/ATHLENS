package com.athlens.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.athlens.app.data.local.dao.AthleteProfileDao
import com.athlens.app.data.local.dao.GrowthMeasurementDao
import com.athlens.app.data.local.dao.TrainingSessionDao
import com.athlens.app.data.local.entity.AthleteProfileEntity
import com.athlens.app.data.local.entity.GrowthMeasurementEntity
import com.athlens.app.data.local.entity.TrainingSessionEntity

@Database(
    entities = [
        AthleteProfileEntity::class,
        TrainingSessionEntity::class,
        GrowthMeasurementEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AthlensDatabase : RoomDatabase() {
    abstract fun athleteProfileDao(): AthleteProfileDao
    abstract fun trainingSessionDao(): TrainingSessionDao
    abstract fun growthMeasurementDao(): GrowthMeasurementDao

    companion object {
        const val DATABASE_NAME = "athlens_encrypted.db"
    }
}
