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
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.athlens.app.domain.model.Language
import com.athlens.app.ui.theme.CardNavy
import com.athlens.app.ui.theme.CardNavyElevated
import com.athlens.app.ui.theme.DeepNavy
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.ElectricEmeraldDark
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextWhite
import com.athlens.app.ui.theme.WarmAmber

@Composable
fun ShareP2PTab(
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
        // Phase 6 P2P Exchange Header Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(2.dp, ElectricEmerald),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WifiTethering,
                            contentDescription = "P2P Share",
                            tint = ElectricEmerald,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (language == Language.TAMIL) "ஆஃப்லைன் திறமை பகிர்வு (P2P)" else "Offline Talent Exchange (P2P)",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextWhite
                        )
                    }

                    Surface(
                        color = ElectricEmeraldDark,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "PHASE 6",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextWhite,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (language == Language.TAMIL) {
                        "4G/5G இணைய சேவை இல்லாத தொலைதூர கிராமப்புற மைதானங்களிலும், Wi-Fi Direct மற்றும் BLE வழியாக ஸ்கவுட்களுக்கு தடகள பாஸ்போர்ட்டை பாதுகாப்பாக பகிரும் தொழில்நுட்பம்."
                    } else {
                        "Decentralized peer-to-peer athlete discovery network. Transfer cryptographically signed sports passports to scouts and coaches on remote grounds without mobile internet."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            }
        }

        // P2P Architecture Capabilities
        Text(
            text = if (language == Language.TAMIL) "ஆஃப்லைன் பரிமாற்ற அம்சங்கள் / P2P Mesh Features" else "Offline Exchange Capabilities",
            style = MaterialTheme.typography.titleMedium,
            color = ElectricEmerald
        )

        P2PFeatureCard(
            title = if (language == Language.TAMIL) "Wi-Fi Direct & BLE கண்ணி வலைப்பின்னல்" else "Wi-Fi Direct & BLE Local Mesh",
            description = if (language == Language.TAMIL) {
                "இணைய சேவர் தேவையின்றி, 100 மீட்டர் சுற்றளவில் உள்ள ஸ்கவுட் சாதனங்களுக்கு வினாடிகளில் தரவு பரிமாற்றம்."
            } else {
                "Direct device-to-device transport layer spanning up to 100 meters across sports grounds with sub-second pairing."
            },
            icon = Icons.Default.Wifi
        )

        P2PFeatureCard(
            title = if (language == Language.TAMIL) "கிரிப்டோ கையொப்பமிட்ட பாஸ்போர்ட்" else "Cryptographically Signed Passport",
            description = if (language == Language.TAMIL) {
                "போலியான திருத்தங்களை தடுக்கும் டிஜிட்டல் கையொப்பம். மாவட்ட மற்றும் மாநில தேர்வாளர்களுக்கு நம்பகமான சான்று."
            } else {
                "Tamper-proof verifiable credential signing ensuring talent stats cannot be forged by third parties."
            },
            icon = Icons.Default.Security
        )

        P2PFeatureCard(
            title = if (language == Language.TAMIL) "விரைவு QR குறியீடு ஸ்கேன்" else "Instant QR Offline Handshake",
            description = if (language == Language.TAMIL) {
                "ஸ்கவுட்கள் ஒரே ஸ்கேனில் வீரரின் முழுமையான பயிற்சி வரலாறு மற்றும் உடல் தகவல்களை சரிபார்க்கலாம்."
            } else {
                "High-density compressed offline QR protocol allowing academy coaches to onboard athletes in a single camera tap."
            },
            icon = Icons.Default.QrCode
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { /* Phase 6 Action */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricEmerald,
                contentColor = DeepNavy
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (language == Language.TAMIL) "P2P பரிமாற்றம் (Phase 6 விரைவில்)" else "P2P Mesh Broadcast (Phase 6 Coming Soon)",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun P2PFeatureCard(
    title: String,
    description: String,
    icon: ImageVector
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = BorderStroke(1.dp, CardNavyElevated),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = WarmAmber,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }
        }
    }
}
