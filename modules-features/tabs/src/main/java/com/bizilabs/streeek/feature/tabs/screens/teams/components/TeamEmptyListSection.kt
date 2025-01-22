package com.bizilabs.streeek.feature.tabs.screens.teams.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.People
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.bizilabs.streeek.feature.tabs.screens.teams.TeamsListScreenState
import com.bizilabs.streeek.lib.common.components.TeamItemComponent
import com.bizilabs.streeek.lib.common.components.paging.SafiPagingComponent
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiRefreshBox
import com.bizilabs.streeek.lib.domain.extensions.asCount
import com.bizilabs.streeek.lib.domain.models.TeamAndMembersDomain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamEmptyListSection(
    modifier: Modifier = Modifier,
    state: TeamsListScreenState,
    availableTeams: LazyPagingItems<TeamAndMembersDomain>,
    onClickTeamRequest: (TeamAndMembersDomain) -> Unit,
    onClickCreateTeam: () -> Unit,
) {
    Column(modifier = modifier) {
        SafiRefreshBox(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = availableTeams.loadState.refresh is LoadState.Loading,
            onRefresh = { availableTeams.refresh() },
        ) {
            SafiPagingComponent(
                modifier = Modifier.fillMaxSize(),
                data = availableTeams,
                prependSuccess = {
                    SafiCenteredColumn(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            modifier =
                                Modifier
                                    .padding(top = 16.dp)
                                    .size(48.dp),
                            imageVector = Icons.Rounded.People,
                            contentDescription = "teams",
                        )
                        Text(
                            modifier = Modifier.padding(vertical = 16.dp),
                            text = "You don't have any teams yet.\nRequest to join public teams to join the challenge.",
                            textAlign = TextAlign.Center,
                        )

                        Button(
                            modifier = Modifier.padding(bottom = 16.dp),
                            onClick = onClickCreateTeam,
                        ) {
                            Text(text = "Create")
                        }

                        HorizontalDivider()
                    }
                },
            ) { team ->
                val requested = team.team.id in state.requestedTeamIds
                TeamItemComponent(
                    modifier =
                        Modifier
                            .padding(vertical = 8.dp)
                            .padding(horizontal = 16.dp),
                    teamId = team.team.id,
                    teamName = team.team.name,
                    teamCount = team.team.count,
                    teamCountLabel = "member".asCount(team.team.count),
                    requested = requested,
                    enabled = state.requestState == null && !requested,
                    requestTeamId = state.requestState?.teamId,
                    requestState = state.requestState?.requestState,
                    membersAvatarUrl = team.members.map { it.avatarUrl },
                ) { onClickTeamRequest(team) }
            }
        }
    }
}
