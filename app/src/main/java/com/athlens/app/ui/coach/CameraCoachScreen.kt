package com.athlens.app.ui.coach

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.athlens.app.data.vision.PoseLandmarkerHelper
import com.athlens.app.domain.kinematics.DrillType
import com.athlens.app.domain.model.Language
import com.athlens.app.ui.theme.CardNavy
import com.athlens.app.ui.theme.CardNavyElevated
import com.athlens.app.ui.theme.CrimsonAlert
import com.athlens.app.ui.theme.DeepNavy
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.GlassBorderAmber
import com.athlens.app.ui.theme.GlassBorderEmerald
import com.athlens.app.ui.theme.GlassBorderStandard
import com.athlens.app.ui.theme.LiquidGlassButton
import com.athlens.app.ui.theme.LiquidGlassPill
import com.athlens.app.ui.theme.OceanTeal
import com.athlens.app.ui.theme.TextGray
import com.athlens.app.ui.theme.TextMuted
import com.athlens.app.ui.theme.TextWhite
import com.athlens.app.ui.theme.WarmAmber
import com.athlens.app.ui.util.BilingualStrings
import java.util.concurrent.Executors
import kotlin.math.roundToInt

@Composable
fun CameraCoachScreen(
    viewModel: CameraCoachViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!hasCameraPermission) {
        CameraPermissionFallback(
            language = uiState.activeLanguage,
            onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            onCancel = onNavigateBack,
            modifier = modifier
        )
        return
    }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val landmarkerHelper = remember {
        PoseLandmarkerHelper(
            context = context,
            listener = object : PoseLandmarkerHelper.LandmarkerListener {
                override fun onResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
                    val landmarks = resultBundle.results?.landmarks()?.firstOrNull()
                    viewModel.onPoseDetected(
                        landmarks = landmarks,
                        imageWidth = resultBundle.inputImageWidth,
                        imageHeight = resultBundle.inputImageHeight,
                        inferenceTimeMs = resultBundle.inferenceTimeMs
                    )
                }

                override fun onError(error: String) {
                    // Non-fatal vision detection errors
                }
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            landmarkerHelper.clear()
            cameraExecutor.shutdown()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // CameraX Live Preview View
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            },
            update = { previewView ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        landmarkerHelper.detectLiveStream(imageProxy, uiState.isFrontCamera)
                    }

                    val cameraSelector = if (uiState.isFrontCamera) {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        // bind error
                    }
                }, ContextCompat.getMainExecutor(previewView.context))
            },
            modifier = Modifier.fillMaxSize()
        )

        // Skeletal Pose Overlay
        PoseOverlayCanvas(
            landmarks = uiState.landmarks,
            imageWidth = uiState.imageWidth,
            imageHeight = uiState.imageHeight,
            formScore = uiState.kinematicFeedback.formScore,
            modifier = Modifier.fillMaxSize()
        )

        // Top HUD Controls (Apple Camera Liquid Glass Top HUD)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            AppleCameraTopHud(
                selectedDrill = uiState.selectedDrill,
                language = uiState.activeLanguage,
                isVoiceMuted = uiState.isVoiceMuted,
                onClose = onNavigateBack,
                onToggleCamera = { viewModel.toggleCamera() },
                onToggleVoice = { viewModel.toggleVoiceMute() },
                onToggleLanguage = {
                    val nextLang = when (uiState.activeLanguage) {
                        Language.ENGLISH -> Language.TAMIL
                        Language.TAMIL -> Language.HINDI
                        Language.HINDI -> Language.ENGLISH
                    }
                    viewModel.setLanguage(nextLang)
                },
                onCycleDrill = {
                    val nextDrill = if (uiState.selectedDrill == DrillType.KABADDI_RAID_STANCE) {
                        DrillType.KHO_KHO_CROUCH_START
                    } else {
                        DrillType.KABADDI_RAID_STANCE
                    }
                    viewModel.setDrill(nextDrill)
                }
            )

            // Floating Score Capsule just below Top HUD (right-aligned)
            val formScore = uiState.kinematicFeedback.formScore
            val scoreColor = when {
                formScore >= 75 -> ElectricEmerald
                formScore >= 50 -> WarmAmber
                else -> CrimsonAlert
            }
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp), ambientColor = Color.Black)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xCC050814))
                    .border(1.2.dp, scoreColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(scoreColor)
                    )
                    Text(
                        text = "$formScore",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = scoreColor
                    )
                    Text(
                        text = "/ 100",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        }

        // Bottom HUD (Apple Camera Liquid Glass HUD with Shutter Action Bar)
        AppleCameraBottomHud(
            feedback = uiState.kinematicFeedback,
            inferenceTimeMs = uiState.inferenceTimeMs,
            isSessionActive = uiState.isSessionActive,
            durationSeconds = uiState.sessionDurationSeconds,
            isSaving = uiState.isSavingSession,
            language = uiState.activeLanguage,
            isVoiceMuted = uiState.isVoiceMuted,
            onStartSession = { viewModel.startSession() },
            onStopSession = { viewModel.stopAndSaveSession() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )

        // Session Save Confirmation Dialog
        if (uiState.sessionSavedSuccessfully) {
            AlertDialog(
                onDismissRequest = onNavigateBack,
                containerColor = DeepNavy,
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ElectricEmerald,
                        modifier = Modifier.size(40.dp)
                    )
                },
                title = {
                    Text(
                        text = if (uiState.activeLanguage == Language.TAMIL) "பயிற்சி சேமிக்கப்பட்டது!" else "Session Completed & Saved!",
                        color = TextWhite,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (uiState.activeLanguage == Language.TAMIL) {
                                "சராசரி மதிப்பெண்: ${uiState.latestSavedScore}/100\nதரவு சாதனத்தில் என்க்ரிப்ட் செய்யப்பட்டு பாதுகாக்கப்பட்டது."
                            } else {
                                "Average Form Score: ${uiState.latestSavedScore}/100\nEncrypted offline in local sports passport database."
                            },
                            color = TextGray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricEmerald,
                            contentColor = DeepNavy
                        )
                    ) {
                        Text(if (uiState.activeLanguage == Language.TAMIL) "பாஸ்போர்ட் பார்க்கவும்" else "View in Passport")
                    }
                }
            )
        }
    }
}

