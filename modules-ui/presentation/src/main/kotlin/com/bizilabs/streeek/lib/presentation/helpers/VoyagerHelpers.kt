package com.bizilabs.streeek.lib.presentation.helpers

import android.app.Application
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.ScreenRegistry.invoke
import com.bizilabs.streeek.feature.authentication.featureAuthentication
import com.bizilabs.streeek.feature.issue.screenIssue
import com.bizilabs.streeek.feature.issues.screenIssues
import com.bizilabs.streeek.feature.landing.featureLanding
import com.bizilabs.streeek.feature.leaderboard.ScreenLeaderboard
import com.bizilabs.streeek.feature.points.ScreenPoints
import com.bizilabs.streeek.feature.profile.featureProfile
import com.bizilabs.streeek.feature.setup.featureSetup
import com.bizilabs.streeek.feature.tabs.featureTabs
import com.bizilabs.streeek.feature.team.screenTeam

fun Application.initVoyager() {
    ScreenRegistry {
        featureLanding()
        featureAuthentication()
        featureSetup()
        featureTabs()
        featureProfile()
        screenTeam()
        screenIssues()
        screenIssue()
        ScreenLeaderboard()
        ScreenPoints()
    }
}
