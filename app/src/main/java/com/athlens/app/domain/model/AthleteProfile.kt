package com.athlens.app.domain.model

import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

data class AthleteProfile(
    val id: String,
    val role: Role,
    val fullName: String,
    val dateOfBirth: Long,
    val district: String,
    val sport: Sport,
    val playingPosition: String,
    val languageCode: String,
    val isMinor: Boolean,
    val guardianName: String?,
    val guardianConsentGiven: Boolean,
    val createdAt: Long = System.currentTimeMillis()
) {
    val calculatedAge: Int
        get() {
            return try {
                val dob = Instant.ofEpochMilli(dateOfBirth)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                val now = LocalDate.now()
                Period.between(dob, now).years
            } catch (e: Exception) {
                0
            }
        }
}
