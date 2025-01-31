package com.bizilabs.streeek.feature.join.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bizilabs.streeek.feature.join.JoinScreenState
import com.bizilabs.streeek.feature.join.TeamInviteAction
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.SafiCircleDecoratorComponent
import com.bizilabs.streeek.lib.design.components.SafiCircularProgressIndicator
import com.bizilabs.streeek.lib.design.helpers.onSuccess
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.domain.helpers.toTimeAgo
import com.bizilabs.streeek.lib.domain.models.team.AccountTeamInvitesDomain

@Composable
fun AccountTeamInviteComponent(
    modifier: Modifier = Modifier,
    state: JoinScreenState,
    accountTeamInvite: AccountTeamInvitesDomain,
    onClickProcessInvite: (TeamInviteAction, AccountTeamInvitesDomain) -> Unit,
) {
    val isProcessed = accountTeamInvite.invite.inviteId in state.processedInvites.values.flatten()
    val inviteState = state.singleInviteState[accountTeamInvite.invite.inviteId]
    Column(
        modifier = modifier,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    modifier =
                        Modifier
                            .size(36.dp)
                            .clip(CircleShape),
                    model = accountTeamInvite.teamOwner.avatarUrl,
                    contentDescription = "user avatar url",
                    contentScale = ContentScale.Crop,
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        modifier = Modifier,
                        text = accountTeamInvite.team.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier,
                            text = accountTeamInvite.teamOwner.username,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                        )

                        SafiCircleDecoratorComponent()

                        Text(
                            modifier = Modifier,
                            text = accountTeamInvite.invite.createdAt.toTimeAgo(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                        )
                    }
                }
            }
            when (isProcessed) {
                true -> {
                    val inviteAction =
                        state.processedInvites.entries.find { accountTeamInvite.invite.inviteId in it.value }?.key

                    val buttonText =
                        when (inviteAction) {
                            TeamInviteAction.ACCEPT -> {
                                inviteAction.value
                            }
                            TeamInviteAction.REJECT -> {
                                inviteAction.value
                            }
                            null -> ""
                        }
                    Row(
                        modifier = Modifier.padding(vertical = 20.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = buttonText,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                        )
                    }
                }

                false -> {
                    AnimatedContent(
                        label = "Animate invite actions",
                        targetState = inviteState,
                    ) { singleInviteState ->
                        when (singleInviteState) {
                            is FetchState.Error -> {
                                Icon(
                                    imageVector = Icons.Rounded.Error,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            }

                            FetchState.Loading -> SafiCircularProgressIndicator()
                            is FetchState.Success -> {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.success,
                                )
                            }

                            null -> {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    SmallFloatingActionButton(
                                        modifier = Modifier,
                                        onClick = {
                                            onClickProcessInvite(
                                                TeamInviteAction.ACCEPT,
                                                accountTeamInvite,
                                            )
                                        },
                                        containerColor = MaterialTheme.colorScheme.success,
                                        contentColor = MaterialTheme.colorScheme.onSuccess,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.DoneAll,
                                            contentDescription = "accept",
                                        )
                                    }
                                    SmallFloatingActionButton(
                                        modifier = Modifier,
                                        onClick = {
                                            onClickProcessInvite(
                                                TeamInviteAction.REJECT,
                                                accountTeamInvite,
                                            )
                                        },
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = "decline",
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
    }
}
