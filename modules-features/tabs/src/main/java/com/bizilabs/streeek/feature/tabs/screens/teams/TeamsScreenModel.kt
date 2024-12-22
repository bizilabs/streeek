package com.bizilabs.streeek.feature.tabs.screens.teams

import org.koin.dsl.module

internal val TeamsModule = module(){
    factory<TeamsScreenModel> {
        TeamsScreenModel()
    }
}

class TeamsScreenModel {
}