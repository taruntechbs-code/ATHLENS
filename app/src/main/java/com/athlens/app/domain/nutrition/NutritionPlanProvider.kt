package com.athlens.app.domain.nutrition

import androidx.annotation.DrawableRes
import com.athlens.app.R
import com.athlens.app.domain.model.Language
import com.athlens.app.domain.model.Sport

enum class NutritionCategory {
    PRE_TRAINING_ENERGY,
    POST_TRAINING_PROTEIN,
    HYDRATION_ELECTROLYTES,
    EVENING_RECOVERY
}

data class NutritionMealItem(
    val id: String,
    val category: NutritionCategory,
    @DrawableRes val imageRes: Int,
    val titleEn: String,
    val titleTa: String,
    val titleHi: String,
    val timingEn: String,
    val timingTa: String,
    val timingHi: String,
    val ingredientsEn: String,
    val ingredientsTa: String,
    val ingredientsHi: String,
    val costEstimate: String,
    val macroBenefitEn: String,
    val macroBenefitTa: String,
    val macroBenefitHi: String
) {
    fun getTitle(lang: Language): String = when (lang) {
        Language.ENGLISH -> titleEn
        Language.TAMIL -> titleTa
        Language.HINDI -> titleHi
    }

    fun getTiming(lang: Language): String = when (lang) {
        Language.ENGLISH -> timingEn
        Language.TAMIL -> timingTa
        Language.HINDI -> timingHi
    }

    fun getIngredients(lang: Language): String = when (lang) {
        Language.ENGLISH -> ingredientsEn
        Language.TAMIL -> ingredientsTa
        Language.HINDI -> ingredientsHi
    }

    fun getMacroBenefit(lang: Language): String = when (lang) {
        Language.ENGLISH -> macroBenefitEn
        Language.TAMIL -> macroBenefitTa
        Language.HINDI -> macroBenefitHi
    }
}

object NutritionPlanProvider {

