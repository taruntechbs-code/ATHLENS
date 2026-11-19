package com.athlens.app.domain.model

enum class Role(
    val id: String,
    val displayNameEn: String,
    val displayNameTa: String,
    val displayNameHi: String
) {
    ATHLETE("ATHLETE", "Athlete", "வீரர் / வீராங்கனை", "खिलाड़ी"),
    COACH("COACH", "Coach", "பயிற்சியாளர்", "प्रशिक्षक / कोच"),
    SCOUT("SCOUT", "Scout", "திறமை கண்டறிபவர்", "स्काउट / चयनकर्ता");

    fun getDisplayName(lang: Language): String = when (lang) {
        Language.ENGLISH -> displayNameEn
        Language.TAMIL -> displayNameTa
        Language.HINDI -> displayNameHi
    }

    companion object {
        fun fromId(id: String): Role = entries.find { it.id.equals(id, ignoreCase = true) } ?: ATHLETE
    }
}
