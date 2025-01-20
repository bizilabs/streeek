package com.bizilabs.streeek.feature.notifications

import cafe.adriel.voyager.core.registry.screenModule
import com.bizilabs.streeek.feature.push.NotificationEventManager
import com.bizilabs.streeek.feature.push.data.NotificationHelper
import com.bizilabs.streeek.feature.push.presentation.NotificationViewModel
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val screenNotifications =
    screenModule {
        register<SharedScreen.Notifications> { NotificationsScreen }
    }

val FeatureNotificationModule =
    module {
        factory { NotificationsScreenModel(repository = get()) }
    }
val PushNotificationsModule =
    module {
        single { NotificationEventManager }
        single { NotificationHelper(get()) }
        viewModel {
            NotificationViewModel(
                get(),
                get(),
            )
        }
    }
