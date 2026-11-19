package com.athlens.app.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

/**
 * Standard Liquid Glass Specular Rim Border:
 * Catches bright light at top-left (0.42f), subtle in the middle (0.08f), and soft reflection at bottom-right (0.22f).
 */
val GlassBorderStandard = Brush.linearGradient(
    0.0f to Color.White.copy(alpha = 0.42f),
    0.35f to Color.White.copy(alpha = 0.12f),
    0.70f to Color.White.copy(alpha = 0.08f),
    1.0f to Color.White.copy(alpha = 0.22f),
    start = Offset(0f, 0f),
    end = Offset.Infinite
)

/**
 * Featured Emerald Glass Specular Rim Border
 */
val GlassBorderEmerald = Brush.linearGradient(
    0.0f to ElectricEmerald.copy(alpha = 0.65f),
    0.40f to Color.White.copy(alpha = 0.25f),
    0.75f to ElectricEmeraldDark.copy(alpha = 0.20f),
    1.0f to ElectricEmerald.copy(alpha = 0.45f),
    start = Offset(0f, 0f),
    end = Offset.Infinite
)

/**
 * Warning / Achievement Amber Glass Specular Rim Border
 */
val GlassBorderAmber = Brush.linearGradient(
    0.0f to WarmAmberLight.copy(alpha = 0.70f),
    0.40f to Color.White.copy(alpha = 0.25f),
    0.75f to WarmAmberDark.copy(alpha = 0.20f),
    1.0f to WarmAmber.copy(alpha = 0.50f),
    start = Offset(0f, 0f),
    end = Offset.Infinite
)

/**
 * Default Haze Glass Blur Style
 */
val LiquidGlassHazeStyle = HazeStyle(
    backgroundColor = ObsidianCanvas.copy(alpha = 0.55f),
    tint = HazeTint(Color.White.copy(alpha = 0.05f)),
    blurRadius = 22.dp,
    noiseFactor = 0.05f
)

/**
 * 1. Ambient Mesh Backdrop
 * Luminous dark obsidian canvas with 3-4 drifting soft radial gradient orbs
 * (Ocean Teal, Electric Emerald, Midnight Sapphire, and subtle Amber accent).
 */
@Composable
fun AmbientGlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_mesh_drift")

    // Slow sinusoidal drifting offsets for orbs (battery-friendly slow tween)
    val drift1 by infiniteTransition.animateFloat(
        initialValue = -40f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift_teal"
    )

    val drift2 by infiniteTransition.animateFloat(
        initialValue = 30f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift_sapphire"
    )

    val drift3 by infiniteTransition.animateFloat(
        initialValue = -25f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift_emerald"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianCanvas)
    ) {
        // Draw the luminous ambient mesh
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Orb 1: Ocean Teal top-left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        OceanTeal.copy(alpha = 0.28f),
                        OceanTeal.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.2f + drift1, height * 0.15f + drift2),
                    radius = width * 0.85f
                )
            )

            // Orb 2: Midnight Sapphire center-right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MidnightSapphire.copy(alpha = 0.32f),
                        MidnightSapphire.copy(alpha = 0.14f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.85f + drift2, height * 0.45f + drift1),
                    radius = width * 0.95f
                )
            )

            // Orb 3: Electric Emerald bottom-left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ElectricEmeraldDark.copy(alpha = 0.22f),
                        ElectricEmerald.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.15f + drift3, height * 0.80f + drift1),
                    radius = width * 0.80f
                )
            )

            // Orb 4: Faint Warm Amber accent near top-right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        WarmAmber.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.90f - drift1, height * 0.10f + drift3),
                    radius = width * 0.50f
                )
            )
        }

        // Child content layer
        content()
    }
}

/**
 * 2. Contour Glass Card
 * Continuous Apple-grade rounded card with real-time backdrop blur,
 * multi-stop translucent fill, diagonal specular rim, and inner highlight line.
 */
