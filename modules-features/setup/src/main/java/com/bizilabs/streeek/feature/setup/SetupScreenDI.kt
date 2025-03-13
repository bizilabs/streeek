package com.bizilabs.streeek.feature.setup

import cafe.adriel.voyager.core.registry.screenModule
import com.bizilabs.streeek.lib.common.navigation.SharedScreen

val featureSetup =
    screenModule {
        register<SharedScreen.Setup> { SetupScreen }
    }
