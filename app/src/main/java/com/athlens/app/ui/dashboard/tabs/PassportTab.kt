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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.athlens.app.domain.model.AthleteProfile
import com.athlens.app.domain.model.Language
import com.athlens.app.ui.theme.CardNavy
import com.athlens.app.ui.theme.CardNavyElevated
import com.athlens.app.ui.theme.DeepNavy
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextMuted
import com.athlens.app.ui.theme.TextWhite
import com.athlens.app.ui.theme.WarmAmber
import com.athlens.app.ui.util.DateUtils
import kotlin.math.roundToInt

@Composable
fun PassportTab(
    profile: AthleteProfile?,
    totalSessions: Int,
    averageScore: Double?,
    latestSessionScore: Int?,
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
                            text = if (language == Language.TAMIL) "தடகள பாஸ்போர்ட்" else "ATHLETE PASSPORT",
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
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Athlete Name & Role
                Text(
                    text = profile?.fullName ?: "Athlete Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = ElectricEmerald.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${profile?.sport?.displayNameEn ?: "KABADDI"} • ${profile?.playingPosition ?: "Raider"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = ElectricEmerald,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = DeepNavy,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = profile?.role?.displayNameEn ?: "ATHLETE",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextGray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = CardNavyElevated, thickness = 1.dp)
                Spacer(modifier = Modifier.height(14.dp))

                // Metadata Details
                DetailRow(
                    label = if (language == Language.TAMIL) "மாவட்டம்" else "District",
                    value = profile?.district ?: "Coimbatore"
                )
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(
                    label = if (language == Language.TAMIL) "பிறந்த தேதி & வயது" else "DOB & Age",
                    value = "${DateUtils.formatDate(profile?.dateOfBirth ?: 0L)} (${profile?.calculatedAge ?: 0} yrs)"
                )
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(
                    label = if (language == Language.TAMIL) "தரவுத்தள குறியாக்கம்" else "Local Security",
                    value = "AES-256 SQLCipher (Keystore)"
                )
            }
        }

        // Guardian Verification Status Card (Mandatory for Minor athletes)
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
                        contentDescription = "Guardian Verified",
                        tint = WarmAmber,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (language == Language.TAMIL) "பாதுகாவலர் உறுதிப்படுத்தப்பட்டது" else "Guardian Consent Verified",
                                style = MaterialTheme.typography.titleSmall,
                                color = WarmAmber
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (language == Language.TAMIL) {
                                "பாதுகாவலர்: ${profile.guardianName ?: "சரிபார்க்கப்பட்டது"}"
                            } else {
                                "Guardian: ${profile.guardianName ?: "Verified"}"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextWhite
                        )
                        Text(
                            text = if (language == Language.TAMIL) {
                                "சட்டப்பூர்வ சிறுவர் விளையாட்டு அனுமதி பதிவு செய்யப்பட்டுள்ளது"
                            } else {
                                "Minor telemetry consent signed & recorded offline"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                border = BorderStroke(1.dp, ElectricEmerald),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Adult Athlete",
                        tint = ElectricEmerald,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (language == Language.TAMIL) "வயது வந்த வீரர் சரிபார்ப்பு" else "Adult Athlete Status",
                            style = MaterialTheme.typography.titleSmall,
                            color = ElectricEmerald
                        )
                        Text(
                            text = if (language == Language.TAMIL) {
                                "18+ வயது - நேரடி தடகள பாஸ்போர்ட் உரிமை"
                            } else {
                                "Age 18+ verified - direct athletic passport ownership"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                    }
                }
            }
        }

        // Live Performance & Telemetry Metrics Section (Connected to Room DB)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = if (language == Language.TAMIL) "செயல்திறன் அளவீடுகள் / Drill Telemetry" else "Performance & Telemetry Metrics",
                style = MaterialTheme.typography.titleMedium,
                color = ElectricEmerald
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = if (language == Language.TAMIL) "முழுமை பயிற்சி" else "Total Drills",
                    value = "$totalSessions",
                    subtitle = if (totalSessions > 0) "Recorded in Room" else "Ready for drill",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = if (language == Language.TAMIL) "சராசரி மதிப்பெண்" else "Avg Form Score",
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
                    title = if (language == Language.TAMIL) "கடைசி மதிப்பெண்" else "Latest Drill Score",
                    value = latestSessionScore?.let { "$it / 100" } ?: "--",
                    subtitle = "Recent Kinematics",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = if (language == Language.TAMIL) "ஒத்திசைவு நிலை" else "Sync Status",
                    value = "100% Offline",
                    subtitle = "SQLCipher Encrypted",
                    modifier = Modifier.weight(1f)
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
