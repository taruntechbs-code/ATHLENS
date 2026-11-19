package com.athlens.app.domain.model

data class TrainingSession(
    val id: String,
    val athleteId: String,
    val drillName: String,
    val formScore: Int,
    val errorCodesJson: String,
    val timestamp: Long,
    val synced: Boolean = false
)
