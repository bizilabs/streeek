package com.bizilabs.streeek.lib.presentation.helpers

import android.app.Application
import com.bizilabs.streeek.feature.authentication.authenticationModule
import com.bizilabs.streeek.feature.landing.landingModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

fun Application.initKoin(vararg modules: Module) {
    startKoin {
        androidContext(androidContext = this@initKoin)
        loadKoinModules(presentationModule)
        loadKoinModules(modules.asList())
    }
}

val presentationModule = module {
    includes(landingModule, authenticationModule)
}