package com.athlens.app.ui.onboarding.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.athlens.app.domain.model.Language
import com.athlens.app.domain.model.Role
import com.athlens.app.ui.theme.CardNavy
import com.athlens.app.ui.theme.CardNavyElevated
import com.athlens.app.ui.theme.DeepNavy
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextWhite
import com.athlens.app.ui.util.BilingualStrings

@Composable
fun Step1LanguageRole(
    selectedLanguage: Language,
    selectedRole: Role,
    onLanguageSelected: (Language) -> Unit,
    onRoleSelected: (Role) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Step Title & Tagline
        Column {
            Text(
                text = BilingualStrings.getStep1Title(selectedLanguage),
                style = MaterialTheme.typography.headlineSmall,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = BilingualStrings.getStep1Subtitle(selectedLanguage),
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray
            )
        }

        // Language Selector Section
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(1.dp, CardNavyElevated),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = when (selectedLanguage) {
                        Language.ENGLISH -> "Select Language"
                        Language.TAMIL -> "மொழியைத் தேர்ந்தெடுக்கவும்"
                        Language.HINDI -> "भाषा चुनें"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = ElectricEmerald
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Language.entries.forEach { lang ->
                        val isSelected = lang == selectedLanguage
                        FilterChip(
                            selected = isSelected,
                            onClick = { onLanguageSelected(lang) },
                            label = {
                                Text(
                                    text = lang.nativeName,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricEmerald,
                                selectedLabelColor = DeepNavy,
                                containerColor = CardNavyElevated,
                                labelColor = TextWhite
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) ElectricEmerald else Color.Transparent
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Role Selection Section
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = when (selectedLanguage) {
                    Language.ENGLISH -> "Select Your Role"
                    Language.TAMIL -> "உங்கள் பங்கைத் தேர்ந்தெடுக்கவும்"
                    Language.HINDI -> "अपनी भूमिका चुनें"
                },
                style = MaterialTheme.typography.titleMedium,
                color = ElectricEmerald
            )

            Role.entries.forEach { role ->
                RoleCard(
                    role = role,
                    isSelected = role == selectedRole,
                    currentLanguage = selectedLanguage,
                    onSelect = { onRoleSelected(role) }
                )
            }
        }
    }
}

@Composable
private fun RoleCard(
    role: Role,
    isSelected: Boolean,
    currentLanguage: Language,
    onSelect: () -> Unit
) {
    val icon: ImageVector = when (role) {
        Role.ATHLETE -> Icons.AutoMirrored.Filled.DirectionsRun
        Role.COACH -> Icons.Default.SportsScore
        Role.SCOUT -> Icons.Default.Visibility
    }

    val subtitle = when (role) {
        Role.ATHLETE -> when (currentLanguage) {
            Language.ENGLISH -> "Player training and building verified athletic passport"
            Language.TAMIL -> "களத்தில் களமிறங்கும் கபடி / கோ-கோ வீரர்"
            Language.HINDI -> "प्रशिक्षण लेने और सत्यापित एथलेटिक पासपोर्ट बनाने वाले खिलाड़ी"
        }
        Role.COACH -> when (currentLanguage) {
            Language.ENGLISH -> "Trainer guiding players with AI biomechanics analysis"
            Language.TAMIL -> "அணி மற்றும் வீரர்களை வழிநடத்தும் பயிற்சியாளர்"
            Language.HINDI -> "एआई बायोमैकेनिक्स विश्लेषण से मार्गदर्शन करने वाले कोच"
        }
        Role.SCOUT -> when (currentLanguage) {
            Language.ENGLISH -> "Talent scout discovering high-potential rural athletes"
            Language.TAMIL -> "கிராமப்புற திறமைகளை அடையாளம் காணும் ஸ்கவுட்"
            Language.HINDI -> "जमीनी स्तर पर प्रतिभाशाली खिलाड़ियों की पहचान करने वाले स्काउट"
        }
    }

    val roleTitle = when (currentLanguage) {
        Language.ENGLISH -> role.displayNameEn
        Language.TAMIL -> "${role.displayNameEn} • ${role.displayNameTa}"
        Language.HINDI -> "${role.displayNameEn} • ${role.displayNameHi}"
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) CardNavyElevated else CardNavy
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) ElectricEmerald else CardNavyElevated
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = role.displayNameEn,
                tint = if (isSelected) ElectricEmerald else TextGray,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = roleTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) TextWhite else TextGray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = ElectricEmerald,
                    unselectedColor = TextGray
                )
            )
        }
    }
}
