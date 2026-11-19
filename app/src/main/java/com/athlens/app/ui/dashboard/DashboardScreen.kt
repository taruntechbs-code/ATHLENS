package com.athlens.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.athlens.app.domain.kinematics.DrillType
import com.athlens.app.domain.model.Language
import com.athlens.app.ui.dashboard.tabs.NutritionTab
import com.athlens.app.ui.dashboard.tabs.PassportTab
import com.athlens.app.ui.dashboard.tabs.ShareP2PTab
import com.athlens.app.ui.dashboard.tabs.TrainTab
import com.athlens.app.ui.theme.AmbientGlassBackground
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.FloatingGlassNavBar
import com.athlens.app.ui.theme.GlassBorderStandard
import com.athlens.app.ui.theme.GlassNavItem
import com.athlens.app.ui.theme.LiquidGlassHazeStyle
import com.athlens.app.ui.theme.LiquidGlassPill
import com.athlens.app.ui.theme.ObsidianCanvas
import com.athlens.app.ui.theme.OceanTeal
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onLaunchCameraCoach: (DrillType) -> Unit = {},
    onLaunchQrScanner: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val p2pState by viewModel.nearbyExchangeManager.state.collectAsStateWithLifecycle()
    val discoveredEndpoints by viewModel.nearbyExchangeManager.discoveredEndpoints.collectAsStateWithLifecycle()
    val statusMessage by viewModel.nearbyExchangeManager.statusMessage.collectAsStateWithLifecycle()

    val hazeState = remember { HazeState() }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianCanvas),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ElectricEmerald)
        }
        return
    }

    AmbientGlassBackground(modifier = modifier.fillMaxSize()) {
        // 1. Content Layer (marked as hazeSource so floating bars blur scrolling content)
        val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState)
                .padding(top = statusBarTop + 62.dp, bottom = 0.dp)
        ) {
            when (uiState.currentTab) {
                DashboardTab.TRAIN -> TrainTab(
                    profile = uiState.athleteProfile,
                    recentSessions = uiState.recentSessions,
                    language = uiState.activeLanguage,
                    onLaunchCameraCoach = onLaunchCameraCoach,
                    hazeState = hazeState
                )
                DashboardTab.PASSPORT -> PassportTab(
                    profile = uiState.athleteProfile,
                    totalSessions = uiState.totalSessions,
                    averageScore = uiState.averageFormScore,
                    latestSessionScore = uiState.recentSessions.firstOrNull()?.formScore,
                    maturityReport = uiState.maturityReport,
                    achievements = uiState.achievements,
                    signedPassport = uiState.signedPassport,
                    language = uiState.activeLanguage,
                    onLogGrowthMeasurement = { h, w ->
                        viewModel.logGrowthMeasurement(h, w)
                    },
                    onAddAchievement = { title, level, issuer, isVerified ->
                        viewModel.addAchievement(title, level, issuer, isVerified)
                    },
                    onDeleteAchievement = { id ->
                        viewModel.deleteAchievement(id)
                    },
                    onVerifyPassport = { payload, sig, pubKey ->
                        viewModel.verifyPassportOffline(payload, sig, pubKey)
                    },
                    onTamperPayload = { payload ->
                        viewModel.simulateTamperedPayload(payload)
                    }
                )
                DashboardTab.NUTRITION -> NutritionTab(
                    sport = uiState.athleteProfile?.sport ?: com.athlens.app.domain.model.Sport.KABADDI,
                    language = uiState.activeLanguage
                )
                DashboardTab.SHARE_P2P -> ShareP2PTab(
                    profile = uiState.athleteProfile,
                    signedPassport = uiState.signedPassport,
                    receivedPassports = uiState.receivedPassports,
                    unsyncedCount = uiState.unsyncedCount,
                    p2pState = p2pState,
                    discoveredEndpoints = discoveredEndpoints,
                    statusMessage = statusMessage,
                    language = uiState.activeLanguage,
                    onStartSharing = { viewModel.startSharingPassport() },
                    onStopSharing = { viewModel.stopP2P() },
                    onStartDiscovery = { viewModel.startDiscovering() },
                    onStopDiscovery = { viewModel.stopP2P() },
                    onConnectEndpoint = { id -> viewModel.connectToEndpoint(id) },
                    onLaunchQrScanner = onLaunchQrScanner,
                    onDeleteReceivedPassport = { id -> viewModel.deleteReceivedPassport(id) },
                    onSyncNow = { viewModel.triggerCloudSync() }
                )
            }
        }

        // 2. Liquid Glass Top Header (Translucent floating bar with real-time blur)
        LiquidGlassHeader(
            activeLanguage = uiState.activeLanguage,
            onToggleLanguage = { viewModel.toggleLanguage() },
            hazeState = hazeState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // 3. Apple-style Floating Capsule Tab Bar (Hovers above bottom system insets)
        FloatingGlassNavBar(
            items = DashboardTab.entries.map { tab ->
                GlassNavItem(
                    title = tab.getTitle(uiState.activeLanguage),
                    icon = tab.icon,
                    isSelected = tab == uiState.currentTab,
                    onClick = { viewModel.selectTab(tab) }
                )
            },
            hazeState = hazeState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * Translucent Floating Liquid Glass Top Header
 */
@Composable
private fun LiquidGlassHeader(
    activeLanguage: Language,
    onToggleLanguage: () -> Unit,
    hazeState: HazeState? = null,
    modifier: Modifier = Modifier
) {
    val navShape = RoundedCornerShape(24.dp)
    val hazeModifier = if (hazeState != null) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                backgroundColor = ObsidianCanvas.copy(alpha = 0.65f),
                tint = HazeTint(Color.White.copy(alpha = 0.05f)),
                blurRadius = 20.dp,
                noiseFactor = 0.05f
            )
        )
    } else Modifier

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(
                elevation = 12.dp,
                shape = navShape,
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = OceanTeal.copy(alpha = 0.25f)
            )
            .then(hazeModifier)
            .clip(navShape)
            .background(
                Brush.linearGradient(
                    0.0f to Color.White.copy(alpha = 0.12f),
                    0.5f to Color(0x280B132B),
                    1.0f to Color.White.copy(alpha = 0.04f)
                )
            )
            .border(1.2.dp, GlassBorderStandard, navShape)
            .drawBehind {
                drawLine(
                    brush = Brush.horizontalGradient(
                        0.0f to Color.Transparent,
                        0.3f to Color.White.copy(alpha = 0.20f),
                        0.7f to Color.White.copy(alpha = 0.20f),
                        1.0f to Color.Transparent
                    ),
                    start = Offset(24f, 1.dp.toPx()),
                    end = Offset(size.width - 24f, 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ATHLENS brand mark with glowing emerald status dot
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "ATHLENS",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    ),
                    color = ElectricEmerald
                )

                // Glowing emerald status dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ElectricEmerald)
                )
            }

            // Compact Glass Language Switcher Pill
            LiquidGlassPill(
                text = when (activeLanguage) {
                    Language.ENGLISH -> "EN"
                    Language.TAMIL -> "தமிழ்"
                    Language.HINDI -> "हिन्दी"
                },
                isSelected = true,
                tintColor = ElectricEmerald,
                onClick = onToggleLanguage
            )
        }
    }
}
