package com.bizilabs.streeek.feature.tabs.screens.teams

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.TransitEnterexit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import com.bizilabs.streeek.feature.tabs.screens.teams.components.TeamTopMemberComponent
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.domain.extensions.asRank
import com.bizilabs.streeek.lib.domain.models.TeamDetailsDomain
import com.bizilabs.streeek.lib.domain.models.TeamDomain

object TeamsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        val screenModel: TeamsScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()

        TeamsScreenContent(
            state = state,
            onClickMenuCreateTeam = screenModel::onClickMenuTeamCreate,
            onClickMenuJoinTeam = screenModel::onClickMenuTeamJoin,
            onClickTeam = screenModel::onClickTeam,
            onValueChangeTeam = screenModel::onValueChangeTeam
        ) { screen ->
            navigator?.push(screen)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreenContent(
    state: TeamsScreenState,
    onClickMenuCreateTeam: () -> Unit,
    onClickMenuJoinTeam: () -> Unit,
    onClickTeam: (TeamDomain) -> Unit,
    onValueChangeTeam: (TeamDetailsDomain) -> Unit,
    navigate: (Screen) -> Unit
) {

    if (state.isCreating)
        navigate(rememberScreen(SharedScreen.Team(isJoining = false, teamId = null)))

    if (state.isJoining)
        navigate(rememberScreen(SharedScreen.Team(isJoining = true, teamId = null)))

    if (state.teamId != null)
        navigate(rememberScreen(SharedScreen.Team(isJoining = false, teamId = state.teamId)))

    Scaffold(
        topBar = {
            TeamsScreenHeaderSection(
                state = state,
                modifier = Modifier.fillMaxWidth(),
                onClickMenuCreateTeam = onClickMenuCreateTeam,
                onClickMenuJoinTeam = onClickMenuJoinTeam,
                onValueChangeTeam = onValueChangeTeam
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            label = "animate teams",
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize(),
            targetState = state.team
        ) { team ->
            when (team) {
                null -> {
                    SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                        SafiInfoSection(
                            icon = Icons.Rounded.People,
                            title = "Empty",
                            description = "Join a team to continue"
                        )
                    }
                }

                else -> {
                    if (state.list.isEmpty())
                        SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                            SafiInfoSection(
                                icon = Icons.Rounded.People,
                                title = "No Other Members",
                                description = "You're only three members in this team, invite others to continue."
                            ) {
                                Button(
                                    modifier = Modifier.padding(16.dp),
                                    onClick = { state.team?.let { it -> onClickTeam(it.team) } }
                                ) {
                                    Text(text = "View More")
                                }
                            }
                        }
                    else
                        LazyColumn {
                            items(state.list) { member ->
                                Card(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 8.dp)
                                        .fillMaxWidth(),
                                    colors = CardDefaults.cardColors()
                                ) {
                                    SafiCenteredRow(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Card(
                                            modifier = Modifier.padding(16.dp),
                                            shape = RoundedCornerShape(20),
                                            border = BorderStroke(
                                                1.dp,
                                                MaterialTheme.colorScheme.onSurface
                                            )
                                        ) {
                                            AsyncImage(
                                                modifier = Modifier
                                                    .size(52.dp)
                                                    .clip(RoundedCornerShape(20)),
                                                model = member.account.avatarUrl,
                                                contentDescription = "user avatar url",
                                                contentScale = ContentScale.Crop
                                            )
                                        }

                                        Column {
                                            AnimatedVisibility(visible = member.account.role.isAdmin) {
                                                Text(
                                                    text = member.account.role.label,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                            Text(
                                                text = member.account.username,
                                                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                                            )
                                            Text(
                                                text = buildString {
                                                    append(member.points)
                                                    append(" pts")
                                                }
                                            )
                                        }

                                        Spacer(modifier = Modifier.weight(1f))

                                        Text(
                                            modifier = Modifier.padding(16.dp),
                                            text = member.rank.asRank(),
                                            fontSize = if (member.rank < 100)
                                                MaterialTheme.typography.titleLarge.fontSize
                                            else
                                                MaterialTheme.typography.bodyLarge.fontSize
                                        )
                                    }
                                }
                            }
                            item {
                                Button(
                                    modifier = Modifier.padding(16.dp),
                                    onClick = { state.team?.let { it -> onClickTeam(it.team) } }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TeamsScreenHeaderSection(
    state: TeamsScreenState,
    onClickMenuCreateTeam: () -> Unit,
    onClickMenuJoinTeam: () -> Unit,
    onValueChangeTeam: (TeamDetailsDomain) -> Unit,
    modifier: Modifier = Modifier
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
                    onClick = onClickMenuJoinTeam
                ) {
                    Icon(
                        imageVector = Icons.Rounded.TransitEnterexit,
                        contentDescription = ""
                    )
                }

                Text(
                    modifier = Modifier.weight(1f),
                    text = "Leaderboard".uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )

                IconButton(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = onClickMenuCreateTeam
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = ""
                    )
                }
            }
            AnimatedVisibility(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                visible = state.teams.isNotEmpty()
            ) {
                val index = state.teams.indexOf(state.team)

                TabRow(
                    modifier = Modifier.fillMaxWidth(),
                    selectedTabIndex = index,
                ) {
                    state.teams.forEach {
                        val selected = it.team.id == state.team?.team?.id
                        Tab(selected = selected, onClick = { onValueChangeTeam(it) }) {
                            Text(
                                modifier = Modifier.padding(8.dp),
                                text = it.team.name,
                                color = if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(0.75f),
                            )
                        }
                    }
                }

            }
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = state.team != null
            ) {
                if (state.team != null)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        TeamTopMemberComponent(
                            isFirst = false,
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 48.dp),
                            member = state.team.top[1]
                        )
                        TeamTopMemberComponent(
                            isFirst = true,
                            modifier = Modifier.weight(1f),
                            member = state.team.top[0]
                        )
                        TeamTopMemberComponent(
                            isFirst = false,
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 48.dp),
                            member = state.team.top[2]
                        )
                    }
            }
        }
    }
}
