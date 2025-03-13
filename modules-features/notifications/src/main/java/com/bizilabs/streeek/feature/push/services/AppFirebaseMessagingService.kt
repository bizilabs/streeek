package com.bizilabs.streeek.feature.push.services

import com.bizilabs.streeek.lib.domain.managers.NotificationChannel
import com.bizilabs.streeek.lib.domain.managers.NotificationData
import com.bizilabs.streeek.lib.domain.managers.NotificationManager
import com.bizilabs.streeek.lib.domain.managers.Notifications
import com.bizilabs.streeek.lib.domain.models.notifications.asNotificationResult
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject
import timber.log.Timber

class AppFirebaseMessagingService : FirebaseMessagingService() {
    val manager: NotificationManager by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag("FCM").d("New token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val dataMap = remoteMessage.data

        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body
        val imageUrl = remoteMessage.notification?.imageUrl
        val result = dataMap["notification_result"]?.asNotificationResult()

        if (title != null && body != null && result != null) {
            val channel = getChannelFromType(result.type)
            manager.send(
                NotificationData(
                    title = title,
                    message = body,
                    channel = channel,
                    imageUri = imageUrl.toString(),
                ),
            )
        }
    }

    private fun getChannelFromType(type: String): NotificationChannel {
        return when (type) {
            "TEAM_REQUESTS" -> Notifications.Channels.teamRequests
            else -> Notifications.Channels.general
        }
    }
}
