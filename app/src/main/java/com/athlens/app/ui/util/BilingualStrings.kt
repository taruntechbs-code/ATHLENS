package com.athlens.app.ui.util

import com.athlens.app.domain.model.Language

object BilingualStrings {

    fun getAppName(lang: Language): String = "ATHLENS"

    fun getTagline(lang: Language): String = when (lang) {
        Language.TAMIL -> "தமிழ்நாடு கிராமப்புற தடகள வீரர்களுக்கான செயற்கை நுண்ணறிவு தளம்"
        Language.ENGLISH -> "AI Sports Lab for Grassroots Tamil Nadu Athletes"
    }

    // Onboarding
    fun getStep1Title(lang: Language): String = when (lang) {
        Language.TAMIL -> "மொழி மற்றும் பங்கு தேர்வு"
        Language.ENGLISH -> "Language & Role Selection"
    }

    fun getStep1Subtitle(lang: Language): String = when (lang) {
        Language.TAMIL -> "உங்கள் விருப்ப மொழி மற்றும் பங்கினைத் தேர்ந்தெடுக்கவும்"
        Language.ENGLISH -> "Select your preferred language and platform role"
    }

    fun getStep2Title(lang: Language): String = when (lang) {
        Language.TAMIL -> "விளையாட்டு விவரக்குறிப்பு"
        Language.ENGLISH -> "Athlete Profile"
    }

    fun getStep2Subtitle(lang: Language): String = when (lang) {
        Language.TAMIL -> "உங்கள் விளையாட்டு மற்றும் மாவட்ட தகவல்களை உள்ளிடவும்"
        Language.ENGLISH -> "Enter your sport and district information"
    }

    fun getStep3Title(lang: Language): String = when (lang) {
        Language.TAMIL -> "பாதுகாவலர் ஒப்புதல் & சரிபார்ப்பு"
        Language.ENGLISH -> "Guardian Consent & Verification"
    }

    fun getStep3Subtitle(lang: Language): String = when (lang) {
        Language.TAMIL -> "இளம்பருவ வீரர் பாதுகாப்புக்கான சட்டப்பூர்வ ஒப்புதல்"
        Language.ENGLISH -> "Mandatory minor protection and consent verification"
    }

    // Field labels
    fun getFullNameLabel(lang: Language): String = when (lang) {
        Language.TAMIL -> "முழு பெயர் *"
        Language.ENGLISH -> "Full Name *"
    }

    fun getDateOfBirthLabel(lang: Language): String = when (lang) {
        Language.TAMIL -> "பிறந்த தேதி (DOB) *"
        Language.ENGLISH -> "Date of Birth (DOB) *"
    }

    fun getDistrictLabel(lang: Language): String = when (lang) {
        Language.TAMIL -> "மாவட்டம் (தமிழ்நாடு) *"
        Language.ENGLISH -> "District (Tamil Nadu) *"
    }

    fun getSportLabel(lang: Language): String = when (lang) {
        Language.TAMIL -> "விளையாட்டு *"
        Language.ENGLISH -> "Sport *"
    }

    fun getPositionLabel(lang: Language): String = when (lang) {
        Language.TAMIL -> "விளையாடும் நிலை *"
        Language.ENGLISH -> "Playing Position *"
    }

    fun getGuardianNameLabel(lang: Language): String = when (lang) {
        Language.TAMIL -> "பெற்றோர் / பாதுகாவலர் முழு பெயர் *"
        Language.ENGLISH -> "Parent / Guardian Full Name *"
    }

    fun getGuardianConsentNotice(lang: Language): String = when (lang) {
        Language.TAMIL -> "18 வயதுக்குட்பட்ட இளம்பருவ வீரர்களுக்கு பாதுகாவலர் அனுமதி கட்டாயமாகும். உடற்பயிற்சி தரவு மற்றும் தடகள பாஸ்போர்ட் உருவாக்கத்திற்கு நான் முழு ஒப்புதல் அளிக்கிறேன்."
        Language.ENGLISH -> "Guardian consent is mandatory for athletes under 18 years. I hereby give full consent for training telemetry and athlete passport creation under sports safety norms."
    }

    fun getAdultNotice(lang: Language): String = when (lang) {
        Language.TAMIL -> "நீங்கள் 18 வயது அல்லது அதற்கு மேற்பட்டவர் (வயது வந்த வீரர்). பாதுகாவலர் ஒப்புதல் தேவையில்லை."
        Language.ENGLISH -> "You are 18 or older (Adult Athlete). Guardian consent is not required."
    }

