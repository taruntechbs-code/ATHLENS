package com.athlens.app.ui.onboarding.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.athlens.app.ui.theme.ContourGlassCard
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.GlassBorderAmber
import com.athlens.app.ui.theme.GlassBorderEmerald
import com.athlens.app.ui.theme.GlassBorderStandard
import com.athlens.app.ui.theme.LiquidGlassPill
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextMuted
import com.athlens.app.ui.theme.TextWhite
import com.athlens.app.ui.theme.WarmAmber
import com.athlens.app.ui.util.BilingualStrings
import com.athlens.app.ui.util.DateUtils
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
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
        verticalArrangement = Arrangement.spacedBy(18.dp)
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
                unfocusedBorderColor = Color.White.copy(alpha = 0.20f),
                focusedLabelColor = ElectricEmerald,
                unfocusedLabelColor = TextGray,
                cursorColor = ElectricEmerald,
                focusedContainerColor = Color(0x181C2541),
                unfocusedContainerColor = Color(0x101C2541)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Date of Birth & Age Pill inside ContourGlassCard
        ContourGlassCard(
            borderBrush = if (dateOfBirthMillis > 0L) {
                if (isMinor) GlassBorderAmber else GlassBorderEmerald
            } else GlassBorderStandard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = BilingualStrings.getDateOfBirthLabel(language),
                style = MaterialTheme.typography.titleSmall,
                color = ElectricEmerald
            )
            Spacer(modifier = Modifier.height(10.dp))

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
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Select DOB",
                        tint = ElectricEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = DateUtils.formatDate(dateOfBirthMillis),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextWhite
                    )
                }

                // Age Status Badge
                if (dateOfBirthMillis > 0L) {
                    LiquidGlassPill(
                        text = if (isMinor) {
                            if (language == Language.TAMIL) "$calculatedAge வயது (இளம்பருவம்)" else "Age $calculatedAge (Minor)"
                        } else {
                            if (language == Language.TAMIL) "$calculatedAge வயது (முதியவர்)" else "Age $calculatedAge (Adult)"
                        },
                        isSelected = true,
                        tintColor = if (isMinor) WarmAmber else ElectricEmerald
                    )
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
                    unfocusedBorderColor = Color.White.copy(alpha = 0.20f),
                    focusedLabelColor = ElectricEmerald,
                    unfocusedLabelColor = TextGray,
                    focusedContainerColor = Color(0x181C2541),
                    unfocusedContainerColor = Color(0x101C2541)
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
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Sport.entries.forEach { sport ->
                    val isSelected = sport == selectedSport
                    LiquidGlassPill(
                        text = sport.getDisplayName(language),
                        isSelected = isSelected,
                        tintColor = ElectricEmerald,
                        onClick = { onSportChanged(sport) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Playing Position Selector with FlowRow
        Column {
            Text(
                text = BilingualStrings.getPositionLabel(language),
                style = MaterialTheme.typography.titleSmall,
                color = ElectricEmerald
            )
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val availablePositions = PlayingPosition.positionsFor(selectedSport)
                availablePositions.forEach { pos ->
                    val isSelected = pos.id == selectedPosition
                    LiquidGlassPill(
                        text = pos.getDisplayName(language),
                        isSelected = isSelected,
                        tintColor = ElectricEmerald,
                        onClick = { onPositionChanged(pos.id) }
                    )
                }
            }
        }
    }
}
