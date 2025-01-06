package com.bizilabs.streeek.feature.authentication

import cafe.adriel.voyager.core.registry.screenModule
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import org.koin.dsl.module

val featureAuthentication =
    screenModule {
        register<SharedScreen.Authentication> { AuthenticationScreen }
    }
val authenticationModule =
    module {
        factory { AuthenticationScreenModel(authenticationRepository = get()) }
    }
