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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import com.athlens.app.ui.theme.ElectricEmeraldDark
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
                    // Vision processing error
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

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        // CameraX Live Preview
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
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
                        // camera binding error
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
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

        // Top HUD Controls
        TopCoachHud(
            selectedDrill = uiState.selectedDrill,
            isFrontCamera = uiState.isFrontCamera,
            formScore = uiState.kinematicFeedback.formScore,
            language = uiState.activeLanguage,
            isVoiceMuted = uiState.isVoiceMuted,
            onClose = onNavigateBack,
            onToggleCamera = { viewModel.toggleCamera() },
            onToggleVoice = { viewModel.toggleVoiceMute() },
            onToggleLanguage = {
                val nextLang = if (uiState.activeLanguage == Language.TAMIL) Language.ENGLISH else Language.TAMIL
                viewModel.setLanguage(nextLang)
            },
            onSelectDrill = { viewModel.setDrill(it) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        )

        // Bottom HUD Coaching Banner & Telemetry
        BottomCoachHud(
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
                containerColor = CardNavy,
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

@Composable
private fun TopCoachHud(
    selectedDrill: DrillType,
    isFrontCamera: Boolean,
    formScore: Int,
    language: Language,
    isVoiceMuted: Boolean,
    onClose: () -> Unit,
    onToggleCamera: () -> Unit,
    onToggleVoice: () -> Unit,
    onToggleLanguage: () -> Unit,
    onSelectDrill: (DrillType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = TextWhite,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Live Form Score Pill
            val scoreColor = when {
                formScore >= 75 -> ElectricEmerald
                formScore >= 50 -> WarmAmber
                else -> CrimsonAlert
            }
            Surface(
                color = DeepNavy.copy(alpha = 0.85f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, scoreColor)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(scoreColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Score: $formScore",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite
                    )
                }
            }

            // Quick Control Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Language Switcher
                Surface(
                    onClick = onToggleLanguage,
                    color = CardNavy.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, ElectricEmerald.copy(alpha = 0.5f)),
                    modifier = Modifier.height(34.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = if (language == Language.TAMIL) "தமிழ்" else "EN",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricEmerald
                        )
                    }
                }

                // Voice Coach Mute/Unmute
                IconButton(
                    onClick = onToggleVoice,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .size(34.dp)
                ) {
                    Icon(
                        imageVector = if (isVoiceMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = if (isVoiceMuted) "Unmute Voice Coach" else "Mute Voice Coach",
                        tint = if (isVoiceMuted) TextMuted else ElectricEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Switch Camera Lens
                IconButton(
                    onClick = onToggleCamera,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = "Switch Camera",
                        tint = TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Drill Selector Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DrillType.entries.forEach { drill ->
                val isSelected = drill == selectedDrill
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectDrill(drill) },
                    label = {
                        Text(
                            text = if (language == Language.TAMIL) drill.displayNameTa else drill.displayNameEn,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricEmerald,
                        selectedLabelColor = DeepNavy,
                        containerColor = CardNavy.copy(alpha = 0.8f),
                        labelColor = TextWhite
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) ElectricEmerald else Color.Transparent
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomCoachHud(
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
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.65f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Coaching Feedback Banner
        val bannerBorderColor = if (feedback.isGoodForm) ElectricEmerald else WarmAmber
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy.copy(alpha = 0.9f)),
            border = BorderStroke(1.5.dp, bannerBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = BilingualStrings.getKinematicFeedback(feedback.primaryErrorCode, language),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (feedback.isGoodForm) ElectricEmerald else WarmAmber,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = if (isVoiceMuted) TextMuted.copy(alpha = 0.15f) else ElectricEmerald.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isVoiceMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = if (isVoiceMuted) TextMuted else ElectricEmerald,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isVoiceMuted) {
                                    BilingualStrings.getVoiceCoachMuted(language)
                                } else {
                                    BilingualStrings.getVoiceCoachActive(language)
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isVoiceMuted) TextMuted else ElectricEmerald
                            )
                        }
                    }
                }

                if (language == Language.TAMIL && feedback.primaryErrorCode != com.athlens.app.domain.kinematics.KinematicFeedback.CODE_GOOD_FORM) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = BilingualStrings.getKinematicFeedback(feedback.primaryErrorCode, Language.ENGLISH),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
            }
        }

        // Telemetry Chips (Knee Angle | Torso Lean | Latency)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TelemetryChip(
                label = BilingualStrings.getKneeAngleLabel(language),
                value = "${feedback.kneeAngleDeg.roundToInt()}°",
                modifier = Modifier.weight(1f)
            )
            TelemetryChip(
                label = BilingualStrings.getTorsoLeanLabel(language),
                value = "${feedback.torsoAngleDeg.roundToInt()}°",
                modifier = Modifier.weight(1f)
            )
            TelemetryChip(
                label = "Latency",
                value = "${inferenceTimeMs}ms",
                modifier = Modifier.weight(1f)
            )
        }

        // Session Action Button (Start / Stop & Save)
        if (!isSessionActive) {
            Button(
                onClick = onStartSession,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricEmerald,
                    contentColor = DeepNavy
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = BilingualStrings.getStartSession(language),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            val timeString = String.format("%02d:%02d", minutes, seconds)

            Button(
                onClick = onStopSession,
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CrimsonAlert,
                    contentColor = TextWhite
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = TextWhite,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${BilingualStrings.getStopAndSaveSession(language)} ($timeString)",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun TelemetryChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CardNavyElevated.copy(alpha = 0.8f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = TextWhite
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (language == Language.TAMIL) "அனுமதி வழங்கவும்" else "Grant Camera Access")
                }

                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = TextMuted
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (language == Language.TAMIL) "பின்செல்ல" else "Cancel")
                }
            }
        }
    }
}
