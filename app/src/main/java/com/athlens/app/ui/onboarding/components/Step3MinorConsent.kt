package com.athlens.app.ui.onboarding.components

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.athlens.app.domain.model.Language
import com.athlens.app.ui.theme.ContourGlassCard
import com.athlens.app.ui.theme.DeepNavy
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.GlassBorderAmber
import com.athlens.app.ui.theme.GlassBorderEmerald
import com.athlens.app.ui.theme.GlassBorderStandard
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextMuted
import com.athlens.app.ui.theme.TextWhite
import com.athlens.app.ui.theme.WarmAmber
import com.athlens.app.ui.util.BilingualStrings

@Composable
fun Step3MinorConsent(
    language: Language,
    isMinor: Boolean,
    calculatedAge: Int,
    guardianName: String,
    guardianConsentGiven: Boolean,
    onGuardianNameChanged: (String) -> Unit,
    onGuardianConsentChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Step Header
        Column {
            Text(
                text = BilingualStrings.getStep3Title(language),
                style = MaterialTheme.typography.headlineSmall,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = BilingualStrings.getStep3Subtitle(language),
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray
            )
        }

        if (isMinor) {
            // Minor Protection Notice Glass Card
            ContourGlassCard(
                borderBrush = GlassBorderAmber,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Minor Protection",
                        tint = WarmAmber,
                        modifier = Modifier.size(28.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (language) {
                                Language.ENGLISH -> "Minor Athlete Protection Notice (Age $calculatedAge)"
                                Language.TAMIL -> "இளம்பருவ வீரர் பாதுகாப்பு அறிவிப்பு ($calculatedAge வயது)"
                                Language.HINDI -> "नाबालिग खिलाड़ी सुरक्षा सूचना (आयु $calculatedAge वर्ष)"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = WarmAmber
                        )
                        Text(
                            text = when (language) {
                                Language.ENGLISH -> "Mandatory Grassroots Sports Policy Compliance"
                                Language.TAMIL -> "தமிழ்நாடு விளையாட்டுக் கொள்கை வழிகாட்டுதல்"
                                Language.HINDI -> "अनिवार्य खेल नीति अनुपालन"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = BilingualStrings.getGuardianConsentNotice(language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhite
                )
            }

            // Mandatory Guardian Name Field
            OutlinedTextField(
                value = guardianName,
                onValueChange = onGuardianNameChanged,
                label = { Text(BilingualStrings.getGuardianNameLabel(language)) },
                placeholder = {
                    Text(
                        when (language) {
                            Language.ENGLISH -> "e.g. S. Ranganathan (Father)"
                            Language.TAMIL -> "எ.கா: சு. ரங்கநாதன் (தந்தை)"
                            Language.HINDI -> "उदा. एस. रंगनाथन (पिता)"
                        },
                        color = TextMuted
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = ElectricEmerald,
                    unfocusedBorderColor = WarmAmber.copy(alpha = 0.5f),
                    focusedLabelColor = ElectricEmerald,
                    unfocusedLabelColor = WarmAmber,
                    cursorColor = ElectricEmerald,
                    focusedContainerColor = Color(0x181C2541),
                    unfocusedContainerColor = Color(0x101C2541)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Mandatory Guardian Consent Checkbox Glass Card
            ContourGlassCard(
                borderBrush = if (guardianConsentGiven) GlassBorderEmerald else GlassBorderStandard,
                containerColor = if (guardianConsentGiven) ElectricEmerald.copy(alpha = 0.08f) else Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGuardianConsentChanged(!guardianConsentGiven) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = guardianConsentGiven,
                        onCheckedChange = onGuardianConsentChanged,
                        colors = CheckboxDefaults.colors(
                            checkedColor = ElectricEmerald,
                            uncheckedColor = WarmAmber,
                            checkmarkColor = DeepNavy
                        )
                    )
                    Text(
                        text = when (language) {
                            Language.ENGLISH -> "I confirm I am the legal guardian and grant full authorization for sports passport & training telemetry processing."
                            Language.TAMIL -> "நான் இந்த வீரரின் சட்டப்பூர்வ பாதுகாவலர் என்பதை உறுதிசெய்து, தடகள பாஸ்போர்ட் மற்றும் பயிற்சி தரவு சேமிப்பிற்கு ஒப்புதல் அளிக்கிறேன்."
                            Language.HINDI -> "मैं पुष्टि करता/करती हूँ कि मैं इस खिलाड़ी का कानूनी अभिभावक हूँ और स्पोर्ट्स पासपोर्ट व प्रशिक्षण टेलीमेट्री के लिए पूर्ण सहमति देता/देती हूँ।"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (guardianConsentGiven) TextWhite else TextGray,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            // Adult Athlete Glass Card
            ContourGlassCard(
                borderBrush = GlassBorderEmerald,
                containerColor = ElectricEmerald.copy(alpha = 0.06f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Adult Athlete",
                        tint = ElectricEmerald,
                        modifier = Modifier.size(32.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (language) {
                                Language.ENGLISH -> "Adult Athlete (Age $calculatedAge)"
                                Language.TAMIL -> "வயது வந்த வீரர் ($calculatedAge வயது)"
                                Language.HINDI -> "वयस्क खिलाड़ी (आयु $calculatedAge वर्ष)"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = ElectricEmerald
                        )
                        Text(
                            text = when (language) {
                                Language.ENGLISH -> "Direct Registration Verified"
                                Language.TAMIL -> "நேரடி பதிவு அனுமதிக்கப்பட்டது"
                                Language.HINDI -> "सीधा पंजीकरण सत्यापित"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = BilingualStrings.getAdultNotice(language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhite
                )
            }
        }

        // Offline Encryption Guarantee
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Encrypted",
                tint = ElectricEmerald,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (language == Language.TAMIL) {
                    "AES-256 SQLCipher என்க்ரிப்ஷன் மூலம் பாதுகாக்கப்பட்டது"
                } else {
                    "Secured with on-device AES-256 SQLCipher encryption"
                },
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}
