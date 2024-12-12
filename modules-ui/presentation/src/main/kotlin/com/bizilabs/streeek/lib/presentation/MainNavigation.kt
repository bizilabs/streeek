package com.bizilabs.streeek.lib.presentation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.bizilabs.streeek.feature.landing.LandingScreen

@Composable
fun MainNavigation() {
    Navigator(LandingScreen)
}
