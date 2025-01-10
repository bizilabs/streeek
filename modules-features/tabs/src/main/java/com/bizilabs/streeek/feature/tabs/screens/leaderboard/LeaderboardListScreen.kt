package com.bizilabs.streeek.feature.tabs.screens.leaderboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Leaderboard
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.feature.tabs.screens.leaderboard.components.TeamTopMemberComponent
import com.bizilabs.streeek.lib.common.components.LeaderboardComponent
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.design.components.SafiCircularProgressIndicator
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.design.helpers.onSuccess
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.domain.extensions.asRank
import com.bizilabs.streeek.lib.domain.models.LeaderboardDomain
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

object LeaderboardListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        val screenModel: LeaderboardListScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()

        LeaderboardListScreenContent(
            state = state,
            onValueChangeLeaderboard = screenModel::onValueChangeLeaderboard,
            onClickViewMore = screenModel::onClickViewMore,
        ) { screen ->
            navigator?.push(screen)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardListScreenContent(
    state: LeaderboardListScreenState,
    onValueChangeLeaderboard: (LeaderboardDomain) -> Unit,
    onClickViewMore: () -> Unit,
    navigate: (Screen) -> Unit,
) {
    if (state.leaderboardName != null) {
        navigate(
            rememberScreen(SharedScreen.Leaderboard(name = state.leaderboardName)),
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                LeaderboardListScreenHeaderSection(
                    state = state,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChangeLeaderboard = onValueChangeLeaderboard,
                )
            },
        ) { paddingValues ->
            AnimatedContent(
                label = "animate teams",
                modifier =
                    Modifier
                        .padding(top = paddingValues.calculateTopPadding())
                        .fillMaxSize(),
                targetState = state.leaderboard,
            ) { leaderboard ->
                when (leaderboard) {
                    null -> {
                        SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                            SafiInfoSection(
                                icon = Icons.Rounded.Leaderboard,
                                title = "Crunching",
                                description = "It's about to get real. Prepare yourself!",
                            ) {
                                SafiCircularProgressIndicator()
                            }
                        }
                    }

                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.list) { member ->
                                LeaderboardComponent(
                                    imageUrl = member.account.avatarUrl,
                                    username = member.account.username,
                                    points = member.rank.points,
                                    rank = member.rank.position.asRank(),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                }
                            }
                            item {
                                SafiCenteredRow(modifier = Modifier.fillMaxWidth()) {
                                    Button(
                                        modifier = Modifier.padding(16.dp),
                                        onClick = onClickViewMore,
                                    ) {
                                        Text(text = "View More")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = state.showConfetti,
        ) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties =
                    remember {
                        listOf(
                            Party(
                                speed = 0f,
                                maxSpeed = 30f,
                                damping = 0.9f,
                                spread = 360,
                                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                                position = Position.Relative(0.5, 0.3),
                                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                            ),
                            Party(
                                speed = 0f,
                                maxSpeed = 30f,
                                damping = 0.9f,
                                spread = 360,
                                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                                position = Position.Relative(0.5, 0.3),
                                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                            ),
                        )
                    },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardListScreenHeaderSection(
    state: LeaderboardListScreenState,
    onValueChangeLeaderboard: (LeaderboardDomain) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1f).padding(vertical = 16.dp),
                    text = "Leaderboard".uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                )
            }
            AnimatedVisibility(
                modifier =
                    Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                visible = state.leaderboards.isNotEmpty() && (state.leaderboards.size != 1),
            ) {
                val index = state.leaderboards.indexOf(state.leaderboard)
                Column(modifier = Modifier.fillMaxWidth()) {
                    TabRow(
                        modifier = Modifier.fillMaxWidth(),
                        selectedTabIndex = index,
                        divider = {},
                    ) {
                        state.leaderboards.forEach { leaderboard ->
                            val selected = leaderboard.name == state.leaderboard?.name
                            Tab(
                                selected = selected,
                                onClick = { onValueChangeLeaderboard(leaderboard) },
                            ) {
                                Box {
                                    Text(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp),
                                        text =
                                            leaderboard.name.lowercase()
                                                .replaceFirstChar { it.uppercase() },
                                        color =
                                            if (selected) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurface.copy(0.75f)
                                            },
                                    )
                                    Column(
                                        modifier =
                                            Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(bottom = 24.dp),
                                    ) {
                                        AnimatedVisibility(
                                            visible = !selected,
                                        ) {
                                            Badge(
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                containerColor = MaterialTheme.colorScheme.success,
                                                contentColor = MaterialTheme.colorScheme.onSuccess,
                                            ) {
                                                Text(
                                                    modifier = Modifier.padding(1.dp),
                                                    text = "${leaderboard.rank.current.position}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }
            }
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = state.leaderboard != null,
            ) {
                if (state.leaderboard != null) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                    ) {
                        TeamTopMemberComponent(
                            isFirst = false,
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(top = 48.dp),
                            member = state.leaderboard.top[1],
                        )
                        TeamTopMemberComponent(
                            isFirst = true,
                            modifier = Modifier.weight(1f),
                            member = state.leaderboard.top[0],
                        )
                        TeamTopMemberComponent(
                            isFirst = false,
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(top = 48.dp),
                            member = state.leaderboard.top[2],
                        )
                    }
                }
            }
        }
    }
}
