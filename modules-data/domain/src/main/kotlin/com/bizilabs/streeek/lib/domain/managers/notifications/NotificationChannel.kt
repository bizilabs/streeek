package com.bizilabs.streeek.lib.domain.managers.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE

enum class AppNotificationChannel(
    val id: String,
    val label: String,
    val importance: Int,
    val group: AppNotificationGroup,
    val description: String,
) {
    GENERAL(
        id = "streeek.general",
        label = "General",
        importance = NotificationManager.IMPORTANCE_DEFAULT,
        group = AppNotificationGroup.UPDATES,
        description = "general updates",
    ),
    UPDATES(
        id = "streeek.updates",
        label = "Updates",
        importance = NotificationManager.IMPORTANCE_DEFAULT,
        group = AppNotificationGroup.UPDATES,
        description = "All application updates",
    ),
    REMINDERS(
        id = "streeek.team.reminders",
        label = "Reminders",
        importance = NotificationManager.IMPORTANCE_MAX,
        group = AppNotificationGroup.REMINDERS,
        description = "alarms for contributing to the app",
    ),
    TEAM(
        id = "streeek.team",
        label = "Team",
        importance = NotificationManager.IMPORTANCE_HIGH,
        group = AppNotificationGroup.USER,
        description = "updates on what's happening in all teams",
    ),
    TEAM_REQUESTS(
        id = "streeek.team.requests",
        label = "Team Requests",
        importance = NotificationManager.IMPORTANCE_HIGH,
        group = AppNotificationGroup.USER,
        description = "updates on joining a team",
    ),
    TEAM_UPDATES(
        id = "streeek.team.updates",
        label = "Team Updates",
        importance = NotificationManager.IMPORTANCE_HIGH,
        group = AppNotificationGroup.USER,
        description = "regular team updates",
    ),
    LEADERBOARD(
        id = "streeek.leaderboard",
        label = "Leaderboard",
        importance = NotificationManager.IMPORTANCE_HIGH,
        group = AppNotificationGroup.USER,
        description = "updates on what's happening in all leaderboards",
    ),
}

enum class AppNotificationGroup(
    val id: String,
) {
    UPDATES(id = "streeek.group.updates"),
    USER(id = "streeek.group.user"),
    REMINDERS(id = "streeek.group.reminders"),
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
