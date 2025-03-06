package com.bizilabs.streeek.lib.domain.managers

data class NotificationGroup(val id: String)

data class NotificationChannel(
    val id: String,
    val label: String,
    val importance: Int,
    val description: String,
    val group: NotificationGroup,
)

data class NotificationData(
    val title: String,
    val message: String,
    val channel: NotificationChannel,
    val imageUri: String? = null,
)

interface NotificationManager {
    fun send(notification: NotificationData)
}
