package com.bizilabs.streeek.feature.tabs

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.PeopleAlt
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.model.StateScreenModel
import com.bizilabs.streeek.feature.tabs.screens.achievements.AchievementsModule
import com.bizilabs.streeek.feature.tabs.screens.feed.FeedModule
import com.bizilabs.streeek.feature.tabs.screens.teams.TeamsModule
import com.bizilabs.streeek.lib.domain.workers.startPeriodicAccountSyncWork
import com.bizilabs.streeek.lib.domain.workers.startPeriodicDailySyncContributionsWork
import com.bizilabs.streeek.lib.domain.workers.startPeriodicLevelsSyncWork
import com.bizilabs.streeek.lib.domain.workers.startPeriodicTeamsSyncWork
import kotlinx.coroutines.flow.update
import org.koin.dsl.module

val tabsModule = module {
    factory {
        TabsScreenModel(context = get(),)
    }
    includes(TeamsModule, FeedModule, AchievementsModule)
}

enum class Tabs {
    TEAMS, FEED, ACHIEVEMENTS;

    val icon: Pair<ImageVector, ImageVector>
        get() = when (this) {
            FEED -> Pair(Icons.Outlined.Explore, Icons.Rounded.Explore)
            TEAMS -> Pair(Icons.Outlined.PeopleAlt, Icons.Rounded.PeopleAlt)
            ACHIEVEMENTS -> Pair(Icons.Outlined.EmojiEvents, Icons.Rounded.EmojiEvents)
        }

    val label: String
        get() = when (this) {
            FEED -> "Feed"
            TEAMS -> "Teams"
            ACHIEVEMENTS -> "Achievements"
        }
}

data class TabsScreenState(
    val tab: Tabs = Tabs.ACHIEVEMENTS,
    val tabs: List<Tabs> = Tabs.entries.toList(),
)

class TabsScreenModel(
    private val context: Context,
) : StateScreenModel<TabsScreenState>(TabsScreenState()) {

    init {
        startWorkers()
    }

    private fun startWorkers() {
        with(context) {
            startPeriodicTeamsSyncWork()
            startPeriodicLevelsSyncWork()
            startPeriodicAccountSyncWork()
            startPeriodicDailySyncContributionsWork()
        }
    }

    fun onValueChangeTab(tab: Tabs) {
        mutableState.update { it.copy(tab = tab) }
    }

}