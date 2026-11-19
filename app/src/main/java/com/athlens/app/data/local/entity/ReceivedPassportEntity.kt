package com.athlens.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "received_passports")
data class ReceivedPassportEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val athleteName: String,
    val district: String,
    val sport: String,
    val playingPosition: String,
    val ageMonths: Int,
    val raeQuarter: String,
    val totalSessions: Int,
    val avgFormScore: Int,
    val verifiedAchievementsCount: Int,
    val signatureVerified: Boolean,
    val publicKeyFingerprint: String,
    val transferMethod: String, // "NEARBY_P2P" or "QR_SCAN"
    val rawEnvelopeJson: String,
    val receivedAt: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)
