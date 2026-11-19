package com.athlens.app.domain.model

enum class Sport(
    val id: String,
    val displayNameEn: String,
    val displayNameTa: String,
    val displayNameHi: String
) {
    KABADDI("KABADDI", "Kabaddi", "கபடி", "कबड्डी"),
    KHO_KHO("KHO_KHO", "Kho-Kho", "கோ-கோ", "खो-खो");

    fun getDisplayName(lang: Language): String = when (lang) {
        Language.ENGLISH -> displayNameEn
        Language.TAMIL -> displayNameTa
        Language.HINDI -> displayNameHi
    }

    companion object {
        fun fromId(id: String): RoleSport = entries.find { it.id.equals(id, ignoreCase = true) } ?: KABADDI
    }
}
typealias RoleSport = Sport
