package com.bizilabs.streeek.feature.tabs.screens.notifications

import cafe.adriel.voyager.core.model.StateScreenModel
import com.bizilabs.streeek.lib.domain.models.NotificationDomain
import com.bizilabs.streeek.lib.domain.repositories.NotificationRepository
import org.koin.dsl.module

internal val ModuleNotifications =
    module {
        factory<NotificationsScreenModel> {
            NotificationsScreenModel(notificationsRepository = get())
        }
    }

data class NotificationsScreenState(
    val notifications: List<NotificationDomain> = emptyList(),
)

class NotificationsScreenModel(
    private val notificationsRepository: NotificationRepository,
) : StateScreenModel<NotificationsScreenState>(NotificationsScreenState()) {
    val notifications = notificationsRepository.notifications
}