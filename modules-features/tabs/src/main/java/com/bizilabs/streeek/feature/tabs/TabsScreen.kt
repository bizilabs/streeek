package com.bizilabs.streeek.feature.tabs

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.bizilabs.streeek.feature.tabs.screens.achievements.AchievementsScreen
import com.bizilabs.streeek.feature.tabs.screens.feed.FeedScreen
import com.bizilabs.streeek.feature.tabs.screens.leaderboard.LeaderboardListScreen
import com.bizilabs.streeek.feature.tabs.screens.notifications.NotificationsScreen
import com.bizilabs.streeek.feature.tabs.screens.teams.TeamsListScreen
import com.bizilabs.streeek.lib.common.navigation.SharedScreen

val featureTabs =
    screenModule {
        register<SharedScreen.Tabs> { params -> TabsScreen(tab = params.tab) }
        register<SharedScreen.Tabs.Companion> { params -> TabsScreen(tab = params.tab) }
    }

open class TabsScreen(val tab: String) : Screen {
    @Composable
    override fun Content() {
        val activity = LocalContext.current as Activity
        val screenModel: TabsScreenModel = getScreenModel()
        screenModel.setTabFromNavigation(value = tab)
        val state by screenModel.state.collectAsStateWithLifecycle()

        BackHandler(enabled = true) {
            if (state.tab != Tabs.FEED) {
                screenModel.onValueChangeTab(Tabs.FEED)
            } else {
                activity.finish()
            }
        }
        TabsScreenContent(state = state) {
            screenModel.onValueChangeTab(it)
        }
    }
}

@Composable
fun TabsScreenContent(
    state: TabsScreenState,
    onValueChangeTab: (Tabs) -> Unit,
) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Tabs.entries.forEach { item ->
                            NavigationBarItem(
                                colors =
                                    NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent,
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor =
                                            MaterialTheme.colorScheme.onSurface.copy(
                                                0.25f,
                                            ),
                                    ),
                                selected = item == state.tab,
                                icon = {
                                    Icon(
                                        imageVector = if (item == state.tab) item.icon.second else item.icon.first,
                                        contentDescription = item.label,
                                    )
                                },
                                onClick = { onValueChangeTab(item) },
                            )
                        }
                    }
                }
            }
        },
    ) { paddingValues ->
        AnimatedContent(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            targetState = state.tab,
            label = "animated_tabs",
        ) { tab ->
            val screen =
                when (tab) {
                    Tabs.FEED -> FeedScreen
                    Tabs.LEADERBOARDS ->
                        LeaderboardListScreen(
                            onNavigateBack = {
                                onValueChangeTab(Tabs.FEED)
                            },
                        )

                    Tabs.TEAMS -> TeamsListScreen
                    Tabs.NOTIFICATIONS -> NotificationsScreen
                    Tabs.ACHIEVEMENTS -> AchievementsScreen
                }
            screen.Content()
        }
    }
}
