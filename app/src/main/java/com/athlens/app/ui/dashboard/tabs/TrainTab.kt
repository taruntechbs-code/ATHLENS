package com.athlens.app.ui.dashboard.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.athlens.app.domain.kinematics.DrillType
import com.athlens.app.domain.model.AthleteProfile
import com.athlens.app.domain.model.Language
import com.athlens.app.domain.model.Sport
import com.athlens.app.domain.model.TrainingSession
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
import com.athlens.app.ui.util.BilingualStrings
import com.athlens.app.ui.util.DateUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TrainTab(
    profile: AthleteProfile?,
    recentSessions: List<TrainingSession>,
    language: Language,
    onLaunchCameraCoach: (DrillType) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val athleteName = profile?.fullName ?: if (language == Language.TAMIL) "வீரர்" else "Athlete"
    var selectedDrill by remember {
        mutableStateOf(
            if (profile?.sport == Sport.KHO_KHO) DrillType.KHO_KHO_CROUCH_START else DrillType.KABADDI_RAID_STANCE
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Athlete Greeting & Sport Position Header Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(1.dp, CardNavyElevated),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = BilingualStrings.getGreeting(language, athleteName),
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextWhite,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == Language.TAMIL) "உங்கள் அடுத்த பயிற்சிக்கு தயாராகுங்கள்" else "Ready for your next high-performance drill",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                    }

                    // Sport Pill
                    val sportLabel = if (profile?.sport == Sport.KHO_KHO) "KHO-KHO" else "KABADDI"
                    val positionLabel = profile?.playingPosition ?: "ATHLETE"
                    Surface(
                        color = DeepNavy,
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, ElectricEmerald)
                    ) {
                        Text(
                            text = "$sportLabel • $positionLabel",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricEmerald,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Offline Mode Ready Status Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(DeepNavy, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(ElectricEmerald)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = BilingualStrings.getOfflineBadge(language),
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricEmerald
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• 100% On-Device AI",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        }

        // Active AI Camera Coach Action Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(2.dp, ElectricEmerald),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header badge
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
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera Coach",
                            tint = ElectricEmerald,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = BilingualStrings.getCameraCoachTitle(language),
                            style = MaterialTheme.typography.titleLarge,
                            color = TextWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        color = ElectricEmeraldDark,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "LIVE AI",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextWhite,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = when (language) {
                        Language.ENGLISH -> "Real-time 33-point pose estimation and sport kinematic analysis. Sub-20ms feedback on device without internet."
                        Language.TAMIL -> "MediaPipe மூலம் நிகழ்நேர 33-மூட்டு தோரணை பகுப்பாய்வு & கபடி/கோ-கோ கோண பிழை திருத்தம். இணையம் தேவையில்லை."
                        Language.HINDI -> "मीडियापाइप द्वारा 33-बिंदु मुद्रा विश्लेषण और खेल-विशिष्ट कोण सुधार। बिना इंटरनेट डिवाइस पर लाइव।"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Drill Selector Chips
                Text(
                    text = when (language) {
                        Language.ENGLISH -> "Select Drill:"
                        Language.TAMIL -> "பயிற்சி வகை:"
                        Language.HINDI -> "अभ्यास चुनें:"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    color = ElectricEmerald
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DrillType.entries.forEach { drill ->
                        val isSelected = drill == selectedDrill
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDrill = drill },
                            label = {
                                Text(
                                    text = drill.getDisplayName(language),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricEmerald,
                                selectedLabelColor = DeepNavy,
                                containerColor = CardNavyElevated,
                                labelColor = TextWhite
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isSelected) ElectricEmerald else Color.Transparent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Launch Coach Button
                Button(
                    onClick = { onLaunchCameraCoach(selectedDrill) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricEmerald,
                        contentColor = DeepNavy
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start Drill",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${BilingualStrings.getStartSession(language)} (${selectedDrill.getDisplayName(language)})",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        // Recent Sessions Section
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == Language.TAMIL) "சமீபத்திய பயிற்சிகள் / Recent Sessions" else "Recent Drill Sessions",
                    style = MaterialTheme.typography.titleMedium,
                    color = ElectricEmerald
                )
                if (recentSessions.isNotEmpty()) {
                    Text(
                        text = "${recentSessions.size} sessions",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }

            if (recentSessions.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = BorderStroke(1.dp, CardNavyElevated),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = if (language == Language.TAMIL) {
                                "இதுவரை பயிற்சிகள் பதிவு செய்யப்படவில்லை"
                            } else {
                                "No training sessions recorded yet"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = TextWhite
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
                    SessionItemCard(
                        session = session,
                        language = language
                    )
                }
            }
        }

        // Bottom space so last card never collides with navigation bar
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SessionItemCard(
    session: TrainingSession,
    language: Language
) {
    val score = session.formScore
    val scoreColor = when {
        score >= 75 -> ElectricEmerald
        score >= 50 -> WarmAmber
        else -> CrimsonAlert
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = BorderStroke(1.dp, CardNavyElevated),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = scoreColor,
                    modifier = Modifier.size(24.dp)
                )
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = session.drillName,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextWhite,
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
            }

            // Score Pill
            Surface(
                color = DeepNavy,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, scoreColor)
            ) {
                Text(
                    text = "$score / 100",
                    style = MaterialTheme.typography.labelMedium,
                    color = scoreColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    maxLines = 1
                )
            }
        }
    }
}
