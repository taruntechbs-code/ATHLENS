package com.athlens.app.ui.dashboard.tabs

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.athlens.app.R
import com.athlens.app.domain.maturity.MaturityContextReport
import com.athlens.app.domain.model.Achievement
import com.athlens.app.domain.model.AchievementLevel
import com.athlens.app.domain.model.AthleteProfile
import com.athlens.app.domain.model.Language
import com.athlens.app.domain.passport.QrCodeGenerator
import com.athlens.app.domain.passport.SignedTalentPassport
import com.athlens.app.ui.theme.ContourGlassCard
import com.athlens.app.ui.theme.CrimsonAlert
import com.athlens.app.ui.theme.DeepNavy
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.ElectricEmeraldDark
import com.athlens.app.ui.theme.GlassBorderEmerald
import com.athlens.app.ui.theme.GlassBorderStandard
import com.athlens.app.ui.theme.LiquidGlassButton
import com.athlens.app.ui.theme.LiquidGlassPill
import com.athlens.app.ui.theme.MidnightSapphire
import com.athlens.app.ui.theme.ObsidianCanvas
import com.athlens.app.ui.theme.OceanTeal
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextMuted
import com.athlens.app.ui.theme.TextWhite
import com.athlens.app.ui.theme.WarmAmber
import com.athlens.app.ui.util.BilingualStrings
import com.athlens.app.ui.util.DateUtils
import dev.chrisbanes.haze.HazeState
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
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
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null
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
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Apple Wallet–Style Athlete Pass Hero Card
        AthleteWalletPassCard(
            profile = profile,
            totalSessions = totalSessions,
            averageScore = averageScore,
            latestSessionScore = latestSessionScore,
            language = language
        )

        // 2. Minor Guardian Consent Badge (if minor)
        if (profile?.isMinor == true) {
            MinorGuardianConsentCard(
                guardianName = profile.guardianName,
                language = language,
                hazeState = hazeState
            )
        }

        // 3. Cryptographic Talent Passport & Offline QR Card
        CryptographicPassportCard(
            signedPassport = signedPassport,
            language = language,
            onShowQrModal = { showQrPassportDialog = true },
            hazeState = hazeState
        )

        // 4. Maturity-Aware Assessment Card
        MaturityAwareCard(
            maturityReport = maturityReport,
            language = language,
            onLogGrowthClick = { showLogGrowthDialog = true },
            hazeState = hazeState
        )

        // 5. Verified Achievements Card
        AchievementsCard(
            achievements = achievements,
            language = language,
            onAddClick = { showAddAchievementDialog = true },
            onDeleteClick = onDeleteAchievement,
            hazeState = hazeState
        )

        // Bottom clearance for floating nav bar
        Spacer(modifier = Modifier.navigationBarsPadding().height(104.dp))
    }

    // Modal: Signed QR Passport Viewer Dialog
    if (showQrPassportDialog && signedPassport != null) {
        QrPassportModalDialog(
            signedPassport = signedPassport,
            language = language,
            onVerify = onVerifyPassport,
            onTamper = onTamperPayload,
            onDismiss = { showQrPassportDialog = false }
        )
    }

    // Modal: Log Height/Weight Dialog
    if (showLogGrowthDialog) {
        LogGrowthDialog(
            language = language,
            onDismiss = { showLogGrowthDialog = false },
            onConfirm = { height, weight ->
                onLogGrowthMeasurement(height, weight)
                showLogGrowthDialog = false
            }
        )
    }

    // Modal: Add Achievement Dialog
    if (showAddAchievementDialog) {
        AddAchievementDialog(
            language = language,
            onDismiss = { showAddAchievementDialog = false },
            onConfirm = { title, level, issuer, verified ->
                onAddAchievement(title, level, issuer, verified)
                showAddAchievementDialog = false
            }
        )
    }
}

