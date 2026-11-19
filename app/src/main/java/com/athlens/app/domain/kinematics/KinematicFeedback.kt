package com.athlens.app.domain.kinematics

data class KinematicFeedback(
    val formScore: Int = 0,
    val activeErrorCodes: List<String> = emptyList(),
    val primaryErrorCode: String = ERROR_NO_POSE_DETECTED,
    val kneeAngleDeg: Float = 0f,
    val torsoAngleDeg: Float = 0f,
    val isGoodForm: Boolean = false
) {
    companion object {
        const val CODE_GOOD_FORM = "GOOD_FORM"
        const val ERROR_NO_POSE_DETECTED = "NO_POSE_DETECTED"

        // Kabaddi Error Codes
        const val ERROR_STANCE_TOO_HIGH = "STANCE_TOO_HIGH"
        const val ERROR_TORSO_TOO_UPRIGHT = "TORSO_TOO_UPRIGHT"
        const val ERROR_UNSTABLE_BASE = "UNSTABLE_BASE"

        // Kho-Kho Error Codes
        const val ERROR_HIPS_TOO_HIGH = "HIPS_TOO_HIGH"
        const val ERROR_KNEES_NOT_BENT = "KNEES_NOT_BENT"
        const val ERROR_UNBALANCED_POSTURE = "UNBALANCED_POSTURE"
    }
}
