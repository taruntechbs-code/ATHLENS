package com.athlens.app.domain.model

import java.util.UUID

data class GrowthMeasurement(
    val id: String = UUID.randomUUID().toString(),
    val athleteId: String,
    val heightCm: Float,
    val weightKg: Float,
    val recordedAt: Long = System.currentTimeMillis()
)
