package com.bizilabs.streeek.lib.presentation

import InAppUpdateManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bizilabs.streeek.lib.common.helpers.BaseActivity
import com.bizilabs.streeek.lib.common.helpers.registerLaunchers
import com.bizilabs.streeek.lib.design.components.SafiContent
import com.bizilabs.streeek.lib.design.theme.SafiTheme
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : BaseActivity() {
    // Inject InAppUpdateManager using Koin and pass the activity as a parameter
    private val inAppUpdateManager: InAppUpdateManager by inject { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        registerLaunchers()
        setContent {
            SafiTheme {
                SafiContent {
                    MainNavigation(intent = intent)
                }
            }
        }
        // Trigger the update check
        inAppUpdateManager.checkForUpdates()
    }

    override fun onResume() {
        super.onResume()
        // Complete any downloaded updates
        inAppUpdateManager.completeUpdateIfNeeded()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources
        inAppUpdateManager.cleanup()
    }
}
