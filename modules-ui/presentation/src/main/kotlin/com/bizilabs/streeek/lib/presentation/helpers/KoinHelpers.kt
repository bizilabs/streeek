package com.bizilabs.streeek.lib.presentation.helpers

import android.app.Application
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import com.bizilabs.streeek.feature.authentication.authenticationModule
import com.bizilabs.streeek.feature.issue.FeatureIssueModule
import com.bizilabs.streeek.feature.issues.FeatureIssuesModule
import com.bizilabs.streeek.feature.join.FeatureJoin
import com.bizilabs.streeek.feature.landing.landingModule
import com.bizilabs.streeek.feature.leaderboard.FeatureLeaderboard
import com.bizilabs.streeek.feature.notifications.FeatureNotificationModule
import com.bizilabs.streeek.feature.notifications.PushNotificationsModule
import com.bizilabs.streeek.feature.points.FeaturePoints
import com.bizilabs.streeek.feature.profile.profileModule
import com.bizilabs.streeek.feature.push.data.NotificationHelper
import com.bizilabs.streeek.feature.setup.setupModule
import com.bizilabs.streeek.feature.tabs.FeatureTabsModule
import com.bizilabs.streeek.feature.team.FeatureTeamModule
import com.bizilabs.streeek.feature.updater.AppUpdaterModule
import com.bizilabs.streeek.lib.domain.workers.SyncAccountWork
import com.bizilabs.streeek.lib.domain.workers.SyncContributionsWork
import com.bizilabs.streeek.lib.domain.workers.SyncDailyContributionsWork
import com.bizilabs.streeek.lib.domain.workers.SyncLeaderboardWork
import com.bizilabs.streeek.lib.domain.workers.SyncLevelsWork
import com.bizilabs.streeek.lib.domain.workers.SyncTeamsWork
import com.bizilabs.streeek.lib.presentation.MainActivity
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import timber.log.Timber

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
        )
        workerOf(::SyncTeamsWork)
        workerOf(::SyncLevelsWork)
        workerOf(::SyncAccountWork)
        workerOf(::SyncLeaderboardWork)
        workerOf(::SyncContributionsWork)
        workerOf(::SyncDailyContributionsWork)
    }

fun MainActivity.initializeNotificationChannel(notificationHelper: NotificationHelper) {
    // Register the ActivityResultLauncher to handle permission request
    val requestNotificationPermission: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, initialize the notification channel
                notificationHelper.initNotificationChannel()
            } else {
                // Permission denied, handle accordingly (show message, guide user to settings, etc.)
                Timber.tag("NotificationPermission").e("Notification permission denied.")
            }
        }
    // Modern approach for requesting notification permission
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            // If notifications are already enabled, initialize the channel
            notificationHelper.initNotificationChannel()
        } else {
            // Request permission if not already granted
            requestNotificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    } else {
        // For versions below Android 13 (API 33), no need for permission request
        notificationHelper.initNotificationChannel()
    }
}
