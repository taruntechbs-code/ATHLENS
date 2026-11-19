package com.athlens.app.domain.kinematics

import com.athlens.app.domain.model.Sport

enum class DrillType(
    val id: String,
    val displayNameEn: String,
    val displayNameTa: String,
    val descriptionEn: String,
    val descriptionTa: String,
    val sport: Sport
) {
    KABADDI_RAID_STANCE(
        id = "KABADDI_RAID_STANCE",
        displayNameEn = "Kabaddi Raid Stance",
        displayNameTa = "கபடி ரெய்டு நிலை",
        descriptionEn = "Ideal knee flexion: 95°–140° • Forward torso lean: 15°–45°",
        descriptionTa = "முழங்கால் கோணம்: 95°–140° • முன்னோக்கிய சாய்வு: 15°–45°",
        sport = Sport.KABADDI
    ),
    KHO_KHO_CROUCH_START(
        id = "KHO_KHO_CROUCH_START",
        displayNameEn = "Kho-Kho Crouch Start",
        displayNameTa = "கோ-கோ தொடக்க நிலை",
        descriptionEn = "Ideal deep crouch knee flexion: 60°–115° • Low hip center",
        descriptionTa = "ஆழமான அமர்வு கோணம்: 60°–115° • தாழ்வான இடுப்பு மையம்",
        sport = Sport.KHO_KHO
    );

    companion object {
        fun fromId(id: String): DrillType =
            entries.find { it.id.equals(id, ignoreCase = true) } ?: KABADDI_RAID_STANCE
    }
}
