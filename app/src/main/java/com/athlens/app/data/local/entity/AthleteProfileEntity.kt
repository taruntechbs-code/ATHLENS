package com.athlens.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.athlens.app.domain.model.AthleteProfile
import com.athlens.app.domain.model.Role
import com.athlens.app.domain.model.Sport

@Entity(tableName = "athlete_profiles")
data class AthleteProfileEntity(
    @PrimaryKey
    val id: String,
    val role: String,
    val fullName: String,
    val dateOfBirth: Long,
    val district: String = "Coimbatore",
    val sport: String,
    val playingPosition: String,
    val languageCode: String,
    val isMinor: Boolean,
    val guardianName: String?,
    val guardianConsentGiven: Boolean,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): AthleteProfile {
        return AthleteProfile(
            id = id,
            role = Role.fromId(role),
            fullName = fullName,
            dateOfBirth = dateOfBirth,
            district = district,
            sport = Sport.fromId(sport),
            playingPosition = playingPosition,
            languageCode = languageCode,
            isMinor = isMinor,
            guardianName = guardianName,
            guardianConsentGiven = guardianConsentGiven,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomainModel(domain: AthleteProfile): AthleteProfileEntity {
            return AthleteProfileEntity(
                id = domain.id,
                role = domain.role.id,
                fullName = domain.fullName,
                dateOfBirth = domain.dateOfBirth,
                district = domain.district,
                sport = domain.sport.id,
                playingPosition = domain.playingPosition,
                languageCode = domain.languageCode,
                isMinor = domain.isMinor,
                guardianName = domain.guardianName,
                guardianConsentGiven = domain.guardianConsentGiven,
                createdAt = domain.createdAt
            )
        }
    }
}
