package com.athlens.app.ui.dashboard.tabs

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.athlens.app.data.local.entity.ReceivedPassportEntity
import com.athlens.app.data.p2p.DiscoveredEndpoint
import com.athlens.app.data.p2p.P2PExchangeState
import com.athlens.app.domain.model.AthleteProfile
import com.athlens.app.domain.model.Language
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
import dev.chrisbanes.haze.HazeState

enum class P2PMode {
    SEND,
    RECEIVE
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShareP2PTab(
    profile: AthleteProfile?,
    signedPassport: SignedTalentPassport?,
    receivedPassports: List<ReceivedPassportEntity>,
    unsyncedCount: Int,
    p2pState: P2PExchangeState,
    discoveredEndpoints: List<DiscoveredEndpoint>,
    statusMessage: String,
    language: Language,
    onStartSharing: () -> Unit,
    onStopSharing: () -> Unit,
    onStartDiscovery: () -> Unit,
    onStopDiscovery: () -> Unit,
    onConnectEndpoint: (String) -> Unit,
    onLaunchQrScanner: () -> Unit,
    onDeleteReceivedPassport: (String) -> Unit,
    onSyncNow: () -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var selectedMode by remember { mutableStateOf(P2PMode.SEND) }
    var showConsentDialog by remember { mutableStateOf(false) }

    // Nearby Runtime Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            showConsentDialog = true
        } else {
            Toast.makeText(
                context,
                "Bluetooth & Nearby permissions required for offline P2P transfer",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val requestNearbyPermissions = {
        val permissions = getRequiredNearbyPermissions()
        permissionLauncher.launch(permissions)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. iOS-Style Glass Segmented Switcher
        GlassSegmentedSwitcher(
            selectedMode = selectedMode,
            language = language,
            onSelectMode = { mode ->
                if (mode == P2PMode.SEND) {
                    onStopDiscovery()
                } else {
                    onStopSharing()
                }
                selectedMode = mode
            }
        )

        // 2. MODE 1: SEND PASSPORT
        if (selectedMode == P2PMode.SEND) {
            // Mini Apple-Wallet-Style Preview Card
            MiniWalletPreviewCard(
                profile = profile,
                signedPassport = signedPassport,
                language = language,
                hazeState = hazeState
            )

            // P2P Advertising / Radar Action Card
            P2PAdvertisingRadarCard(
                p2pState = p2pState,
                statusMessage = statusMessage,
                language = language,
                onStartClick = requestNearbyPermissions,
                onStopClick = onStopSharing,
                hazeState = hazeState
            )
        }

        // 3. MODE 2: RECEIVE (SCOUT)
        if (selectedMode == P2PMode.RECEIVE) {
            // Action Row: Scan Nearby P2P + Scan QR Passport
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val isDiscovering = p2pState == P2PExchangeState.DISCOVERING
                LiquidGlassButton(
                    onClick = {
                        if (isDiscovering) {
                            onStopDiscovery()
                        } else {
                            val permissions = getRequiredNearbyPermissions()
                            permissionLauncher.launch(permissions)
                            onStartDiscovery()
                        }
                    },
                    isPrimary = !isDiscovering,
                    borderBrush = if (isDiscovering) Brush.linearGradient(listOf(CrimsonAlert, CrimsonAlert)) else GlassBorderEmerald,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isDiscovering) Icons.Default.Stop else Icons.Default.Radar,
                        contentDescription = null,
                        tint = if (isDiscovering) CrimsonAlert else DeepNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isDiscovering) "Stop Radar" else "Scan Nearby",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDiscovering) CrimsonAlert else DeepNavy,
                        maxLines = 1
                    )
                }

                LiquidGlassButton(
                    onClick = onLaunchQrScanner,
                    isPrimary = true,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = DeepNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Scan QR",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = DeepNavy,
                        maxLines = 1
                    )
                }
            }

            // Discovered Nearby Endpoints Card (if any)
            if (discoveredEndpoints.isNotEmpty()) {
                DiscoveredEndpointsCard(
                    endpoints = discoveredEndpoints,
                    onConnect = onConnectEndpoint,
                    hazeState = hazeState
                )
            }

            // Verified Scout Inbox Card
            ScoutInboxCard(
                receivedPassports = receivedPassports,
                language = language,
                onDelete = onDeleteReceivedPassport,
                hazeState = hazeState
            )
        }

        // 4. Cloud Sync Status Card
        CloudSyncStatusCard(
            unsyncedCount = unsyncedCount,
            language = language,
            onSyncNow = onSyncNow,
            hazeState = hazeState
        )

        // Bottom clearance for floating nav bar
        Spacer(modifier = Modifier.navigationBarsPadding().height(104.dp))
    }

    // Consent Confirmation Dialog (Dark Glass Style)
    if (showConsentDialog) {
        ShareConsentDialog(
            language = language,
            onDismiss = { showConsentDialog = false },
            onConfirm = {
                showConsentDialog = false
                onStartSharing()
            }
        )
    }
}

