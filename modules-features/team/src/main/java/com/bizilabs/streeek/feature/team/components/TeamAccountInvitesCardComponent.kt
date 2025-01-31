package com.bizilabs.streeek.feature.team.components

import android.content.res.Configuration
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
import com.bizilabs.streeek.feature.team.InviteWithdrawalState
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.SafiCircularProgressIndicator
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.design.theme.SafiTheme
import com.bizilabs.streeek.lib.domain.helpers.SystemLocalDateTime
import com.bizilabs.streeek.lib.domain.helpers.toTimeAgo
import com.bizilabs.streeek.lib.domain.models.team.TeamAccountInvitesDomain

@Composable
fun InvitedAccountCardComponent(
    modifier: Modifier = Modifier,
    isWithdrawn: Boolean,
    accountInvite: TeamAccountInvitesDomain,
    inviteWithdrawalState: InviteWithdrawalState?,
    onClickWithdraw: (TeamAccountInvitesDomain) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
                    model = accountInvite.avatarUrl,
                    contentDescription = "user avatar url",
                    contentScale = ContentScale.Crop,
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = accountInvite.username,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = "Invited: ${accountInvite.invitedOn.toTimeAgo()}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                    )
                }
            }
            when (isWithdrawn) {
                true -> {
                    Row(
                        modifier = Modifier.padding(vertical = 20.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Withdrawn",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                        )
                    }
                }

                false -> {
                    OutlinedButton(
                        modifier = Modifier.padding(8.dp),
                        enabled = inviteWithdrawalState == null,
                        onClick = { onClickWithdraw(accountInvite) },
                    ) {
                        AnimatedContent(
                            label = "",
                            targetState = inviteWithdrawalState,
                        ) { inviteWithdrawalState ->

                            when (inviteWithdrawalState) {
                                null -> { // No invite in progress
                                    Text(
                                        text = "Withdraw",
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                }

                                else -> {
                                    when {
                                        inviteWithdrawalState.inviteId == accountInvite.inviteId -> {
                                            when (inviteWithdrawalState.withdrawalState) {
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
                                                text = "Withdraw",
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
        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InvitedAccountCardComponentPreview() {
    SafiTheme {
        Surface {
            InvitedAccountCardComponent(
                modifier = Modifier,
                isWithdrawn = false,
                accountInvite =
                    TeamAccountInvitesDomain(
                        inviteId = 1L,
                        inviteeId = 2L,
                        username = "Dummy Name",
                        avatarUrl = "",
                        invitedOn = SystemLocalDateTime,
                    ),
                inviteWithdrawalState = null,
                onClickWithdraw = { },
            )
        }
    }
}
