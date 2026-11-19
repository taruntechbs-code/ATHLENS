package com.athlens.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.athlens.app.data.local.dao.AchievementDao
import com.athlens.app.data.local.dao.AthleteProfileDao
import com.athlens.app.data.local.dao.GrowthMeasurementDao
import com.athlens.app.data.local.dao.ReceivedPassportDao
import com.athlens.app.data.local.dao.TrainingSessionDao
import com.athlens.app.data.local.entity.AchievementEntity
import com.athlens.app.data.local.entity.AthleteProfileEntity
import com.athlens.app.data.local.entity.GrowthMeasurementEntity
import com.athlens.app.data.local.entity.ReceivedPassportEntity
import com.athlens.app.data.local.entity.TrainingSessionEntity

@Database(
    entities = [
        AthleteProfileEntity::class,
        TrainingSessionEntity::class,
        GrowthMeasurementEntity::class,
        AchievementEntity::class,
        ReceivedPassportEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AthlensDatabase : RoomDatabase() {
    abstract fun athleteProfileDao(): AthleteProfileDao
    abstract fun trainingSessionDao(): TrainingSessionDao
    abstract fun growthMeasurementDao(): GrowthMeasurementDao
    abstract fun achievementDao(): AchievementDao
    abstract fun receivedPassportDao(): ReceivedPassportDao

    companion object {
        const val DATABASE_NAME = "athlens_encrypted.db"
    }
}
