package com.bizilabs.streeek.feature.setup

import cafe.adriel.voyager.core.registry.screenModule
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import org.koin.dsl.module

val featureSetup = screenModule {
    register<SharedScreen.Setup> { SetupScreen }
}

val setupModule = module {
    factory { SetupScreenModel(context = get(), userRepository = get(), accountRepository = get()) }
}
