package com.athlens.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.athlens.app.domain.model.Achievement
import com.athlens.app.domain.model.AchievementLevel
import java.util.UUID

@Entity(tableName = "athlete_achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val athleteId: String,
    val title: String,
    val level: String, // SCHOOL, DISTRICT, STATE
    val issuerName: String,
    val awardedAt: Long = System.currentTimeMillis(),
    val isVerified: Boolean = false
) {
    fun toDomainModel(): Achievement = Achievement(
        id = id,
        athleteId = athleteId,
        title = title,
        level = AchievementLevel.fromId(level),
        issuerName = issuerName,
        awardedAt = awardedAt,
        isVerified = isVerified
    )

    companion object {
        fun fromDomainModel(domain: Achievement): AchievementEntity = AchievementEntity(
            id = domain.id,
            athleteId = domain.athleteId,
            title = domain.title,
            level = domain.level.id,
            issuerName = domain.issuerName,
            awardedAt = domain.awardedAt,
            isVerified = domain.isVerified
        )
    }
}
