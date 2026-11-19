package com.athlens.app.ui.dashboard.tabs

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.athlens.app.domain.model.Language
import com.athlens.app.domain.model.Sport
import com.athlens.app.domain.nutrition.NutritionCategory
import com.athlens.app.domain.nutrition.NutritionMealItem
import com.athlens.app.domain.nutrition.NutritionPlanProvider
import com.athlens.app.ui.theme.CardNavy
import com.athlens.app.ui.theme.CardNavyElevated
import com.athlens.app.ui.theme.DeepNavy
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.ElectricEmeraldDark
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextMuted
import com.athlens.app.ui.theme.TextWhite
import com.athlens.app.ui.theme.WarmAmber
import com.athlens.app.ui.util.BilingualStrings
import java.time.LocalDate

@Composable
fun NutritionTab(
    sport: Sport = Sport.KABADDI,
    language: Language,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Persistent Daily Hydration Tracker (saved per calendar day in SharedPreferences)
    val todayDateStr = remember { LocalDate.now().toString() }
    val hydrationPrefs = remember {
        context.getSharedPreferences("athlens_hydration_prefs", Context.MODE_PRIVATE)
    }
    var waterConsumedMl by remember {
        mutableIntStateOf(hydrationPrefs.getInt("water_$todayDateStr", 0))
    }

    val checkedMeals = remember { mutableStateMapOf<String, Boolean>() }

    val meals = remember(sport) {
        NutritionPlanProvider.getPlanForSport(sport)
    }

    val targetMl = 3000
    val progress = (waterConsumedMl.toFloat() / targetMl.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(2.dp, ElectricEmerald),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = "Nutrition",
                            tint = ElectricEmerald,
                            modifier = Modifier.size(26.dp)
                        )
                        Text(
                            text = BilingualStrings.getNutritionHeaderTitle(language),
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        color = ElectricEmeraldDark,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = sport.getDisplayName(language),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextWhite,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = BilingualStrings.getNutritionHeaderSubtitle(language),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }
        }

        // Daily Hydration Tracker Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(1.dp, CardNavyElevated),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Hydration",
                            tint = ElectricEmerald,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = BilingualStrings.getHydrationCardTitle(language),
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = BilingualStrings.getHydrationGoalLabel(waterConsumedMl, language),
                        style = MaterialTheme.typography.titleSmall,
                        color = ElectricEmerald
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = ElectricEmerald,
                    trackColor = DeepNavy,
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val newAmount = (waterConsumedMl + 250).coerceAtMost(5000)
                            waterConsumedMl = newAmount
                            hydrationPrefs.edit().putInt("water_$todayDateStr", newAmount).apply()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricEmerald,
                            contentColor = DeepNavy
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = BilingualStrings.getAddWaterButton(language),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            waterConsumedMl = 0
                            hydrationPrefs.edit().putInt("water_$todayDateStr", 0).apply()
                        },
                        border = BorderStroke(1.dp, CardNavyElevated),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.heightIn(min = 44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = BilingualStrings.getResetHydration(language),
                            style = MaterialTheme.typography.labelMedium,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Daily Nutrition & Recovery Plan Meal Checklist
        Text(
            text = when (language) {
                Language.ENGLISH -> "Daily Meal & Recovery Checklist"
                Language.TAMIL -> "தினசரி உணவு மற்றும் மீட்பு பட்டியல்"
                Language.HINDI -> "दैनिक आहार और रिकवरी चेकलिस्ट"
            },
            style = MaterialTheme.typography.titleMedium,
            color = ElectricEmerald
        )

        meals.forEach { meal ->
            val isChecked = checkedMeals[meal.id] ?: false
            MealItemCard(
                meal = meal,
                isChecked = isChecked,
                language = language,
                onCheckedChange = { checkedMeals[meal.id] = it }
            )
        }

        // Educational Disclaimer Footer
        Surface(
            color = CardNavyElevated.copy(alpha = 0.5f),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = BilingualStrings.getNutritionDisclaimer(language),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Bottom space so last card glides smoothly underneath floating glass nav bar
        Spacer(modifier = Modifier.navigationBarsPadding().height(104.dp))
    }
}

@Composable
private fun MealItemCard(
    meal: NutritionMealItem,
    isChecked: Boolean,
    language: Language,
    onCheckedChange: (Boolean) -> Unit
) {
    val categoryIcon = when (meal.category) {
        NutritionCategory.PRE_TRAINING_ENERGY -> Icons.Default.FitnessCenter
        NutritionCategory.POST_TRAINING_PROTEIN -> Icons.Default.Eco
        NutritionCategory.HYDRATION_ELECTROLYTES -> Icons.Default.LocalDrink
        NutritionCategory.EVENING_RECOVERY -> Icons.Default.Nightlight
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isChecked) CardNavyElevated else CardNavy
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isChecked) ElectricEmerald.copy(alpha = 0.6f) else CardNavyElevated
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = categoryIcon,
                        contentDescription = null,
                        tint = if (isChecked) ElectricEmerald else WarmAmber,
                        modifier = Modifier.size(22.dp)
                    )
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = meal.getTitle(language),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isChecked) ElectricEmerald else TextWhite,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = meal.getTiming(language),
                            style = MaterialTheme.typography.labelSmall,
                            color = WarmAmber,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    color = DeepNavy,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, CardNavyElevated)
                ) {
                    Text(
                        text = meal.costEstimate,
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricEmerald,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1
                    )
                }

                Checkbox(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = ElectricEmerald,
                        checkmarkColor = DeepNavy,
                        uncheckedColor = TextMuted
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = CardNavyElevated)
            Spacer(modifier = Modifier.height(10.dp))

            // Ingredients
            Text(
                text = meal.getIngredients(language),
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Macro / Recovery Benefit
            Surface(
                color = DeepNavy.copy(alpha = 0.6f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(ElectricEmerald)
                    )
                    Text(
                        text = meal.getMacroBenefit(language),
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricEmerald
                    )
                }
            }
        }
    }
}