    fun getPlanForSport(sport: Sport): List<NutritionMealItem> {
        val basePlan = listOf(
            NutritionMealItem(
                id = "pre_train_1",
                category = NutritionCategory.PRE_TRAINING_ENERGY,
                imageRes = R.drawable.img_food_ragi_koozh,
                titleEn = "Ragi Koozh with Small Onions",
                titleTa = "கேழ்வரகு கூழ் மற்றும் சின்ன வெங்காயம்",
                titleHi = "रागी का दलिया (कूझ) व छोटे प्याज",
                timingEn = "30–45 mins before training",
                timingTa = "பயிற்சிக்கு 30–45 நிமிடங்களுக்கு முன்",
                timingHi = "अभ्यास से 30–45 मिनट पहले",
                ingredientsEn = "Finger millet flour (ragi), curd/buttermilk, mor milagai, shallots",
                ingredientsTa = "கேழ்வரகு மாவு, மோர், மோர் மிளகாய், சின்ன வெங்காயம்",
                ingredientsHi = "रागी का आटा, छाछ, भुनी हरी मिर्च, छोटे प्याज",
                costEstimate = "₹15–20",
                macroBenefitEn = "Complex low-GI carbs + sustained endurance & cooling electrolytes",
                macroBenefitTa = "நீடித்த சகிப்புத்தன்மைக்கான சிக்கலான கார்போஹைட்ரேட் மற்றும் குளிர்ச்சி",
                macroBenefitHi = "धीमी गति से पचने वाले जटिल कार्ब्स, लंबी ऊर्जा व शीतलता"
            ),
            NutritionMealItem(
                id = "pre_train_2",
                category = NutritionCategory.PRE_TRAINING_ENERGY,
                imageRes = R.drawable.img_food_banana_peanuts,
                titleEn = "Ripe Banana + Roasted Peanuts (Kadalai)",
                titleTa = "வாழைப்பழம் + வறுத்த வேர்க்கடலை",
                titleHi = "पका केला + भुनी हुई मूंगफली",
                timingEn = "20–30 mins before sprint / raid drills",
                timingTa = "ரெய்டு / ஸ்பிரிண்ட் பயிற்சிக்கு 20–30 நிமி முன்",
                timingHi = "रेड / स्प्रिंट ड्रिल से 20–30 मिनट पहले",
                ingredientsEn = "2 Poovan / Karpooravalli bananas, 30g roasted peanuts",
                ingredientsTa = "2 பூவன்/கற்பூரவள்ளி வாழைப்பழம், 30 கிராம் வேர்க்கடலை",
                ingredientsHi = "2 केले, 30 ग्राम भुनी मूंगफली",
                costEstimate = "₹15–25",
                macroBenefitEn = "Quick potassium for muscle contractions + dense healthy fats",
                macroBenefitTa = "தசைப்பிடிப்பைத் தடுக்கும் பொட்டாசியம் மற்றும் ஆரோக்கியமான கொழுப்பு",
                macroBenefitHi = "मांसपेशियों के संकुचन के लिए पोटेशियम व स्वस्थ वसा"
            ),
            NutritionMealItem(
                id = "post_train_1",
                category = NutritionCategory.POST_TRAINING_PROTEIN,
                imageRes = R.drawable.img_food_sundal,
                titleEn = "Boiled Black Chana Sundal (Kondakadalai)",
                titleTa = "கருப்பு கொண்டைக்கடலை சுண்டல்",
                titleHi = "उबला काला चना सुंडल",
                timingEn = "Within 45 mins after session",
                timingTa = "பயிற்சி முடிந்த 45 நிமிடங்களுக்குள்",
                timingHi = "सत्र समाप्त होने के 45 मिनट के भीतर",
                ingredientsEn = "100g soaked black chickpeas, mustard, curry leaves, grated coconut",
                ingredientsTa = "100 கிராம் கருப்பு சுண்டல், கடுகு, கருவேப்பிலை, தேங்காய் துருவல்",
                ingredientsHi = "100 ग्राम भीगा व उबला काला चना, करी पत्ता, थोड़ा कसा नारियल",
                costEstimate = "₹20–25",
                macroBenefitEn = "~15g plant protein + iron & magnesium for muscle micro-tear repair",
                macroBenefitTa = "~15 கிராம் தாவர புரதம், இரும்புச்சத்து மற்றும் தசை மீட்பு",
                macroBenefitHi = "~15 ग्राम पादप प्रोटीन, आयरन व मांसपेशियों की मरम्मत"
            ),
            NutritionMealItem(
                id = "post_train_2",
                category = NutritionCategory.POST_TRAINING_PROTEIN,
                imageRes = R.drawable.img_food_eggs_sprouts,
                titleEn = "2 Boiled Country Eggs / Sprouted Green Gram",
                titleTa = "2 நாட்டுக்கோழி முட்டை / முளைகட்டிய பாசிப்பயறு",
                titleHi = "2 उबले अंडे / अंकुरित हरी मूंग",
                timingEn = "Within 30–60 mins post-session",
                timingTa = "பயிற்சி முடிந்த 30–60 நிமிடங்களுக்குள்",
                timingHi = "अभ्यास के 30–60 मिनट के भीतर",
                ingredientsEn = "2 boiled eggs with salt & pepper OR 1 cup sprouted moong",
                ingredientsTa = "2 வேகவைத்த முட்டை மிளகுத்தூளுடன் அல்லது 1 கப் முளைப்பயறு",
                ingredientsHi = "2 उबले अंडे (सेंधा नमक/काली मिर्च) या 1 कप अंकुरित मूंग",
                costEstimate = "₹20–30",
                macroBenefitEn = "Complete amino acid profile (BCAAs) for fast muscle synthesis",
                macroBenefitTa = "விரைவான தசை வளர்ச்சி மற்றும் பலத்திற்கான முழுமையான அமினோ அமிலம்",
                macroBenefitHi = "मांसपेशियों के निर्माण के लिए पूर्ण अमीनो एसिड प्रोफाइल (BCAA)"
            ),
            NutritionMealItem(
                id = "hydration_1",
                category = NutritionCategory.HYDRATION_ELECTROLYTES,
                imageRes = R.drawable.img_food_neer_mor,
                titleEn = "Spiced Neer Mor (Village Buttermilk)",
                titleTa = "கிராமத்து மசாலா நீர் மோர்",
                titleHi = "मसालेदार छाछ (नीरु मोर)",
                timingEn = "During short drill breaks & right after training",
                timingTa = "பயிற்சி இடைவேளையில் மற்றும் முடிந்தவுடன்",
                timingHi = "अभ्यास के दौरान पानी ब्रेक पर व तुरंत बाद",
                ingredientsEn = "Thin buttermilk, crushed ginger, green chili, curry leaves, rock salt",
                ingredientsTa = "கடைந்த மோர், இஞ்சி, பச்சை மிளகாய், கருவேப்பிலை, கல் உப்பு",
                ingredientsHi = "पतली छाछ, कुटा अदरक, करी पत्ता, थोड़ा सेंधा नमक",
                costEstimate = "₹10",
                macroBenefitEn = "Natural sodium + potassium electrolyte replenishment, prevents cramps",
                macroBenefitTa = "இயற்கை சோடியம் மற்றும் பொட்டாசியம், தசைப்பிடிப்பைத் தடுக்கும்",
                macroBenefitHi = "प्राकृतिक सोडियम-पोटेशियम इलेक्ट्रोलाइट, ऐंठन से बचाव"
            ),
            NutritionMealItem(
                id = "evening_recovery_1",
                category = NutritionCategory.EVENING_RECOVERY,
                imageRes = R.drawable.img_food_sathumaavu,
                titleEn = "Traditional Sathumaavu (Multi-Grain Health Mix)",
                titleTa = "பாரம்பரிய சத்துமாவு கஞ்சி",
                titleHi = "पारंपरिक सत्तू / बहु-अनाज हेल्थ मिक्स दलिया",
                timingEn = "Evening / 1.5 hrs before bed",
                timingTa = "மாலை அல்லது உறங்குவதற்கு 1.5 மணி நேரம் முன்",
                timingHi = "शाम को या सोने से 1.5 घंटे पहले",
                ingredientsEn = "Millets, roasted gram, barley, nuts powder boiled with hot milk/water",
                ingredientsTa = "சிறுதானியங்கள், பொட்டுக்கடலை, பார்லி மாவு கஞ்சி",
                ingredientsHi = "बाजरा, भुने चने, जौ व मेवों का सत्तू गर्म दूध/पानी के साथ",
                costEstimate = "₹20–25",
                macroBenefitEn = "Slow-release protein + calcium + deep cellular recovery during sleep",
                macroBenefitTa = "மெதுவாக வெளிப்படும் புரதம் மற்றும் ஆழ்ந்த தூக்கத்திற்கான கால்சியம்",
                macroBenefitHi = "धीमा प्रोटीन, कैल्शियम व गहरी नींद के दौरान कोशिकीय रिकवरी"
            )
        )

        return if (sport == Sport.KABADDI) {
            basePlan
        } else {
            // Kho-Kho: additional focus on continuous running agility & rapid glycogen restoration
            basePlan
        }
    }
}