/**
 * 1. Apple Wallet–Style Athlete Pass Hero Card
 * Full-bleed stadium background with rich dark gradient scrim, specular emerald contour rim,
 * micro-brand badge, tabular telemetry strip, and FlowRow metadata pills.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AthleteWalletPassCard(
    profile: AthleteProfile?,
    totalSessions: Int,
    averageScore: Double?,
    latestSessionScore: Int?,
    language: Language
) {
    val passShape = RoundedCornerShape(28.dp)
    val shortId = profile?.id?.take(8)?.uppercase() ?: "NEW"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(passShape)
            .border(1.2.dp, GlassBorderEmerald, passShape)
    ) {
        // Stadium floodlights photography backdrop
        Image(
            painter = painterResource(R.drawable.img_passport_banner),
            contentDescription = "Stadium Track Backdrop",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // Rich dark gradient scrim ensuring razor-sharp text
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color(0x99050814),
                        0.45f to Color(0xDD050814),
                        1.0f to Color(0xF8050814)
                    )
                )
        )

        // Pass Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Row: Micro-label + ID capsule
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(ElectricEmerald)
                    )
                    Text(
                        text = when (language) {
                            Language.ENGLISH -> "ATHLENS TALENT PASSPORT"
                            Language.TAMIL -> "தடகளத் திறன் பாஸ்போர்ட்"
                            Language.HINDI -> "खिलाड़ी प्रतिभा पासपोर्ट"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.3.sp
                        ),
                        color = ElectricEmerald,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                LiquidGlassPill(
                    text = "#$shortId",
                    isSelected = true,
                    tintColor = ElectricEmerald
                )
            }

            // Athlete Name & FlowRow Pills
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = profile?.fullName ?: "Grassroots Athlete",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LiquidGlassPill(
                        text = profile?.sport?.getDisplayName(language) ?: "Kabaddi",
                        isSelected = true,
                        tintColor = ElectricEmerald
                    )
                    LiquidGlassPill(
                        text = profile?.playingPosition ?: "Raider",
                        isSelected = false
                    )
                    LiquidGlassPill(
                        text = "${profile?.district ?: "Coimbatore"}, TN",
                        isSelected = false
                    )
                    if (profile?.isMinor == true) {
                        LiquidGlassPill(
                            text = "GUARDIAN VERIFIED",
                            isSelected = true,
                            tintColor = WarmAmber
                        )
                    }
                }
            }

            // Embedded Telemetry Strip: 3 Tabular Numeral Glass Tiles
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, GlassBorderStandard, RoundedCornerShape(18.dp))
                    .padding(vertical = 12.dp, horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PassMetricTile(
                    label = when (language) {
                        Language.ENGLISH -> "Sessions"
                        Language.TAMIL -> "அமர்வுகள்"
                        Language.HINDI -> "सत्र"
                    },
                    value = "$totalSessions"
                )
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.12f))
                )
                PassMetricTile(
                    label = when (language) {
                        Language.ENGLISH -> "Avg Score"
                        Language.TAMIL -> "சராசரி"
                        Language.HINDI -> "औसत"
                    },
                    value = averageScore?.roundToInt()?.let { "$it/100" } ?: "--"
                )
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.12f))
                )
                PassMetricTile(
                    label = when (language) {
                        Language.ENGLISH -> "Latest"
                        Language.TAMIL -> "சமீபத்திய"
                        Language.HINDI -> "नवीनतम"
                    },
                    value = latestSessionScore?.let { "$it/100" } ?: "--"
                )
            }
        }
    }
}

@Composable
private fun PassMetricTile(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontFeatureSettings = "tnum"
            ),
            color = TextWhite
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
    }
}

/**
 * 2. Minor Guardian Legal Consent Card
 */
