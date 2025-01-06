package com.bizilabs.streeek.feature.team.components

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.feature.team.TeamScreenState
import com.bizilabs.streeek.lib.common.models.FetchListState
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.design.helpers.onSuccess
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.design.theme.SafiTheme
import com.bizilabs.streeek.lib.domain.models.team.TeamInvitationDomain
import com.bizilabs.streeek.lib.resources.strings.SafiStrings
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamInvitationBottomSheet(
    activity: Activity,
    state: TeamScreenState,
    onDismissSheet: () -> Unit,
    onClickInvitationGet: () -> Unit,
    onClickInvitationCreate: () -> Unit,
    onClickInvitationRetry: () -> Unit,
    onSwipeInvitationDelete: (TeamInvitationDomain) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        onDismissRequest = {
            scope.launch { if (sheetState.isVisible) sheetState.hide() }
            onDismissSheet()
        },
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onDismissSheet) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "close invitations sheet",
                            )
                        }
                    },
                    title = { Text(text = "Invitations") },
                    actions = {
                        AnimatedVisibility(
                            visible = state.invitationsState is FetchListState.Empty || state.invitationsState is FetchListState.Success,
                        ) {
                            IconButton(onClick = onClickInvitationGet) {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = "refresh invitations list",
                                )
                            }
                        }
                    },
                )
            },
        ) { innerPadding ->
            AnimatedContent(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                targetState = state.invitationsState,
                label = "animated_invitations",
            ) { result ->
                when (result) {
                    FetchListState.Empty -> {
                        SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                            SafiInfoSection(
                                icon = Icons.Rounded.People,
                                title = "No invitations",
                                description = "You have no invitations",
                                action = {
                                    Button(onClick = onClickInvitationCreate) {
                                        Text(text = "Generate")
                                    }
                                },
                            )
                        }
                    }

                    FetchListState.Loading -> {
                        SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator()
                        }
                    }

                    is FetchListState.Error -> {
                        SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                            SafiInfoSection(
                                icon = Icons.Rounded.People,
                                title = "Error Getting Invitations",
                                description = result.message,
                                action = {
                                    Button(onClick = onClickInvitationRetry) {
                                        Text(text = "Retry")
                                    }
                                },
                            )
                        }
                    }

                    is FetchListState.Success -> {
                        TeamInvitationsSection(
                            activity = activity,
                            state = state,
                            result = result,
                            onSwipeInvitationDelete = onSwipeInvitationDelete,
                            onClickInvitationCreate = onClickInvitationCreate,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamInvitationsSection(
    activity: Activity,
    state: TeamScreenState,
    result: FetchListState.Success<TeamInvitationDomain>,
    onSwipeInvitationDelete: (TeamInvitationDomain) -> Unit,
    onClickInvitationCreate: () -> Unit,
) {
    val team = (state.fetchState as? FetchState.Success)?.value?.team

    Column(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = state.isLoadingInvitationsPartially,
        ) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) {
            items(result.list) { invite ->
                TeamInviteCardComponent(
                    modifier = Modifier.fillMaxWidth(),
                    invite = invite,
                    onSwipeInvitationDelete = { onSwipeInvitationDelete(invite) },
                ) {
                    activity.share(teamName = team?.name ?: "The Unknowns", teamCode = invite.code)
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            AnimatedContent(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                targetState = state.createInvitationState,
                label = "animate create invitation",
            ) { result ->
                when (result) {
                    FetchState.Loading -> {
                        SafiCenteredRow(modifier = Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier
                                        .size(48.dp)
                                        .padding(16.dp),
                            )
                        }
                    }

                    is FetchState.Error -> {
                        InfoCardComponent(
                            modifier = Modifier.fillMaxWidth(),
                            title = "Invite Code Error",
                            message = result.message,
                        ) {
                            Button(onClick = onClickInvitationCreate) {
                                Text(text = "Retry")
                            }
                        }
                    }

                    is FetchState.Success -> {
                        InfoCardComponent(
                            modifier = Modifier.fillMaxWidth(),
                            title = "Invite Code Success",
                            message = "Invite code created successfully",
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.success,
                                    contentColor = MaterialTheme.colorScheme.onSuccess,
                                ),
                        ) {
                        }
                    }

                    null -> {
                        Button(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            onClick = onClickInvitationCreate,
                        ) {
                            Text(text = "Generate")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamInviteCardComponent(
    invite: TeamInvitationDomain,
    modifier: Modifier = Modifier,
    onSwipeInvitationDelete: () -> Unit,
    onClickShare: () -> Unit,
) {
    val delete =
        SwipeAction(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "delete invitation",
                    tint = MaterialTheme.colorScheme.error,
                )
            },
            background = MaterialTheme.colorScheme.background,
            onSwipe = onSwipeInvitationDelete,
        )

    SwipeableActionsBox(
        modifier = modifier,
        startActions = listOf(delete),
        endActions = listOf(delete),
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
        ) {
            SafiCenteredRow(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
            ) {
                Column {
                    Text(
                        text = "${invite.code}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text =
                            buildString {
                                append("Expires on ")
                                append(
                                    invite.expiresAt.dayOfWeek.name
                                        .lowercase()
                                        .replaceFirstChar { it.uppercase() },
                                )
                                append(" , ")
                                append(invite.expiresAt.dayOfMonth)
                                append(" ")
                                append(
                                    invite.expiresAt.month.name
                                        .lowercase()
                                        .replaceFirstChar { it.uppercase() },
                                )
                                append(" ")
                                append(invite.expiresAt.year)
                            },
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = onClickShare) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = "share invite code",
                    )
                }
            }
        }
    }
}

private fun Activity.share(
    teamName: String,
    teamCode: String,
) {
    val sendIntent: Intent =
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                getString(SafiStrings.Messages.MessageTeamInvite, teamCode, teamName, teamCode),
            )
            type = "text/plain"
        }
    val shareIntent = Intent.createChooser(sendIntent, "Share with")
    startActivity(shareIntent)
}

@Composable
fun InfoCardComponent(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    action: @Composable () -> Unit,
) {
    Card(
        modifier = modifier,
        colors = colors,
        elevation = elevation,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = message,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            action()
        }
    }
}

@Preview
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InfoCardComponentLightPreview() {
    SafiTheme {
        Surface {
            InfoCardComponent(
                modifier = Modifier.padding(16.dp),
                title = "Error",
                message = "Something went wrong",
                action = {
                    TextButton(onClick = { }) {
                        Text(text = "Retry")
                    }
                },
            )
        }
    }
}
