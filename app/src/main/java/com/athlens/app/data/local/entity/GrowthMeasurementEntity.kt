package com.athlens.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "growth_measurements")
data class GrowthMeasurementEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val athleteId: String,
    val heightCm: Float,
    val weightKg: Float,
    val recordedAt: Long = System.currentTimeMillis()
)
