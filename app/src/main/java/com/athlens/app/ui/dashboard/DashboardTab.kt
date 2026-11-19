package com.athlens.app.ui.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.ui.graphics.vector.ImageVector
import com.athlens.app.domain.model.Language
import com.athlens.app.ui.util.BilingualStrings

enum class DashboardTab(
    val id: String,
    val icon: ImageVector
) {
    TRAIN("train", Icons.AutoMirrored.Filled.DirectionsRun),
    PASSPORT("passport", Icons.Default.Badge),
    NUTRITION("nutrition", Icons.Default.Restaurant),
    SHARE_P2P("share_p2p", Icons.Default.WifiTethering);

    fun getTitle(lang: Language): String = when (this) {
        TRAIN -> BilingualStrings.getTabTrain(lang)
        PASSPORT -> BilingualStrings.getTabPassport(lang)
        NUTRITION -> BilingualStrings.getTabNutrition(lang)
        SHARE_P2P -> BilingualStrings.getTabShareP2P(lang)
    }
}
