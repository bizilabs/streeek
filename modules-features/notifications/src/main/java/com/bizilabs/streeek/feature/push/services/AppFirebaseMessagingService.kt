package com.bizilabs.streeek.feature.push.services

import com.bizilabs.streeek.feature.push.NotificationEvent
import com.bizilabs.streeek.feature.push.NotificationEventManager
import com.bizilabs.streeek.feature.push.data.NotificationData
import com.bizilabs.streeek.feature.push.data.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

class AppFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val DEFAULT_TITLE = "New Notification"
        private const val DEFAULT_BODY = "Tap to see notification details"
    }

    private val notificationEventManager: NotificationEventManager by inject()
    private val notificationHelper: NotificationHelper by inject()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag("FCM").d("New token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val dataMap = remoteMessage.data

        val data =
            NotificationData(
                eventType = dataMap["eventType"],
                userId = dataMap["userId"],
                issueId = dataMap["issueId"],
                title = dataMap["title"],
                body = dataMap["body"],
            )

        val event =
            when (data.eventType) {
                "global" ->
                    NotificationEvent.GlobalEvent(
                        title = data.title ?: DEFAULT_TITLE,
                        body = data.body ?: DEFAULT_BODY,
                    )

                "user" ->
                    NotificationEvent.UserEvent(
                        userId = data.userId ?: "",
                        title = data.title ?: DEFAULT_TITLE,
                        body = data.body ?: DEFAULT_BODY,
                    )

                "issue" ->
                    NotificationEvent.IssueEvent(
                        issueId = data.issueId ?: "",
                        title = data.title ?: DEFAULT_TITLE,
                        body = data.body ?: DEFAULT_BODY,
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
        val title = data.title ?: DEFAULT_TITLE
        val message = data.body ?: DEFAULT_BODY

        notificationHelper.showNotification(
            title = title,
            message = message,
        )
    }
}
