package com.bizilabs.streeek.lib.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bizilabs.streeek.lib.design.components.SafiContent
import com.bizilabs.streeek.lib.design.theme.SafiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SafiTheme {
                SafiContent {
                   MainNavigation()
                }
            }
        }
    }
}
