package com.athlens.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.athlens.app.data.local.entity.ReceivedPassportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceivedPassportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceivedPassport(entity: ReceivedPassportEntity)

    @Query("DELETE FROM received_passports WHERE id = :id")
    suspend fun deleteReceivedPassport(id: String)

    @Query("SELECT * FROM received_passports ORDER BY receivedAt DESC")
    fun getReceivedPassports(): Flow<List<ReceivedPassportEntity>>

    @Query("SELECT * FROM received_passports WHERE synced = 0")
    suspend fun getUnsyncedPassports(): List<ReceivedPassportEntity>

    @Query("UPDATE received_passports SET synced = 1 WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<String>)

    @Query("SELECT COUNT(*) FROM received_passports WHERE synced = 0")
    fun getUnsyncedCount(): Flow<Int>
}
