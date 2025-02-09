package com.bizilabs.streeek.feature.tabs.screens.achievements

import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.SubcomposeAsyncImage
import com.bizilabs.streeek.feature.tabs.screens.achievements.component.LevelComponent
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.common.navigation.SharedScreen.Tabs.Companion.tab
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.design.components.SafiProfileArc
import com.bizilabs.streeek.lib.design.components.SafiRefreshBox
import com.bizilabs.streeek.lib.design.components.SafiTopBarHeader
import com.bizilabs.streeek.lib.design.components.shimmerEffect
import kotlinx.coroutines.launch
import timber.log.Timber

object AchievementsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val activity = LocalContext.current as ComponentActivity
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

        LifecycleStartEffect(Unit) {
            activity.let { screenModel.requestReview(it) }
            onStopOrDispose { Timber.d("Achievements page is onStopOrDispose") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreenContent(
    state: AchievementScreenState,
    onClickAccount: () -> Unit,
    onClickRefreshProfile: () -> Unit,
    onClickTab: (AchievementTab) -> Unit,
) {
    val pagerState = rememberPagerState(state.tabs.indexOf(state.tab)) { state.tabs.size }
    SafiRefreshBox(
        isRefreshing = state.isSyncingAccount,
        onRefresh = onClickRefreshProfile,
    ) {
        Scaffold(topBar = {
            AchievementScreenHeader(
                state = state,
                onClickAccount = onClickAccount,
                onClickTab = onClickTab,
                pagerState = pagerState,
            )
        }) { paddingValues ->
            SafiCenteredColumn(
                modifier =
                    Modifier
                        .padding(top = paddingValues.calculateTopPadding())
                        .fillMaxSize(),
            ) {
                HorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState,
                ) { pageIndex ->
                    when (state.tabs[pageIndex]) {
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
}

@Composable
fun AchievementScreenHeader(
    state: AchievementScreenState,
    onClickAccount: () -> Unit,
    onClickTab: (AchievementTab) -> Unit,
    pagerState: PagerState,
) {
    val coroutineScope = rememberCoroutineScope()

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
                SafiTopBarHeader(
                    modifier =
                        Modifier
                            .padding(start = 16.dp)
                            .weight(1f),
                    title = "Achievements",
                )
                IconButton(onClick = onClickAccount) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "settings",
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
                selectedTabIndex = pagerState.currentPage,
            ) {
                state.tabs.forEachIndexed { index, tab ->
                    val isSelected = pagerState.currentPage == index
                    val (selectedIcon, unselectedIcon) = tab.icon
                    Tab(
                        selected = isSelected,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(state.tabs.indexOf(tab))
                            }
                            onClickTab(tab)
                        },
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
                val lazyListState = rememberLazyListState()
                val coroutineScope = rememberCoroutineScope()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    reverseLayout = false,
                ) {
                    itemsIndexed(levels, key = { index, level -> level.id }) { index, level ->

                        val current = level
                        val accountLevel = state.level

                        LaunchedEffect(current) {
                            if (index == levels.lastIndex) {
                                coroutineScope.launch { lazyListState.animateScrollToItem(index) }
                            }
                        }

                        accountLevel?.let {
                            LevelComponent(
                                points = state.points,
                                current = current,
                                accountLevel = it,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                        .padding(horizontal = 24.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