/**
 * 1. iOS-Style Glass Segmented Switcher
 */
@Composable
private fun GlassSegmentedSwitcher(
    selectedMode: P2PMode,
    language: Language,
    onSelectMode: (P2PMode) -> Unit
) {
    val containerShape = RoundedCornerShape(24.dp)
    val pillShape = CircleShape

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(containerShape)
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, GlassBorderStandard, containerShape)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Send Passport Segment
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(pillShape)
                    .background(
                        if (selectedMode == P2PMode.SEND) Color.White.copy(alpha = 0.18f) else Color.Transparent
                    )
                    .border(
                        1.dp,
                        if (selectedMode == P2PMode.SEND) GlassBorderEmerald else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                        pillShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onSelectMode(P2PMode.SEND) }
                    )
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = BilingualStrings.getSendPassportTab(language),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        letterSpacing = (-0.3).sp
                    ),
                    color = if (selectedMode == P2PMode.SEND) TextWhite else TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Receive (Scout) Segment
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(pillShape)
                    .background(
                        if (selectedMode == P2PMode.RECEIVE) Color.White.copy(alpha = 0.18f) else Color.Transparent
                    )
                    .border(
                        1.dp,
                        if (selectedMode == P2PMode.RECEIVE) GlassBorderEmerald else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                        pillShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onSelectMode(P2PMode.RECEIVE) }
                    )
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = BilingualStrings.getReceiveScoutTab(language),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        letterSpacing = (-0.3).sp
                    ),
                    color = if (selectedMode == P2PMode.RECEIVE) TextWhite else TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * 2. Mini Apple-Wallet-Style Preview Card
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MiniWalletPreviewCard(
    profile: AthleteProfile?,
    signedPassport: SignedTalentPassport?,
    language: Language,
    hazeState: HazeState? = null
) {
    ContourGlassCard(
        hazeState = hazeState,
        borderBrush = GlassBorderEmerald,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(ElectricEmerald)
                    )
                    Text(
                        text = when (language) {
                            Language.ENGLISH -> "TALENT PASSPORT PREVIEW"
                            Language.TAMIL -> "பாஸ்போர்ட் மாதிரிக் காட்சி"
                            Language.HINDI -> "पासपोर्ट पूर्वावलोकन"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = ElectricEmerald
                    )
                }

                val shortId = profile?.id?.take(8)?.uppercase() ?: "NEW"
                LiquidGlassPill(
                    text = "#$shortId",
                    isSelected = true,
                    tintColor = ElectricEmerald
                )
            }

            // Athlete Name & Position
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = profile?.fullName ?: "Grassroots Athlete",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
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
                }
            }

            // Quick Stats 3-Tile Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, GlassBorderStandard, RoundedCornerShape(14.dp))
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatColumn(label = "RAE", value = signedPassport?.payload?.raeQuarter ?: "Q1")
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.12f))
                )
                StatColumn(
                    label = "Avg Score",
                    value = "${signedPassport?.payload?.avgScore ?: 0}/100",
                    valueColor = ElectricEmerald
                )
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.12f))
                )
                StatColumn(
                    label = "Medals",
                    value = "${signedPassport?.payload?.verifiedAchievements ?: 0}",
                    valueColor = WarmAmber
                )
            }

            // Fingerprint Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "ECDSA P-256 Key",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
                Text(
                    text = signedPassport?.keyFingerprint ?: "--",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ElectricEmerald
                )
            }
        }
    }
}

@Composable
private fun StatColumn(
    label: String,
    value: String,
    valueColor: Color = TextWhite
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFeatureSettings = "tnum"
            ),
            color = valueColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
    }
}

