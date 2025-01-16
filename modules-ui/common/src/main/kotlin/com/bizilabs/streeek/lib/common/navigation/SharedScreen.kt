package com.bizilabs.streeek.lib.common.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class SharedScreen : ScreenProvider {
    object Landing : SharedScreen()

    object Authentication : SharedScreen()

    object Tabs : SharedScreen()

    object Setup : SharedScreen()

    object Profile : SharedScreen()

    class Team(val isJoining: Boolean = false, val teamId: Long?) : SharedScreen()

    object Issues : SharedScreen()

    data class Issue(val id: Long? = null) : SharedScreen()

    data class Leaderboard(val name: String) : SharedScreen()

    object Points : SharedScreen()
}