    fun getCreateProfileButton(lang: Language): String = when (lang) {
        Language.TAMIL -> "விவரக்குறிப்பை உருவாக்கு"
        Language.ENGLISH -> "Create Athlete Profile"
    }

    fun getNextButton(lang: Language): String = when (lang) {
        Language.TAMIL -> "அடுத்து"
        Language.ENGLISH -> "Next"
    }

    fun getBackButton(lang: Language): String = when (lang) {
        Language.TAMIL -> "பின்செல்"
        Language.ENGLISH -> "Back"
    }

    // Navigation Tabs
    fun getTabTrain(lang: Language): String = when (lang) {
        Language.TAMIL -> "பயிற்சி"
        Language.ENGLISH -> "Train"
    }

    fun getTabPassport(lang: Language): String = when (lang) {
        Language.TAMIL -> "பாஸ்போர்ட்"
        Language.ENGLISH -> "Passport"
    }

    fun getTabNutrition(lang: Language): String = when (lang) {
        Language.TAMIL -> "ஊட்டச்சத்து"
        Language.ENGLISH -> "Nutrition"
    }

    fun getTabShareP2P(lang: Language): String = when (lang) {
        Language.TAMIL -> "பகிர் P2P"
        Language.ENGLISH -> "Share P2P"
    }

    // Dashboard
    fun getGreeting(lang: Language, name: String): String = when (lang) {
        Language.TAMIL -> "வணக்கம், $name!"
        Language.ENGLISH -> "Welcome, $name!"
    }

    fun getOfflineBadge(lang: Language): String = when (lang) {
        Language.TAMIL -> "ஆஃப்லைன் பயன்முறை தயார்"
        Language.ENGLISH -> "Offline Mode Ready"
    }

    fun getOfflineTooltip(lang: Language): String = when (lang) {
        Language.TAMIL -> "அனைத்து தரவுகளும் உங்கள் சாதனத்தில் என்க்ரிப்ட் செய்யப்பட்டுள்ளது"
        Language.ENGLISH -> "All data is securely encrypted on this device"
    }

    // Kinematic Feedback & Coaching
    fun getKinematicFeedback(errorCode: String, lang: Language): String = when (errorCode) {
        "GOOD_FORM" -> when (lang) {
            Language.TAMIL -> "சிறந்த நிலை • தாக்குதலுக்கு தயார்"
            Language.ENGLISH -> "Optimal Stance • Ready for Action"
        }
        "STANCE_TOO_HIGH" -> when (lang) {
            Language.TAMIL -> "நிலையை தாழ்த்தவும் • முழங்கால்களை மேலும் மடக்கவும்"
            Language.ENGLISH -> "Lower Your Stance • Bend Knees More"
        }
        "TORSO_TOO_UPRIGHT" -> when (lang) {
            Language.TAMIL -> "உடலை முன்னோக்கி சாய்க்கவும் • தாக்குதல் கோணம் தேவை"
            Language.ENGLISH -> "Lean Torso Forward • Engage Attack Angle"
        }
        "UNSTABLE_BASE" -> when (lang) {
            Language.TAMIL -> "கால்களை அகல வைக்கவும் • நிலையான அடித்தளம் தேவை"
            Language.ENGLISH -> "Widen Foot Stance • Strengthen Base"
        }
        "KNEES_NOT_BENT" -> when (lang) {
            Language.TAMIL -> "ஆழமாக அமரவும் • முழங்கால்களை நன்கு மடக்கவும்"
            Language.ENGLISH -> "Deepen Crouch • Bend Both Knees"
        }
        "HIPS_TOO_HIGH" -> when (lang) {
            Language.TAMIL -> "இடுப்பு நிலையை கீழே இறக்கவும் • தாழ்வாக இருங்கள்"
            Language.ENGLISH -> "Lower Hips Closer to Knee Level"
        }
        "UNBALANCED_POSTURE" -> when (lang) {
            Language.TAMIL -> "உடல் எடையை சமநிலையில் வைக்கவும் • சமச்சீர் நிலை தேவை"
            Language.ENGLISH -> "Balance Weight Evenly on Footing"
        }
        "NO_POSE_DETECTED" -> when (lang) {
            Language.TAMIL -> "கேமரா பார்வையில் முழு உடலும் தெரிய நிற்கவும்"
            Language.ENGLISH -> "Step into Camera Frame"
        }
        else -> when (lang) {
            Language.TAMIL -> "நிலையை சரிசெய்யவும்"
            Language.ENGLISH -> "Adjust Your Posture"
        }
    }

