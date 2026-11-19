package com.athlens.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.athlens.app.di.AppContainer
import com.athlens.app.di.ViewModelFactory
import com.athlens.app.ui.coach.CameraCoachScreen
import com.athlens.app.ui.coach.CameraCoachViewModel
import com.athlens.app.ui.dashboard.DashboardScreen
import com.athlens.app.ui.dashboard.DashboardViewModel
import com.athlens.app.ui.onboarding.OnboardingScreen
import com.athlens.app.ui.onboarding.OnboardingViewModel
import com.athlens.app.ui.theme.DeepNavy
import com.athlens.app.ui.theme.ElectricEmerald

import com.athlens.app.ui.p2p.QrScannerScreen

@Composable
fun AthlensNavGraph(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val factory = ViewModelFactory(appContainer)
    val hasProfileState by appContainer.athleteRepository.hasProfile()
        .collectAsStateWithLifecycle(initialValue = null)

    // While determining if profile exists in encrypted Room DB
    val hasProfile = hasProfileState
    if (hasProfile == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepNavy),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ElectricEmerald)
        }
        return
    }

    val startDestination = if (hasProfile) Screen.Dashboard.route else Screen.Onboarding.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Onboarding.route) {
            val onboardingViewModel: OnboardingViewModel = viewModel(factory = factory)
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onOnboardingComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = viewModel(factory = factory)
            DashboardScreen(
                viewModel = dashboardViewModel,
                onLaunchCameraCoach = { drill ->
                    navController.navigate(Screen.CameraCoach.route)
                },
                onLaunchQrScanner = {
                    navController.navigate(Screen.QrScanner.route)
                }
            )
        }

        composable(Screen.CameraCoach.route) {
            val cameraCoachViewModel: CameraCoachViewModel = viewModel(factory = factory)
            CameraCoachScreen(
                viewModel = cameraCoachViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.QrScanner.route) {
            val dashboardBackStackEntry = remember(navController) {
                navController.getBackStackEntry(Screen.Dashboard.route)
            }
            val dashboardViewModel: DashboardViewModel = viewModel(
                viewModelStoreOwner = dashboardBackStackEntry,
                factory = factory
            )
            val dashboardState by dashboardViewModel.uiState.collectAsStateWithLifecycle()

            QrScannerScreen(
                passportCryptoManager = appContainer.passportCryptoManager,
                language = dashboardState.activeLanguage,
                onPassportScanned = { entity ->
                    dashboardViewModel.saveReceivedPassport(entity)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
