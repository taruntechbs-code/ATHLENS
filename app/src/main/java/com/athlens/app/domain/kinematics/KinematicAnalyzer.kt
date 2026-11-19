package com.athlens.app.domain.kinematics

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.roundToInt

class KinematicAnalyzer {

    private val scoreHistory = ArrayDeque<Int>()
    private val maxHistorySize = 8

    fun analyze(
        landmarks: List<NormalizedLandmark>?,
        drillType: DrillType
    ): KinematicFeedback {
        if (landmarks == null || landmarks.size < 33) {
            scoreHistory.clear()
            return KinematicFeedback(
                formScore = 0,
                activeErrorCodes = listOf(KinematicFeedback.ERROR_NO_POSE_DETECTED),
                primaryErrorCode = KinematicFeedback.ERROR_NO_POSE_DETECTED,
                kneeAngleDeg = 0f,
                torsoAngleDeg = 0f,
                isGoodForm = false
            )
        }

        return when (drillType) {
            DrillType.KABADDI_RAID_STANCE -> analyzeKabaddiRaidStance(landmarks)
            DrillType.KHO_KHO_CROUCH_START -> analyzeKhoKhoCrouchStart(landmarks)
        }
    }

    private fun analyzeKabaddiRaidStance(landmarks: List<NormalizedLandmark>): KinematicFeedback {
        // Landmarks indices:
        // Left: shoulder 11, hip 23, knee 25, ankle 27
        // Right: shoulder 12, hip 24, knee 26, ankle 28
        val leftShoulder = landmarks[11]
        val rightShoulder = landmarks[12]
        val leftHip = landmarks[23]
        val rightHip = landmarks[24]
        val leftKnee = landmarks[25]
        val rightKnee = landmarks[26]
        val leftAnkle = landmarks[27]
        val rightAnkle = landmarks[28]

        val leftKneeAngle = calculateAngle(leftHip, leftKnee, leftAnkle)
        val rightKneeAngle = calculateAngle(rightHip, rightKnee, rightAnkle)
        // Average or lead knee angle
        val avgKneeAngle = (leftKneeAngle + rightKneeAngle) / 2f

        val midShoulderX = (leftShoulder.x() + rightShoulder.x()) / 2f
        val midShoulderY = (leftShoulder.y() + rightShoulder.y()) / 2f
        val midHipX = (leftHip.x() + rightHip.x()) / 2f
        val midHipY = (leftHip.y() + rightHip.y()) / 2f

        val torsoLeanAngle = calculateTorsoLean(midShoulderX, midShoulderY, midHipX, midHipY)

        val shoulderWidth = abs(leftShoulder.x() - rightShoulder.x()).coerceAtLeast(0.05f)
        val stanceWidth = abs(leftAnkle.x() - rightAnkle.x())

        val errors = mutableListOf<String>()
        var penalties = 0

        // Knee flexion assessment: Ideal 95°–140°
        if (avgKneeAngle > 140f) {
            errors.add(KinematicFeedback.ERROR_STANCE_TOO_HIGH)
            penalties += ((avgKneeAngle - 140f) * 1.5f).toInt()
        } else if (avgKneeAngle < 90f) {
            penalties += ((90f - avgKneeAngle) * 1.2f).toInt()
        }

        // Torso lean assessment: Ideal 15°–45° forward
        if (torsoLeanAngle < 15f) {
            errors.add(KinematicFeedback.ERROR_TORSO_TOO_UPRIGHT)
            penalties += ((15f - torsoLeanAngle) * 2.0f).toInt()
        } else if (torsoLeanAngle > 50f) {
            penalties += ((torsoLeanAngle - 50f) * 1.5f).toInt()
        }

        // Base width assessment: Ideal >= 0.85 * shoulderWidth
        if (stanceWidth < shoulderWidth * 0.80f) {
            errors.add(KinematicFeedback.ERROR_UNSTABLE_BASE)
            penalties += 20
        }

        val rawScore = (100 - penalties).coerceIn(10, 100)
        val smoothedScore = smoothScore(rawScore)
        val isGood = errors.isEmpty() && smoothedScore >= 75
        val primaryError = if (errors.isEmpty()) KinematicFeedback.CODE_GOOD_FORM else errors.first()

        return KinematicFeedback(
            formScore = smoothedScore,
            activeErrorCodes = if (errors.isEmpty()) listOf(KinematicFeedback.CODE_GOOD_FORM) else errors,
            primaryErrorCode = primaryError,
            kneeAngleDeg = avgKneeAngle,
            torsoAngleDeg = torsoLeanAngle,
            isGoodForm = isGood
        )
    }

