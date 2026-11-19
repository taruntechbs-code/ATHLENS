package com.athlens.app.ui.dashboard.tabs

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athlens.app.R
import com.athlens.app.domain.model.Language
import com.athlens.app.domain.model.Sport
import com.athlens.app.domain.nutrition.NutritionCategory
import com.athlens.app.domain.nutrition.NutritionMealItem
import com.athlens.app.domain.nutrition.NutritionPlanProvider
import com.athlens.app.ui.theme.ContourGlassCard
import com.athlens.app.ui.theme.DeepNavy
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.GlassBorderEmerald
import com.athlens.app.ui.theme.GlassBorderStandard
import com.athlens.app.ui.theme.LiquidGlassButton
import com.athlens.app.ui.theme.LiquidGlassPill
import com.athlens.app.ui.theme.OceanTeal
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextMuted
import com.athlens.app.ui.theme.TextWhite
import com.athlens.app.ui.theme.WarmAmber
import com.athlens.app.ui.util.BilingualStrings
import dev.chrisbanes.haze.HazeState
import java.time.LocalDate

@Composable
fun NutritionTab(
    sport: Sport = Sport.KABADDI,
    language: Language,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
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
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600),
        label = "water_progress"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Editorial Header Bar
        NutritionHeaderBar(sport = sport, language = language, hazeState = hazeState)

        // 2. Liquid Hydration Tracker Card
        ContourGlassCard(
            hazeState = hazeState,
            borderBrush = GlassBorderEmerald,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Row: Water drop icon + Title + Percentage
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(OceanTeal.copy(alpha = 0.22f))
                                .border(1.2.dp, OceanTeal, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Water Intake",
                                tint = OceanTeal,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = when (language) {
                                    Language.ENGLISH -> "Daily Hydration Tracker"
                                    Language.TAMIL -> "தினசரி நீரேற்றம்"
                                    Language.HINDI -> "दैनिक जलयोजन ट्रैकर"
                                },
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextWhite
                            )
                            Text(
                                text = "Electrolytes & Fluid Balance",
                                style = MaterialTheme.typography.bodySmall,
                                color = OceanTeal
                            )
                        }
                    }

                    LiquidGlassPill(
                        text = "${(progress * 100).toInt()}%",
                        isSelected = true,
                        tintColor = OceanTeal
                    )
                }

                // Translucent Glass Water Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, GlassBorderStandard, CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(OceanTeal, ElectricEmerald)
                                )
                            )
                    )
                }

                // Volume readout & Controls Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$waterConsumedMl / $targetMl ml",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = TextWhite
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LiquidGlassButton(
                            onClick = {
                                waterConsumedMl = (waterConsumedMl + 250).coerceAtMost(5000)
                                hydrationPrefs.edit().putInt("water_$todayDateStr", waterConsumedMl).apply()
                            },
                            isPrimary = true,
                            modifier = Modifier.heightIn(min = 38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = DeepNavy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+250 ml",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFeatureSettings = "tnum"
                                ),
                                color = DeepNavy
                            )
                        }

                        LiquidGlassPill(
                            text = "Reset",
                            isSelected = false,
                            onClick = {
                                waterConsumedMl = 0
                                hydrationPrefs.edit().putInt("water_$todayDateStr", 0).apply()
                            }
                        )
                    }
                }
            }
        }

        // 3. Section Title: Grassroots Nutrition Checklist
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (language) {
                    Language.ENGLISH -> "Daily Fueling Checklist"
                    Language.TAMIL -> "தினசரி உணவுப் பட்டியல்"
                    Language.HINDI -> "दैनिक पोषण योजना"
                },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = ElectricEmerald
            )

            val checkedCount = checkedMeals.values.count { it }
            LiquidGlassPill(
                text = "$checkedCount / ${meals.size} Done",
                isSelected = checkedCount > 0,
                tintColor = ElectricEmerald
            )
        }

        // 4. Photo-Backed Meal Cards
        meals.forEach { meal ->
            val isChecked = checkedMeals[meal.id] ?: false
            MealPhotoGlassCard(
                meal = meal,
                isChecked = isChecked,
                language = language,
                onToggle = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    checkedMeals[meal.id] = !isChecked
                },
                hazeState = hazeState
            )
        }

        // 5. Muted Disclaimer Footer
        ContourGlassCard(
            hazeState = hazeState,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = when (language) {
                        Language.ENGLISH -> "Formulated for Tamil Nadu grassroots kabaddi & kho-kho athletes using affordable, indigenous low-cost staples."
                        Language.TAMIL -> "தமிழ்நாட்டு தடகள வீரர்களுக்கான ஊட்டச்சத்து வழிகாட்டி - எளிய, சத்தான பாரம்பரிய உணவுப் பொருட்கள்."
                        Language.HINDI -> "तमिलनाडु के खिलाड़ियों हेतु किफायती, स्थानीय पौष्टिक आहार योजना।"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                    color = TextGray
                )
            }
        }

        // Bottom clearance for floating nav bar
        Spacer(modifier = Modifier.navigationBarsPadding().height(104.dp))
    }
}

/**
 * Editorial Header Bar
 */
@Composable
private fun NutritionHeaderBar(
    sport: Sport,
    language: Language,
    hazeState: HazeState? = null
) {
    ContourGlassCard(
        hazeState = hazeState,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ElectricEmerald.copy(alpha = 0.18f))
                        .border(1.2.dp, ElectricEmerald, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = "Nutrition",
                        tint = ElectricEmerald,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = BilingualStrings.getNutritionHeaderTitle(language),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        ),
                        color = TextWhite,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "100% Offline Indigenous Food Guide",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            LiquidGlassPill(
                text = sport.getDisplayName(language),
                isSelected = true,
                tintColor = ElectricEmerald
            )
        }
    }
}

/**
 * Photo-Backed Meal & Recovery Checklist Card
 */
@Composable
private fun MealPhotoGlassCard(
    meal: NutritionMealItem,
    isChecked: Boolean,
    language: Language,
    onToggle: () -> Unit,
    hazeState: HazeState? = null
) {
    val cardBorder = if (isChecked) GlassBorderEmerald else GlassBorderStandard

    ContourGlassCard(
        hazeState = hazeState,
        borderBrush = cardBorder,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggle
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Rounded 84dp x 84dp Real Food Photography Thumbnail
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
            ) {
                Image(
                    painter = painterResource(meal.imageRes),
                    contentDescription = meal.getTitle(language),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Glowing emerald overlay when marked completed
                if (isChecked) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ElectricEmerald.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(DeepNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = ElectricEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Right: Content Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Top row: Title + Cost Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = meal.getTitle(language),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 19.sp
                        ),
                        color = if (isChecked) ElectricEmerald else TextWhite,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    LiquidGlassPill(
                        text = meal.costEstimate,
                        isSelected = isChecked,
                        tintColor = WarmAmber
                    )
                }

                // Timing Subtitle
                Text(
                    text = meal.getTiming(language),
                    style = MaterialTheme.typography.labelSmall,
                    color = OceanTeal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Ingredients / Macro Benefit
                Text(
                    text = meal.getIngredients(language),
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                    color = TextGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = meal.getMacroBenefit(language),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Trailing Glass Checkbox indicator
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (isChecked) ElectricEmerald else Color.White.copy(alpha = 0.08f))
                    .border(
                        1.2.dp,
                        if (isChecked) ElectricEmerald else Color.White.copy(alpha = 0.30f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isChecked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = DeepNavy,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
