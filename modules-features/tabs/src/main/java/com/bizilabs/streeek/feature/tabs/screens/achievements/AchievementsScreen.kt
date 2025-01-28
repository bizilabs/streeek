package com.bizilabs.streeek.feature.tabs.screens.achievements

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.SubcomposeAsyncImage
import com.bizilabs.streeek.feature.tabs.screens.achievements.component.LevelComponent
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.design.components.SafiProfileArc
import com.bizilabs.streeek.lib.design.components.SafiTopBarHeader
import com.bizilabs.streeek.lib.design.components.shimmerEffect
import timber.log.Timber

object AchievementsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val profileScreen = rememberScreen(SharedScreen.Profile)
        val screenModel: AchievementsScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()
        AchievementsScreenContent(
            state = state,
            onClickTab = screenModel::onClickTab,
            onClickRefreshProfile = screenModel::onClickRefreshProfile,
            onClickAccount = {
                Timber.tag("AchievementsScreen").d("onClickAccount")
                navigator?.push(profileScreen)
            },
        )
    }
}

@Composable
fun AchievementsScreenContent(
    state: AchievementScreenState,
    onClickAccount: () -> Unit,
    onClickRefreshProfile: () -> Unit,
    onClickTab: (AchievementTab) -> Unit,
) {
    Scaffold(topBar = {
        AchievementScreenHeader(
            state = state,
            onClickAccount = onClickAccount,
            onClickTab = onClickTab,
            onClickRefreshProfile = onClickRefreshProfile,
        )
    }) { paddingValues ->
        SafiCenteredColumn(
            modifier =
                Modifier
                    .padding(top = paddingValues.calculateTopPadding())
                    .fillMaxSize(),
        ) {
            AnimatedContent(targetState = state.tab, label = "animate achievements") { tab ->
                when (tab) {
                    AchievementTab.BADGES -> {
                        SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                            Text(text = "Coming soon...")
                        }
                    }

                    AchievementTab.LEVELS -> {
                        AchievementsLevelsScreenSection(
                            state = state,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementScreenHeader(
    state: AchievementScreenState,
    onClickAccount: () -> Unit,
    onClickRefreshProfile: () -> Unit,
    onClickTab: (AchievementTab) -> Unit,
) {
    Surface(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onClickAccount) {
                    Icon(
                        imageVector = Icons.Rounded.AccountCircle,
                        contentDescription = "refresh profile",
                    )
                }
                SafiTopBarHeader(
                    modifier = Modifier.weight(1f),
                    title = "Achievements",
                    align = TextAlign.Center,
                )
                IconButton(onClick = onClickRefreshProfile) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "refresh profile",
                    )
                }
            }
            Spacer(modifier = Modifier.padding(8.dp))
            SafiCenteredColumn(
                modifier =
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
            ) {
                state.account?.let { account ->
                    SafiProfileArc(
                        progress = account.points,
                        maxProgress = account.level?.maxPoints ?: account.points.plus(500),
                        modifier = Modifier.size(148.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    ) {
                        SubcomposeAsyncImage(
                            modifier =
                                Modifier
                                    .size(128.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                            model = account.avatarUrl,
                            contentDescription = "user avatar url",
                            contentScale = ContentScale.Crop,
                            loading = {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .shimmerEffect(),
                                )
                            },
                        )
                    }
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = account.username,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = account.level?.name?.replaceFirstChar { it.uppercase() } ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                    )
                    Text(
                        text =
                            buildString {
                                append("LV.")
                                append(account.level?.number)
                                append(" | ")
                                append(account.points)
                                append(" EXP")
                            },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                    )
                }
            }
            TabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = state.tabs.indexOf(state.tab),
            ) {
                state.tabs.forEach { tab ->
                    val isSelected = tab == state.tab
                    val (selectedIcon, unselectedIcon) = tab.icon
                    Tab(
                        selected = isSelected,
                        onClick = { onClickTab(tab) },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(0.25f),
                    ) {
                        SafiCenteredRow(modifier = Modifier.padding(16.dp)) {
                            Icon(
                                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                                contentDescription = tab.label,
                            )
                            Spacer(modifier = Modifier.padding(8.dp))
                            Text(text = tab.label)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementsLevelsScreenSection(
    state: AchievementScreenState,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        modifier = modifier,
        targetState = state.levels,
        label = "animated levels",
    ) { levels ->
        when {
            levels.isEmpty() -> {
                SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = false
                ) {
                    itemsIndexed(levels, key = { index, level -> level.id }){
                        index, level ->

                        val next = levels.getOrNull(index + 1)
                        val current = level
                        val accountLevel = state.level

                        accountLevel?.let {
                            LevelComponent(
                                points = state.points,
                                current = current,
                                accountLevel = it,
                                modifier = Modifier.fillMaxWidth()
                                    .height(150.dp)
                                    .padding(horizontal = 24.dp)
                            )
                        }
                    }

                }
            }
        }
    }
}
