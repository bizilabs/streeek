package com.bizilabs.streeek.feature.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Leaderboard
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PeopleAlt
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.model.StateScreenModel
import com.bizilabs.streeek.feature.tabs.screens.achievements.AchievementsModule
import com.bizilabs.streeek.feature.tabs.screens.feed.FeedModule
import com.bizilabs.streeek.feature.tabs.screens.leaderboard.LeaderboardModule
import com.bizilabs.streeek.feature.tabs.screens.notifications.ModuleNotifications
import com.bizilabs.streeek.feature.tabs.screens.teams.TeamsListModule
import com.bizilabs.streeek.lib.domain.helpers.tryOrNull
import com.bizilabs.streeek.lib.domain.repositories.WorkerType
import com.bizilabs.streeek.lib.domain.repositories.WorkersRepository
import kotlinx.coroutines.flow.update
import org.koin.dsl.module
import kotlin.time.Duration.Companion.minutes

val FeatureTabsModule =
    module {
        factory { TabsScreenModel(workersRepository = get()) }
        includes(
            LeaderboardModule,
            FeedModule,
            AchievementsModule,
            TeamsListModule,
            ModuleNotifications,
        )
    }

enum class Tabs {
    LEADERBOARDS,
    TEAMS,
    FEED,
    NOTIFICATIONS,
    ACHIEVEMENTS,
    ;

    val icon: Pair<ImageVector, ImageVector>
        get() =
            when (this) {
                FEED -> Pair(Icons.Outlined.Explore, Icons.Rounded.Explore)
                LEADERBOARDS -> Pair(Icons.Outlined.Leaderboard, Icons.Rounded.Leaderboard)
                TEAMS -> Pair(Icons.Outlined.PeopleAlt, Icons.Rounded.PeopleAlt)
                NOTIFICATIONS -> Pair(Icons.Outlined.Notifications, Icons.Rounded.Notifications)
                ACHIEVEMENTS -> Pair(Icons.Outlined.EmojiEvents, Icons.Rounded.EmojiEvents)
            }

    val label: String
        get() =
            when (this) {
                FEED -> "Feed"
                LEADERBOARDS -> "Leaderboard"
                TEAMS -> "Teams"
                NOTIFICATIONS -> "Notifications"
                ACHIEVEMENTS -> "Achievements"
            }
}

data class TabsScreenState(
    val hasSetTabFromNavigation: Boolean = false,
    val tab: Tabs = Tabs.FEED,
    val tabs: List<Tabs> = Tabs.entries.toList(),
)

class TabsScreenModel(
    private val workersRepository: WorkersRepository,
) : StateScreenModel<TabsScreenState>(TabsScreenState()) {
    init {
        startWorkers()
    }

    private fun startWorkers() {
        with(workersRepository) {
            runSaveToken()
            runSyncTeams(type = WorkerType.Periodic(duration = 30.minutes))
            runSyncLevels(type = WorkerType.Periodic(duration = 30.minutes))
            runSyncAccount(type = WorkerType.Periodic(duration = 30.minutes))
            runSyncLeaderboard(type = WorkerType.Periodic(duration = 30.minutes))
            runSyncDailyContributions()
        }
    }

    fun setTabFromNavigation(value: String) {
        val tab = tryOrNull { Tabs.valueOf(value.uppercase()) } ?: return
        if (state.value.hasSetTabFromNavigation) return
        mutableState.update { it.copy(hasSetTabFromNavigation = true, tab = tab) }
    }

    fun onValueChangeTab(tab: Tabs) {
        mutableState.update { it.copy(tab = tab) }
    }
}
