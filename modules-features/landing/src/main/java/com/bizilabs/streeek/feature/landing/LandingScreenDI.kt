package com.bizilabs.streeek.feature.landing

import org.koin.dsl.module

val landingModule = module {
    factory { LandingScreenModel(repository = get()) }
}
