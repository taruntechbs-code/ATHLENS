package com.athlens.app.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.athlens.app.domain.kinematics.DrillType
import com.athlens.app.domain.model.Language
import com.athlens.app.ui.dashboard.tabs.NutritionTab
import com.athlens.app.ui.dashboard.tabs.PassportTab
import com.athlens.app.ui.dashboard.tabs.ShareP2PTab
import com.athlens.app.ui.dashboard.tabs.TrainTab
import com.athlens.app.ui.theme.CardNavy
import com.athlens.app.ui.theme.CardNavyElevated
import com.athlens.app.ui.theme.DeepNavy
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextMuted
import com.athlens.app.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onLaunchCameraCoach: (DrillType) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepNavy),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ElectricEmerald)
        }
        return
    }

    Scaffold(
        containerColor = DeepNavy,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ATHLENS",
                            style = MaterialTheme.typography.titleLarge,
                            color = ElectricEmerald
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ElectricEmerald)
                        )
                    }
                },
                actions = {
                    // Quick Language Switcher Pill
                    Surface(
                        color = CardNavy,
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, CardNavyElevated),
                        modifier = Modifier
                            .clickable { viewModel.toggleLanguage() }
                            .padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Switch Language",
                                tint = ElectricEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (uiState.activeLanguage) {
                                    Language.ENGLISH -> "EN"
                                    Language.TAMIL -> "தமிழ்"
                                    Language.HINDI -> "हिन्दी"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = TextWhite
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepNavy,
                    titleContentColor = TextWhite
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CardNavy,
                tonalElevation = 8.dp
            ) {
                DashboardTab.entries.forEach { tab ->
                    val isSelected = tab == uiState.currentTab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.getTitle(uiState.activeLanguage),
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.getTitle(uiState.activeLanguage),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepNavy,
                            selectedTextColor = ElectricEmerald,
                            indicatorColor = ElectricEmerald,
                            unselectedIconColor = TextGray,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                DashboardTab.TRAIN -> TrainTab(
                    profile = uiState.athleteProfile,
                    recentSessions = uiState.recentSessions,
                    language = uiState.activeLanguage,
                    onLaunchCameraCoach = onLaunchCameraCoach
                )
                DashboardTab.PASSPORT -> PassportTab(
                    profile = uiState.athleteProfile,
                    totalSessions = uiState.totalSessions,
                    averageScore = uiState.averageFormScore,
                    latestSessionScore = uiState.recentSessions.firstOrNull()?.formScore,
                    maturityReport = uiState.maturityReport,
                    language = uiState.activeLanguage,
                    onLogGrowthMeasurement = { h, w ->
                        viewModel.logGrowthMeasurement(h, w)
                    }
                )
                DashboardTab.NUTRITION -> NutritionTab(
                    sport = uiState.athleteProfile?.sport ?: com.athlens.app.domain.model.Sport.KABADDI,
                    language = uiState.activeLanguage
                )
                DashboardTab.SHARE_P2P -> ShareP2PTab(
                    language = uiState.activeLanguage
                )
            }
        }
    }
}
