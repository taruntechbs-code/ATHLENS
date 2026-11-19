package com.athlens.app.ui.dashboard.tabs

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.athlens.app.domain.maturity.MaturityContextReport
import com.athlens.app.domain.model.AthleteProfile
import com.athlens.app.domain.model.Language
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
import com.athlens.app.ui.util.DateUtils
import kotlin.math.roundToInt

@Composable
fun PassportTab(
    profile: AthleteProfile?,
    totalSessions: Int,
    averageScore: Double?,
    latestSessionScore: Int?,
    maturityReport: MaturityContextReport?,
    language: Language,
    onLogGrowthMeasurement: (Float, Float) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showLogGrowthDialog by remember { mutableStateOf(false) }

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
                        label = if (language == Language.TAMIL) "பங்கு" else "Platform Role",
                        value = profile?.role?.getDisplayName(language) ?: "Athlete"
                    )
                    DetailRow(
                        label = if (language == Language.TAMIL) "மாவட்டம்" else "District",
                        value = "${profile?.district ?: "Coimbatore"}, Tamil Nadu"
                    )
                    DetailRow(
                        label = if (language == Language.TAMIL) "பிறந்த தேதி" else "Date of Birth",
                        value = profile?.let { DateUtils.formatDate(it.dateOfBirth) } ?: "--"
                    )
                    DetailRow(
                        label = if (language == Language.TAMIL) "கணக்கிடப்பட்ட வயது" else "Calculated Age",
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
                            text = if (language == Language.TAMIL) "பாதுகாவலர் ஒப்புதல் சரிபார்க்கப்பட்டது" else "Guardian Legal Consent Verified",
                            style = MaterialTheme.typography.titleSmall,
                            color = WarmAmber
                        )
                        Text(
                            text = if (language == Language.TAMIL) {
                                "பாதுகாவலர்: ${profile.guardianName ?: "சரிபார்க்கப்பட்டது"} (சட்டப்பூர்வ அனுமதி வழங்கப்பட்டது)"
                            } else {
                                "Guardian: ${profile.guardianName ?: "Verified"} (Authorized for sports telemetry)"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                    }
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
                text = if (language == Language.TAMIL) "செயல்திறன் அளவீடுகள்" else "Performance & Telemetry Metrics",
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

    // Dialog for Logging Height and Weight
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
