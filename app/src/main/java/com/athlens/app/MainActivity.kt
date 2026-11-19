package com.athlens.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.athlens.app.ui.navigation.AthlensNavGraph
import com.athlens.app.ui.theme.AthlensTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as AthlensApplication).container

        setContent {
            AthlensTheme {
                AthlensNavGraph(appContainer = appContainer)
            }
        }
    }
}
