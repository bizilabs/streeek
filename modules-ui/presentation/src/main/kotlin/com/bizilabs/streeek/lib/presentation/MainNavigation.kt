package com.bizilabs.streeek.lib.presentation

import android.content.Intent
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.bizilabs.streeek.feature.landing.LandingScreen
import com.bizilabs.streeek.lib.domain.models.notifications.NotificationResult
import com.bizilabs.streeek.lib.domain.models.notifications.asNotificationResult

@Composable
fun MainNavigation(intent: Intent) {
    val destination = intent.asNotificationResult().asNavigationDestination()
    Navigator(destination)
}

fun NotificationResult?.asNavigationDestination(): Screen =
    when (this) {
        null -> LandingScreen
        else -> LandingScreen
    }
