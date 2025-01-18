package com.bizilabs.streeek.feature.push

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NotificationEventManager {
    private val _events = MutableSharedFlow<NotificationEvent>(replay = 0)
    val events = _events.asSharedFlow()

    suspend fun postEvent(event: NotificationEvent) {
        _events.emit(event)
    }
}

sealed class NotificationEvent {
    data class GlobalEvent(val title: String, val body: String) : NotificationEvent()

    data class UserEvent(val userId: String, val title: String, val body: String) : NotificationEvent()

    data class IssueEvent(val issueId: String, val title: String, val body: String) : NotificationEvent()
}
