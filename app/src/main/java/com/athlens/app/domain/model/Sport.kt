package com.athlens.app.domain.model

enum class Sport(val id: String, val displayNameEn: String, val displayNameTa: String) {
    KABADDI("KABADDI", "Kabaddi", "கபடி"),
    KHO_KHO("KHO_KHO", "Kho-Kho", "கோ-கோ");

    companion object {
        fun fromId(id: String): Sport = entries.find { it.id.equals(id, ignoreCase = true) } ?: KABADDI
    }
}
