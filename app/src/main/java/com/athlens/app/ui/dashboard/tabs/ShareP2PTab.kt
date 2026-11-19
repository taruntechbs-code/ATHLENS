package com.athlens.app.ui.dashboard.tabs

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.athlens.app.data.local.entity.ReceivedPassportEntity
import com.athlens.app.data.p2p.DiscoveredEndpoint
import com.athlens.app.data.p2p.P2PExchangeState
import com.athlens.app.domain.model.AthleteProfile
import com.athlens.app.domain.model.Language
import com.athlens.app.domain.passport.SignedTalentPassport
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
    modifier: Modifier = Modifier
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Mode Switcher (Send Passport vs Receive Scout) with generous vertical padding
        Surface(
            color = CardNavy,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, CardNavyElevated),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = if (selectedMode == P2PMode.SEND) ElectricEmeraldDark else DeepNavy,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            if (selectedMode != P2PMode.SEND) {
                                onStopDiscovery()
                                selectedMode = P2PMode.SEND
                            }
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                    ) {
                        Text(
                            text = BilingualStrings.getSendPassportTab(language),
                            style = MaterialTheme.typography.titleSmall,
                            color = if (selectedMode == P2PMode.SEND) TextWhite else TextMuted,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    color = if (selectedMode == P2PMode.RECEIVE) ElectricEmeraldDark else DeepNavy,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            if (selectedMode != P2PMode.RECEIVE) {
                                onStopSharing()
                                selectedMode = P2PMode.RECEIVE
                            }
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                    ) {
                        Text(
                            text = BilingualStrings.getReceiveScoutTab(language),
                            style = MaterialTheme.typography.titleSmall,
                            color = if (selectedMode == P2PMode.RECEIVE) TextWhite else TextMuted,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // MODE 1: SEND PASSPORT
        if (selectedMode == P2PMode.SEND) {
            // Signed Passport Preview Card
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
                        Row(
                            modifier = Modifier.weight(1f, fill = false),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = ElectricEmerald,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = when (language) {
                                    Language.ENGLISH -> "TALENT PASSPORT"
                                    Language.TAMIL -> "திறமை பாஸ்போர்ட்"
                                    Language.HINDI -> "टैलेंट पासपोर्ट"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = ElectricEmerald,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        val shortId = profile?.id?.take(8)?.uppercase() ?: "NEW"
                        Surface(
                            color = DeepNavy,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, CardNavyElevated)
                        ) {
                            Text(
                                text = "#$shortId",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextWhite,
                                softWrap = false,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = profile?.fullName ?: "Grassroots Athlete",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    FlowRow(
                        modifier = Modifier.padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = ElectricEmeraldDark,
                            shape = RoundedCornerShape(8.dp)
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
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = profile?.playingPosition ?: "Raider",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricEmerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = CardNavyElevated)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Stats 3-Column Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Birth Quarter", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = signedPassport?.payload?.raeQuarter ?: "Q1",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column {
                            Text(text = "Avg Form Score", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = "${signedPassport?.payload?.avgScore ?: 0} / 100",
                                style = MaterialTheme.typography.titleMedium,
                                color = ElectricEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column {
                            Text(text = "Verified Medals", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = "${signedPassport?.payload?.verifiedAchievements ?: 0}",
                                style = MaterialTheme.typography.titleMedium,
                                color = WarmAmber,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Key Fingerprint Pill
                    Surface(
                        color = DeepNavy,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
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
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = ElectricEmerald,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // P2P Advertising Status or Action Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                border = BorderStroke(1.dp, CardNavyElevated),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (p2pState) {
                        P2PExchangeState.ADVERTISING, P2PExchangeState.CONNECTING, P2PExchangeState.CONNECTED -> {
                            CircularProgressIndicator(
                                color = ElectricEmerald,
                                modifier = Modifier.size(42.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = statusMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ElectricEmerald,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onStopSharing,
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Stop, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(BilingualStrings.getStopSharingButton(language))
                            }
                        }
                        P2PExchangeState.TRANSFER_SUCCESS -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ElectricEmerald,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Passport successfully transmitted to scout!",
                                style = MaterialTheme.typography.titleMedium,
                                color = ElectricEmerald,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { requestNearbyPermissions() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricEmerald,
                                    contentColor = DeepNavy
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp)
                            ) {
                                Text("Share with Another Scout")
                            }
                        }
                        else -> {
                            Text(
                                text = "Exchange your signed credentials offline with coaches and scouts without internet or mobile data.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextGray
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { requestNearbyPermissions() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricEmerald,
                                    contentColor = DeepNavy
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WifiTethering,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = BilingualStrings.getShareOfflineButton(language),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // MODE 2: RECEIVE (SCOUT)
        if (selectedMode == P2PMode.RECEIVE) {
            // Scout Action Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Scan Nearby Button
                Button(
                    onClick = {
                        if (p2pState == P2PExchangeState.DISCOVERING) {
                            onStopDiscovery()
                        } else {
                            val permissions = getRequiredNearbyPermissions()
                            permissionLauncher.launch(permissions)
                            onStartDiscovery()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (p2pState == P2PExchangeState.DISCOVERING) CrimsonAlert else ElectricEmerald,
                        contentColor = DeepNavy
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 46.dp)
                ) {
                    Icon(
                        imageVector = if (p2pState == P2PExchangeState.DISCOVERING) Icons.Default.Stop else Icons.Default.Radar,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (p2pState == P2PExchangeState.DISCOVERING) {
                            BilingualStrings.getStopScanButton(language)
                        } else {
                            BilingualStrings.getScanNearbyButton(language)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Scan QR Button
                OutlinedButton(
                    onClick = onLaunchQrScanner,
                    border = BorderStroke(1.dp, ElectricEmerald),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricEmerald),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 46.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = BilingualStrings.getScanQrButton(language),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Discovered Nearby Athletes Section
            if (p2pState == P2PExchangeState.DISCOVERING || discoveredEndpoints.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = BorderStroke(1.5.dp, ElectricEmerald),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Devices,
                                    contentDescription = null,
                                    tint = ElectricEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = BilingualStrings.getDiscoveredAthletesTitle(language),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            CircularProgressIndicator(
                                color = ElectricEmerald,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (discoveredEndpoints.isEmpty()) {
                            Text(
                                text = BilingualStrings.getNoAthletesDiscovered(language),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                discoveredEndpoints.forEach { endpoint ->
                                    Surface(
                                        color = DeepNavy,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = endpoint.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextWhite,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.weight(1f),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Button(
                                                onClick = { onConnectEndpoint(endpoint.id) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = ElectricEmerald,
                                                    contentColor = DeepNavy
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = BilingualStrings.getConnectAndReceiveButton(language),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Verified Scout Inbox Section
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                border = BorderStroke(1.dp, CardNavyElevated),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inbox,
                                contentDescription = null,
                                tint = ElectricEmerald,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = BilingualStrings.getScoutInboxTitle(language),
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Surface(
                            color = CardNavyElevated,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${receivedPassports.size} Athletes",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricEmerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (receivedPassports.isEmpty()) {
                        Surface(
                            color = DeepNavy,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = BilingualStrings.getNoPassportsReceived(language),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            receivedPassports.forEach { item ->
                                ReceivedPassportCard(
                                    passport = item,
                                    language = language,
                                    onDelete = { onDeleteReceivedPassport(item.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Cloud Sync Status Bar
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(1.dp, if (unsyncedCount > 0) WarmAmber else ElectricEmeraldDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (unsyncedCount > 0) Icons.Default.CloudSync else Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = if (unsyncedCount > 0) WarmAmber else ElectricEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = BilingualStrings.getCloudSyncTitle(language),
                            style = MaterialTheme.typography.titleSmall,
                            color = TextWhite,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (unsyncedCount > 0) {
                                BilingualStrings.getPendingRecordsLabel(unsyncedCount, language)
                            } else {
                                BilingualStrings.getAllSyncedLabel(language)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (unsyncedCount > 0) WarmAmber else ElectricEmerald,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Button(
                    onClick = {
                        onSyncNow()
                        Toast.makeText(context, "WorkManager cloud sync queued", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (unsyncedCount > 0) WarmAmber else CardNavyElevated,
                        contentColor = if (unsyncedCount > 0) DeepNavy else TextWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.heightIn(min = 40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = BilingualStrings.getSyncNowButton(language),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Bottom space so last card glides smoothly underneath floating glass nav bar
        Spacer(modifier = Modifier.navigationBarsPadding().height(104.dp))
    }

    // Minor Guardian / P2P Sharing Consent Dialog
    if (showConsentDialog) {
        AlertDialog(
            onDismissRequest = { showConsentDialog = false },
            containerColor = CardNavy,
            icon = {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = ElectricEmerald,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = BilingualStrings.getConsentDialogTitle(language),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite
                )
            },
            text = {
                Text(
                    text = BilingualStrings.getConsentDialogMessage(
                        guardianName = profile?.guardianName,
                        isMinor = profile?.isMinor == true,
                        lang = language
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConsentDialog = false
                        onStartSharing()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricEmerald,
                        contentColor = DeepNavy
                    )
                ) {
                    Text(BilingualStrings.getApproveButton(language))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConsentDialog = false }) {
                    Text(BilingualStrings.getCancelButton(language), color = TextGray)
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReceivedPassportCard(
    passport: ReceivedPassportEntity,
    language: Language,
    onDelete: () -> Unit
) {
    Surface(
        color = DeepNavy,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CardNavyElevated),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = passport.athleteName,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${passport.district} • ${passport.sport} (${passport.playingPosition})",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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

            Spacer(modifier = Modifier.height(10.dp))

            // Badges row: Transfer Method + Verification Badge using FlowRow
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = CardNavyElevated,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (passport.transferMethod == "NEARBY_P2P") "P2P Transfer" else "QR Scan",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextWhite,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = if (passport.signatureVerified) ElectricEmeraldDark.copy(alpha = 0.25f) else CrimsonAlert.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, if (passport.signatureVerified) ElectricEmerald else CrimsonAlert)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (passport.signatureVerified) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (passport.signatureVerified) ElectricEmerald else CrimsonAlert,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (passport.signatureVerified) {
                                BilingualStrings.getEcdsaVerifiedBadge(language)
                            } else {
                                BilingualStrings.getSignatureInvalidBadge(language)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (passport.signatureVerified) ElectricEmerald else CrimsonAlert,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Telemetry & Key details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Score: ${passport.avgFormScore}/100 • RAE: ${passport.raeQuarter}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Key: ${passport.publicKeyFingerprint}",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = ElectricEmerald
                )
            }
        }
    }
}

private fun getRequiredNearbyPermissions(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.NEARBY_WIFI_DEVICES
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}