    fun getCameraCoachTitle(lang: Language): String = when (lang) {
        Language.TAMIL -> "AI கேமரா பயிற்சியாளர்"
        Language.ENGLISH -> "AI Camera Coach"
    }

    fun getStartSession(lang: Language): String = when (lang) {
        Language.TAMIL -> "பயிற்சியைத் தொடங்கு"
        Language.ENGLISH -> "Start Drill Session"
    }

    fun getStopAndSaveSession(lang: Language): String = when (lang) {
        Language.TAMIL -> "முடித்து சேமிக்கவும்"
        Language.ENGLISH -> "Stop & Save Session"
    }

    fun getSessionSaved(lang: Language, score: Int): String = when (lang) {
        Language.TAMIL -> "பயிற்சி சேமிக்கப்பட்டது! மதிப்பெண்: $score/100"
        Language.ENGLISH -> "Session Saved! Form Score: $score/100"
    }

    fun getKneeAngleLabel(lang: Language): String = when (lang) {
        Language.TAMIL -> "முழங்கால்"
        Language.ENGLISH -> "Knee"
    }

    fun getTorsoLeanLabel(lang: Language): String = when (lang) {
        Language.TAMIL -> "உடல் சாய்வு"
        Language.ENGLISH -> "Torso"
    }

    // Voice Coaching Spoken Cues
    fun getVoiceCue(errorCode: String, lang: Language): String = when (errorCode) {
        "GOOD_FORM" -> when (lang) {
            Language.TAMIL -> "சிறந்த நிலை! தொடர்ந்து செய்யுங்கள்."
            Language.ENGLISH -> "Great form! Keep going."
        }
        "STANCE_TOO_HIGH" -> when (lang) {
            Language.TAMIL -> "முழங்கால்களை மேலும் மடக்குங்கள். நிலையை தாழ்த்துங்கள்."
            Language.ENGLISH -> "Lower your stance. Bend your knees."
        }
        "TORSO_TOO_UPRIGHT" -> when (lang) {
            Language.TAMIL -> "உடலை முன்னோக்கி சாய்க்கவும்."
            Language.ENGLISH -> "Lean your torso forward."
        }
        "UNSTABLE_BASE" -> when (lang) {
            Language.TAMIL -> "கால்களை சற்று அகலமாக வையுங்கள்."
            Language.ENGLISH -> "Widen your feet for stability."
        }
        "KNEES_NOT_BENT" -> when (lang) {
            Language.TAMIL -> "முழங்கால்களை நன்கு மடக்கவும்."
            Language.ENGLISH -> "Bend your knees deeper."
        }
        "HIPS_TOO_HIGH" -> when (lang) {
            Language.TAMIL -> "இடுப்பை கீழே இறக்குங்கள்."
            Language.ENGLISH -> "Lower your hips."
        }
        "UNBALANCED_POSTURE" -> when (lang) {
            Language.TAMIL -> "உடல் எடையை சமமாக வைக்கவும்."
            Language.ENGLISH -> "Balance your weight evenly."
        }
        "NO_POSE_DETECTED" -> when (lang) {
            Language.TAMIL -> "கேமராவின் முன் முழுமையாக நிற்கவும்."
            Language.ENGLISH -> "Step fully into camera frame."
        }
        else -> when (lang) {
            Language.TAMIL -> "நிலையை சரிசெய்யவும்."
            Language.ENGLISH -> "Adjust your stance."
        }
    }

    fun getVoiceSessionStarted(lang: Language): String = when (lang) {
        Language.TAMIL -> "பயிற்சி தொடங்குகிறது. தயாராக இருங்கள்."
        Language.ENGLISH -> "Session started. Ready, go!"
    }

    fun getVoiceSessionStopped(score: Int, lang: Language): String = when (lang) {
        Language.TAMIL -> "பயிற்சி முடிந்தது. மதிப்பெண் $score."
        Language.ENGLISH -> "Session completed! Form score is $score."
    }

    fun getVoiceCoachMuted(lang: Language): String = when (lang) {
        Language.TAMIL -> "குரல் முடக்கப்பட்டது"
        Language.ENGLISH -> "Voice Muted"
    }

    fun getVoiceCoachActive(lang: Language): String = when (lang) {
        Language.TAMIL -> "குரல் வழிகாட்டல் ஆன்"
        Language.ENGLISH -> "Voice Coach Active"
    }
}
