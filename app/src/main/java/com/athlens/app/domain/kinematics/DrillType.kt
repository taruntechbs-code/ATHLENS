package com.athlens.app.domain.kinematics

import com.athlens.app.domain.model.Language
import com.athlens.app.domain.model.Sport

enum class DrillType(
    val id: String,
    val displayNameEn: String,
    val displayNameTa: String,
    val displayNameHi: String,
    val descriptionEn: String,
    val descriptionTa: String,
    val descriptionHi: String,
    val sport: Sport
) {
    KABADDI_RAID_STANCE(
        id = "KABADDI_RAID_STANCE",
        displayNameEn = "Kabaddi Raid Stance",
        displayNameTa = "கபடி ரெய்டு நிலை",
        displayNameHi = "कबड्डी रेड स्टांस",
        descriptionEn = "Ideal knee flexion: 95°–140° • Forward torso lean: 15°–45°",
        descriptionTa = "முழங்கால் கோணம்: 95°–140° • முன்னோக்கிய சாய்வு: 15°–45°",
        descriptionHi = "घुटने का मोड़: 95°–140° • आगे का झुकाव: 15°–45°",
        sport = Sport.KABADDI
    ),
    KHO_KHO_CROUCH_START(
        id = "KHO_KHO_CROUCH_START",
        displayNameEn = "Kho-Kho Crouch Start",
        displayNameTa = "கோ-கோ தொடக்க நிலை",
        displayNameHi = "खो-खो क्राउच स्टार्ट",
        descriptionEn = "Ideal deep crouch knee flexion: 60°–115° • Low hip center",
        descriptionTa = "ஆழமான அமர்வு கோணம்: 60°–115° • தாழ்வான இடுப்பு மையம்",
        descriptionHi = "गहरा क्राउच: 60°–115° • निचला हिप सेंटर",
        sport = Sport.KHO_KHO
    );

    fun getDisplayName(lang: Language): String = when (lang) {
        Language.ENGLISH -> displayNameEn
        Language.TAMIL -> displayNameTa
        Language.HINDI -> displayNameHi
    }

    fun getDescription(lang: Language): String = when (lang) {
        Language.ENGLISH -> descriptionEn
        Language.TAMIL -> descriptionTa
        Language.HINDI -> descriptionHi
    }

    companion object {
        fun fromId(id: String): DrillType =
            entries.find { it.id.equals(id, ignoreCase = true) } ?: KABADDI_RAID_STANCE
    }
}
