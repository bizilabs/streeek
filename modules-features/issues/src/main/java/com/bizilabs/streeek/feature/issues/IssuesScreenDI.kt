package com.bizilabs.streeek.feature.issues

import cafe.adriel.voyager.core.registry.screenModule
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import org.koin.dsl.module

val screenIssues =
    screenModule {
        register<SharedScreen.Issues> { IssuesScreen }
    }

val FeatureIssuesModule =
    module {
        factory { IssuesScreenModel(repository = get()) }
    }
