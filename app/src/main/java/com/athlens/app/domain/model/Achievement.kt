package com.athlens.app.domain.model

import java.util.UUID

enum class AchievementLevel(
    val id: String,
    val displayNameEn: String,
    val displayNameTa: String,
    val displayNameHi: String
) {
    SCHOOL("SCHOOL", "School", "பள்ளி", "विद्यालय"),
    DISTRICT("DISTRICT", "District", "மாவட்டம்", "ज़िला"),
    STATE("STATE", "State", "மாநிலம்", "राज्य");

    fun getDisplayName(lang: Language): String = when (lang) {
        Language.ENGLISH -> displayNameEn
        Language.TAMIL -> displayNameTa
        Language.HINDI -> displayNameHi
    }

    companion object {
        fun fromId(id: String): AchievementLevel =
            entries.find { it.id.equals(id, ignoreCase = true) } ?: DISTRICT
    }
}

data class Achievement(
    val id: String = UUID.randomUUID().toString(),
    val athleteId: String,
    val title: String,
    val level: AchievementLevel,
    val issuerName: String,
    val awardedAt: Long = System.currentTimeMillis(),
    val isVerified: Boolean = false
)
