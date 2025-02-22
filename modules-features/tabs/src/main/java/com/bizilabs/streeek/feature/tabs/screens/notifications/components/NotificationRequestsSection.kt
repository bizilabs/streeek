package com.bizilabs.streeek.feature.tabs.screens.notifications.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.bizilabs.streeek.feature.tabs.screens.notifications.NotificationsScreenState
import com.bizilabs.streeek.lib.common.components.paging.SafiPagingComponent
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCircularProgressIndicator
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.design.components.SafiRefreshBox
import com.bizilabs.streeek.lib.domain.extensions.asCount
import com.bizilabs.streeek.lib.domain.models.team.MemberAccountRequestDomain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationRequestsSection(
    state: NotificationsScreenState,
    data: LazyPagingItems<MemberAccountRequestDomain>,
    modifier: Modifier = Modifier,
    onClickCancelRequest: (MemberAccountRequestDomain) -> Unit,
) {
    SafiRefreshBox(
        modifier = modifier,
        isRefreshing = data.loadState.refresh is LoadState.Loading,
        onRefresh = { data.refresh() },
    ) {
        SafiPagingComponent(
            modifier = Modifier.fillMaxSize(),
            data = data,
            refreshEmpty = {
                SafiCenteredColumn(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                ) {
                    SafiInfoSection(
                        icon = Icons.Rounded.People,
                        title = "No Requests Currently",
                        description = "",
                    ) {
                    }
                }
            },
        ) { value ->
            JoinRequestComponent(value, state, onClickCancelRequest)
        }
    }
}

@Composable
private fun JoinRequestComponent(
    value: MemberAccountRequestDomain,
    state: NotificationsScreenState,
    onClickCancelRequest: (MemberAccountRequestDomain) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        for (member in value.members) {
                            val start =
                                0.dp + (value.members.indexOf(member).times(20).dp)
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
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(
                            text = value.team.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text =
                                buildString {
                                    append(value.team.count)
                                    append(" ")
                                    append("member".asCount(value.team.count))
                                },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                }
            }
            val requested = value.team.id in state.cancelledTeamIds
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = { onClickCancelRequest(value) },
                enabled = state.requestState == null && !requested,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
            ) {
                AnimatedContent(
                    label = "",
                    targetState = state.requestState,
                ) { request ->
                    when (request) {
                        null -> {
                            Text(
                                text = if (requested) "Cancelled" else "Cancel",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }

                        else -> {
                            when {
                                request.teamId == value.team.id -> {
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
                                        text = if (requested) "Cancelled" else "Cancel",
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider()
    }
}
