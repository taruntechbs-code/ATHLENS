package com.athlens.app.ui.p2p

import android.util.Base64
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.athlens.app.data.local.entity.ReceivedPassportEntity
import com.athlens.app.data.security.PassportCryptoManager
import com.athlens.app.domain.model.Language
import com.athlens.app.domain.passport.PassportSerializer
import com.athlens.app.ui.theme.ContourGlassCard
import com.athlens.app.ui.theme.CrimsonAlert
import com.athlens.app.ui.theme.DeepNavy
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.GlassBorderEmerald
import com.athlens.app.ui.theme.GlassBorderStandard
import com.athlens.app.ui.theme.LiquidGlassPill
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

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        // 1. CameraX Preview
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

        // 2. Translucent Mask & Scanning Reticle with Animated Laser
        val scanTransition = rememberInfiniteTransition(label = "laser_sweep")
        val laserProgress by scanTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "laser_pos"
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val boxSize = 260.dp.toPx()
            val left = (width - boxSize) / 2f
            val top = (height - boxSize) / 2f
            val cornerRadius = 24.dp.toPx()
            val armLength = 28.dp.toPx()
            val strokeWidth = 3.5.dp.toPx()

            // Translucent Mask outside reticle
            val path = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(left, top, left + boxSize, top + boxSize),
                        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                    )
                )
            }

            clipPath(path, clipOp = ClipOp.Difference) {
                drawRect(color = Color(0x99050814))
            }

            // Glowing Emerald Corner Contour Brackets
            val bracketColor = ElectricEmerald

            // Top-Left
            drawLine(bracketColor, Offset(left, top + armLength), Offset(left, top + cornerRadius), strokeWidth)
            drawLine(bracketColor, Offset(left + cornerRadius, top), Offset(left + armLength, top), strokeWidth)

            // Top-Right
            drawLine(bracketColor, Offset(left + boxSize - armLength, top), Offset(left + boxSize - cornerRadius, top), strokeWidth)
            drawLine(bracketColor, Offset(left + boxSize, top + cornerRadius), Offset(left + boxSize, top + armLength), strokeWidth)

            // Bottom-Left
            drawLine(bracketColor, Offset(left, top + boxSize - armLength), Offset(left, top + boxSize - cornerRadius), strokeWidth)
            drawLine(bracketColor, Offset(left + cornerRadius, top + boxSize), Offset(left + armLength, top + boxSize), strokeWidth)

            // Bottom-Right
            drawLine(bracketColor, Offset(left + boxSize - armLength, top + boxSize), Offset(left + boxSize - cornerRadius, top + boxSize), strokeWidth)
            drawLine(bracketColor, Offset(left + boxSize, top + boxSize - armLength), Offset(left + boxSize, top + boxSize - cornerRadius), strokeWidth)

            // Animated Horizontal Laser Scan Line
            val laserY = top + (boxSize * laserProgress)
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        ElectricEmerald.copy(alpha = 0.85f),
                        Color.Transparent
                    )
                ),
                start = Offset(left + 8.dp.toPx(), laserY),
                end = Offset(left + boxSize - 8.dp.toPx(), laserY),
                strokeWidth = 2.dp.toPx()
            )
        }

        // 3. Floating Liquid Glass Top Bar (Apple-Style)
        val topBarShape = RoundedCornerShape(24.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .shadow(12.dp, topBarShape, ambientColor = Color.Black)
                .clip(topBarShape)
                .background(Color(0xD9060B18))
                .border(1.2.dp, GlassBorderStandard, topBarShape)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Circular Back Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                        .clickable(onClick = onNavigateBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when (language) {
                            Language.ENGLISH -> "Scout QR Scanner"
                            Language.TAMIL -> "ஸ்கவுட் QR ஸ்கேனர்"
                            Language.HINDI -> "स्काउट क्यूआर स्कैनर"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Hardware ECDSA P-256 Verifier",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricEmerald,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                LiquidGlassPill(
                    text = "OFFLINE",
                    isSelected = true,
                    tintColor = ElectricEmerald
                )
            }
        }

        // 4. Floating Bottom Contour Glass Card
        val entity = scannedEntity
        ContourGlassCard(
            borderBrush = if (entity != null) {
                if (entity.signatureVerified) GlassBorderEmerald else GlassBorderStandard
            } else GlassBorderStandard,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            if (entity != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (entity.signatureVerified) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (entity.signatureVerified) ElectricEmerald else CrimsonAlert,
                            modifier = Modifier.size(26.dp)
                        )
                        Text(
                            text = if (entity.signatureVerified) {
                                "ECDSA P-256 Validated Offline!"
                            } else {
                                "Signature Verification Failed"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (entity.signatureVerified) ElectricEmerald else CrimsonAlert,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

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
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = ElectricEmerald,
                        modifier = Modifier.size(26.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (language) {
                                Language.ENGLISH -> "Align athlete's Signed QR Passport"
                                Language.TAMIL -> "வீரரின் QR பாஸ்போர்ட்டை பொருத்தவும்"
                                Language.HINDI -> "खिलाड़ी के क्यूआर पासपोर्ट को बॉक्स में रखें"
                            },
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Offline ECDSA P-256 Verification Ready",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
