package com.bizilabs.streeek.lib.presentation

import InAppUpdateManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bizilabs.streeek.lib.common.helpers.BaseActivity
import com.bizilabs.streeek.lib.common.helpers.registerLaunchers
import com.bizilabs.streeek.lib.design.components.SafiContent
import com.bizilabs.streeek.lib.design.theme.SafiTheme
import com.bizilabs.streeek.lib.domain.monitors.NetworkMonitor
import com.bizilabs.streeek.lib.presentation.helpers.initFirebaseMessaging
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : BaseActivity() {
    // Inject InAppUpdateManager using Koin and pass the activity as a parameter
    private val inAppUpdateManager: InAppUpdateManager by inject { parametersOf(this) }
    private val viewModel: MainViewModel by inject()
    private val networkMonitor: NetworkMonitor by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        registerLaunchers()
        initFirebaseMessaging()
        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            SafiTheme(
                typography = state.typography,
            ) {
                SafiContent(
                    isNetworkConnected = state.hasNetworkConnection,
                    barColors = state.barColors,
                ) {
                    MainNavigation(onValueChangeBarColors = viewModel::onValueChangeBarColors)
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
        networkMonitor.register()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources
        inAppUpdateManager.cleanup()
        networkMonitor.unregister()
    }
}
