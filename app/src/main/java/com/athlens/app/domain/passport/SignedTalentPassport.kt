package com.athlens.app.domain.passport

data class PassportPayload(
    val version: Int = 1,
    val athleteId: String,
    val fullName: String,
    val district: String,
    val sport: String,
    val playingPosition: String,
    val ageMonths: Int,
    val raeQuarter: String,
    val totalSessions: Int,
    val avgScore: Int,
    val progressionDelta: Int?,
    val verifiedAchievements: Int,
    val issuedAtSeconds: Long
)

data class SignedTalentPassport(
    val payload: PassportPayload,
    val canonicalPayloadJson: String,
    val signatureBase64: String,
    val publicKeyBase64: String,
    val envelopeJson: String,
    val keyFingerprint: String
)
