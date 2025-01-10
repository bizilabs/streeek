package com.bizilabs.streeek.feature.tabs.screens.teams

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.rounded.TransitEnterexit
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.feature.tabs.screens.teams.components.TeamComponent
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.domain.models.TeamDetailsDomain
import com.bizilabs.streeek.lib.resources.strings.SafiStringLabels
import java.util.Locale

object TeamsListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        val screenModel: TeamsListScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()

        TeamsListScreenContent(
            state = state,
            onClickMenuCreateTeam = screenModel::onClickMenuTeamCreate,
            onClickMenuJoinTeam = screenModel::onClickMenuTeamJoin,
            onClickSwipeToRefreshTeam = screenModel::onClickMenuRefreshTeam,
            onClickTeam = screenModel::onClickTeam,
        ) { screen ->
            navigator?.push(screen)
        }
    }
}

@Composable
fun TeamsListScreenContent(
    state: TeamsListScreenState,
    onClickMenuCreateTeam: () -> Unit,
    onClickMenuJoinTeam: () -> Unit,
    onClickSwipeToRefreshTeam: () -> Unit,
    onClickTeam: (TeamDetailsDomain) -> Unit,
    navigate: (Screen) -> Unit,
) {
    if (state.isCreating) {
        navigate(rememberScreen(SharedScreen.Team(isJoining = false, teamId = null)))
    }

    if (state.isJoining) {
        navigate(rememberScreen(SharedScreen.Team(isJoining = true, teamId = null)))
    }

    if (state.teamId != null) {
        navigate(rememberScreen(SharedScreen.Team(isJoining = false, teamId = state.teamId)))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TeamsScreenHeaderSection(
                    modifier = Modifier.fillMaxWidth(),
                    onClickMenuCreateTeam = onClickMenuCreateTeam,
                    onClickMenuJoinTeam = onClickMenuJoinTeam,
                )
            },
        ) { paddingValues ->

            if (state.teams.isEmpty()) {
                SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                    SafiInfoSection(
                        icon = Icons.Rounded.People,
                        title = "Empty",
                        description = "Join a team to continue",
                    )

                    Button(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .padding(horizontal = 16.dp),
                        onClick = onClickMenuCreateTeam,
                    ) {
                        Text(text = "Create Team")
                    }

                    OutlinedButton(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                        onClick = onClickMenuJoinTeam,
                    ) {
                        Text(text = "Join Team")
                    }
                }
            } else {
                LazyColumn(
                    modifier =
                        Modifier
                            .padding(top = paddingValues.calculateTopPadding())
                            .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(state.teams) { teamDetails ->
                        TeamComponent(
                            teamDetails = teamDetails,
                            onClickAction = { onClickTeam(teamDetails) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeamsScreenHeaderSection(
    onClickMenuCreateTeam: () -> Unit,
    onClickMenuJoinTeam: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
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
                    text = stringResource(SafiStringLabels.Teams).uppercase(Locale.getDefault()),
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
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }
    }
}
