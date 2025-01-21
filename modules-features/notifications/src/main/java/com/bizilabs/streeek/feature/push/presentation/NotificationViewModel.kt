package com.bizilabs.streeek.feature.push.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizilabs.streeek.feature.push.NotificationEvent
import com.bizilabs.streeek.feature.push.NotificationEventManager
import com.bizilabs.streeek.feature.push.data.NotificationHelper
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notificationEventManager: NotificationEventManager,
    private val notificationHelper: NotificationHelper,
) : ViewModel() {
    init {
        viewModelScope.launch {
            notificationEventManager.events.collect { event ->
                when (event) {
                    is NotificationEvent.GlobalEvent -> handleGlobalEvent(event)
                    is NotificationEvent.UserEvent -> handleUserEvent(event)
                    is NotificationEvent.IssueEvent -> handleIssueEvent(event)
                }
            }
        }
    }

    private fun handleGlobalEvent(event: NotificationEvent.GlobalEvent) {
        notificationHelper.showNotification(
            title = event.title,
            message = event.body,
        )
    }

    private fun handleUserEvent(event: NotificationEvent.UserEvent) {
        notificationHelper.showNotification(
            title = event.title,
            message = event.body,
        )
    }

    private fun handleIssueEvent(event: NotificationEvent.IssueEvent) {
        notificationHelper.showNotification(
            title = event.title,
            message = event.body,
        )
    }
}
