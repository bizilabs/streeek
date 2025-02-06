package com.bizilabs.streeek.lib.presentation.helpers

import android.app.Application
import com.bizilabs.streeek.feature.authentication.authenticationModule
import com.bizilabs.streeek.feature.issue.FeatureIssueModule
import com.bizilabs.streeek.feature.issues.FeatureIssuesModule
import com.bizilabs.streeek.feature.join.FeatureJoin
import com.bizilabs.streeek.feature.landing.landingModule
import com.bizilabs.streeek.feature.leaderboard.FeatureLeaderboard
import com.bizilabs.streeek.feature.notifications.FeatureNotificationModule
import com.bizilabs.streeek.feature.notifications.PushNotificationsModule
import com.bizilabs.streeek.feature.onboarding.FeatureModuleOnBoarding
import com.bizilabs.streeek.feature.points.FeaturePoints
import com.bizilabs.streeek.feature.profile.profileModule
import com.bizilabs.streeek.feature.reminders.FeatureModuleReminders
import com.bizilabs.streeek.feature.reviews.ReviewModule
import com.bizilabs.streeek.feature.setup.setupModule
import com.bizilabs.streeek.feature.tabs.FeatureTabsModule
import com.bizilabs.streeek.feature.team.FeatureTeamModule
import com.bizilabs.streeek.feature.updater.AppUpdaterModule
import com.bizilabs.streeek.lib.domain.workers.ReminderWorker
import com.bizilabs.streeek.lib.domain.workers.SaveFCMTokenWork
import com.bizilabs.streeek.lib.domain.workers.SyncAccountWork
import com.bizilabs.streeek.lib.domain.workers.SyncContributionsWork
import com.bizilabs.streeek.lib.domain.workers.SyncDailyContributionsWork
import com.bizilabs.streeek.lib.domain.workers.SyncLeaderboardWork
import com.bizilabs.streeek.lib.domain.workers.SyncLevelsWork
import com.bizilabs.streeek.lib.domain.workers.SyncTeamsWork
import com.bizilabs.streeek.lib.presentation.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun Application.initKoin(vararg modules: Module) {
    startKoin {
        androidContext(androidContext = this@initKoin)
        workManagerFactory()
        loadKoinModules(PresentationModule)
        loadKoinModules(modules.asList())
    }
}

val PresentationModule =
    module {
        viewModel { MainViewModel(preferenceRepository = get()) }
        includes(
            landingModule,
            authenticationModule,
            setupModule,
            FeatureTabsModule,
            profileModule,
            FeatureTeamModule,
            FeatureNotificationModule,
            FeatureIssuesModule,
            FeatureIssueModule,
            FeatureLeaderboard,
            FeaturePoints,
            AppUpdaterModule,
            PushNotificationsModule,
            FeatureJoin,
            FeatureModuleOnBoarding,
            FeatureModuleReminders,
            ReviewModule,
        )
        workerOf(::SyncTeamsWork)
        workerOf(::SyncLevelsWork)
        workerOf(::SyncAccountWork)
        workerOf(::SaveFCMTokenWork)
        workerOf(::SyncLeaderboardWork)
        workerOf(::SyncContributionsWork)
        workerOf(::SyncDailyContributionsWork)
        workerOf(::ReminderWorker)
    }