    private fun analyzeKhoKhoCrouchStart(landmarks: List<NormalizedLandmark>): KinematicFeedback {
        val leftShoulder = landmarks[11]
        val rightShoulder = landmarks[12]
        val leftHip = landmarks[23]
        val rightHip = landmarks[24]
        val leftKnee = landmarks[25]
        val rightKnee = landmarks[26]
        val leftAnkle = landmarks[27]
        val rightAnkle = landmarks[28]

        val leftKneeAngle = calculateAngle(leftHip, leftKnee, leftAnkle)
        val rightKneeAngle = calculateAngle(rightHip, rightKnee, rightAnkle)
        val leadKneeAngle = minOf(leftKneeAngle, rightKneeAngle)

        val midShoulderX = (leftShoulder.x() + rightShoulder.x()) / 2f
        val midShoulderY = (leftShoulder.y() + rightShoulder.y()) / 2f
        val midHipX = (leftHip.x() + rightHip.x()) / 2f
        val midHipY = (leftHip.y() + rightHip.y()) / 2f
        val avgKneeY = (leftKnee.y() + rightKnee.y()) / 2f

        val torsoLeanAngle = calculateTorsoLean(midShoulderX, midShoulderY, midHipX, midHipY)

        val errors = mutableListOf<String>()
        var penalties = 0

        // Deep crouch knee flexion: Ideal 60°–115°
        if (leadKneeAngle > 115f) {
            errors.add(KinematicFeedback.ERROR_KNEES_NOT_BENT)
            penalties += ((leadKneeAngle - 115f) * 1.5f).toInt()
        }

        // Low hip center: In normalized coordinates (Y down), hip should be close in elevation to knee
        val hipKneeVerticalDiff = avgKneeY - midHipY
        if (hipKneeVerticalDiff > 0.22f) {
            errors.add(KinematicFeedback.ERROR_HIPS_TOO_HIGH)
            penalties += ((hipKneeVerticalDiff - 0.22f) * 150f).toInt()
        }

        // Lateral balance
        val lateralOffset = abs(midShoulderX - midHipX)
        if (lateralOffset > 0.25f || torsoLeanAngle > 60f) {
            errors.add(KinematicFeedback.ERROR_UNBALANCED_POSTURE)
            penalties += 25
        }

        val rawScore = (100 - penalties).coerceIn(10, 100)
        val smoothedScore = smoothScore(rawScore)
        val isGood = errors.isEmpty() && smoothedScore >= 75
        val primaryError = if (errors.isEmpty()) KinematicFeedback.CODE_GOOD_FORM else errors.first()

        return KinematicFeedback(
            formScore = smoothedScore,
            activeErrorCodes = if (errors.isEmpty()) listOf(KinematicFeedback.CODE_GOOD_FORM) else errors,
            primaryErrorCode = primaryError,
            kneeAngleDeg = leadKneeAngle,
            torsoAngleDeg = torsoLeanAngle,
            isGoodForm = isGood
        )
    }

    private fun smoothScore(newScore: Int): Int {
        if (scoreHistory.size >= maxHistorySize) {
            scoreHistory.removeFirst()
        }
        scoreHistory.addLast(newScore)
        return scoreHistory.average().roundToInt()
    }

    fun resetSmoothing() {
        scoreHistory.clear()
    }

    private fun calculateAngle(
        a: NormalizedLandmark,
        b: NormalizedLandmark, // Vertex
        c: NormalizedLandmark
    ): Float {
        val radians = atan2(c.y() - b.y(), c.x() - b.x()) - atan2(a.y() - b.y(), a.x() - b.x())
        var angle = abs(radians * 180f / Math.PI.toFloat())
        if (angle > 180f) {
            angle = 360f - angle
        }
        return angle
    }

    private fun calculateTorsoLean(
        shoulderX: Float,
        shoulderY: Float,
        hipX: Float,
        hipY: Float
    ): Float {
        val dx = abs(shoulderX - hipX)
        val dy = abs(hipY - shoulderY).coerceAtLeast(0.001f)
        val radians = atan2(dx, dy)
        return radians * 180f / Math.PI.toFloat()
    }
}
