package com.bizilabs.streeek.feature.tabs

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
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
import com.bizilabs.streeek.feature.tabs.screens.AchievementsScreen
import com.bizilabs.streeek.feature.tabs.screens.TeamsScreen
import com.bizilabs.streeek.feature.tabs.screens.feed.FeedScreen
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.helpers.SetupNavigationBarColor

val featureTabs = screenModule {
    register<SharedScreen.Tabs> { TabsScreen }
}

object TabsScreen : Screen {
    @Composable
    override fun Content() {
        val activity = LocalContext.current as Activity
        SetupNavigationBarColor(color = MaterialTheme.colorScheme.surface)
        val screenModel: TabsScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()
        BackHandler(enabled = true) {
            if (state.tab != Tabs.FEED)
                screenModel.onValueChangeTab(Tabs.FEED)
            else
                activity.finish()
        }
        TabsScreenContent(state = state) {
            screenModel.onValueChangeTab(it)
        }
    }
}

@Composable
fun TabsScreenContent(state: TabsScreenState, onValueChangeTab: (Tabs) -> Unit) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tabs.entries.forEach { item ->
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(0.25f)
                        ),
                        selected = item == state.tab,
                        icon = {
                            Icon(
                                imageVector = if (item == state.tab) item.icon.second else item.icon.first,
                                contentDescription = item.label
                            )
                        },
                        onClick = { onValueChangeTab(item) }
                    )
                }
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            targetState = state.tab,
            label = "animated_tabs"
        ) { tab ->
            val screen = when (tab) {
                Tabs.FEED -> FeedScreen
                Tabs.TEAMS -> TeamsScreen
                Tabs.ACHIEVEMENTS -> AchievementsScreen
            }
            screen.Content()
        }
    }
}

//@Composable
//fun TabsScreenContent(state: TabsScreenState) {
//    Scaffold { paddingValues ->
//        Column(
//            modifier = Modifier
//                .padding(paddingValues)
//                .fillMaxSize()
//        ) {
//            AnimatedContent(
//                modifier = Modifier.fillMaxWidth(),
//                targetState = state.accountState,
//                label = "account_animation"
//            ) { result ->
//                when (result) {
//                    is FetchState.Error -> {
//                        SafiCenteredColumn {
//                            Text(
//                                text = "Error",
//                                modifier = Modifier.padding(16.dp),
//                                fontSize = 24.sp
//                            )
//                            Text(text = result.message)
//                        }
//                    }
//
//                    FetchState.Loading -> {
//                        CircularProgressIndicator()
//                    }
//
//                    is FetchState.Success -> {
//                        val user = result.value
//                        SafiCenteredColumn(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(bottom = 16.dp)
//                        ) {
//                            AsyncImage(
//                                modifier = Modifier
//                                    .size(100.dp)
//                                    .padding(16.dp)
//                                    .clip(CircleShape),
//                                model = user.avatarUrl,
//                                contentDescription = null,
//                            )
//                            Text(
//                                modifier = Modifier.padding(16.dp),
//                                text = user.id.toString()
//                            )
//                            Text(text = user.username)
//                            Text(text = user.email)
//                            Text(
//                                text = user.bio,
//                                modifier = Modifier.fillMaxWidth(0.5f),
//                                textAlign = TextAlign.Center
//                            )
//                        }
//                    }
//                }
//            }
//            HorizontalDivider(modifier = Modifier.fillMaxWidth())
//            AnimatedContent(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxSize(),
//                targetState = state.contributions,
//                label = "events_animation"
//            ) { contributions ->
//                when (contributions) {
//                    FetchState.Loading -> {
//                        SafiCenteredColumn(modifier = Modifier.fillMaxSize()) { CircularProgressIndicator() }
//                    }
//
//                    is FetchState.Error -> {
//                        SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
//                            Text(text = "Error")
//                            Text(text = contributions.message)
//                        }
//
//                    }
//
//                    is FetchState.Success -> {
//                        val context = LocalContext.current
//                        val list = contributions.value
//                        AnimatedContent(
//                            modifier = Modifier.fillMaxSize(),
//                            targetState = list,
//                            label = "list_animation"
//                        ) {
//                            when {
//                                it.isEmpty() -> {
//                                    SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
//                                        SafiInfoSection(
//                                            icon = Icons.Rounded.PushPin,
//                                            title = "No Contributions Found",
//                                            description = "You haven't been busy today... Push some few commits!"
//                                        )
//                                    }
//                                }
//
//                                else -> {
//                                    LazyColumn(modifier = Modifier.fillMaxSize()) {
//                                        items(it) {
//                                            Card(
//                                                modifier = Modifier
//                                                    .fillMaxWidth()
//                                                    .padding(8.dp),
//                                                onClick = {
//                                                    Toast.makeText(
//                                                        context,
//                                                        it.githubEventType,
//                                                        Toast.LENGTH_SHORT
//                                                    ).show()
//                                                },
//                                                border = BorderStroke(
//                                                    1.dp,
//                                                    MaterialTheme.colorScheme.onBackground.copy(
//                                                        0.2f
//                                                    )
//                                                ),
//                                            ) {
//                                                Column(
//                                                    modifier = Modifier
//                                                        .padding(16.dp)
//                                                        .fillMaxWidth()
//                                                ) {
//                                                    Text(
//                                                        text = it.createdAt.asString()
//                                                            ?: "Some Date..."
//                                                    )
//                                                    Text(text = "ID : ${it.id} > git : ${it.githubEventId}")
//                                                    SafiCenteredRow {
//                                                        Text(text = it.githubEventType)
//                                                        Text(text = " > ", fontSize = 24.sp)
//                                                        Text(text = it.githubEventRepo.name)
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
