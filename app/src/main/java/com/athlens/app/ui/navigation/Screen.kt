package com.athlens.app.ui.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Dashboard : Screen("dashboard")
    data object CameraCoach : Screen("camera_coach")
}