@Composable
fun ContourGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(26.dp),
    hazeState: HazeState? = null,
    borderBrush: Brush = GlassBorderStandard,
    borderWidth: Dp = 1.2.dp,
    containerColor: Color = Color.Transparent,
    content: @Composable ColumnScope.() -> Unit
) {
    val baseModifier = if (hazeState != null) {
        modifier.hazeEffect(state = hazeState, style = LiquidGlassHazeStyle)
    } else {
        modifier
    }

    Box(
        modifier = baseModifier
            .clip(shape)
            .background(
                Brush.linearGradient(
                    0.0f to Color.White.copy(alpha = 0.11f),
                    0.45f to Color(0x181C2541),
                    1.0f to Color.White.copy(alpha = 0.03f)
                )
            )
            .then(
                if (containerColor != Color.Transparent) {
                    Modifier.background(containerColor)
                } else Modifier
            )
            .border(borderWidth, borderBrush, shape)
            .drawBehind {
                // Subtle 1.dp inner top highlight line for 3D glass thickness
                drawLine(
                    brush = Brush.horizontalGradient(
                        0.0f to Color.Transparent,
                        0.25f to Color.White.copy(alpha = 0.18f),
                        0.75f to Color.White.copy(alpha = 0.18f),
                        1.0f to Color.Transparent
                    ),
                    start = Offset(24f, 1.dp.toPx()),
                    end = Offset(size.width - 24f, 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}

/**
 * 3. Liquid Glass Button
 * Apple-grade capsule button with spring press physics, haptics, and specular rim.
 */
@Composable
fun LiquidGlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    borderBrush: Brush = if (isPrimary) GlassBorderEmerald else GlassBorderStandard,
    content: @Composable RowScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "glass_button_scale"
    )

    val backgroundBrush = if (isPrimary) {
        Brush.horizontalGradient(
            listOf(
                ElectricEmerald.copy(alpha = 0.88f),
                OceanTeal.copy(alpha = 0.85f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.12f),
                Color(0x331C2541),
                Color.White.copy(alpha = 0.05f)
            )
        )
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(backgroundBrush)
            .border(1.2.dp, borderBrush, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content
        )
    }
}

/**
 * Liquid Glass Pill (Compact Tag / Filter / Chip)
 */
@Composable
fun LiquidGlassPill(
    text: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    tintColor: Color = ElectricEmerald,
    onClick: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    val shape = CircleShape

    val backgroundBrush = if (isSelected) {
        Brush.horizontalGradient(
            listOf(
                tintColor.copy(alpha = 0.35f),
                tintColor.copy(alpha = 0.20f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.08f),
                Color.White.copy(alpha = 0.02f)
            )
        )
    }

    val borderBrush = if (isSelected) {
        Brush.linearGradient(
            listOf(
                tintColor.copy(alpha = 0.80f),
                Color.White.copy(alpha = 0.35f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.25f),
                Color.White.copy(alpha = 0.08f)
            )
        )
    }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        }
    } else Modifier

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundBrush)
            .border(1.dp, borderBrush, shape)
            .then(clickableModifier)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) TextWhite else TextGray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Tab item definition for Floating Glass Navigation Bar
 */
data class GlassNavItem(
    val title: String,
    val icon: ImageVector,
    val isSelected: Boolean,
    val onClick: () -> Unit
)

/**
 * 4. Floating Liquid Glass Navigation Bar
 * Apple first-party floating capsule tab bar hovering above navigation bars inset.
 * Features real-time frosted backdrop blur, specular rim, soft ambient glow,
 * and glowing active pill indicator.
 */
@Composable
fun FloatingGlassNavBar(
    items: List<GlassNavItem>,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null
) {
    val haptic = LocalHapticFeedback.current
    val navBarShape = RoundedCornerShape(32.dp)

    val hazeModifier = if (hazeState != null) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                backgroundColor = ObsidianCanvas.copy(alpha = 0.65f),
                tint = HazeTint(Color.White.copy(alpha = 0.06f)),
                blurRadius = 24.dp,
                noiseFactor = 0.05f
            )
        )
    } else Modifier

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .shadow(
                elevation = 16.dp,
                shape = navBarShape,
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor = OceanTeal.copy(alpha = 0.35f)
            )
            .then(hazeModifier)
            .clip(navBarShape)
            .background(
                Brush.linearGradient(
                    0.0f to Color.White.copy(alpha = 0.12f),
                    0.5f to Color(0x2E0B132B),
                    1.0f to Color.White.copy(alpha = 0.04f)
                )
            )
            .border(1.2.dp, GlassBorderStandard, navBarShape)
            .drawBehind {
                // Top inner specular line
                drawLine(
                    brush = Brush.horizontalGradient(
                        0.0f to Color.Transparent,
                        0.3f to Color.White.copy(alpha = 0.22f),
                        0.7f to Color.White.copy(alpha = 0.22f),
                        1.0f to Color.Transparent
                    ),
                    start = Offset(32f, 1.dp.toPx()),
                    end = Offset(size.width - 32f, 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val pillShape = RoundedCornerShape(20.dp)
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()

                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.94f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "tab_scale"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clip(pillShape)
                        .then(
                            if (item.isSelected) {
                                Modifier
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color.White.copy(alpha = 0.16f),
                                                ElectricEmerald.copy(alpha = 0.18f)
                                            )
                                        )
                                    )
                                    .border(
                                        1.dp,
                                        Brush.linearGradient(
                                            listOf(
                                                ElectricEmerald.copy(alpha = 0.70f),
                                                Color.White.copy(alpha = 0.35f)
                                            )
                                        ),
                                        pillShape
                                    )
                            } else Modifier
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            item.onClick()
                        }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (item.isSelected) ElectricEmerald else TextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = if (item.isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (item.isSelected) TextWhite else TextMuted,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
