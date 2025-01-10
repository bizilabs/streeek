package com.bizilabs.streeek.feature.tabs

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.bizilabs.streeek.feature.tabs.screens.leaderboard.LeaderboardScreen
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.helpers.SetupNavigationBarColor
import com.bizilabs.streeek.lib.design.helpers.SetupStatusBarColor

val featureTabs =
    screenModule {
        register<SharedScreen.Tabs> { TabsScreen }
    }

object TabsScreen : Screen {
    @Composable
    override fun Content() {
        SetupNavigationBarColor(color = MaterialTheme.colorScheme.surface)
        SetupStatusBarColor(color = MaterialTheme.colorScheme.surface)

        val activity = LocalContext.current as Activity
        val screenModel: TabsScreenModel = getScreenModel()
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
                Tabs.entries.forEach { item ->
                    NavigationBarItem(
                        colors =
                            NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent,
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(0.25f),
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
                    Tabs.TEAMS -> LeaderboardScreen
                    Tabs.ACHIEVEMENTS -> AchievementsScreen
                }
            screen.Content()
        }
    }
}