/**
 * Top HUD: Circular Close, Compact Drill Pill, Lens Switch, Mute, Language
 */
@Composable
private fun AppleCameraTopHud(
    selectedDrill: DrillType,
    language: Language,
    isVoiceMuted: Boolean,
    onClose: () -> Unit,
    onToggleCamera: () -> Unit,
    onToggleVoice: () -> Unit,
    onToggleLanguage: () -> Unit,
    onCycleDrill: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Circular 42.dp Glass Close Button
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0x99060B18))
                .border(1.2.dp, GlassBorderStandard, CircleShape)
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClose()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = TextWhite,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Center: Compact Drill Capsule Pill
        Box(
            modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0x99060B18))
                .border(1.2.dp, GlassBorderEmerald, RoundedCornerShape(22.dp))
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onCycleDrill()
                }
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = selectedDrill.getDisplayName(language),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Switch Drill",
                    tint = ElectricEmerald,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Right Control Cluster
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Voice Mute Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0x99060B18))
                    .border(
                        1.2.dp,
                        if (!isVoiceMuted) GlassBorderEmerald else GlassBorderStandard,
                        CircleShape
                    )
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onToggleVoice()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isVoiceMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Voice Coach",
                    tint = if (!isVoiceMuted) ElectricEmerald else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Camera Flip Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0x99060B18))
                    .border(1.2.dp, GlassBorderStandard, CircleShape)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onToggleCamera()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FlipCameraAndroid,
                    contentDescription = "Flip Camera",
                    tint = TextWhite,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Language Cycler Pill
            LiquidGlassPill(
                text = when (language) {
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

/**
 * Bottom HUD: Live Coaching Banner + Telemetry Pills + Apple-Camera Shutter Action Bar
 */
@Composable
private fun AppleCameraBottomHud(
    feedback: com.athlens.app.domain.kinematics.KinematicFeedback,
    inferenceTimeMs: Long,
    isSessionActive: Boolean,
    durationSeconds: Int,
    isSaving: Boolean,
    language: Language,
    isVoiceMuted: Boolean,
    onStartSession: () -> Unit,
    onStopSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val bannerBorder = if (feedback.isGoodForm) GlassBorderEmerald else GlassBorderAmber
    val bannerColor = if (feedback.isGoodForm) ElectricEmerald else WarmAmber

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Live Coaching Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(22.dp), ambientColor = Color.Black)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xD9060B18))
                .border(1.2.dp, bannerBorder, RoundedCornerShape(22.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = BilingualStrings.getKinematicFeedback(feedback.primaryErrorCode, language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = bannerColor,
                        modifier = Modifier.weight(1f)
                    )

                    // Compact voice indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isVoiceMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = if (isVoiceMuted) TextMuted else ElectricEmerald,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isVoiceMuted) "Muted" else "Voice ON",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isVoiceMuted) TextMuted else ElectricEmerald
                        )
                    }
                }

                if (language == Language.TAMIL && feedback.primaryErrorCode != com.athlens.app.domain.kinematics.KinematicFeedback.CODE_GOOD_FORM) {
                    Text(
                        text = BilingualStrings.getKinematicFeedback(feedback.primaryErrorCode, Language.ENGLISH),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }

                // Compact Telemetry Pills: Knee Angle | Torso Lean | Latency
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TelemetryGlassPill(
                        label = BilingualStrings.getKneeAngleLabel(language),
                        value = "${feedback.kneeAngleDeg.roundToInt()}°",
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryGlassPill(
                        label = BilingualStrings.getTorsoLeanLabel(language),
                        value = "${feedback.torsoAngleDeg.roundToInt()}°",
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryGlassPill(
                        label = "Latency",
                        value = "${inferenceTimeMs}ms",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 2. Apple-Camera-Style Shutter Action Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xD9060B18))
                .border(1.2.dp, GlassBorderStandard, RoundedCornerShape(32.dp))
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Stopwatch Timer
                val minutes = durationSeconds / 60
                val seconds = durationSeconds % 60
                val timeString = String.format("%02d:%02d", minutes, seconds)

                Column {
                    Text(
                        text = if (isSessionActive) "RECORDING" else "SESSION TIME",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSessionActive) CrimsonAlert else TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = timeString,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = TextWhite
                    )
                }

                // Center: Tactile Shutter Capsule Button
                val infiniteTransition = rememberInfiniteTransition(label = "recording_pulse")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = if (isSessionActive) 1.08f else 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 800),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse"
                )

                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .border(
                            2.5.dp,
                            if (isSessionActive) CrimsonAlert else ElectricEmerald,
                            CircleShape
                        )
                        .clickable(enabled = !isSaving) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            if (isSessionActive) onStopSession() else onStartSession()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = TextWhite,
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else if (isSessionActive) {
                        // Apple-style Recording Stop Button (Square in ring)
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .graphicsLayer {
                                    scaleX = pulseScale
                                    scaleY = pulseScale
                                }
                                .clip(RoundedCornerShape(6.dp))
                                .background(CrimsonAlert)
                        )
                    } else {
                        // Apple-style Shutter Button (Solid emerald circle)
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(ElectricEmerald),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Start",
                                tint = DeepNavy,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }

                // Right: Action Prompt / Badge
                Box(modifier = Modifier.width(72.dp), contentAlignment = Alignment.CenterEnd) {
                    Text(
                        text = if (isSessionActive) {
                            if (language == Language.TAMIL) "நிறுத்து" else "TAP TO\nSTOP"
                        } else {
                            if (language == Language.TAMIL) "தொடங்கு" else "TAP TO\nSTART"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isSessionActive) CrimsonAlert else ElectricEmerald,
                        textAlign = TextAlign.End,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

/**
 * Compact Glass Telemetry Pill
 */
@Composable
private fun TelemetryGlassPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum"
                ),
                color = TextWhite,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CameraPermissionFallback(
    language: Language,
    onRequestPermission: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavy)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(1.dp, CardNavyElevated),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = ElectricEmerald,
                    modifier = Modifier.size(56.dp)
                )

                Text(
                    text = if (language == Language.TAMIL) "கேமரா அனுமதி தேவை" else "Camera Permission Required",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextWhite
                )

                Text(
                    text = if (language == Language.TAMIL) {
                        "உடல் தோரணை மற்றும் கோணங்களை நிகழ்நேரத்தில் கணிக்க கேமரா அனுமதி அவசியமாகும். உங்கள் வீடியோ எங்கும் பகிரப்படாது (100% ஆஃப்லைன்)."
                    } else {
                        "ATHLENS analyzes pose telemetry completely on-device without internet access. Grant camera permission to start the AI Camera Coach."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )

                Button(
                    onClick = onRequestPermission,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricEmerald,
                        contentColor = DeepNavy
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) {
                    Text(if (language == Language.TAMIL) "அனுமதி வழங்கவும்" else "Grant Camera Access")
                }

                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = TextMuted
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) {
                    Text(if (language == Language.TAMIL) "பின்செல்ல" else "Cancel")
                }
            }
        }
    }
}