/**
 * AirDrop-Style Concentric Pulsing Radar Animation
 */
@Composable
private fun AnimatedRadarRings(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "radar_transition")

    val pulse1 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_1"
    )

    val pulse2 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, delayMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_2"
    )

    Box(
        modifier = modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val maxRadius = size.minDimension / 2

            // Outer pulse 1
            drawCircle(
                color = ElectricEmerald.copy(alpha = (1f - pulse1) * 0.45f),
                radius = maxRadius * pulse1,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Outer pulse 2
            drawCircle(
                color = ElectricEmerald.copy(alpha = (1f - pulse2) * 0.45f),
                radius = maxRadius * pulse2,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Central Broadcast Icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(ElectricEmerald.copy(alpha = 0.20f))
                .border(1.5.dp, ElectricEmerald, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.WifiTethering,
                contentDescription = "Radar Broadcast",
                tint = ElectricEmerald,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * P2P Advertising Status / Action Card
 */
@Composable
private fun P2PAdvertisingRadarCard(
    p2pState: P2PExchangeState,
    statusMessage: String,
    language: Language,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    hazeState: HazeState? = null
) {
    ContourGlassCard(
        hazeState = hazeState,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (p2pState) {
                P2PExchangeState.ADVERTISING, P2PExchangeState.CONNECTING, P2PExchangeState.CONNECTED -> {
                    // Concentric Animated Radar Rings
                    AnimatedRadarRings()

                    Text(
                        text = statusMessage.ifBlank { "Advertising via BLE & Wi-Fi Direct" },
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = ElectricEmerald
                    )

                    LiquidGlassButton(
                        onClick = onStopClick,
                        isPrimary = false,
                        borderBrush = Brush.linearGradient(listOf(CrimsonAlert, CrimsonAlert)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 46.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = null,
                            tint = CrimsonAlert,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = BilingualStrings.getStopSharingButton(language),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = CrimsonAlert
                        )
                    }
                }
                P2PExchangeState.TRANSFER_SUCCESS -> {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(ElectricEmerald.copy(alpha = 0.20f))
                            .border(1.5.dp, ElectricEmerald, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ElectricEmerald,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "Passport successfully transmitted to scout!",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = ElectricEmerald
                    )

                    LiquidGlassButton(
                        onClick = onStartClick,
                        isPrimary = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 46.dp)
                    ) {
                        Text(
                            text = "Share with Another Scout",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = DeepNavy
                        )
                    }
                }
                else -> {
                    Text(
                        text = "Exchange your cryptographically signed talent passport offline with scouts and coaches using Wi-Fi Direct & BLE.",
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                        color = TextGray
                    )

                    LiquidGlassButton(
                        onClick = onStartClick,
                        isPrimary = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiTethering,
                            contentDescription = null,
                            tint = DeepNavy,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = BilingualStrings.getShareOfflineButton(language),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = DeepNavy
                        )
                    }
                }
            }
        }
    }
}

/**
 * Discovered Endpoints Card
 */
@Composable
private fun DiscoveredEndpointsCard(
    endpoints: List<DiscoveredEndpoint>,
    onConnect: (String) -> Unit,
    hazeState: HazeState? = null
) {
    ContourGlassCard(
        hazeState = hazeState,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Nearby Devices Found",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = OceanTeal
            )

            endpoints.forEach { endpoint ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = endpoint.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )

                    LiquidGlassButton(
                        onClick = { onConnect(endpoint.id) },
                        isPrimary = true,
                        modifier = Modifier.heightIn(min = 34.dp)
                    ) {
                        Text(
                            text = "Connect",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = DeepNavy
                        )
                    }
                }
            }
        }
    }
}

