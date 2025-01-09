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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.TransitEnterexit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import com.bizilabs.streeek.feature.tabs.screens.leaderboard.components.LeaderboardItemComponent
import com.bizilabs.streeek.feature.tabs.screens.leaderboard.components.TeamTopMemberComponent
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.domain.models.LeaderboardDomain
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

object LeaderboardScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        val screenModel: LeaderboardScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()

        LeaderboardScreenContent(
            state = state,
            onClickMenuCreateTeam = screenModel::onClickMenuTeamCreate,
            onClickMenuJoinTeam = screenModel::onClickMenuTeamJoin,
            onClickMenuRefreshTeam = screenModel::onClickMenuRefreshTeam,
            onValueChangeLeaderboard = screenModel::onValueChangeLeaderboard,
        ) { screen ->
            navigator?.push(screen)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreenContent(
    state: LeaderboardScreenState,
    onClickMenuCreateTeam: () -> Unit,
    onClickMenuJoinTeam: () -> Unit,
    onClickMenuRefreshTeam: () -> Unit,
    onValueChangeLeaderboard: (LeaderboardDomain) -> Unit,
    navigate: (Screen) -> Unit,
) {
    if (state.isCreating) {
        navigate(rememberScreen(SharedScreen.Team(isJoining = false, teamId = null)))
    }

    if (state.isJoining) {
        navigate(rememberScreen(SharedScreen.Team(isJoining = true, teamId = null)))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                LeaderboardScreenHeaderSection(
                    state = state,
                    modifier = Modifier.fillMaxWidth(),
                    onClickMenuCreateTeam = onClickMenuCreateTeam,
                    onClickMenuJoinTeam = onClickMenuJoinTeam,
                    onValueChangeLeaderboard = onValueChangeLeaderboard,
                    onClickMenuRefreshTeam = onClickMenuRefreshTeam,
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
            ) { team ->
                when (team) {
                    null -> {
                        SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                            SafiInfoSection(
                                icon = Icons.Rounded.People,
                                title = "Empty",
                                description = "Join a team to continue",
                            )
                        }
                    }

                    else -> {
                        if (state.list.isEmpty()) {
                            SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                                SafiInfoSection(
                                    icon = Icons.Rounded.People,
                                    title = "No Other Members",
                                    description = "You're only ${state.leaderboard?.list?.size} members in this team, invite others to continue.",
                                ) {
                                }
                            }
                        } else {
                            LazyColumn {
                                items(state.list) { member ->
                                    LeaderboardItemComponent(member = member)
                                }
                                item {
                                    Button(
                                        modifier = Modifier.padding(16.dp),
                                        onClick = {  },
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LeaderboardScreenHeaderSection(
    state: LeaderboardScreenState,
    onClickMenuCreateTeam: () -> Unit,
    onClickMenuRefreshTeam: () -> Unit,
    onClickMenuJoinTeam: () -> Unit,
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
                IconButton(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = onClickMenuJoinTeam,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.TransitEnterexit,
                        contentDescription = "",
                    )
                }

                Text(
                    modifier = Modifier.weight(1f),
                    text = "Leaderboard".uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                )

                IconButton(
                    modifier = Modifier.padding(end = 16.dp),
                    onClick = onClickMenuCreateTeam,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "",
                    )
                }
                IconButton(
                    modifier = Modifier.padding(end = 16.dp),
                    onClick = onClickMenuRefreshTeam,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "",
                    )
                }
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
                    ScrollableTabRow(
                        modifier = Modifier.fillMaxWidth(),
                        edgePadding = 0.dp,
                        selectedTabIndex = index,
                        divider = {},
                    ) {
                        state.leaderboards.forEach { leaderboard ->
                            val selected = leaderboard.name == state.leaderboard?.name
                            Tab(selected = selected, onClick = { onValueChangeLeaderboard(leaderboard) }) {
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = leaderboard.name.lowercase().replaceFirstChar { it.uppercase() },
                                    color =
                                        if (selected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface.copy(0.75f)
                                        },
                                )
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
                                .padding(vertical = 16.dp),
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
