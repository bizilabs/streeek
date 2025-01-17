package com.bizilabs.streeek.feature.tabs.screens.teams.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.People
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.bizilabs.streeek.feature.tabs.screens.teams.TeamsListScreenState
import com.bizilabs.streeek.lib.common.components.paging.SafiPagingComponent
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCircularProgressIndicator
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
                Card(
                    modifier =
                        Modifier
                            .padding(vertical = 8.dp)
                            .padding(horizontal = 16.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        ),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = team.team.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(modifier = Modifier.padding(8.dp)) {
                                    for (member in team.members) {
                                        val start =
                                            0.dp + (team.members.indexOf(member).times(20).dp)
                                        Card(
                                            modifier = Modifier.padding(start = start),
                                            shape = CircleShape,
                                            border =
                                                BorderStroke(
                                                    2.dp,
                                                    MaterialTheme.colorScheme.onBackground,
                                                ),
                                        ) {
                                            AsyncImage(
                                                model = member.avatarUrl,
                                                contentDescription = null,
                                                modifier = Modifier.size(36.dp),
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text =
                                        buildString {
                                            append(team.team.count)
                                            append(" ")
                                            append("member".asCount(team.team.count))
                                        },
                                )
                            }
                        }
                        val requested = team.team.id in state.requestedTeamIds
                        Button(
                            modifier = Modifier.padding(8.dp),
                            onClick = { onClickTeamRequest(team) },
                            enabled = state.requestState == null && !requested,
                        ) {
                            AnimatedContent(
                                label = "",
                                targetState = state.requestState,
                            ) { request ->
                                when (request) {
                                    null -> {
                                        Text(
                                            text = if (requested) "Requested" else "Request",
                                            style = MaterialTheme.typography.labelSmall,
                                        )
                                    }

                                    else -> {
                                        when {
                                            request.teamId == team.team.id -> {
                                                when (request.requestState) {
                                                    FetchState.Loading -> SafiCircularProgressIndicator()
                                                    is FetchState.Error -> {
                                                        Icon(
                                                            imageVector = Icons.Rounded.Error,
                                                            contentDescription = "",
                                                        )
                                                    }

                                                    is FetchState.Success -> {
                                                        Icon(
                                                            imageVector = Icons.Rounded.CheckCircle,
                                                            contentDescription = "",
                                                        )
                                                    }
                                                }
                                            }

                                            else -> {
                                                Text(
                                                    text = if (requested) "Requested" else "Request",
                                                    style = MaterialTheme.typography.labelSmall,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