@Composable
private fun MinorGuardianConsentCard(
    guardianName: String?,
    language: Language,
    hazeState: HazeState? = null
) {
    ContourGlassCard(
        hazeState = hazeState,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(WarmAmber.copy(alpha = 0.18f))
                    .border(1.2.dp, WarmAmber.copy(alpha = 0.8f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Minor Consent",
                    tint = WarmAmber,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (language) {
                        Language.ENGLISH -> "Guardian Legal Consent Verified"
                        Language.TAMIL -> "பாதுகாவலர் சட்டப்பூர்வ ஒப்புதல்"
                        Language.HINDI -> "अभिभावक सहमति सत्यापित"
                    },
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = WarmAmber,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = when (language) {
                        Language.ENGLISH -> "Authorized by ${guardianName ?: "Guardian"} for kinematic telemetry"
                        Language.TAMIL -> "${guardianName ?: "பாதுகாவலர்"} மூலம் அனுமதி சரிபார்க்கப்பட்டது"
                        Language.HINDI -> "${guardianName ?: "अभिभावक"} द्वारा खेल टेलीमेट्री अधिकृत"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * 3. Cryptographic Talent Passport & Offline QR Card
 */
@Composable
private fun CryptographicPassportCard(
    signedPassport: SignedTalentPassport?,
    language: Language,
    onShowQrModal: () -> Unit,
    hazeState: HazeState? = null
) {
    ContourGlassCard(
        hazeState = hazeState,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
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
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = ElectricEmerald,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = BilingualStrings.getDigitalPassportTitle(language),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Hardware KeyStore • 100% Offline",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElectricEmerald,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                LiquidGlassPill(
                    text = "ECDSA P-256",
                    isSelected = true,
                    tintColor = ElectricEmerald
                )
            }

            // Fingerprint & Canonical Payload Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, GlassBorderStandard, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = BilingualStrings.getKeyFingerprintLabel(language),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                val fingerprint = signedPassport?.keyFingerprint ?: "CREATING..."
                Text(
                    text = fingerprint,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = ElectricEmerald
                )
            }

            // Primary Show QR Passport Button
            LiquidGlassButton(
                onClick = onShowQrModal,
                isPrimary = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = null,
                    tint = DeepNavy,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (language) {
                        Language.ENGLISH -> "Show Signed QR Passport"
                        Language.TAMIL -> "கையொப்பமிட்ட QR பாஸ்போர்ட்"
                        Language.HINDI -> "हस्ताक्षरित क्यूआर पासपोर्ट दिखाएं"
                    },
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = (-0.2).sp
                    ),
                    color = DeepNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * 4. Maturity-Aware Assessment Card
 */
@Composable
private fun MaturityAwareCard(
    maturityReport: MaturityContextReport?,
    language: Language,
    onLogGrowthClick: () -> Unit,
    hazeState: HazeState? = null
) {
    ContourGlassCard(
        hazeState = hazeState,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
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
                            .background(OceanTeal.copy(alpha = 0.20f))
                            .border(1.2.dp, OceanTeal, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = OceanTeal,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = BilingualStrings.getMaturityCardTitle(language),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            ),
                            color = TextWhite,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Growth Velocity & Bio-Banding",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = OceanTeal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                val raeQuarter = maturityReport?.raeQuarter ?: "Q1"
                LiquidGlassPill(
                    text = "RAE $raeQuarter",
                    isSelected = true,
                    tintColor = ElectricEmerald
                )
            }

            // 2x2 Glass Metric Tiles
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassMetricMiniCard(
                        title = when (language) {
                            Language.ENGLISH -> "Exact Age"
                            Language.TAMIL -> "துல்லிய வயது"
                            Language.HINDI -> "सटीक आयु"
                        },
                        value = maturityReport?.let {
                            "${it.exactAgeYears}y ${it.exactAgeMonths}m"
                        } ?: "--",
                        modifier = Modifier.weight(1f)
                    )
                    GlassMetricMiniCard(
                        title = when (language) {
                            Language.ENGLISH -> "RAE Status"
                            Language.TAMIL -> "RAE நிலை"
                            Language.HINDI -> "आरएई स्थिति"
                        },
                        value = when (maturityReport?.raeQuarter) {
                            "Q1", "Q2" -> "Advantaged"
                            "Q3", "Q4" -> "Late Maturing"
                            else -> "Normal"
                        },
                        valueColor = OceanTeal,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassMetricMiniCard(
                        title = when (language) {
                            Language.ENGLISH -> "Growth Velocity"
                            Language.TAMIL -> "வளர்ச்சி வேகம்"
                            Language.HINDI -> "वृद्धि दर"
                        },
                        value = maturityReport?.growthVelocityCmPerYear?.let {
                            "%.1f cm/yr".format(it)
                        } ?: "Pending",
                        modifier = Modifier.weight(1f)
                    )
                    GlassMetricMiniCard(
                        title = when (language) {
                            Language.ENGLISH -> "Progression"
                            Language.TAMIL -> "முன்னேற்றம்"
                            Language.HINDI -> "प्रगति"
                        },
                        value = maturityReport?.progressionDeltaPercent?.let {
                            "%+d%%".format(it)
                        } ?: "--",
                        valueColor = if ((maturityReport?.progressionDeltaPercent ?: 0) >= 0) ElectricEmerald else CrimsonAlert,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Coach Advisory Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = when (language) {
                        Language.ENGLISH -> "Biological age normalization protects late-maturing talent from premature deselection."
                        Language.TAMIL -> "உயிரியல் வயது சமன்பாடு பின்தங்கிய பருவ வயதினரை முன்கூட்டியே நிராகரிப்பதில் இருந்து பாதுகாக்கிறது."
                        Language.HINDI -> "जैविक आयु सामान्यीकरण देर से परिपक्व होने वाली प्रतिभा को समयपूर्व बाहर होने से बचाता है।"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                    color = TextGray
                )
            }

            // Log Height & Weight Button
            LiquidGlassButton(
                onClick = onLogGrowthClick,
                isPrimary = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = TextWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when (language) {
                        Language.ENGLISH -> "Log Height & Weight"
                        Language.TAMIL -> "உயரம் & எடை பதிவு"
                        Language.HINDI -> "लंबाई एवं वज़न दर्ज करें"
                    },
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
            }
        }
    }
}

@Composable
private fun GlassMetricMiniCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextWhite
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, GlassBorderStandard, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum"
                ),
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 5. Verified Achievements Card
 */
@Composable
private fun AchievementsCard(
    achievements: List<Achievement>,
    language: Language,
    onAddClick: () -> Unit,
    onDeleteClick: (String) -> Unit,
    hazeState: HazeState? = null
) {
    ContourGlassCard(
        hazeState = hazeState,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
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
                            .background(WarmAmber.copy(alpha = 0.18f))
                            .border(1.2.dp, WarmAmber, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = WarmAmber,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (language) {
                                Language.ENGLISH -> "Verified Achievements"
                                Language.TAMIL -> "சரிபார்க்கப்பட்ட சாதனைகள்"
                                Language.HINDI -> "सत्यापित उपलब्धियां"
                            },
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${achievements.size} Medals / Certificates",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmAmber,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                LiquidGlassPill(
                    text = "+ Add",
                    isSelected = true,
                    tintColor = WarmAmber,
                    onClick = onAddClick
                )
            }

            if (achievements.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (language) {
                            Language.ENGLISH -> "No verified achievements logged yet. Tap '+ Add' to record tournament medals."
                            Language.TAMIL -> "சாதனைகள் எதுவும் பதிவாகவில்லை. பதக்கங்களைச் சேர்க்க '+ Add' தட்டவும்."
                            Language.HINDI -> "कोई उपलब्धि नहीं। पदक जोड़ने के लिए '+ Add' दबाएं।"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    achievements.forEach { achievement ->
                        AchievementGlassRow(
                            achievement = achievement,
                            onDelete = { onDeleteClick(achievement.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementGlassRow(
    achievement: Achievement,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, GlassBorderStandard, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(WarmAmber.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = WarmAmber,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${achievement.level.name} • ${achievement.issuerName}",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (achievement.isVerified) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(ElectricEmerald.copy(alpha = 0.2f))
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Verified",
                    tint = ElectricEmerald,
                    modifier = Modifier.size(16.dp)
                )
            }
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

/**
 * 6. Signed QR Passport Modal Dialog
 */
@Composable
private fun QrPassportModalDialog(
    signedPassport: SignedTalentPassport,
    language: Language,
    onVerify: (String, String, String) -> Boolean,
    onTamper: (String) -> String,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var verificationStatus by remember { mutableStateOf<Boolean?>(null) }
    var currentPayload by remember { mutableStateOf(signedPassport.canonicalPayloadJson) }

    LaunchedEffect(currentPayload) {
        val qrContent = signedPassport.envelopeJson
        qrBitmap = QrCodeGenerator.generateQrBitmap(qrContent, 600)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.82f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(ObsidianCanvas.copy(alpha = 0.94f))
                    .border(1.2.dp, GlassBorderEmerald, RoundedCornerShape(28.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Modal Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = BilingualStrings.getDigitalPassportTitle(language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite
                        )
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.10f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Crisp White 24.dp Rounded QR Quiet-Zone Card
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color.White)
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (qrBitmap != null) {
                            Image(
                                bitmap = qrBitmap!!.asImageBitmap(),
                                contentDescription = "Signed QR Passport",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            CircularProgressIndicator(color = DeepNavy)
                        }
                    }

                    // Key Fingerprint Pill
                    LiquidGlassPill(
                        text = "ECDSA KEY: ${signedPassport.keyFingerprint}",
                        isSelected = true,
                        tintColor = ElectricEmerald
                    )

                    // Verification Status Badge
                    if (verificationStatus != null) {
                        val isValid = verificationStatus == true
                        val badgeColor = if (isValid) ElectricEmerald else CrimsonAlert
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(badgeColor.copy(alpha = 0.16f))
                            .border(1.2.dp, badgeColor, RoundedCornerShape(14.dp))
                            .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isValid) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = badgeColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isValid) "Cryptographic Signature VALID (100% Offline)" else "Signature INVALID! Tampering Detected",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = badgeColor
                            )
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LiquidGlassButton(
                            onClick = {
                                verificationStatus = onVerify(
                                    currentPayload,
                                    signedPassport.signatureBase64,
                                    signedPassport.publicKeyBase64
                                )
                            },
                            isPrimary = true,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Verify Integrity",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = DeepNavy,
                                maxLines = 1
                            )
                        }

                        LiquidGlassButton(
                            onClick = {
                                currentPayload = onTamper(currentPayload)
                                verificationStatus = onVerify(
                                    currentPayload,
                                    signedPassport.signatureBase64,
                                    signedPassport.publicKeyBase64
                                )
                            },
                            isPrimary = false,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Simulate Tamper",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = CrimsonAlert,
                                maxLines = 1
                            )
                        }
                    }

                    // Copy Signed JSON Button
                    LiquidGlassButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(signedPassport.envelopeJson))
                            Toast.makeText(context, "Signed JSON copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        isPrimary = false,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Copy Signed JSON",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextWhite
                        )
                    }
                }
            }
        }
    }
}

