package com.athlens.app.ui.dashboard.tabs

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athlens.app.R
import com.athlens.app.domain.kinematics.DrillType
import com.athlens.app.domain.model.AthleteProfile
import com.athlens.app.domain.model.Language
import com.athlens.app.domain.model.Sport
import com.athlens.app.domain.model.TrainingSession
import com.athlens.app.ui.theme.ContourGlassCard
import com.athlens.app.ui.theme.CrimsonAlert
import com.athlens.app.ui.theme.DeepNavy
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.GlassBorderEmerald
import com.athlens.app.ui.theme.GlassBorderStandard
import com.athlens.app.ui.theme.LiquidGlassButton
import com.athlens.app.ui.theme.LiquidGlassPill
import com.athlens.app.ui.theme.ObsidianCanvas
import com.athlens.app.ui.theme.OceanTeal
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextMuted
import com.athlens.app.ui.theme.TextWhite
import com.athlens.app.ui.theme.WarmAmber
import com.athlens.app.ui.util.BilingualStrings
import com.athlens.app.ui.util.DateUtils
import dev.chrisbanes.haze.HazeState

@Composable
fun TrainTab(
    profile: AthleteProfile?,
    recentSessions: List<TrainingSession>,
    language: Language,
    onLaunchCameraCoach: (DrillType) -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var selectedDrill by remember(profile?.sport) {
        mutableStateOf(
            if (profile?.sport == Sport.KHO_KHO) DrillType.KHO_KHO_CROUCH_START else DrillType.KABADDI_RAID_STANCE
        )
    }

    val athleteName = profile?.fullName?.split(" ")?.firstOrNull() ?: "Athlete"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Compact Apple-Style Greeting Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = BilingualStrings.getGreeting(language, athleteName),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp
                        ),
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    // Glowing online/offline-ready status dot
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(ElectricEmerald)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = when (language) {
                        Language.ENGLISH -> "100% On-Device AI Vision"
                        Language.TAMIL -> "100% ஆஃப்லைன் AI பார்வை"
                        Language.HINDI -> "100% ऑन-डिवाइस एआई विजन"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            // Sport & Position Glass Pill
            val sportLabel = if (profile?.sport == Sport.KHO_KHO) "KHO-KHO" else "KABADDI"
            val positionLabel = profile?.playingPosition ?: "RAIDER"
            LiquidGlassPill(
                text = "$sportLabel • $positionLabel",
                isSelected = true,
                tintColor = ElectricEmerald
            )
        }

        // 2. Editorial Hero Card ("AI Camera Coach" with real photo background)
        val heroShape = RoundedCornerShape(28.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = heroShape,
                    ambientColor = Color.Black.copy(alpha = 0.5f),
                    spotColor = OceanTeal.copy(alpha = 0.35f)
                )
                .clip(heroShape)
                .border(1.2.dp, GlassBorderEmerald, heroShape)
        ) {
            // Full-bleed Action Photography Background
            Image(
                painter = painterResource(id = R.drawable.img_train_hero),
                contentDescription = "AI Camera Coach Hero",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Deep obsidian gradient scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color(0x33050814),
                            0.45f to Color(0x77050814),
                            0.80f to Color(0xEB050814),
                            1.0f to Color(0xF8050814)
                        )
                    )
            )

            // Top Floating Frosted Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LiquidGlassPill(
                    text = "ON-DEVICE AI • 33 LANDMARKS",
                    isSelected = true,
                    tintColor = ElectricEmerald
                )

                LiquidGlassPill(
                    text = "LIVE AI",
                    isSelected = true,
                    tintColor = OceanTeal
                )
            }

            // Bottom Content Overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = BilingualStrings.getCameraCoachTitle(language),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.3).sp
                    ),
                    color = TextWhite
                )

                Text(
                    text = when (language) {
                        Language.ENGLISH -> "Sub-20ms real-time kinematic posture analysis and live voice cues on device."
                        Language.TAMIL -> "நிகழ்நேர 33-மூட்டு தோரணை பகுப்பாய்வு மற்றும் உடனடி ஆஃப்லைன் குரல் வழிகாட்டல்."
                        Language.HINDI -> "रीयल-टाइम 33-बिंदु मुद्रा विश्लेषण एवं ऑफलाइन वॉयस कोचिंग।"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = if (language != Language.ENGLISH) 18.sp else 16.sp
                    ),
                    color = TextGray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Action Row: Primary Button + Test Voice Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LiquidGlassButton(
                        onClick = { onLaunchCameraCoach(selectedDrill) },
                        isPrimary = true,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 46.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = DeepNavy,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = BilingualStrings.getStartSession(language),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = if (language == Language.TAMIL) 12.sp else 13.5.sp
                            ),
                            color = DeepNavy,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    LiquidGlassPill(
                        text = when (language) {
                            Language.ENGLISH -> "Test Voice"
                            Language.TAMIL -> "குரல் சோதனை"
                            Language.HINDI -> "वॉइस टेस्ट"
                        },
                        isSelected = false,
                        tintColor = WarmAmber,
                        onClick = {
                            Toast.makeText(
                                context,
                                BilingualStrings.getVoiceCue("GOOD_FORM", language),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }

        // 3. Visual Drill Selector Cards (Side-by-Side Photo Cards)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = when (language) {
                    Language.ENGLISH -> "Select Drill:"
                    Language.TAMIL -> "பயிற்சி வகை:"
                    Language.HINDI -> "अभ्यास चुनें:"
                },
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = ElectricEmerald
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Drill Card 1: Kabaddi Raid Stance
                VisualDrillPhotoCard(
                    title = DrillType.KABADDI_RAID_STANCE.getDisplayName(language),
                    imageRes = R.drawable.img_drill_kabaddi,
                    isSelected = selectedDrill == DrillType.KABADDI_RAID_STANCE,
                    onClick = { selectedDrill = DrillType.KABADDI_RAID_STANCE },
                    modifier = Modifier.weight(1f)
                )

                // Drill Card 2: Kho-Kho Crouch Start
                VisualDrillPhotoCard(
                    title = DrillType.KHO_KHO_CROUCH_START.getDisplayName(language),
                    imageRes = R.drawable.img_drill_khokho,
                    isSelected = selectedDrill == DrillType.KHO_KHO_CROUCH_START,
                    onClick = { selectedDrill = DrillType.KHO_KHO_CROUCH_START },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Recent Sessions Section (Photo-backed / Glass Telemetry Rows)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == Language.TAMIL) "சமீபத்திய பயிற்சிகள் / Recent Sessions" else "Recent Drill Sessions",
                    style = MaterialTheme.typography.titleMedium,
                    color = ElectricEmerald,
                    fontWeight = FontWeight.Bold
                )
                if (recentSessions.isNotEmpty()) {
                    LiquidGlassPill(
                        text = "${recentSessions.size} sessions",
                        isSelected = false,
                        tintColor = OceanTeal
                    )
                }
            }

            if (recentSessions.isEmpty()) {
                ContourGlassCard(
                    hazeState = hazeState,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(34.dp)
                        )
                        Text(
                            text = if (language == Language.TAMIL) {
                                "இதுவரை பயிற்சிகள் பதிவு செய்யப்படவில்லை"
                            } else {
                                "No training sessions recorded yet"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = TextWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (language == Language.TAMIL) {
                                "மேலே உள்ள 'AI கேமரா பயிற்சியாளர்' பொத்தானைத் தட்டி உங்கள் முதல் பயிற்சியைத் தொடங்குங்கள்!"
                            } else {
                                "Tap 'Start Drill Session' above to record your first verified posture telemetry."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                    }
                }
            } else {
                recentSessions.take(5).forEach { session ->
                    SessionGlassItemCard(
                        session = session,
                        language = language,
                        hazeState = hazeState
                    )
                }
            }
        }

        // Bottom space so last card glides smoothly underneath floating glass nav bar
        Spacer(modifier = Modifier.navigationBarsPadding().height(104.dp))
    }
}

