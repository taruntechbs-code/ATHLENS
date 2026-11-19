package com.athlens.app.ui.dashboard.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.athlens.app.domain.model.Language
import com.athlens.app.ui.theme.CardNavy
import com.athlens.app.ui.theme.CardNavyElevated
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.ElectricEmeraldDark
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextMuted
import com.athlens.app.ui.theme.TextWhite
import com.athlens.app.ui.theme.WarmAmber

@Composable
fun NutritionTab(
    language: Language,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Phase 4 Nutrition Plan Banner Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(2.dp, ElectricEmerald),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = "Nutrition",
                            tint = ElectricEmerald,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (language == Language.TAMIL) "கிராமப்புற விளையாட்டு ஊட்டச்சத்து" else "Affordable Sports Nutrition",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextWhite
                        )
                    }

                    Surface(
                        color = ElectricEmeraldDark,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "PHASE 4",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextWhite,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (language == Language.TAMIL) {
                        "தமிழ்நாட்டு கிராமப்புற தடகள வீரர்களுக்கான எளிய, மலிவான, அதிக சத்துள்ள பாரம்பரிய உணவு மற்றும் மீட்பு திட்டம் (Phase 4)."
                    } else {
                        "Indigenous, highly accessible Tamil Nadu nutritional and recovery protocol designed specifically for grassroots Kabaddi & Kho-Kho endurance."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            }
        }

        // Local Grassroots Superfoods List
        Text(
            text = if (language == Language.TAMIL) "முக்கிய பாரம்பரிய உணவுகள் / TN Indigenous Superfoods" else "Grassroots Superfoods (Tamil Nadu)",
            style = MaterialTheme.typography.titleMedium,
            color = ElectricEmerald
        )

        SuperfoodCard(
            title = if (language == Language.TAMIL) "ராகி களி / கேழ்வரகு கூழ்" else "Ragi Kali & Finger Millet Koozh",
            benefit = if (language == Language.TAMIL) {
                "அதிக கால்சியம் எலும்பு வலிமைக்கு, மெதுவான ஆற்றல் வெளியீடு கபடி ஆட்டத்திற்கு உகந்தது."
            } else {
                "High calcium for bone resilience in contact tackles, sustained complex carbohydrates."
            },
            costTag = "< ₹15 / meal",
            icon = Icons.Default.Eco
        )

        SuperfoodCard(
            title = if (language == Language.TAMIL) "வேகவைத்த சுண்டல் & முட்டை" else "Boiled Sundal & Farm Eggs",
            benefit = if (language == Language.TAMIL) {
                "25-30 கிராம் தசை மீட்பு புரதம். தசை கிழிசல் மற்றும் பயிற்சி சோர்வை விரைவாக குணப்படுத்தும்."
            } else {
                "25-30g cost-effective protein for rapid muscular repair after grueling Kho-Kho & Kabaddi drills."
            },
            costTag = "High Protein",
            icon = Icons.Default.FitnessCenter
        )

        SuperfoodCard(
            title = if (language == Language.TAMIL) "இஞ்சி உப்பு நீர் மோர்" else "Spiced Neer Mor (Buttermilk)",
            benefit = if (language == Language.TAMIL) {
                "வியர்வை இழப்பை ஈடுசெய்யும் இயற்கை எலக்ட்ரோலைட், தசைப்பிடிப்பை (Cramps) தடுக்கும்."
            } else {
                "Natural electrolyte recovery balancing sodium-potassium balance in TN summer heat without artificial drinks."
            },
            costTag = "Electrolytes",
            icon = Icons.Default.LocalDrink
        )
    }
}

@Composable
private fun SuperfoodCard(
    title: String,
    benefit: String,
    costTag: String,
    icon: ImageVector
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = BorderStroke(1.dp, CardNavyElevated),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = WarmAmber,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite
                    )
                    Surface(
                        color = CardNavyElevated,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = costTag,
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricEmerald,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = benefit,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }
        }
    }
}
