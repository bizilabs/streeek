package com.bizilabs.streeek.feature.push.services

import com.bizilabs.streeek.feature.push.NotificationEvent
import com.bizilabs.streeek.feature.push.NotificationEventManager
import com.bizilabs.streeek.feature.push.data.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

class AppFirebaseMessagingService : FirebaseMessagingService() {
    private val notificationEventManager: NotificationEventManager by inject()
    private val notificationHelper: NotificationHelper by inject()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag("FCM").d("New token: $token")
        // Send token to your backend
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data
        // Determine the event type and process it
        val event =
            when (data["eventType"]) {
                "global" ->
                    NotificationEvent.GlobalEvent(
                        title = data["title"] ?: "New Notification",
                        body = data["body"] ?: "Tap to see notification Details",
                    )

                "user" ->
                    NotificationEvent.UserEvent(
                        userId = data["userId"] ?: "",
                        title = data["title"] ?: "New Notification",
                        body = data["body"] ?: "Tap to see notification Details",
                    )

                "issue" ->
                    NotificationEvent.IssueEvent(
                        issueId = data["issueId"] ?: "",
                        title = data["title"] ?: "New Notification",
                        body = data["body"] ?: "Tap to see notification Details",
                    )

                else -> {
                    Timber.tag("FCM").w("Unknown event type received")
                    return
                }
            }

        // Post the event to your manager
        scope.launch {
            notificationEventManager.postEvent(event)
        }

        // Show the notification explicitly using NotificationHelper
        val title = data["title"] ?: "New Notification"
        val message = data["body"] ?: "Tap to see notification Details"

        notificationHelper.showNotification(
            title = title,
            message = message,
        )
    }
}
