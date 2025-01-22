package com.bizilabs.streeek.lib.common.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.SafiCircularProgressIndicator

@Composable
fun TeamItemComponent(
    teamId: Long,
    teamName: String,
    teamCount: Long,
    teamCountLabel: String,
    requested: Boolean,
    enabled: Boolean,
    requestTeamId: Long?,
    requestState: FetchState<Boolean>?,
    membersAvatarUrl: List<String>,
    modifier: Modifier = Modifier,
    onClickTeamRequest: () -> Unit,
) {
    Card(
        modifier = modifier,
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
                    text = teamName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.padding(8.dp)) {
                        for (member in membersAvatarUrl) {
                            val start = 0.dp + (membersAvatarUrl.indexOf(member).times(20).dp)
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
                                    model = member,
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp),
                                )
                            }
                        }
                    }
                    Text(
                        text =
                            buildString {
                                append(teamCount)
                                append(" ")
                                append(teamCountLabel)
                            },
                    )
                }
            }
            OutlinedButton(
                modifier = Modifier.padding(8.dp),
                onClick = { onClickTeamRequest() },
                enabled = enabled,
            ) {
                AnimatedContent(
                    label = "",
                    targetState = requestState,
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
                                requestTeamId == teamId -> {
                                    when (request) {
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
