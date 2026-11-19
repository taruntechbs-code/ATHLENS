package com.athlens.app.ui.coach

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.athlens.app.ui.theme.CrimsonAlert
import com.athlens.app.ui.theme.ElectricEmerald
import com.athlens.app.ui.theme.WarmAmber
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

@Composable
fun PoseOverlayCanvas(
    landmarks: List<NormalizedLandmark>?,
    imageWidth: Int,
    imageHeight: Int,
    formScore: Int,
    modifier: Modifier = Modifier
) {
    val skeletonColor = when {
        formScore >= 75 -> ElectricEmerald
        formScore >= 50 -> WarmAmber
        else -> CrimsonAlert
    }

    val jointColor = Color.White

    Canvas(modifier = modifier.fillMaxSize()) {
        if (landmarks == null || landmarks.size < 33 || imageWidth <= 0 || imageHeight <= 0) {
            return@Canvas
        }

        val canvasWidth = size.width
        val canvasHeight = size.height

        // CameraX PreviewView with FILL_CENTER scales image to fill canvas
        val scale = maxOf(canvasWidth / imageWidth.toFloat(), canvasHeight / imageHeight.toFloat())
        val scaledWidth = imageWidth * scale
        val scaledHeight = imageHeight * scale
        val offsetX = (canvasWidth - scaledWidth) / 2f
        val offsetY = (canvasHeight - scaledHeight) / 2f

        fun mapX(normX: Float): Float = offsetX + normX * scaledWidth
        fun mapY(normY: Float): Float = offsetY + normY * scaledHeight

        fun getOffset(index: Int): Offset {
            val lm = landmarks[index]
            return Offset(mapX(lm.x()), mapY(lm.y()))
        }

        // Draw bone connections
        val bonePairs = listOf(
            // Shoulders & Torso
            11 to 12,
            11 to 23,
            12 to 24,
            23 to 24,
            // Left Arm
            11 to 13,
            13 to 15,
            // Right Arm
            12 to 14,
            14 to 16,
            // Left Leg
            23 to 25,
            25 to 27,
            27 to 29,
            27 to 31,
            // Right Leg
            24 to 26,
            26 to 28,
            28 to 30,
            28 to 32,
            // Head / Neck connection
            0 to 11,
            0 to 12
        )

        for ((start, end) in bonePairs) {
            val p1 = getOffset(start)
            val p2 = getOffset(end)
            drawLine(
                color = skeletonColor,
                start = p1,
                end = p2,
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )
        }

        // Key joint dots
        val keyJointIndices = listOf(
            0, // Nose
            11, 12, // Shoulders
            13, 14, // Elbows
            15, 16, // Wrists
            23, 24, // Hips
            25, 26, // Knees
            27, 28  // Ankles
        )

        for (idx in keyJointIndices) {
            val center = getOffset(idx)
            // Outer glow ring
            drawCircle(
                color = skeletonColor.copy(alpha = 0.5f),
                radius = 10f,
                center = center
            )
            // Inner dot
            drawCircle(
                color = jointColor,
                radius = 5f,
                center = center
            )
        }
    }
}
