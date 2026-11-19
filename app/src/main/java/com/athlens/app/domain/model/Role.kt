package com.athlens.app.domain.model

enum class Role(val id: String, val displayNameEn: String, val displayNameTa: String) {
    ATHLETE("ATHLETE", "Athlete", "வீரர் / வீராங்கனை"),
    COACH("COACH", "Coach", "பயிற்சியாளர்"),
    SCOUT("SCOUT", "Scout", "திறமை கண்டறிபவர்");

    companion object {
        fun fromId(id: String): Role = entries.find { it.id.equals(id, ignoreCase = true) } ?: ATHLETE
    }
}
