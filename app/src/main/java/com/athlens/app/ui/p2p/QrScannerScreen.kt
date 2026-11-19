package com.athlens.app.ui.p2p

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.athlens.app.data.local.entity.ReceivedPassportEntity
import com.athlens.app.data.security.PassportCryptoManager
import com.athlens.app.domain.model.Language
import com.athlens.app.domain.passport.PassportSerializer
import com.athlens.app.ui.theme.CardNavy
import com.athlens.app.ui.theme.CrimsonAlert
import com.athlens.app.ui.theme.DeepNavy
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextMuted
import com.athlens.app.ui.theme.TextWhite
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.delay
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.Executors

@Composable
fun QrScannerScreen(
    passportCryptoManager: PassportCryptoManager,
    language: Language,
    onPassportScanned: (ReceivedPassportEntity) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var scannedEntity by remember { mutableStateOf<ReceivedPassportEntity?>(null) }
    var hasProcessed by remember { mutableStateOf(false) }

    // Auto-return after showing verification feedback
    LaunchedEffect(scannedEntity) {
        if (scannedEntity != null) {
            delay(1600)
            onPassportScanned(scannedEntity!!)
            onNavigateBack()
        }
    }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(DeepNavy)) {
        // CameraX Preview
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    val reader = MultiFormatReader().apply {
                        val hints = mapOf<DecodeHintType, Any>(
                            DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
                            DecodeHintType.CHARACTER_SET to "UTF-8",
                            DecodeHintType.TRY_HARDER to true
                        )
                        setHints(hints)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        if (hasProcessed) {
                            imageProxy.close()
                            return@setAnalyzer
                        }

                        val plane = imageProxy.planes[0]
                        val buffer = plane.buffer
                        val data = ByteArray(buffer.remaining())
                        buffer.get(data)

                        val width = imageProxy.width
                        val height = imageProxy.height

                        try {
                            val source = PlanarYUVLuminanceSource(
                                data, width, height,
                                0, 0, width, height, false
                            )
                            val bitmap = BinaryBitmap(HybridBinarizer(source))
                            val result = reader.decodeWithState(bitmap)
                            val qrContent = result.text

                            if (!hasProcessed && qrContent.isNotBlank()) {
                                val envelope = PassportSerializer.parseEnvelope(qrContent)

                                if (envelope != null) {
                                    hasProcessed = true
                                    val (canonicalJson, sigBase64, pubKeyBase64) = envelope
                                    val verified = passportCryptoManager.verifySignature(
                                        canonicalPayload = canonicalJson,
                                        signatureBase64 = sigBase64,
                                        publicKeyBase64 = pubKeyBase64
                                    )

                                    val hexFingerprint = try {
                                        val keyBytes = Base64.decode(pubKeyBase64, Base64.NO_WRAP)
                                        val md = MessageDigest.getInstance("SHA-256")
                                        val hash = md.digest(keyBytes)
                                        hash.take(6).joinToString("") { "%02X".format(it) }
                                    } catch (e: Exception) {
                                        "QR-KEY"
                                    }
                                    val parsedPayload = PassportSerializer.parseCanonicalPayload(canonicalJson)

                                    val entity = ReceivedPassportEntity(
                                        id = UUID.randomUUID().toString(),
                                        athleteName = parsedPayload?.fullName ?: "Athlete",
                                        district = parsedPayload?.district ?: "Coimbatore",
                                        sport = parsedPayload?.sport ?: "KABADDI",
                                        playingPosition = parsedPayload?.playingPosition ?: "Raider",
                                        ageMonths = parsedPayload?.ageMonths ?: 0,
                                        raeQuarter = parsedPayload?.raeQuarter ?: "Q1",
                                        totalSessions = parsedPayload?.totalSessions ?: 0,
                                        avgFormScore = parsedPayload?.avgScore ?: 0,
                                        verifiedAchievementsCount = parsedPayload?.verifiedAchievements ?: 0,
                                        signatureVerified = verified,
                                        publicKeyFingerprint = hexFingerprint,
                                        transferMethod = "QR_SCAN",
                                        rawEnvelopeJson = qrContent,
                                        receivedAt = System.currentTimeMillis(),
                                        synced = false
                                    )
                                    scannedEntity = entity
                                }
                            }
                        } catch (e: Exception) {
                            // QR not found in frame, continue
                        } finally {
                            imageProxy.close()
                            reader.reset()
                        }
                    }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.e("QrScannerScreen", "Camera bind error", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Viewfinder reticle overlay
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(BorderStroke(2.dp, ElectricEmerald), RoundedCornerShape(16.dp))
            )
        }

        // Top Navigation Header with statusBarsPadding
        Surface(
            color = DeepNavy.copy(alpha = 0.85f),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when (language) {
                            Language.ENGLISH -> "Scout QR Passport Scanner"
                            Language.TAMIL -> "சாரணர் QR பாஸ்போர்ட் ஸ்கேனர்"
                            Language.HINDI -> "स्काउट क्यूआर पासपोर्ट स्कैनर"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "100% Offline Biometric & ECDSA Verifier",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricEmerald,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Bottom Instruction Card / Feedback Banner with navigationBarsPadding
        Surface(
            color = DeepNavy.copy(alpha = 0.90f),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, CardNavy)
        ) {
            val entity = scannedEntity
            if (entity != null) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (entity.signatureVerified) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (entity.signatureVerified) ElectricEmerald else CrimsonAlert,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = if (entity.signatureVerified) {
                                "ECDSA P-256 Validated Offline!"
                            } else {
                                "Signature Verification Failed"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = if (entity.signatureVerified) ElectricEmerald else CrimsonAlert,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${entity.athleteName} • ${entity.district} (${entity.sport})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Score: ${entity.avgFormScore}/100 • Key: ${entity.publicKeyFingerprint}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = ElectricEmerald,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = when (language) {
                            Language.ENGLISH -> "Align athlete's Signed QR Passport inside the target box"
                            Language.TAMIL -> "வீரரின் கையொப்பமிட்ட QR பாஸ்போர்ட்டை கட்டத்திற்குள் பொருத்தவும்"
                            Language.HINDI -> "खिलाड़ी के हस्ताक्षरित क्यूआर पासपोर्ट को बॉक्स के अंदर रखें"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
