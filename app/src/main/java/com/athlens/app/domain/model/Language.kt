package com.athlens.app.domain.model

enum class Language(val code: String, val displayName: String, val labelTamil: String) {
    TAMIL("ta", "தமிழ்", "தமிழ்"),
    ENGLISH("en", "English", "ஆங்கிலம்");

    companion object {
        fun fromCode(code: String): Language =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: TAMIL
    }
}
