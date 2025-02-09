package com.bizilabs.streeek.feature.push.services

import com.bizilabs.streeek.lib.domain.managers.notifications.AppNotificationChannel
import com.bizilabs.streeek.lib.domain.managers.notifications.notify
import com.bizilabs.streeek.lib.domain.models.notifications.asNotificationResult
import com.bizilabs.streeek.lib.domain.workers.startProcessingNotificationWork
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class AppFirebaseMessagingService : FirebaseMessagingService() {
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
            notify(title = title, body = body, channel = channel, imageUrl = imageUrl)
            startProcessingNotificationWork(result = result)
        }
    }

    private fun getChannelFromType(type: String): AppNotificationChannel {
        return when (type) {
            "GENERAL" -> AppNotificationChannel.GENERAL
            "TEAM_REQUESTS" -> AppNotificationChannel.TEAM_REQUESTS
            else -> AppNotificationChannel.GENERAL
        }
    }
}
