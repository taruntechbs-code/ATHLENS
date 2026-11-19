package com.athlens.app.domain.model

enum class PlayingPosition(
    val id: String,
    val sport: Sport,
    val displayNameEn: String,
    val displayNameTa: String
) {
    // Kabaddi
    RAIDER("RAIDER", Sport.KABADDI, "Raider", "ரெய்டர்"),
    DEFENDER("DEFENDER", Sport.KABADDI, "Defender", "தடுப்பாளர்"),
    ALL_ROUNDER_KABADDI("ALL_ROUNDER", Sport.KABADDI, "All-Rounder", "ஆல்-ரவுண்டர்"),

    // Kho-Kho
    CHASER("CHASER", Sport.KHO_KHO, "Chaser", "சேஸர்"),
    RUNNER("RUNNER", Sport.KHO_KHO, "Runner", "ரன்னர்"),
    ALL_ROUNDER_KHO_KHO("ALL_ROUNDER", Sport.KHO_KHO, "All-Rounder", "ஆல்-ரவுண்டர்");

    companion object {
        fun positionsFor(sport: Sport): List<PlayingPosition> {
            return entries.filter { it.sport == sport }
        }

        fun fromIdAndSport(id: String, sport: Sport): PlayingPosition {
            return entries.find { it.sport == sport && it.id.equals(id, ignoreCase = true) }
                ?: positionsFor(sport).first()
        }
    }
}
