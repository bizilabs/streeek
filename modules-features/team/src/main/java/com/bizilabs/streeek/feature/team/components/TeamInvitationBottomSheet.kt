package com.bizilabs.streeek.feature.team.components

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.PeopleAlt
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.bizilabs.streeek.feature.team.SnackBarType
import com.bizilabs.streeek.feature.team.TeamScreenState
import com.bizilabs.streeek.lib.common.components.paging.SafiPagingComponent
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.design.components.SafiSearchComponent
import com.bizilabs.streeek.lib.design.helpers.onSuccess
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.design.theme.SafiTheme
import com.bizilabs.streeek.lib.domain.models.team.AccountsNotInTeamDomain
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
    onClickRefreshInvitation: () -> Unit,
    onClickInvitationCreate: () -> Unit,
    onClickInvitationRetry: () -> Unit,
    onSwipeInvitationDelete: (TeamInvitationDomain) -> Unit,
    onSuccessOrErrorCodeCreation: (SnackBarType) -> Unit,
    modifier: Modifier = Modifier,
    accountsNotInTeam: LazyPagingItems<AccountsNotInTeamDomain>,
    onClickInviteAccount: (AccountsNotInTeamDomain) -> Unit,
    onSearchParamChanged: (String) -> Unit,
    onClickClearSearch: () -> Unit,
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
                            visible = state.codeInvitationsState == null || state.codeInvitationsState is FetchState.Success,
                        ) {
                            IconButton(onClick = onClickRefreshInvitation) {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = "refresh invitations list",
                                )
                            }
                        }
                    },
                )
            },
            snackbarHost = {
                AnimatedVisibility(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .padding(bottom = 96.dp),
                    visible = state.isInvitationSnackBarOpen,
                ) {
                    when (state.invitationSnackBarType) {
                        SnackBarType.SUCCESS -> {
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

                        SnackBarType.ERROR -> {
                            InfoCardComponent(
                                modifier = Modifier.fillMaxWidth(),
                                title = "Invite Code Error",
                                message = (state.codeInvitationsState as FetchState.Error).message,
                            ) {
                                Button(onClick = onClickInvitationCreate) {
                                    Text(text = "Retry")
                                }
                            }
                        }
                    }
                }
            },
        ) { innerPadding ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                verticalArrangement = Arrangement.Bottom,
            ) {
                Column(
                    modifier =
                        modifier
                            .fillMaxWidth(),
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))

                    SafiSearchComponent(
                        searchParam = state.searchParam,
                        onSearchParamChanged = onSearchParamChanged,
                        onClickClearSearch = onClickClearSearch,
                        placeholder = "Search for an account",
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                }

                SafiPagingComponent(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    data = accountsNotInTeam,
                    refreshEmpty = {
                        SafiCenteredColumn(modifier = Modifier.fillMaxWidth()) {
                            SafiInfoSection(
                                modifier = Modifier.fillMaxWidth(),
                                icon = Icons.Rounded.PeopleAlt,
                                title = if (state.searchParam.isNotEmpty()) "No results found" else "All app users are part of this team.",
                                description = if (state.searchParam.isNotEmpty()) "No results found for ${state.searchParam}" else "",
                            )
                        }
                    },
                ) { accountNotInTeam ->

                    val invited = accountNotInTeam.accountId in state.accountsInvitedIds
                    InviteAccountCardComponent(
                        modifier = modifier.fillMaxWidth(),
                        isInvited = invited,
                        accountNotInTeam = accountNotInTeam,
                        inviteAccountState = state.inviteAccountState,
                        onClickInvite = onClickInviteAccount,
                    )
                }

                // Generate Code section
                Column(
                    modifier =
                        Modifier
                            .background(color = MaterialTheme.colorScheme.surface)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(bottom = 24.dp),
                ) {
                    HorizontalDivider(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                    )
                    Text(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                        text = "Or Generate Code",
                        textAlign = TextAlign.Center,
                    )
                    AnimatedContent(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                        targetState = state.codeInvitationsState,
                        label = "animated_invitations",
                    ) { result ->
                        when (result) {
                            null -> {
                                SafiCenteredRow(
                                    modifier =
                                        Modifier
                                            .padding(horizontal = 16.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .padding(16.dp),
                                ) {
                                    Icon(
                                        modifier = Modifier.size(50.dp),
                                        imageVector = Icons.Rounded.People,
                                        contentDescription = "No Invitation code",
                                    )
                                    Column(
                                        modifier =
                                            Modifier
                                                .wrapContentHeight()
                                                .weight(1f)
                                                .padding(horizontal = 16.dp),
                                    ) {
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = "No invitations",
                                            maxLines = 1,
                                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                            fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
                                        )
                                        Text(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 4.dp),
                                            text = "You have no codes yet.",
                                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                            fontStyle = MaterialTheme.typography.labelSmall.fontStyle,
                                        )
                                    }
                                    Button(onClick = onClickInvitationCreate) {
                                        Text(text = "Generate")
                                    }
                                }
                            }

                            FetchState.Loading -> {
                                SafiCenteredColumn(
                                    modifier =
                                        Modifier
                                            .wrapContentHeight()
                                            .padding(24.dp),
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            is FetchState.Error -> {
                                SafiCenteredRow(
                                    modifier =
                                        Modifier
                                            .padding(16.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .padding(16.dp),
                                ) {
                                    Icon(
                                        modifier = Modifier.size(50.dp),
                                        imageVector = Icons.Rounded.People,
                                        contentDescription = "Error Getting Invitations",
                                    )
                                    Column(
                                        modifier =
                                            Modifier
                                                .wrapContentHeight()
                                                .weight(1f)
                                                .padding(horizontal = 16.dp),
                                    ) {
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = "Error Getting Invitations",
                                            maxLines = 1,
                                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                            fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
                                        )
                                        Text(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 4.dp),
                                            text = result.message,
                                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                            fontStyle = MaterialTheme.typography.labelSmall.fontStyle,
                                        )
                                    }
                                    Button(onClick = onClickInvitationRetry) {
                                        Text(text = "Retry")
                                    }
                                }
                            }

                            is FetchState.Success -> {
                                TeamInvitationsSection(
                                    activity = activity,
                                    state = state,
                                    result = result,
                                    onSwipeInvitationDelete = onSwipeInvitationDelete,
                                    onClickInvitationCreate = onClickInvitationCreate,
                                    onSuccessOrErrorCodeCreation = onSuccessOrErrorCodeCreation,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamInvitationsSection(
    modifier: Modifier = Modifier,
    activity: Activity,
    state: TeamScreenState,
    result: FetchState.Success<TeamInvitationDomain>,
    onSwipeInvitationDelete: (TeamInvitationDomain) -> Unit,
    onClickInvitationCreate: () -> Unit,
    onSuccessOrErrorCodeCreation: (SnackBarType) -> Unit,
) {
    val team = (state.fetchState as? FetchState.Success)?.value?.team
    val invite = result.value

    Column(modifier = modifier.wrapContentHeight()) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = state.isLoadingInvitationsPartially,
        ) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        TeamInviteCardComponent(
            modifier = Modifier.fillMaxWidth(),
            invite = invite,
            state = state,
            onSwipeInvitationDelete = { onSwipeInvitationDelete(invite) },
            onClickInvitationCreate = onClickInvitationCreate,
            onSuccessOrErrorCodeCreation = onSuccessOrErrorCodeCreation,
        ) {
            activity.share(teamName = team?.name ?: "The Unknowns", teamCode = invite.code)
        }
    }
}

@Composable
fun TeamInviteCardComponent(
    invite: TeamInvitationDomain,
    state: TeamScreenState,
    modifier: Modifier = Modifier,
    onSwipeInvitationDelete: () -> Unit,
    onClickInvitationCreate: () -> Unit,
    onSuccessOrErrorCodeCreation: (SnackBarType) -> Unit,
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
                        text = invite.code,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text =
                            buildString {
                                append("Expires in ")
                                append(
                                    state.expiryTimeToNow,
                                )
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

                AnimatedContent(
                    modifier =
                        Modifier
                            .padding(16.dp),
                    targetState = state.createInvitationState,
                    label = "animate create invitation",
                ) { result ->
                    when (result) {
                        FetchState.Loading -> {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier
                                        .size(48.dp)
                                        .padding(16.dp),
                            )
                        }

                        is FetchState.Error -> {
                            onSuccessOrErrorCodeCreation(SnackBarType.ERROR)

                            IconButton(enabled = false, onClick = onClickInvitationCreate) {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = "Regenerate a code",
                                )
                            }
                        }

                        is FetchState.Success -> {
                            onSuccessOrErrorCodeCreation(SnackBarType.SUCCESS)
                            IconButton(enabled = false, onClick = onClickInvitationCreate) {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = "Regenerate a code",
                                )
                            }
                        }

                        null -> {
                            IconButton(onClick = onClickInvitationCreate) {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = "Regenerate a code",
                                )
                            }
                        }
                    }
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
