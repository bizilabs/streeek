package com.bizilabs.streeek.lib.common.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE

enum class AppNotificationChannel(
    val id: String,
    val label: String,
    val importance: Int,
    val description: String,
) {
    GENERAL(
        id = "streeek.general",
        label = "General",
        importance = NotificationManager.IMPORTANCE_DEFAULT,
        description = "general updates",
    ),
    TEAM_REQUESTS(
        id = "streeek.team.requests",
        label = "Team Requests",
        importance = NotificationManager.IMPORTANCE_HIGH,
        description = "updates on joining a team",
    ),
}

private fun AppNotificationChannel.asNotificationChannel() =
    NotificationChannel(id, label, importance).apply {
        description = this@asNotificationChannel.description
    }

fun Context.initNotificationChannels() {
    val channels = AppNotificationChannel.entries.map { it.asNotificationChannel() }
    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannels(channels)
}
