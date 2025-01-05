package com.bizilabs.streeek.feature.notifications

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.feature.notifications.NotificationsTab.*
import com.bizilabs.streeek.lib.domain.helpers.SystemLocalDateTime
import com.bizilabs.streeek.lib.domain.models.NotificationDomain
import com.bizilabs.streeek.lib.domain.repositories.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.enums.EnumEntries

enum class NotificationsTab {
    ALL, UNREAD;

    val label: String
        get() = when (this) {
            ALL -> "All"
            UNREAD -> "Unread"
        }
}

data class NotificationsScreenState(
    val tab: NotificationsTab = ALL,
    val pastInitial: Boolean = false,
    val isSyncing: Boolean = false,
    val notifications: List<NotificationDomain> = emptyList()
) {
    val tabs: EnumEntries<NotificationsTab>
        get() = NotificationsTab.entries
}

class NotificationsScreenModel(
    private val repository: NotificationRepository
) : StateScreenModel<NotificationsScreenState>(NotificationsScreenState()) {

    private val _tab: MutableStateFlow<NotificationsTab> = MutableStateFlow(ALL)
    private val _notifications: Flow<List<NotificationDomain>> = _tab.flatMapLatest { tab ->
        when (tab) {
            ALL -> repository.notifications
            UNREAD -> repository.unreadNotifications
        }
    }

    init {
        observeTab()
        observeNotifications()
    }

    private fun observeTab() {
        screenModelScope.launch {
            _tab.collectLatest { tab ->
                mutableState.update { it.copy(tab = tab) }
            }
        }
    }

    private fun observeNotifications() {
        screenModelScope.launch {
            _notifications.collectLatest { notifications ->
                mutableState.update {
                    it.copy(
                        notifications = notifications,
                        pastInitial = true,
                        isSyncing = false
                    )
                }
            }
        }
    }

    fun onClickTab(notificationsTab: NotificationsTab) {
        mutableState.update { it.copy(tab = notificationsTab) }
    }

    fun onRefreshNotifications() {
        screenModelScope.launch {
            mutableState.update { it.copy(isSyncing = true) }
        }
    }

    fun onClickNotification(notification: NotificationDomain) {
        screenModelScope.launch {
            repository.update(notification.copy(readAt = SystemLocalDateTime))
        }
    }

}
