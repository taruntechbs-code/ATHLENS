package com.athlens.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ElectricEmerald,
    onPrimary = DeepNavy,
    primaryContainer = ElectricEmeraldDark,
    onPrimaryContainer = TextWhite,
    secondary = WarmAmber,
    onSecondary = DeepNavy,
    secondaryContainer = WarmAmberDark,
    onSecondaryContainer = TextWhite,
    tertiary = ElectricEmeraldLight,
    onTertiary = DeepNavy,
    background = DeepNavy,
    onBackground = TextWhite,
    surface = CardNavy,
    onSurface = TextWhite,
    surfaceVariant = SurfaceNavy,
    onSurfaceVariant = TextGray,
    outline = BorderDark,
    error = CrimsonAlert,
    onError = TextWhite
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricEmeraldDark,
    onPrimary = TextWhite,
    primaryContainer = ElectricEmeraldLight,
    onPrimaryContainer = DeepNavy,
    secondary = WarmAmber,
    onSecondary = DeepNavy,
    background = SurfaceLight,
    onBackground = TextDark,
    surface = CardLight,
    onSurface = TextDark,
    surfaceVariant = SurfaceNavy,
    onSurfaceVariant = TextGray,
    outline = BorderDark,
    error = CrimsonAlert,
    onError = TextWhite
)

@Composable
fun AthlensTheme(
    darkTheme: Boolean = true, // Default to sleek sports-tech dark mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AthlensTypography,
        shapes = AthlensShapes,
        content = content
    )
}
