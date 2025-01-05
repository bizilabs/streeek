package com.bizilabs.streeek.feature.notifications

import cafe.adriel.voyager.core.registry.screenModule
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import org.koin.dsl.module

val screenNotifications = screenModule {
    register<SharedScreen.Notifications> { NotificationsScreen }
}

val FeatureNotificationModule = module {
    factory { NotificationsScreenModel(repository = get()) }
}
