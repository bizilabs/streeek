package com.bizilabs.streeek.feature.issue

import cafe.adriel.voyager.core.registry.screenModule
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import org.koin.dsl.module

val screenIssue =
    screenModule {
        register<SharedScreen.Issue> { IssueScreen(id = it.id) }
    }

val editScreenIssue =
    screenModule {
        register<SharedScreen.EditIssue> { IssueEditScreen(id = it.id) }
    }

val FeatureIssueModule =
    module {
        factory { IssueScreenModel(issueRepository = get(), labelRepository = get()) }
    }