/**
 * Visual Drill Selector Card with Real Photo Background & Contour Emerald Rim
 */
@Composable
private fun VisualDrillPhotoCard(
    title: String,
    imageRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(22.dp)
    val borderBrush = if (isSelected) GlassBorderEmerald else GlassBorderStandard
    val borderWidth = if (isSelected) 1.8.dp else 1.dp

    Box(
        modifier = modifier
            .height(152.dp)
            .shadow(
                elevation = if (isSelected) 12.dp else 6.dp,
                shape = cardShape,
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = if (isSelected) ElectricEmerald.copy(alpha = 0.35f) else Color.Transparent
            )
            .clip(cardShape)
            .border(borderWidth, borderBrush, cardShape)
            .clickable(onClick = onClick)
    ) {
        // Real Athletic Photography
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark gradient scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color(0x33050814),
                        0.50f to Color(0x88050814),
                        1.0f to Color(0xF2050814)
                    )
                )
        )

        // Selected state checkmark indicator
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(ElectricEmerald)
                    .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = DeepNavy,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Bottom Title
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.1).sp
                ),
                color = if (isSelected) TextWhite else TextGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Session Item in Liquid Contour Glass Card
 */
@Composable
private fun SessionGlassItemCard(
    session: TrainingSession,
    language: Language,
    hazeState: HazeState? = null
) {
    val score = session.formScore
    val scoreColor = when {
        score >= 75 -> ElectricEmerald
        score >= 50 -> WarmAmber
        else -> CrimsonAlert
    }

    ContourGlassCard(
        hazeState = hazeState,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Circular tabular score badge
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(scoreColor.copy(alpha = 0.16f))
                    .border(1.2.dp, scoreColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = scoreColor
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.drillName,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = DateUtils.formatDateTime(session.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Sync / Storage pill
            LiquidGlassPill(
                text = "Offline Validated",
                isSelected = false,
                tintColor = ElectricEmerald
            )
        }
    }
}
