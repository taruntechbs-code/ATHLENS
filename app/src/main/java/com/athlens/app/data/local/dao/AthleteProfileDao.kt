package com.athlens.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.athlens.app.data.local.entity.AthleteProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AthleteProfileDao {
    @Query("SELECT * FROM athlete_profiles LIMIT 1")
    fun getActiveProfile(): Flow<AthleteProfileEntity?>

    @Query("SELECT * FROM athlete_profiles LIMIT 1")
    suspend fun getActiveProfileOnce(): AthleteProfileEntity?

    @Query("SELECT COUNT(*) FROM athlete_profiles")
    fun getProfileCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM athlete_profiles")
    suspend fun getProfileCountOnce(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: AthleteProfileEntity)

    @Update
    suspend fun updateProfile(profile: AthleteProfileEntity)

    @Query("DELETE FROM athlete_profiles")
    suspend fun deleteAllProfiles()
}
