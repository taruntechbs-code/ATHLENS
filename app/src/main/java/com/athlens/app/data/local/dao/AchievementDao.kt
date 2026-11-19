package com.athlens.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.athlens.app.data.local.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(entity: AchievementEntity): Long

    @Query("DELETE FROM athlete_achievements WHERE id = :id")
    suspend fun deleteAchievement(id: String)

    @Query("SELECT * FROM athlete_achievements WHERE athleteId = :athleteId ORDER BY awardedAt DESC")
    fun getAchievementsForAthlete(athleteId: String): Flow<List<AchievementEntity>>

    @Query("SELECT COUNT(*) FROM athlete_achievements WHERE athleteId = :athleteId AND isVerified = 1")
    fun getVerifiedAchievementCount(athleteId: String): Flow<Int>
}
