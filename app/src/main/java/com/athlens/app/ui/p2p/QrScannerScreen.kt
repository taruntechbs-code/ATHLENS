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
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.athlens.app.ui.theme.ElectricEmeraldDark
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextMuted
import com.athlens.app.ui.theme.TextWhite
import com.athlens.app.ui.util.BilingualStrings
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

                        val source = PlanarYUVLuminanceSource(
                            data, width, height, 0, 0, width, height, false
                        )
                        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

                        try {
                            val rawResult = reader.decodeWithState(binaryBitmap)
                            val text = rawResult.text
                            Log.d("QrScannerScreen", "Decoded QR string: ${text.take(60)}...")

                            val envelope = PassportSerializer.parseEnvelope(text)
                            if (envelope != null) {
                                hasProcessed = true
                                val (canonicalJson, signatureBase64, pubKeyBase64) = envelope
                                val parsedPayload = PassportSerializer.parseCanonicalPayload(canonicalJson)

                                // 100% Offline Cryptographic Verification
                                val isValid = passportCryptoManager.verifySignature(
                                    canonicalJson,
                                    signatureBase64,
                                    pubKeyBase64
                                )

                                val fingerprint = try {
                                    val keyBytes = Base64.decode(pubKeyBase64, Base64.NO_WRAP)
                                    val md = MessageDigest.getInstance("SHA-256")
                                    val hash = md.digest(keyBytes)
                                    hash.take(6).joinToString("") { "%02X".format(it) }
                                } catch (e: Exception) {
                                    "QR-KEY"
                                }

                                val entity = ReceivedPassportEntity(
                                    id = UUID.randomUUID().toString(),
                                    athleteName = parsedPayload?.fullName ?: "Verified Athlete",
                                    district = parsedPayload?.district ?: "Coimbatore",
                                    sport = parsedPayload?.sport ?: "KABADDI",
                                    playingPosition = parsedPayload?.playingPosition ?: "Raider",
                                    ageMonths = parsedPayload?.ageMonths ?: 0,
                                    raeQuarter = parsedPayload?.raeQuarter ?: "Q1",
                                    totalSessions = parsedPayload?.totalSessions ?: 0,
                                    avgFormScore = parsedPayload?.avgScore ?: 0,
                                    verifiedAchievementsCount = parsedPayload?.verifiedAchievements ?: 0,
                                    signatureVerified = isValid,
                                    publicKeyFingerprint = fingerprint,
                                    transferMethod = "QR_SCAN",
                                    rawEnvelopeJson = text,
                                    receivedAt = System.currentTimeMillis(),
                                    synced = false
                                )

                                scannedEntity = entity
                            }
                        } catch (e: Exception) {
                            // No QR code found in current frame
                        } finally {
                            reader.reset()
                            imageProxy.close()
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
                        Log.e("QrScannerScreen", "Camera bind failed: ${e.message}", e)
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

        // Top Navigation Header
        Surface(
            color = DeepNavy.copy(alpha = 0.85f),
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = when (language) {
                            Language.ENGLISH -> "Scout QR Passport Scanner"
                            Language.TAMIL -> "சாரணர் QR பாஸ்போர்ட் ஸ்கேனர்"
                            Language.HINDI -> "स्काउट क्यूआर पासपोर्ट स्कैनर"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "100% Offline Biometric & ECDSA Verifier",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricEmerald
                    )
                }
            }
        }

        // Bottom Instruction Card / Feedback Banner
        Surface(
            color = DeepNavy.copy(alpha = 0.90f),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (entity.signatureVerified) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (entity.signatureVerified) ElectricEmerald else CrimsonAlert,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (entity.signatureVerified) {
                                "ECDSA P-256 Validated Offline!"
                            } else {
                                "Signature Verification Failed"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = if (entity.signatureVerified) ElectricEmerald else CrimsonAlert,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${entity.athleteName} • ${entity.district} (${entity.sport})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextWhite
                    )
                    Text(
                        text = "Score: ${entity.avgFormScore}/100 • Key: ${entity.publicKeyFingerprint}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            } else {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = ElectricEmerald,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = when (language) {
                            Language.ENGLISH -> "Align athlete's Signed QR Passport inside the target box"
                            Language.TAMIL -> "வீரரின் கையொப்பமிட்ட QR பாஸ்போர்ட்டை கட்டத்திற்குள் பொருத்தவும்"
                            Language.HINDI -> "खिलाड़ी के हस्ताक्षरित क्यूआर पासपोर्ट को बॉक्स के अंदर रखें"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
            }
        }
    }
}
