package com.bizilabs.streeek.lib.common.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class SharedScreen : ScreenProvider {
    object Landing : SharedScreen()

    object OnBoarding : SharedScreen()

    object Authentication : SharedScreen()

    open class Tabs(val tab: String = "FEED") : SharedScreen() {
        companion object : Tabs()
    }

    object Setup : SharedScreen()

    object Profile : SharedScreen()

    class Team(val teamId: Long?) : SharedScreen()

    object Issues : SharedScreen()

    data class Issue(val id: Long? = null) : SharedScreen()

    data class EditIssue(val id: Long? = null) : SharedScreen()

    data class Leaderboard(val name: String) : SharedScreen()

    object Points : SharedScreen()

    object Join : SharedScreen()

    object Notifications : SharedScreen()

    object Reminders : SharedScreen()

    data class Reminder(val label: String, val day: Int, val code: Int) : SharedScreen()
}