/**
 * 7. Log Height & Weight Dialog
 */
@Composable
private fun LogGrowthDialog(
    language: Language,
    onDismiss: () -> Unit,
    onConfirm: (Float, Float) -> Unit
) {
    var heightInput by remember { mutableStateOf("") }
    var weightInput by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.80f))
                .imePadding()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(ObsidianCanvas.copy(alpha = 0.95f))
                    .border(1.2.dp, GlassBorderEmerald, RoundedCornerShape(26.dp))
                    .padding(20.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when (language) {
                            Language.ENGLISH -> "Log Growth Measurement"
                            Language.TAMIL -> "வளர்ச்சி அளவீடு பதிவு"
                            Language.HINDI -> "शारीरिक माप दर्ज करें"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )

                    OutlinedTextField(
                        value = heightInput,
                        onValueChange = { heightInput = it },
                        label = { Text("Height (cm)", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricEmerald,
                            unfocusedBorderColor = TextMuted.copy(alpha = 0.3f),
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Weight (kg)", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricEmerald,
                            unfocusedBorderColor = TextMuted.copy(alpha = 0.3f),
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LiquidGlassButton(
                            onClick = onDismiss,
                            isPrimary = false,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Cancel",
                                color = TextWhite,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        LiquidGlassButton(
                            onClick = {
                                val h = heightInput.toFloatOrNull() ?: 0f
                                val w = weightInput.toFloatOrNull() ?: 0f
                                if (h > 0f && w > 0f) {
                                    onConfirm(h, w)
                                }
                            },
                            isPrimary = true,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Save",
                                color = DeepNavy,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 8. Add Achievement Dialog
 */
@Composable
private fun AddAchievementDialog(
    language: Language,
    onDismiss: () -> Unit,
    onConfirm: (String, AchievementLevel, String, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var issuer by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf(AchievementLevel.DISTRICT) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.80f))
                .imePadding()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(ObsidianCanvas.copy(alpha = 0.95f))
                    .border(1.2.dp, GlassBorderEmerald, RoundedCornerShape(26.dp))
                    .padding(20.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when (language) {
                            Language.ENGLISH -> "Add Tournament Achievement"
                            Language.TAMIL -> "போட்டி சாதனை சேர்"
                            Language.HINDI -> "प्रतियोगिता उपलब्धि जोड़ें"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title (e.g. Gold Medal Raider)", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricEmerald,
                            unfocusedBorderColor = TextMuted.copy(alpha = 0.3f),
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = issuer,
                        onValueChange = { issuer = it },
                        label = { Text("Issuing Authority (e.g. TNKBA)", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricEmerald,
                            unfocusedBorderColor = TextMuted.copy(alpha = 0.3f),
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Level selector chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AchievementLevel.entries.forEach { level ->
                            LiquidGlassPill(
                                text = level.name,
                                isSelected = level == selectedLevel,
                                tintColor = WarmAmber,
                                onClick = { selectedLevel = level }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LiquidGlassButton(
                            onClick = onDismiss,
                            isPrimary = false,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Cancel",
                                color = TextWhite,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        LiquidGlassButton(
                            onClick = {
                                if (title.isNotBlank()) {
                                    onConfirm(title, selectedLevel, issuer.ifBlank { "TNKBA" }, true)
                                }
                            },
                            isPrimary = true,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Add",
                                color = DeepNavy,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}
