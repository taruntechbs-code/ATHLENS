package com.athlens.app.domain.model

enum class Language(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    TAMIL("ta", "Tamil", "தமிழ்"),
    HINDI("hi", "Hindi", "हिन्दी");

    val labelTamil: String get() = when (this) {
        ENGLISH -> "ஆங்கிலம்"
        TAMIL -> "தமிழ்"
        HINDI -> "இந்தி"
    }

    companion object {
        fun fromCode(code: String): Language =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
    }
}
