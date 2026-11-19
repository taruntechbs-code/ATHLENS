package com.athlens.app.ui.util

import com.athlens.app.domain.model.Language

object BilingualStrings {

    fun getAppName(lang: Language): String = "ATHLENS"

    fun getTagline(lang: Language): String = when (lang) {
        Language.ENGLISH -> "AI Sports Lab for Grassroots Athletes"
        Language.TAMIL -> "கிராமப்புற தடகள வீரர்களுக்கான AI விளையாட்டு தளம்"
        Language.HINDI -> "जमीनी स्तर के खिलाड़ियों के लिए एआई स्पोर्ट्स लैब"
    }

    // Onboarding Steps
    fun getStep1Title(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Language & Role Selection"
        Language.TAMIL -> "மொழி மற்றும் பங்கு தேர்வு"
        Language.HINDI -> "भाषा और भूमिका का चयन"
    }

    fun getStep1Subtitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Select your preferred language and platform role"
        Language.TAMIL -> "உங்கள் விருப்ப மொழி மற்றும் பங்கினைத் தேர்ந்தெடுக்கவும்"
        Language.HINDI -> "अपनी पसंदीदा भाषा और मंच भूमिका चुनें"
    }

    fun getStep2Title(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Athlete Profile"
        Language.TAMIL -> "விளையாட்டு விவரக்குறிப்பு"
        Language.HINDI -> "खिलाड़ी प्रोफाइल"
    }

    fun getStep2Subtitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Enter your sport and district information"
        Language.TAMIL -> "உங்கள் விளையாட்டு மற்றும் மாவட்ட தகவல்களை உள்ளிடவும்"
        Language.HINDI -> "अपने खेल और जिले की जानकारी दर्ज करें"
    }

    fun getStep3Title(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Guardian Consent & Verification"
        Language.TAMIL -> "பாதுகாவலர் ஒப்புதல் & சரிபார்ப்பு"
        Language.HINDI -> "अभिभावक सहमति और सत्यापन"
    }

    fun getStep3Subtitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Mandatory minor protection and consent verification"
        Language.TAMIL -> "இளம்பருவ வீரர் பாதுகாப்புக்கான சட்டப்பூர்வ ஒப்புதல்"
        Language.HINDI -> "नाबालिग सुरक्षा और सहमति का अनिवार्य सत्यापन"
    }

    // Field labels
    fun getFullNameLabel(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Full Name *"
        Language.TAMIL -> "முழு பெயர் *"
        Language.HINDI -> "पूरा नाम *"
    }

    fun getDateOfBirthLabel(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Date of Birth (DOB) *"
        Language.TAMIL -> "பிறந்த தேதி (DOB) *"
        Language.HINDI -> "जन्म तिथि (DOB) *"
    }

    fun getDistrictLabel(lang: Language): String = when (lang) {
        Language.ENGLISH -> "District (Tamil Nadu) *"
        Language.TAMIL -> "மாவட்டம் (தமிழ்நாடு) *"
        Language.HINDI -> "ज़िला (तमिलनाडु) *"
    }

    fun getSportLabel(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Sport *"
        Language.TAMIL -> "விளையாட்டு *"
        Language.HINDI -> "खेल *"
    }

    fun getPositionLabel(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Playing Position *"
        Language.TAMIL -> "விளையாடும் நிலை *"
        Language.HINDI -> "खेलने की स्थिति *"
    }

    fun getGuardianNameLabel(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Parent / Guardian Full Name *"
        Language.TAMIL -> "பெற்றோர் / பாதுகாவலர் முழு பெயர் *"
        Language.HINDI -> "माता-पिता / अभिभावक का पूरा नाम *"
    }

    fun getGuardianConsentNotice(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Guardian consent is mandatory for athletes under 18 years. I hereby give full consent for training telemetry and athlete passport creation under sports safety norms."
        Language.TAMIL -> "18 வயதுக்குட்பட்ட இளம்பருவ வீரர்களுக்கு பாதுகாவலர் அனுமதி கட்டாயமாகும். உடற்பயிற்சி தரவு மற்றும் தடகள பாஸ்போர்ட் உருவாக்கத்திற்கு நான் முழு ஒப்புதல் அளிக்கிறேன்."
        Language.HINDI -> "18 वर्ष से कम आयु के खिलाड़ियों के लिए अभिभावक सहमति अनिवार्य है। मैं खेल सुरक्षा नियमों के तहत प्रशिक्षण टेलीमेट्री और एथलीट पासपोर्ट निर्माण की पूर्ण सहमति देता/देती हूँ।"
    }

    fun getAdultNotice(lang: Language): String = when (lang) {
        Language.ENGLISH -> "You are 18 or older (Adult Athlete). Guardian consent is not required."
        Language.TAMIL -> "நீங்கள் 18 வயது அல்லது அதற்கு மேற்பட்டவர் (வயது வந்த வீரர்). பாதுகாவலர் ஒப்புதல் தேவையில்லை."
        Language.HINDI -> "आपकी आयु 18 वर्ष या उससे अधिक है (वयस्क खिलाड़ी)। अभिभावक सहमति की आवश्यकता नहीं है।"
    }

    fun getCreateProfileButton(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Create Athlete Profile"
        Language.TAMIL -> "விவரக்குறிப்பை உருவாக்கு"
        Language.HINDI -> "खिलाड़ी प्रोफाइल बनाएं"
    }

    fun getNextButton(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Next"
        Language.TAMIL -> "அடுத்து"
        Language.HINDI -> "आगे बढ़ें"
    }

    fun getBackButton(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Back"
        Language.TAMIL -> "பின்செல்"
        Language.HINDI -> "पीछे"
    }

    // Navigation Tabs
    fun getTabTrain(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Train"
        Language.TAMIL -> "பயிற்சி"
        Language.HINDI -> "अभ्यास"
    }

    fun getTabPassport(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Passport"
        Language.TAMIL -> "பாஸ்போர்ட்"
        Language.HINDI -> "पासपोर्ट"
    }

    fun getTabNutrition(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Nutrition"
        Language.TAMIL -> "ஊட்டச்சத்து"
        Language.HINDI -> "पोषण"
    }

    fun getTabShareP2P(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Share P2P"
        Language.TAMIL -> "பகிர் P2P"
        Language.HINDI -> "शेयर P2P"
    }

    // Dashboard
    fun getGreeting(lang: Language, name: String): String = when (lang) {
        Language.ENGLISH -> "Welcome, $name!"
        Language.TAMIL -> "வணக்கம், $name!"
        Language.HINDI -> "नमस्ते, $name!"
    }

    fun getOfflineBadge(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Offline Mode Ready"
        Language.TAMIL -> "ஆஃப்லைன் பயன்முறை தயார்"
        Language.HINDI -> "ऑफ़लाइन मोड तैयार"
    }

    fun getOfflineTooltip(lang: Language): String = when (lang) {
        Language.ENGLISH -> "All data is securely encrypted on this device"
        Language.TAMIL -> "அனைத்து தரவுகளும் உங்கள் சாதனத்தில் என்க்ரிப்ட் செய்யப்பட்டுள்ளது"
        Language.HINDI -> "सभी डेटा इस डिवाइस पर सुरक्षित रूप से एन्क्रिप्टेड है"
    }

    // Kinematic Feedback & Coaching
    fun getKinematicFeedback(errorCode: String, lang: Language): String = when (errorCode) {
        "GOOD_FORM" -> when (lang) {
            Language.ENGLISH -> "Optimal Stance • Ready for Action"
            Language.TAMIL -> "சிறந்த நிலை • தாக்குதலுக்கு தயார்"
            Language.HINDI -> "उत्कृष्ट स्टांस • खेल के लिए तैयार"
        }
        "STANCE_TOO_HIGH" -> when (lang) {
            Language.ENGLISH -> "Lower Your Stance • Bend Knees More"
            Language.TAMIL -> "நிலையை தாழ்த்தவும் • முழங்கால்களை மேலும் மடக்கவும்"
            Language.HINDI -> "स्टांस नीचा करें • घुटने अधिक मोड़ें"
        }
        "TORSO_TOO_UPRIGHT" -> when (lang) {
            Language.ENGLISH -> "Lean Torso Forward • Engage Attack Angle"
            Language.TAMIL -> "உடலை முன்னோக்கி சாய்க்கவும் • தாக்குதல் கோணம் தேவை"
            Language.HINDI -> "शरीर आगे झुकाएं • आक्रमण कोण बनाएं"
        }
        "UNSTABLE_BASE" -> when (lang) {
            Language.ENGLISH -> "Widen Foot Stance • Strengthen Base"
            Language.TAMIL -> "கால்களை அகல வைக்கவும் • நிலையான அடித்தளம் தேவை"
            Language.HINDI -> "पैर चौड़े रखें • आधार मजबूत करें"
        }
        "KNEES_NOT_BENT" -> when (lang) {
            Language.ENGLISH -> "Deepen Crouch • Bend Both Knees"
            Language.TAMIL -> "ஆழமாக அமரவும் • முழங்கால்களை நன்கு மடக்கவும்"
            Language.HINDI -> "गहरा क्राउच बनाएं • दोनों घुटने मोड़ें"
        }
        "HIPS_TOO_HIGH" -> when (lang) {
            Language.ENGLISH -> "Lower Hips Closer to Knee Level"
            Language.TAMIL -> "இடுப்பு நிலையை கீழே இறக்கவும் • தாழ்வாக இருங்கள்"
            Language.HINDI -> "कूल्हों को नीचे लाएं • घुटने के स्तर तक झुकें"
        }
        "UNBALANCED_POSTURE" -> when (lang) {
            Language.ENGLISH -> "Balance Weight Evenly on Footing"
            Language.TAMIL -> "உடல் எடையை சமநிலையில் வைக்கவும் • சமச்சீர் நிலை தேவை"
            Language.HINDI -> "दोनों पैरों पर वजन संतुलित रखें"
        }
        "NO_POSE_DETECTED" -> when (lang) {
            Language.ENGLISH -> "Step into Camera Frame"
            Language.TAMIL -> "கேமரா பார்வையில் முழு உடலும் தெரிய நிற்கவும்"
            Language.HINDI -> "कैमरा फ्रेम के सामने आएं"
        }
        else -> when (lang) {
            Language.ENGLISH -> "Adjust Your Posture"
            Language.TAMIL -> "நிலையை சரிசெய்யவும்"
            Language.HINDI -> "अपनी मुद्रा सही करें"
        }
    }

    fun getCameraCoachTitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "AI Camera Coach"
        Language.TAMIL -> "AI கேமரா பயிற்சியாளர்"
        Language.HINDI -> "एआई कैमरा कोच"
    }

    fun getStartSession(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Start Drill Session"
        Language.TAMIL -> "பயிற்சியைத் தொடங்கு"
        Language.HINDI -> "ड्रिल सत्र शुरू करें"
    }

    fun getStopAndSaveSession(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Stop & Save Session"
        Language.TAMIL -> "முடித்து சேமிக்கவும்"
        Language.HINDI -> "सत्र समाप्त व सहेजें"
    }

    fun getSessionSaved(lang: Language, score: Int): String = when (lang) {
        Language.ENGLISH -> "Session Saved! Form Score: $score/100"
        Language.TAMIL -> "பயிற்சி சேமிக்கப்பட்டது! மதிப்பெண்: $score/100"
        Language.HINDI -> "सत्र सहेजा गया! फॉर्म स्कोर: $score/100"
    }

    fun getKneeAngleLabel(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Knee"
        Language.TAMIL -> "முழங்கால்"
        Language.HINDI -> "घुटना"
    }

    fun getTorsoLeanLabel(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Torso"
        Language.TAMIL -> "உடல் சாய்வு"
        Language.HINDI -> "धड़ का झुकाव"
    }

    // Voice Coaching Spoken Cues
    fun getVoiceCue(errorCode: String, lang: Language): String = when (errorCode) {
        "GOOD_FORM" -> when (lang) {
            Language.ENGLISH -> "Great form! Keep going."
            Language.TAMIL -> "சிறந்த நிலை! தொடர்ந்து செய்யுங்கள்."
            Language.HINDI -> "बहुत बढ़िया फॉर्म! जारी रखें।"
        }
        "STANCE_TOO_HIGH" -> when (lang) {
            Language.ENGLISH -> "Lower your stance. Bend your knees."
            Language.TAMIL -> "முழங்கால்களை மேலும் மடக்குங்கள். நிலையை தாழ்த்துங்கள்."
            Language.HINDI -> "स्टांस नीचा करें। घुटने और मोड़ें।"
        }
        "TORSO_TOO_UPRIGHT" -> when (lang) {
            Language.ENGLISH -> "Lean your torso forward."
            Language.TAMIL -> "உடலை சற்று முன்னோக்கி சாய்க்கவும்."
            Language.HINDI -> "धड़ को थोड़ा आगे झुकाएं।"
        }
        "UNSTABLE_BASE" -> when (lang) {
            Language.ENGLISH -> "Widen your feet for stability."
            Language.TAMIL -> "கால்களை சற்று அகலமாக வையுங்கள்."
            Language.HINDI -> "संतुलन के लिए पैर चौड़े करें।"
        }
        "KNEES_NOT_BENT" -> when (lang) {
            Language.ENGLISH -> "Bend your knees deeper."
            Language.TAMIL -> "முழங்கால்களை நன்கு மடக்கவும்."
            Language.HINDI -> "घुटने और गहराई तक मोड़ें।"
        }
        "HIPS_TOO_HIGH" -> when (lang) {
            Language.ENGLISH -> "Lower your hips."
            Language.TAMIL -> "இடுப்பை கீழே இறக்குங்கள்."
            Language.HINDI -> "कूल्हों को नीचे लाएं।"
        }
        "UNBALANCED_POSTURE" -> when (lang) {
            Language.ENGLISH -> "Balance your weight evenly."
            Language.TAMIL -> "உடல் எடையை சமமாக வைக்கவும்."
            Language.HINDI -> "शरीर का वजन बराबर बांटें।"
        }
        "NO_POSE_DETECTED" -> when (lang) {
            Language.ENGLISH -> "Step fully into camera frame."
            Language.TAMIL -> "கேமராவின் முன் முழுமையாக நிற்கவும்."
            Language.HINDI -> "कैमरे के पूरे फ्रेम में आएं।"
        }
        else -> when (lang) {
            Language.ENGLISH -> "Adjust your stance."
            Language.TAMIL -> "நிலையை சரிசெய்யவும்."
            Language.HINDI -> "अपनी मुद्रा ठीक करें।"
        }
    }

    fun getVoiceSessionStarted(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Session started. Ready, go!"
        Language.TAMIL -> "பயிற்சி தொடங்குகிறது. தயாராக இருங்கள்."
        Language.HINDI -> "सत्र शुरू हो गया है। तैयार, शुरू!"
    }

    fun getVoiceSessionStopped(score: Int, lang: Language): String = when (lang) {
        Language.ENGLISH -> "Session completed! Form score is $score."
        Language.TAMIL -> "பயிற்சி முடிந்தது. மதிப்பெண் $score."
        Language.HINDI -> "सत्र समाप्त! फॉर्म स्कोर है $score."
    }

    fun getVoiceCoachMuted(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Voice Muted"
        Language.TAMIL -> "குரல் முடக்கப்பட்டது"
        Language.HINDI -> "आवाज म्यूट"
    }

    fun getVoiceCoachActive(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Voice Coach Active"
        Language.TAMIL -> "குரல் வழிகாட்டல் ஆன்"
        Language.HINDI -> "वॉयस कोच सक्रिय"
    }

    // Phase 4A: Maturity-Aware Talent Assessment Strings
    fun getMaturityCardTitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Maturity-Aware Talent Context"
        Language.TAMIL -> "வளர்ச்சி நிலை விழிப்புணர்வு சூழல்"
        Language.HINDI -> "परिपक्वता-जागरूक प्रतिभा संदर्भ"
    }

    fun getMaturityDisclaimer(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Development context for coaches — not an automated player ranking."
        Language.TAMIL -> "பயிற்சியாளர்களுக்கான வளர்ச்சி சூழல் — தானியங்கி வீரர் தரவரிசை அல்ல."
        Language.HINDI -> "प्रशिक्षकों के लिए विकास संदर्भ — यह कोई स्वचालित खिलाड़ी रैंकिंग नहीं है।"
    }

    fun getExactAgeLabel(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Exact Age"
        Language.TAMIL -> "துல்லியமான வயது"
        Language.HINDI -> "सटीक आयु"
    }

    fun getRaeQuarterLabel(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Birth Quarter (RAE)"
        Language.TAMIL -> "பிறந்த காலாண்டு (RAE)"
        Language.HINDI -> "जन्म तिमाही (RAE)"
    }

    fun getGrowthVelocityLabel(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Growth Velocity"
        Language.TAMIL -> "வளர்ச்சி வேகம்"
        Language.HINDI -> "वृद्धि दर (वेलोसिटी)"
    }

    fun getProgressionDeltaLabel(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Form Progression"
        Language.TAMIL -> "படிவ முன்னேற்றம்"
        Language.HINDI -> "प्रगति सुधार"
    }

    fun getLogGrowthButton(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Log Height / Weight"
        Language.TAMIL -> "உயரம் / எடை பதிவுசெய்"
        Language.HINDI -> "ऊंचाई / वजन दर्ज करें"
    }

    fun getHeightLabel(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Height (cm)"
        Language.TAMIL -> "உயரம் (செ.மீ)"
        Language.HINDI -> "ऊंचाई (सेमी)"
    }

    fun getWeightLabel(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Weight (kg)"
        Language.TAMIL -> "எடை (கிலோ)"
        Language.HINDI -> "वजन (किग्रा)"
    }

    fun getSaveMeasurement(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Save Measurement"
        Language.TAMIL -> "அளவீட்டைச் சேமி"
        Language.HINDI -> "माप सहेजें"
    }

    fun getCancelButton(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Cancel"
        Language.TAMIL -> "ரத்துசெய்"
        Language.HINDI -> "रद्द करें"
    }

    fun getLateBornBadge(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Q3/Q4 Late-Born Athlete • Maturation buffer active"
        Language.TAMIL -> "Q3/Q4 தாமதமாகப் பிறந்த வீரர் • முதிர்ச்சி பாதுகாப்பு செயல்படுகிறது"
        Language.HINDI -> "Q3/Q4 उत्तरार्ध में जन्मे खिलाड़ी • परिपक्वता बफर सक्रिय"
    }

    // Phase 4B: Nutrition & Recovery Strings
    fun getNutritionHeaderTitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Affordable Sports Nutrition"
        Language.TAMIL -> "மலிவு விளையாட்டு ஊட்டச்சத்து"
        Language.HINDI -> "किफायती खेल पोषण एवं रिकवरी"
    }

    fun getNutritionHeaderSubtitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Grassroots recovery & high-protein plans under ₹30/day"
        Language.TAMIL -> "நாள் ஒன்றுக்கு ₹30-க்குள் கிடைக்கும் உள்ளூர் மீட்பு உணவுகள்"
        Language.HINDI -> "₹30/दिन के अंदर स्थानीय उच्च-प्रोटीन और रिकवरी आहार"
    }

    fun getHydrationCardTitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Daily Hydration Tracker"
        Language.TAMIL -> "தினசரி நீர்ச்சத்து கண்காணிப்பு"
        Language.HINDI -> "दैनिक जलयोजन (हाइड्रेशन) ट्रैकर"
    }

    fun getHydrationGoalLabel(currentMl: Int, lang: Language): String = when (lang) {
        Language.ENGLISH -> "$currentMl / 3,000 ml"
        Language.TAMIL -> "$currentMl / 3,000 மி.லி"
        Language.HINDI -> "$currentMl / 3,000 मिली"
    }

    fun getAddWaterButton(lang: Language): String = when (lang) {
        Language.ENGLISH -> "+250 ml Glass"
        Language.TAMIL -> "+250 மி.லி டம்ளர்"
        Language.HINDI -> "+250 मिली ग्लास"
    }

    fun getResetHydration(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Reset"
        Language.TAMIL -> "மீட்டமை"
        Language.HINDI -> "रीसेट"
    }

    fun getPreTrainingTitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Pre-Training Energy (30-45m before)"
        Language.TAMIL -> "பயிற்சிக்கு முந்தைய ஆற்றல் (30-45 நிமி முன்)"
        Language.HINDI -> "अभ्यास-पूर्व ऊर्जा (30-45 मिनट पहले)"
    }

    fun getPostTrainingTitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Post-Training Protein (within 45m)"
        Language.TAMIL -> "பயிற்சிக்கு பிந்தைய புரதம் (45 நிமிக்குள்)"
        Language.HINDI -> "अभ्यास बाद प्रोटीन (45 मिनट के भीतर)"
    }

    fun getElectrolytesTitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Hydration & Electrolytes"
        Language.TAMIL -> "நீர்ச்சத்து & எலக்ட்ரோலைட்டுகள்"
        Language.HINDI -> "इलेक्ट्रोलाइट्स एवं जलयोजन"
    }

    fun getEveningRecoveryTitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> "Evening Recovery & Sleep Discipline"
        Language.TAMIL -> "மாலை மீட்பு & தூக்க ஒழுக்கம்"
        Language.HINDI -> "रात्रि रिकवरी एवं नींद अनुशासन"
    }

    fun getNutritionDisclaimer(lang: Language): String = when (lang) {
        Language.ENGLISH -> "General sports nutrition guidance for grassroots athletes — not a substitute for clinical dietetic advice."
        Language.TAMIL -> "கிராமப்புற வீரர்களுக்கான பொது விளையாட்டு ஊட்டச்சத்து வழிகாட்டல் — மருத்துவ உணவு ஆலோசனைக்கு மாற்றாகாது."
        Language.HINDI -> "खिलाड़ियों के लिए सामान्य खेल पोषण मार्गदर्शन — यह चिकित्सीय आहार परामर्श का विकल्प नहीं है।"
    }
}
