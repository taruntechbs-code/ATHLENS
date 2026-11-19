package com.athlens.app.ui.dashboard.tabs

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.athlens.app.domain.maturity.MaturityContextReport
import com.athlens.app.domain.model.Achievement
import com.athlens.app.domain.model.AchievementLevel
import com.athlens.app.domain.model.AthleteProfile
import com.athlens.app.domain.model.Language
import com.athlens.app.domain.passport.QrCodeGenerator
import com.athlens.app.domain.passport.SignedTalentPassport
import com.athlens.app.ui.theme.CardNavy
import com.athlens.app.ui.theme.CardNavyElevated
import com.athlens.app.ui.theme.CrimsonAlert
import com.athlens.app.ui.theme.DeepNavy
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.ElectricEmeraldDark
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextMuted
import com.athlens.app.ui.theme.TextWhite
import com.athlens.app.ui.theme.WarmAmber
import com.athlens.app.ui.theme.WarmAmberDark
import com.athlens.app.ui.util.BilingualStrings
import com.athlens.app.ui.util.DateUtils
import kotlin.math.roundToInt

@Composable
fun PassportTab(
    profile: AthleteProfile?,
    totalSessions: Int,
    averageScore: Double?,
    latestSessionScore: Int?,
    maturityReport: MaturityContextReport?,
    achievements: List<Achievement> = emptyList(),
    signedPassport: SignedTalentPassport? = null,
    language: Language,
    onLogGrowthMeasurement: (Float, Float) -> Unit = { _, _ -> },
    onAddAchievement: (String, AchievementLevel, String, Boolean) -> Unit = { _, _, _, _ -> },
    onDeleteAchievement: (String) -> Unit = {},
    onVerifyPassport: (String, String, String) -> Boolean = { _, _, _ -> false },
    onTamperPayload: (String) -> String = { it },
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showLogGrowthDialog by remember { mutableStateOf(false) }
    var showAddAchievementDialog by remember { mutableStateOf(false) }
    var showQrPassportDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Digital Sports Passport Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(2.dp, ElectricEmerald),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Top Header with Passport Tag & Athlete ID
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = "Passport",
                            tint = ElectricEmerald,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (language) {
                                Language.ENGLISH -> "ATHLETE PASSPORT"
                                Language.TAMIL -> "தடகள பாஸ்போர்ட்"
                                Language.HINDI -> "खिलाड़ी पासपोर्ट"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = ElectricEmerald
                        )
                    }

                    val shortId = profile?.id?.take(8)?.uppercase() ?: "NEW"
                    Surface(
                        color = DeepNavy,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, CardNavyElevated)
                    ) {
                        Text(
                            text = "ID: ATH-$shortId",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextWhite,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Athlete Name & Position
                Text(
                    text = profile?.fullName ?: "Grassroots Athlete",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextWhite
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = ElectricEmeraldDark,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = profile?.sport?.getDisplayName(language) ?: "Kabaddi",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextWhite,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        color = CardNavyElevated,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = profile?.playingPosition ?: "Raider",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricEmerald,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = CardNavyElevated)
                Spacer(modifier = Modifier.height(14.dp))

                // Detail Rows
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow(
                        label = when (language) {
                            Language.ENGLISH -> "Platform Role"
                            Language.TAMIL -> "பங்கு"
                            Language.HINDI -> "भूमिका"
                        },
                        value = profile?.role?.getDisplayName(language) ?: "Athlete"
                    )
                    DetailRow(
                        label = when (language) {
                            Language.ENGLISH -> "District"
                            Language.TAMIL -> "மாவட்டம்"
                            Language.HINDI -> "ज़िला"
                        },
                        value = "${profile?.district ?: "Coimbatore"}, Tamil Nadu"
                    )
                    DetailRow(
                        label = when (language) {
                            Language.ENGLISH -> "Date of Birth"
                            Language.TAMIL -> "பிறந்த தேதி"
                            Language.HINDI -> "जन्म तिथि"
                        },
                        value = profile?.let { DateUtils.formatDate(it.dateOfBirth) } ?: "--"
                    )
                    DetailRow(
                        label = when (language) {
                            Language.ENGLISH -> "Calculated Age"
                            Language.TAMIL -> "கணக்கிடப்பட்ட வயது"
                            Language.HINDI -> "गणना की गई आयु"
                        },
                        value = "${profile?.calculatedAge ?: 0} years"
                    )
                }
            }
        }

        // Minor Guardian Legal Authorization Badge (if minor)
        if (profile?.isMinor == true) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                border = BorderStroke(1.5.dp, WarmAmber),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Minor Protection",
                        tint = WarmAmber,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = when (language) {
                                Language.ENGLISH -> "Guardian Legal Consent Verified"
                                Language.TAMIL -> "பாதுகாவலர் ஒப்புதல் சரிபார்க்கப்பட்டது"
                                Language.HINDI -> "अभिभावक सहमति सत्यापित"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = WarmAmber
                        )
                        Text(
                            text = when (language) {
                                Language.ENGLISH -> "Guardian: ${profile.guardianName ?: "Verified"} (Authorized for sports telemetry)"
                                Language.TAMIL -> "பாதுகாவலர்: ${profile.guardianName ?: "சரிபார்க்கப்பட்டது"} (சட்டப்பூர்வ அனுமதி வழங்கப்பட்டது)"
                                Language.HINDI -> "अभिभावक: ${profile.guardianName ?: "सत्यापित"} (खेल टेलीमेट्री हेतु अधिकृत)"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                    }
                }
            }
        }

        // Phase 5: Cryptographic Talent Passport & Offline QR Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(1.5.dp, ElectricEmerald),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Crypto",
                            tint = ElectricEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = BilingualStrings.getDigitalPassportTitle(language),
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite
                        )
                    }

                    Surface(
                        color = ElectricEmerald.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ElectricEmerald)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = ElectricEmerald,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ECDSA P-256",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Key Fingerprint & Security explanation
                Surface(
                    color = DeepNavy,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = BilingualStrings.getKeyFingerprintLabel(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                        Text(
                            text = signedPassport?.keyFingerprint ?: "SHA256:--",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = TextWhite,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Hardware-backed offline cryptographic attestation. Talent credentials and kinematic progression can be mathematically verified without network connectivity.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { showQrPassportDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricEmerald,
                        contentColor = DeepNavy
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = BilingualStrings.getShowQrPassportButton(language),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Phase 5: Verified Achievements Section
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(1.dp, CardNavyElevated),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Achievements",
                            tint = WarmAmber,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = BilingualStrings.getAchievementsTitle(language),
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite
                        )
                    }

                    Surface(
                        color = CardNavyElevated,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        val verifiedCount = achievements.count { it.isVerified }
                        Text(
                            text = "$verifiedCount/${achievements.size} Verified",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (verifiedCount > 0) ElectricEmerald else TextMuted,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (achievements.isEmpty()) {
                    Surface(
                        color = DeepNavy,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = BilingualStrings.getNoAchievementsYet(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        achievements.forEach { achievement ->
                            AchievementItemRow(
                                achievement = achievement,
                                language = language,
                                onDelete = { onDeleteAchievement(achievement.id) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = { showAddAchievementDialog = true },
                    border = BorderStroke(1.dp, WarmAmber),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmAmber),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = BilingualStrings.getAddAchievementButton(language),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        // Phase 4A: Maturity-Aware Talent Assessment Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(1.5.dp, ElectricEmerald),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = "Maturity",
                            tint = ElectricEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = BilingualStrings.getMaturityCardTitle(language),
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite
                        )
                    }

                    if (maturityReport != null) {
                        Surface(
                            color = if (maturityReport.isLateBorn) WarmAmber.copy(alpha = 0.2f) else ElectricEmerald.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (maturityReport.isLateBorn) WarmAmber else ElectricEmerald)
                        ) {
                            Text(
                                text = maturityReport.getSummaryBadge(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (maturityReport.isLateBorn) WarmAmber else ElectricEmerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Maturity Metrics 2x2 Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = BilingualStrings.getExactAgeLabel(language),
                        value = maturityReport?.let { "${it.exactAgeYears}y ${it.exactAgeMonths}m" }
                            ?: "${profile?.calculatedAge ?: 0} yrs",
                        subtitle = "DOB: ${profile?.let { DateUtils.formatDate(it.dateOfBirth) } ?: "--"}",
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = BilingualStrings.getRaeQuarterLabel(language),
                        value = maturityReport?.raeQuarter ?: "Q1",
                        subtitle = if (maturityReport?.isLateBorn == true) {
                            "Late-Born Buffer"
                        } else {
                            "Early-Born Quarter"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val growthVelocityText = maturityReport?.growthVelocityCmPerYear?.let {
                        val sign = if (it >= 0f) "+" else ""
                        "$sign${String.format("%.1f", it)} cm/yr"
                    } ?: "--"

                    val growthSubtitle = when {
                        maturityReport?.isRapidGrowthSpurt == true -> "Rapid Spurt Alert"
                        maturityReport != null && maturityReport.totalMeasurements >= 2 -> "Steady Growth Pace"
                        else -> "Log 2+ measurements"
                    }

                    MetricCard(
                        title = BilingualStrings.getGrowthVelocityLabel(language),
                        value = growthVelocityText,
                        subtitle = growthSubtitle,
                        modifier = Modifier.weight(1f)
                    )

                    val progressionText = maturityReport?.progressionDeltaPercent?.let {
                        val sign = if (it >= 0) "+" else ""
                        "$sign$it%"
                    } ?: "--"

                    MetricCard(
                        title = BilingualStrings.getProgressionDeltaLabel(language),
                        value = progressionText,
                        subtitle = if (totalSessions >= 2) "Baseline vs Recent" else "Need 2+ sessions",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Growth Measurement summary
                if (maturityReport?.latestHeightCm != null || maturityReport?.latestWeightKg != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = DeepNavy,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Current: ${maturityReport.latestHeightCm ?: "--"} cm • ${maturityReport.latestWeightKg ?: "--"} kg",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextWhite
                            )
                            Text(
                                text = "Records: ${maturityReport.totalMeasurements}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Log Height / Weight Action Button
                OutlinedButton(
                    onClick = { showLogGrowthDialog = true },
                    border = BorderStroke(1.dp, ElectricEmerald),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricEmerald),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Straighten,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = BilingualStrings.getLogGrowthButton(language),
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Coach Development Context Disclaimer
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = BilingualStrings.getMaturityDisclaimer(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }

        // Live Performance & Telemetry Metrics Section
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = when (language) {
                    Language.ENGLISH -> "Performance & Telemetry Metrics"
                    Language.TAMIL -> "செயல்திறன் அளவீடுகள்"
                    Language.HINDI -> "प्रदर्शन एवं टेलीमेट्री मेट्रिक्स"
                },
                style = MaterialTheme.typography.titleMedium,
                color = ElectricEmerald
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = when (language) {
                        Language.ENGLISH -> "Total Drills"
                        Language.TAMIL -> "முழுமை பயிற்சி"
                        Language.HINDI -> "कुल अभ्यास"
                    },
                    value = "$totalSessions",
                    subtitle = if (totalSessions > 0) "Recorded in Room" else "Ready for drill",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = when (language) {
                        Language.ENGLISH -> "Avg Form Score"
                        Language.TAMIL -> "சராசரி மதிப்பெண்"
                        Language.HINDI -> "औसत फॉर्म स्कोर"
                    },
                    value = averageScore?.let { "${it.roundToInt()} / 100" } ?: "-- / 100",
                    subtitle = if (averageScore != null) "AI MediaPipe Live" else "Awaiting drills",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = when (language) {
                        Language.ENGLISH -> "Latest Drill Score"
                        Language.TAMIL -> "கடைசி மதிப்பெண்"
                        Language.HINDI -> "नवीनतम अभ्यास स्कोर"
                    },
                    value = latestSessionScore?.let { "$it / 100" } ?: "--",
                    subtitle = "Recent Kinematics",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = when (language) {
                        Language.ENGLISH -> "Sync Status"
                        Language.TAMIL -> "ஒத்திசைவு நிலை"
                        Language.HINDI -> "सिंक स्थिति"
                    },
                    value = "100% Offline",
                    subtitle = "SQLCipher Encrypted",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Dialog 1: Log Growth Measurement Dialog
    if (showLogGrowthDialog) {
        var heightInput by remember { mutableStateOf("") }
        var weightInput by remember { mutableStateOf("") }
        var inputError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showLogGrowthDialog = false },
            containerColor = CardNavy,
            title = {
                Text(
                    text = BilingualStrings.getLogGrowthButton(language),
                    color = TextWhite,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Track growth velocity to identify adolescent awkwardness and maturation timing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )

                    OutlinedTextField(
                        value = heightInput,
                        onValueChange = {
                            heightInput = it
                            inputError = false
                        },
                        label = { Text(BilingualStrings.getHeightLabel(language)) },
                        placeholder = { Text("e.g. 158.5") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricEmerald,
                            unfocusedBorderColor = CardNavyElevated,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = {
                            weightInput = it
                            inputError = false
                        },
                        label = { Text(BilingualStrings.getWeightLabel(language)) },
                        placeholder = { Text("e.g. 48.0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricEmerald,
                            unfocusedBorderColor = CardNavyElevated,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (inputError) {
                        Text(
                            text = "Please enter valid positive numbers for height and weight.",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmAmber
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val h = heightInput.toFloatOrNull()
                        val w = weightInput.toFloatOrNull()
                        if (h != null && h > 0f && w != null && w > 0f) {
                            onLogGrowthMeasurement(h, w)
                            showLogGrowthDialog = false
                        } else {
                            inputError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricEmerald,
                        contentColor = DeepNavy
                    )
                ) {
                    Text(BilingualStrings.getSaveMeasurement(language))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogGrowthDialog = false }) {
                    Text(BilingualStrings.getCancelButton(language), color = TextGray)
                }
            }
        )
    }

    // Dialog 2: Add Achievement Dialog
    if (showAddAchievementDialog) {
        var titleInput by remember { mutableStateOf("") }
        var issuerInput by remember { mutableStateOf("") }
        var selectedLevel by remember { mutableStateOf(AchievementLevel.DISTRICT) }
        var isVerifiedChecked by remember { mutableStateOf(true) }
        var inputError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddAchievementDialog = false },
            containerColor = CardNavy,
            title = {
                Text(
                    text = BilingualStrings.getAddAchievementDialogTitle(language),
                    color = TextWhite,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = {
                            titleInput = it
                            inputError = false
                        },
                        label = { Text(BilingualStrings.getAchievementTitleLabel(language)) },
                        placeholder = { Text("e.g. Best Raider U-16") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricEmerald,
                            unfocusedBorderColor = CardNavyElevated,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = issuerInput,
                        onValueChange = {
                            issuerInput = it
                            inputError = false
                        },
                        label = { Text(BilingualStrings.getIssuerLabel(language)) },
                        placeholder = { Text("e.g. Coimbatore Sports Assoc.") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricEmerald,
                            unfocusedBorderColor = CardNavyElevated,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Level FilterChips
                    Text(
                        text = BilingualStrings.getLevelLabel(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AchievementLevel.entries.forEach { level ->
                            FilterChip(
                                selected = selectedLevel == level,
                                onClick = { selectedLevel = level },
                                label = {
                                    Text(
                                        text = level.getDisplayName(language),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricEmeraldDark,
                                    selectedLabelColor = TextWhite,
                                    containerColor = DeepNavy,
                                    labelColor = TextGray
                                )
                            )
                        }
                    }

                    // Coach Verified Checkbox
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isVerifiedChecked,
                            onCheckedChange = { isVerifiedChecked = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = ElectricEmerald,
                                uncheckedColor = TextMuted,
                                checkmarkColor = DeepNavy
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = BilingualStrings.getCoachVerifiedLabel(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextWhite
                        )
                    }

                    if (inputError) {
                        Text(
                            text = "Please enter both title and issuing body.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CrimsonAlert
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleInput.isNotBlank() && issuerInput.isNotBlank()) {
                            onAddAchievement(titleInput.trim(), selectedLevel, issuerInput.trim(), isVerifiedChecked)
                            showAddAchievementDialog = false
                        } else {
                            inputError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricEmerald,
                        contentColor = DeepNavy
                    )
                ) {
                    Text(BilingualStrings.getSaveButton(language))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAchievementDialog = false }) {
                    Text(BilingualStrings.getCancelButton(language), color = TextGray)
                }
            }
        )
    }

    // Dialog 3: Cryptographic QR Talent Passport Dialog
    if (showQrPassportDialog && signedPassport != null) {
        val context = LocalContext.current
        val clipboardManager = LocalClipboardManager.current
        var qrBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
        var verificationResult by remember { mutableStateOf<Boolean?>(null) }

        LaunchedEffect(signedPassport) {
            qrBitmap = QrCodeGenerator.generateQrBitmap(signedPassport.envelopeJson, 512)
        }

        AlertDialog(
            onDismissRequest = {
                showQrPassportDialog = false
                verificationResult = null
            },
            containerColor = CardNavy,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = BilingualStrings.getQrDialogTitle(language),
                        color = TextWhite,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Surface(
                        color = ElectricEmerald.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "v1.0",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricEmerald,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // QR Code Display
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val bmp = qrBitmap
                        if (bmp != null) {
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Signed QR Code",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            CircularProgressIndicator(
                                color = DeepNavy,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    // Key Fingerprint Pill
                    Surface(
                        color = DeepNavy,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Key Fingerprint",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Text(
                                text = signedPassport.keyFingerprint,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = ElectricEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Verification Status Banner (Live feedback)
                    when (verificationResult) {
                        true -> {
                            Surface(
                                color = ElectricEmeraldDark.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, ElectricEmerald),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Valid",
                                        tint = ElectricEmerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = BilingualStrings.getSignatureValidBanner(language),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ElectricEmerald,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        false -> {
                            Surface(
                                color = CrimsonAlert.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, CrimsonAlert),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = "Tampered",
                                        tint = CrimsonAlert,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = BilingualStrings.getSignatureTamperedBanner(language),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = CrimsonAlert,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        null -> {
                            Text(
                                text = BilingualStrings.getQrScanInstruction(language),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGray
                            )
                        }
                    }

                    // Offline Verification Demo Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val isValid = onVerifyPassport(
                                    signedPassport.canonicalPayloadJson,
                                    signedPassport.signatureBase64,
                                    signedPassport.publicKeyBase64
                                )
                                verificationResult = isValid
                            },
                            border = BorderStroke(1.dp, ElectricEmerald),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = BilingualStrings.getVerifyIntegrityButton(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricEmerald
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val tampered = onTamperPayload(signedPassport.canonicalPayloadJson)
                                val isValid = onVerifyPassport(
                                    tampered,
                                    signedPassport.signatureBase64,
                                    signedPassport.publicKeyBase64
                                )
                                verificationResult = isValid
                            },
                            border = BorderStroke(1.dp, WarmAmber),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = BilingualStrings.getSimulateTamperButton(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = WarmAmber
                            )
                        }
                    }

                    // Copy Signed JSON Button
                    TextButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(signedPassport.envelopeJson))
                            Toast.makeText(
                                context,
                                BilingualStrings.getCopiedToClipboard(language),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = BilingualStrings.getCopySignedJsonButton(language),
                            style = MaterialTheme.typography.labelMedium,
                            color = TextGray
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showQrPassportDialog = false
                        verificationResult = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricEmerald,
                        contentColor = DeepNavy
                    )
                ) {
                    Text(BilingualStrings.getCloseButton(language))
                }
            }
        )
    }
}

@Composable
private fun AchievementItemRow(
    achievement: Achievement,
    language: Language,
    onDelete: () -> Unit
) {
    Surface(
        color = DeepNavy,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CardNavyElevated),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Level Badge
                    Surface(
                        color = when (achievement.level) {
                            AchievementLevel.STATE -> WarmAmberDark.copy(alpha = 0.3f)
                            AchievementLevel.DISTRICT -> ElectricEmeraldDark.copy(alpha = 0.3f)
                            AchievementLevel.SCHOOL -> CardNavyElevated
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = achievement.level.getDisplayName(language),
                            style = MaterialTheme.typography.labelSmall,
                            color = when (achievement.level) {
                                AchievementLevel.STATE -> WarmAmber
                                AchievementLevel.DISTRICT -> ElectricEmerald
                                AchievementLevel.SCHOOL -> TextGray
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Verified vs Pending Badge
                    if (achievement.isVerified) {
                        Surface(
                            color = ElectricEmeraldDark.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = ElectricEmerald,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = BilingualStrings.getVerifiedBadge(language),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricEmerald
                                )
                            }
                        }
                    } else {
                        Surface(
                            color = CardNavyElevated,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = BilingualStrings.getPendingBadge(language),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${achievement.issuerName} • ${DateUtils.formatDate(achievement.awardedAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextGray
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = TextWhite
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = BorderStroke(1.dp, CardNavyElevated),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = ElectricEmerald
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
        }
    }
}
