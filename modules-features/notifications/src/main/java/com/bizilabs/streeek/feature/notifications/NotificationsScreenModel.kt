package com.bizilabs.streeek.feature.notifications

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.domain.helpers.SystemLocalDateTime
import com.bizilabs.streeek.lib.domain.models.NotificationDomain
import com.bizilabs.streeek.lib.domain.repositories.NotificationRepository
import kotlinx.coroutines.launch

data class NotificationsScreenState(
    val pastInitial: Boolean = false,
    val isSyncing: Boolean = false,
    val notifications: List<NotificationDomain> = emptyList(),
)

class NotificationsScreenModel(
    private val repository: NotificationRepository,
) : StateScreenModel<NotificationsScreenState>(NotificationsScreenState()) {
    val notifications = repository.notifications

    fun onClickNotification(notification: NotificationDomain) {
        screenModelScope.launch {
            repository.update(notification.copy(readAt = SystemLocalDateTime))
        }
    }
}
