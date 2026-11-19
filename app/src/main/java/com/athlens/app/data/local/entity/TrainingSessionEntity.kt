package com.athlens.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.athlens.app.domain.model.TrainingSession

@Entity(tableName = "training_sessions")
data class TrainingSessionEntity(
    @PrimaryKey
    val id: String,
    val athleteId: String,
    val drillName: String,
    val formScore: Int,
    val errorCodesJson: String,
    val timestamp: Long,
    val synced: Boolean = false
) {
    fun toDomainModel(): TrainingSession {
        return TrainingSession(
            id = id,
            athleteId = athleteId,
            drillName = drillName,
            formScore = formScore,
            errorCodesJson = errorCodesJson,
            timestamp = timestamp,
            synced = synced
        )
    }

    companion object {
        fun fromDomainModel(domain: TrainingSession): TrainingSessionEntity {
            return TrainingSessionEntity(
                id = domain.id,
                athleteId = domain.athleteId,
                drillName = domain.drillName,
                formScore = domain.formScore,
                errorCodesJson = domain.errorCodesJson,
                timestamp = domain.timestamp,
                synced = domain.synced
            )
        }
    }
}
