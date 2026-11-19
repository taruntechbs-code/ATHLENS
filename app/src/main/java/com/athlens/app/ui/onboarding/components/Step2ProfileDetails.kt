package com.athlens.app.ui.onboarding.components

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.athlens.app.domain.model.Language
import com.athlens.app.domain.model.PlayingPosition
import com.athlens.app.domain.model.Sport
import com.athlens.app.ui.theme.BorderDark
import com.athlens.app.ui.theme.CardNavy
import com.athlens.app.ui.theme.CardNavyElevated
import com.athlens.app.ui.theme.DeepNavy
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextMuted
import com.athlens.app.ui.theme.TextWhite
import com.athlens.app.ui.theme.WarmAmber
import com.athlens.app.ui.util.BilingualStrings
import com.athlens.app.ui.util.DateUtils
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

@Composable
fun Step2ProfileDetails(
    language: Language,
    fullName: String,
    dateOfBirthMillis: Long,
    calculatedAge: Int,
    isMinor: Boolean,
    district: String,
    selectedSport: Sport,
    selectedPosition: String,
    onFullNameChanged: (String) -> Unit,
    onDateOfBirthChanged: (Long) -> Unit,
    onDistrictChanged: (String) -> Unit,
    onSportChanged: (Sport) -> Unit,
    onPositionChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var districtMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step Header
        Column {
            Text(
                text = BilingualStrings.getStep2Title(language),
                style = MaterialTheme.typography.headlineSmall,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = BilingualStrings.getStep2Subtitle(language),
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray
            )
        }

        // Full Name Field
        OutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChanged,
            label = { Text(BilingualStrings.getFullNameLabel(language)) },
            placeholder = {
                Text(
                    if (language == Language.TAMIL) "எ.கா: சுந்தர் செல்வம்" else "e.g. Sundar Selvam",
                    color = TextMuted
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = ElectricEmerald,
                unfocusedBorderColor = BorderDark,
                focusedLabelColor = ElectricEmerald,
                unfocusedLabelColor = TextGray,
                cursorColor = ElectricEmerald
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Date of Birth & Age Pill
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(1.dp, CardNavyElevated),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = BilingualStrings.getDateOfBirthLabel(language),
                    style = MaterialTheme.typography.titleSmall,
                    color = ElectricEmerald
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val calendar = Calendar.getInstance()
                            if (dateOfBirthMillis > 0L) {
                                calendar.timeInMillis = dateOfBirthMillis
                            } else {
                                calendar.add(Calendar.YEAR, -16)
                            }

                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val localDate = LocalDate.of(year, month + 1, dayOfMonth)
                                    val millis = localDate.atStartOfDay(ZoneId.systemDefault())
                                        .toInstant()
                                        .toEpochMilli()
                                    onDateOfBirthChanged(millis)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Select DOB",
                            tint = ElectricEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = DateUtils.formatDate(dateOfBirthMillis),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextWhite
                        )
                    }

                    // Age Status Chip
                    if (dateOfBirthMillis > 0L) {
                        SuggestionChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = if (isMinor) {
                                        if (language == Language.TAMIL) "$calculatedAge வயது (இளம்பருவம்)" else "Age $calculatedAge (Minor)"
                                    } else {
                                        if (language == Language.TAMIL) "$calculatedAge வயது (முதியவர்)" else "Age $calculatedAge (Adult)"
                                    },
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isMinor) Icons.Default.Warning else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isMinor) WarmAmber else ElectricEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (isMinor) CardNavyElevated else DeepNavy,
                                labelColor = if (isMinor) WarmAmber else ElectricEmerald
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isMinor) WarmAmber else ElectricEmerald
                            )
                        )
                    }
                }
            }
        }

        // District Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = district,
                onValueChange = {},
                readOnly = true,
                label = { Text(BilingualStrings.getDistrictLabel(language)) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select District",
                        tint = ElectricEmerald,
                        modifier = Modifier.clickable { districtMenuExpanded = true }
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = ElectricEmerald,
                    unfocusedBorderColor = BorderDark,
                    focusedLabelColor = ElectricEmerald,
                    unfocusedLabelColor = TextGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { districtMenuExpanded = true }
            )

            DropdownMenu(
                expanded = districtMenuExpanded,
                onDismissRequest = { districtMenuExpanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                DateUtils.TAMIL_NADU_DISTRICTS.forEach { districtName ->
                    DropdownMenuItem(
                        text = { Text(districtName) },
                        onClick = {
                            onDistrictChanged(districtName)
                            districtMenuExpanded = false
                        }
                    )
                }
            }
        }

        // Sport Selector
        Column {
            Text(
                text = BilingualStrings.getSportLabel(language),
                style = MaterialTheme.typography.titleSmall,
                color = ElectricEmerald
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Sport.entries.forEach { sport ->
                    val isSelected = sport == selectedSport
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSportChanged(sport) },
                        label = {
                            Text(
                                text = "${sport.displayNameEn} (${sport.displayNameTa})",
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricEmerald,
                            selectedLabelColor = DeepNavy,
                            containerColor = CardNavy,
                            labelColor = TextWhite
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) ElectricEmerald else BorderDark
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Playing Position Selector
        Column {
            Text(
                text = BilingualStrings.getPositionLabel(language),
                style = MaterialTheme.typography.titleSmall,
                color = ElectricEmerald
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val availablePositions = PlayingPosition.positionsFor(selectedSport)
                availablePositions.forEach { pos ->
                    val isSelected = pos.id == selectedPosition
                    FilterChip(
                        selected = isSelected,
                        onClick = { onPositionChanged(pos.id) },
                        label = {
                            Text(
                                text = if (language == Language.TAMIL) pos.displayNameTa else pos.displayNameEn,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricEmerald,
                            selectedLabelColor = DeepNavy,
                            containerColor = CardNavy,
                            labelColor = TextWhite
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) ElectricEmerald else BorderDark
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
