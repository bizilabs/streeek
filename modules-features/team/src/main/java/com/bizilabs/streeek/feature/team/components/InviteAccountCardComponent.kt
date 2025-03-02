package com.bizilabs.streeek.feature.team.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bizilabs.streeek.feature.team.InviteAccountState
import com.bizilabs.streeek.feature.team.TeamScreenState
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.SafiCircularProgressIndicator
import com.bizilabs.streeek.lib.design.helpers.onSuccess
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.design.theme.SafiTheme
import com.bizilabs.streeek.lib.domain.helpers.SystemLocalDateTime
import com.bizilabs.streeek.lib.domain.models.team.AccountsNotInTeamDomain

@Composable
fun InviteAccountCardComponent(
    modifier: Modifier = Modifier,
    state: TeamScreenState,
    isInvited: Boolean,
    accountNotInTeam: AccountsNotInTeamDomain,
    inviteAccountState: InviteAccountState?,
    onClickInvite: (AccountsNotInTeamDomain) -> Unit,
    onClickAccountAvatar: (AccountsNotInTeamDomain) -> Unit,
) {
    val clickable = state.inviteMultipleAccountsState == null && !isInvited
    Column(
        modifier = modifier,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 80.dp)
                    .clickable(enabled = clickable) { onClickAccountAvatar(accountNotInTeam) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimatedContent(
                    modifier =
                        Modifier
                            .clip(CircleShape)
                            .clickable(enabled = clickable) { onClickAccountAvatar(accountNotInTeam) },
                    label = "Animate Account Selection",
                    targetState = state.selectedAccountsIds.contains(accountNotInTeam.accountId),
                ) { isSelected ->
                    when (isSelected) {
                        false -> {
                            AsyncImage(
                                modifier =
                                    Modifier
                                        .size(36.dp)
                                        .clip(CircleShape),
                                model = accountNotInTeam.avatarUrl,
                                contentDescription = "user avatar url",
                                contentScale = ContentScale.Crop,
                            )
                        }

                        true -> {
                            Box(
                                modifier =
                                    Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.success)
                                        .size(36.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    tint = MaterialTheme.colorScheme.onSuccess,
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = "Check",
                                )
                            }
                        }
                    }
                }
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = accountNotInTeam.username,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (!clickable) MaterialTheme.colorScheme.onSurface.copy(0.5f) else MaterialTheme.colorScheme.onSurface,
                )
            }
            // Fetch states for multi-invites and if it's part of that
            AnimatedVisibility(
                visible = state.selectedAccountsIds.isEmpty(),
            ) {
                when (isInvited) {
                    true -> {
                        Row(
                            modifier = Modifier.padding(vertical = 20.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "Invited",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                            )
                        }
                    }

                    false -> {
                        OutlinedButton(
                            modifier = Modifier.padding(8.dp),
                            enabled = inviteAccountState == null,
                            onClick = { onClickInvite(accountNotInTeam) },
                        ) {
                            AnimatedContent(
                                label = "",
                                targetState = inviteAccountState,
                            ) { inviteAccountState ->

                                when (inviteAccountState) {
                                    null -> { // No invite in progress
                                        Text(
                                            text = "Invite",
                                            style = MaterialTheme.typography.labelSmall,
                                        )
                                    }

                                    else -> {
                                        when {
                                            inviteAccountState.accountId == accountNotInTeam.accountId -> {
                                                when (inviteAccountState.inviteState) {
                                                    FetchState.Loading -> {
                                                        SafiCircularProgressIndicator()
                                                    }

                                                    is FetchState.Error -> {
                                                        Icon(
                                                            imageVector = Icons.Rounded.Error,
                                                            contentDescription = "",
                                                            tint = MaterialTheme.colorScheme.error,
                                                        )
                                                    }

                                                    is FetchState.Success -> {
                                                        Icon(
                                                            imageVector = Icons.Rounded.CheckCircle,
                                                            contentDescription = "",
                                                            tint = MaterialTheme.colorScheme.success,
                                                        )
                                                    }
                                                }
                                            }

                                            else -> {
                                                Text(
                                                    text = "Invite",
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

            AnimatedVisibility(
                visible = state.selectedAccountsIds.isNotEmpty(),
            ) {
                if (state.selectedAccountsIds.contains(accountNotInTeam.accountId)) {
                    when (state.inviteMultipleAccountsState?.multipleInvitesState) {
                        FetchState.Loading -> {
                            SafiCircularProgressIndicator()
                        }

                        is FetchState.Error -> {
                            Icon(
                                imageVector = Icons.Rounded.Error,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }

                        is FetchState.Success -> {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.success,
                            )
                        }

                        null -> {
                        }
                    }
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InviteAccountCardPreview() {
    SafiTheme {
        Surface {
            InviteAccountCardComponent(
                modifier = Modifier,
                state = TeamScreenState(),
                accountNotInTeam =
                    AccountsNotInTeamDomain(
                        accountId = 1L,
                        username = "Dummy Name",
                        avatarUrl = "",
                        createdAt = SystemLocalDateTime,
                        isInvited = false,
                    ),
                onClickInvite = {},
                inviteAccountState = null,
                isInvited = false,
                onClickAccountAvatar = {},
            )
        }
    }
}