/**
 * Verified Scout Inbox Card
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ScoutInboxCard(
    receivedPassports: List<ReceivedPassportEntity>,
    language: Language,
    onDelete: (String) -> Unit,
    hazeState: HazeState? = null
) {
    ContourGlassCard(
        hazeState = hazeState,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = null,
                        tint = ElectricEmerald,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = when (language) {
                            Language.ENGLISH -> "Scout Inbox"
                            Language.TAMIL -> "ஸ்கவுட் இன்பாக்ஸ்"
                            Language.HINDI -> "स्काउट इनबॉक्स"
                        },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                }

                LiquidGlassPill(
                    text = "${receivedPassports.size} Athletes",
                    isSelected = receivedPassports.isNotEmpty(),
                    tintColor = ElectricEmerald
                )
            }

            if (receivedPassports.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (language) {
                            Language.ENGLISH -> "No passports received yet. Scan a QR Passport or connect via Nearby P2P."
                            Language.TAMIL -> "பாஸ்போர்ட்கள் எதுவும் பெறப்படவில்லை. QR குறியீட்டை ஸ்கேன் செய்யவும்."
                            Language.HINDI -> "कोई पासपोर्ट नहीं। क्यूआर स्कैन करें या नियरबाय से जुड़ें।"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    receivedPassports.forEach { passport ->
                        ReceivedPassportGlassItem(
                            passport = passport,
                            onDelete = { onDelete(passport.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReceivedPassportGlassItem(
    passport: ReceivedPassportEntity,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(
                1.dp,
                if (passport.signatureVerified) GlassBorderEmerald else Brush.linearGradient(listOf(CrimsonAlert, CrimsonAlert)),
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Top row: Name + Delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = passport.athleteName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${passport.district} • ${passport.sport} (${passport.playingPosition})",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Chips Row
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LiquidGlassPill(
                    text = passport.transferMethod,
                    isSelected = false
                )
                LiquidGlassPill(
                    text = "RAE: ${passport.raeQuarter}",
                    isSelected = false
                )
                LiquidGlassPill(
                    text = "Score: ${passport.avgFormScore}/100",
                    isSelected = true,
                    tintColor = ElectricEmerald
                )
            }

            // Cryptographic Verification Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (passport.signatureVerified) ElectricEmerald.copy(alpha = 0.16f) else CrimsonAlert.copy(alpha = 0.16f)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (passport.signatureVerified) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = if (passport.signatureVerified) ElectricEmerald else CrimsonAlert,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (passport.signatureVerified) "ECDSA P-256 Verified Offline" else "Signature INVALID",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (passport.signatureVerified) ElectricEmerald else CrimsonAlert
                )
            }
        }
    }
}

/**
 * 4. Cloud Sync Status Card
 */
@Composable
private fun CloudSyncStatusCard(
    unsyncedCount: Int,
    language: Language,
    onSyncNow: () -> Unit,
    hazeState: HazeState? = null
) {
    ContourGlassCard(
        hazeState = hazeState,
        modifier = Modifier.fillMaxWidth()
    ) {
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
                        .background(if (unsyncedCount == 0) ElectricEmerald.copy(alpha = 0.20f) else OceanTeal.copy(alpha = 0.20f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (unsyncedCount == 0) Icons.Default.CloudDone else Icons.Default.CloudSync,
                        contentDescription = null,
                        tint = if (unsyncedCount == 0) ElectricEmerald else OceanTeal,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when (language) {
                            Language.ENGLISH -> "Offline Sync Engine"
                            Language.TAMIL -> "கிளவுட் ஒத்திசைவு"
                            Language.HINDI -> "ऑफलाइन सिंक इंजन"
                        },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (unsyncedCount == 0) "All local telemetry synced" else "$unsyncedCount records queued offline",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (unsyncedCount == 0) ElectricEmerald else WarmAmber,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            LiquidGlassPill(
                text = "Sync Now",
                isSelected = true,
                tintColor = ElectricEmerald,
                onClick = onSyncNow
            )
        }
    }
}

/**
 * Consent Confirmation Dialog (Dark Glass Style)
 */
@Composable
private fun ShareConsentDialog(
    language: Language,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.80f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(ObsidianCanvas.copy(alpha = 0.95f))
                    .border(1.2.dp, GlassBorderEmerald, RoundedCornerShape(28.dp))
                    .padding(22.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(ElectricEmerald.copy(alpha = 0.20f))
                            .border(1.2.dp, ElectricEmerald, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiTethering,
                            contentDescription = null,
                            tint = ElectricEmerald,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Text(
                        text = "Broadcast Athlete Passport?",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )

                    Text(
                        text = "Your cryptographically signed athletic credentials will be shared offline with nearby coaches and scouts via Wi-Fi Direct. No personal phone number or private data is exposed.",
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                        color = TextGray
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
                            onClick = onConfirm,
                            isPrimary = true,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Start Sharing",
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

private fun getRequiredNearbyPermissions(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.NEARBY_WIFI_DEVICES,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )
    }
}
