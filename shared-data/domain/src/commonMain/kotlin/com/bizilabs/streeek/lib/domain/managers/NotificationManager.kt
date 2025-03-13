package com.bizilabs.streeek.lib.domain.managers

data class NotificationGroup(
    val id: String,
    val title: String,
)

data class NotificationChannel(
    val id: String,
    val label: String,
    val importance: Int,
    val description: String,
    val group: NotificationGroup,
)

object Notifications {
    object Codes {
        const val REMINDERS = 1000
    }

    object Groups {
        val reminders = NotificationGroup(id = "streeek.group.reminders", title = "Reminders")
        val updates = NotificationGroup(id = "streeek.group.updates", title = "Updates")
        val user = NotificationGroup(id = "streeek.group.user", title = "User")
    }

    object Channels {
        val general =
            NotificationChannel(
                id = "streeek.general",
                label = "General",
                importance = 3,
                group = Groups.updates,
                description = "general updates",
            )

        val updates =
            NotificationChannel(
                id = "streeek.updates",
                label = "Updates",
                importance = 3,
                group = Groups.updates,
                description = "All application updates",
            )

        val reminders =
            NotificationChannel(
                id = "streeek.team.reminders",
                label = "Reminders",
                importance = 5,
                group = Groups.reminders,
                description = "alarms for contributing to the app",
            )

        val team =
            NotificationChannel(
                id = "streeek.team",
                label = "Team",
                importance = 4,
                group = Groups.user,
                description = "updates on what's happening in all teams",
            )

        val teamRequests =
            NotificationChannel(
                id = "streeek.team.requests",
                label = "Team Requests",
                importance = 4,
                group = Groups.user,
                description = "updates on joining a team",
            )

        val teamUpdates =
            NotificationChannel(
                id = "streeek.team.updates",
                label = "Team Updates",
                importance = 4,
                group = Groups.user,
                description = "regular team updates",
            )

        val leaderboard =
            NotificationChannel(
                id = "streeek.leaderboard",
                label = "Leaderboard",
                importance = 4,
                group = Groups.user,
                description = "updates on what's happening in all leaderboards",
            )

        val values =
            listOf(
                general,
                updates,
                reminders,
                team,
                teamRequests,
            )
    }
}

data class NotificationData(
    val title: String,
    val message: String,
    val channel: NotificationChannel,
    val imageUri: String? = null,
)

interface NotificationManager {
    fun send(notification: NotificationData)
}
