package com.bizilabs.streeek.feature.team.section

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material.icons.rounded.PeopleAlt
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.bizilabs.streeek.feature.team.SelectionAction
import com.bizilabs.streeek.feature.team.TeamJoinersTab
import com.bizilabs.streeek.feature.team.TeamRequestAction
import com.bizilabs.streeek.feature.team.TeamScreenState
import com.bizilabs.streeek.feature.team.components.InvitedAccountCardComponent
import com.bizilabs.streeek.lib.common.components.paging.SafiPagingComponent
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.design.components.SafiCircularProgressIndicator
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.design.components.SafiTopBarHeader
import com.bizilabs.streeek.lib.design.helpers.onSuccess
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.domain.models.team.TeamAccountInvitesDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamAccountJoinRequestDomain
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamJoinRequestsBottomSheet(
    state: TeamScreenState,
    requestsData: LazyPagingItems<TeamAccountJoinRequestDomain>,
    teamAccountsInvites: LazyPagingItems<TeamAccountInvitesDomain>,
    modifier: Modifier = Modifier,
    onDismissSheet: () -> Unit,
    onClickToggleSelectRequest: (TeamAccountJoinRequestDomain) -> Unit,
    onClickProcessSelectedRequests: (Boolean) -> Unit,
    onClickSelectedRequestsSelection: (SelectionAction, List<TeamAccountJoinRequestDomain>) -> Unit,
    onClickProcessRequest: (TeamAccountJoinRequestDomain, TeamRequestAction) -> Unit,
    onClickWithdraw: (TeamAccountInvitesDomain) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pagerState =
        rememberPagerState(
            initialPage = state.joinerTabs.indexOf(state.joinerTabs.first()),
        ) { state.joinerTabs.size }

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
            topBar = {
                TeamJoinersSheetHeader(
                    modifier = Modifier,
                    state = state,
                    onDismissSheet = onDismissSheet,
                    requestsData = requestsData,
                    invitesData = teamAccountsInvites,
                    pagerState = pagerState,
                )
            },
        ) { innerPadding ->
            HorizontalPager(
                modifier = Modifier.fillMaxWidth(),
                state = pagerState,
            ) { pageIndex ->
                when (state.joinerTabs[pageIndex]) {
                    TeamJoinersTab.REQUESTS -> {
                        Column(
                            modifier =
                                Modifier
                                    .padding(innerPadding)
                                    .fillMaxSize(),
                        ) {
                            AnimatedVisibility(
                                modifier = Modifier.fillMaxWidth(),
                                visible = state.selectedRequestIds.isNotEmpty(),
                            ) {
                                Row(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(text = "${state.selectedRequestIds.size} selected")
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        TextButton(
                                            onClick = {
                                                onClickSelectedRequestsSelection(
                                                    SelectionAction.SELECT_ALL,
                                                    requestsData.itemSnapshotList.items,
                                                )
                                            },
                                            colors =
                                                ButtonDefaults.textButtonColors(
                                                    contentColor = MaterialTheme.colorScheme.success,
                                                ),
                                        ) {
                                            Text(text = "Select All (${requestsData.itemCount})")
                                        }
                                        TextButton(
                                            onClick = {
                                                onClickSelectedRequestsSelection(
                                                    SelectionAction.CLEAR_ALL,
                                                    listOf(),
                                                )
                                            },
                                            colors =
                                                ButtonDefaults.textButtonColors(
                                                    contentColor = MaterialTheme.colorScheme.error,
                                                ),
                                        ) {
                                            Text(text = "Clear (${state.selectedRequestIds.size})")
                                        }
                                    }
                                }
                            }
                            SafiPagingComponent(
                                data = requestsData,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                refreshEmpty = {
                                    SafiCenteredColumn(modifier = Modifier.fillMaxWidth()) {
                                        SafiInfoSection(
                                            icon = Icons.Rounded.Inbox,
                                            title = "No New Requests",
                                            description = "",
                                        )
                                    }
                                },
                            ) {
                                TeamJoinRequestCard(
                                    state = state,
                                    modifier = Modifier.fillMaxWidth(),
                                    request = it,
                                    onClickToggleSelectRequest = { onClickToggleSelectRequest(it) },
                                    onClickProcessRequest = onClickProcessRequest,
                                )
                            }
                            AnimatedVisibility(
                                visible = state.selectedRequestIds.isNotEmpty(),
                            ) {
                                Row(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Button(
                                        enabled = state.processingMultipleRequestsState == null,
                                        modifier = Modifier.weight(1f),
                                        onClick = { onClickProcessSelectedRequests(true) },
                                        colors =
                                            ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.success,
                                                contentColor = MaterialTheme.colorScheme.onSuccess,
                                            ),
                                    ) {
                                        Text(text = "Accept")
                                    }
                                    Spacer(modifier = Modifier.padding(horizontal = 16.dp))
                                    Button(
                                        enabled = state.processingMultipleRequestsState == null,
                                        modifier = Modifier.weight(1f),
                                        onClick = { onClickProcessSelectedRequests(false) },
                                        colors =
                                            ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error,
                                                contentColor = MaterialTheme.colorScheme.onError,
                                            ),
                                    ) {
                                        Text(text = "Decline")
                                    }
                                }
                            }
                        }
                    }

                    TeamJoinersTab.INVITES -> {
                        Column(
                            modifier =
                                Modifier
                                    .padding(innerPadding)
                                    .fillMaxSize(),
                        ) {
                            SafiPagingComponent(
                                data = teamAccountsInvites,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                refreshEmpty = {
                                    SafiCenteredColumn(modifier = Modifier.fillMaxWidth()) {
                                        SafiInfoSection(
                                            modifier = Modifier.fillMaxWidth(),
                                            icon = Icons.Rounded.PeopleAlt,
                                            title = "No Team Invites",
                                            description = "You have not invited anyone to team yet.",
                                        )
                                    }
                                },
                            ) {
                                val isWithdrawn = it.inviteId in state.withdrawnInvitesIds

                                InvitedAccountCardComponent(
                                    modifier = modifier.fillMaxWidth(),
                                    isWithdrawn = isWithdrawn,
                                    accountInvite = it,
                                    inviteWithdrawalState = state.inviteWithdrawalState,
                                    onClickWithdraw = onClickWithdraw,
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
fun TeamJoinRequestCard(
    state: TeamScreenState,
    request: TeamAccountJoinRequestDomain,
    modifier: Modifier = Modifier,
    onClickToggleSelectRequest: () -> Unit,
    onClickProcessRequest: (TeamAccountJoinRequestDomain, TeamRequestAction) -> Unit,
) {
    val account = request.account
    val processing =
        if (state.processingMultipleRequestsState?.requestIds?.contains(request.request.id) == true) {
            state.processingMultipleRequestsState.fetchState
        } else {
            null
        }
    val fetchState = state.processingSingleRequestsState[request.request.id] ?: processing
    val isAlreadyProcessed = state.processedRequests.values.flatten().contains(request.request.id)
    Column(
        modifier = modifier.clickable(enabled = fetchState == null && isAlreadyProcessed.not()) { onClickToggleSelectRequest() },
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Checkbox(
                enabled = fetchState == null && isAlreadyProcessed.not(),
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .clip(CircleShape),
                checked = state.selectedRequestIds.contains(request.request.id),
                onCheckedChange = { onClickToggleSelectRequest() },
                colors =
                    CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.success,
                        uncheckedColor = MaterialTheme.colorScheme.onBackground,
                        checkmarkColor = MaterialTheme.colorScheme.onSuccess,
                    ),
            )
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    modifier =
                        Modifier
                            .size(36.dp)
                            .clip(CircleShape),
                    model = account.avatarUrl,
                    contentDescription = "user avatar url",
                    contentScale = ContentScale.Crop,
                )
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = account.username,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            AnimatedContent(
                label = "animate process actions",
                targetState = fetchState,
            ) { fetch ->
                when (fetch) {
                    FetchState.Loading -> {
                        SafiCircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }

                    is FetchState.Error -> {
                        Icon(
                            modifier = Modifier.padding(16.dp),
                            imageVector = Icons.Rounded.Error,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }

                    is FetchState.Success -> {
                        Icon(
                            modifier = Modifier.padding(16.dp),
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.success,
                        )
                    }

                    null -> {
                        AnimatedContent(
                            label = "animated processed",
                            targetState = isAlreadyProcessed,
                        ) { processed ->
                            when (processed) {
                                true -> {
                                    Icon(
                                        modifier = Modifier.padding(16.dp),
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.success,
                                    )
                                }

                                false -> {
                                    AnimatedVisibility(
                                        visible = state.selectedRequestIds.isEmpty() && !isAlreadyProcessed,
                                        enter = fadeIn(),
                                        exit = fadeOut(),
                                    ) {
                                        Row {
                                            SmallFloatingActionButton(
                                                modifier = Modifier.padding(end = 16.dp),
                                                onClick = {
                                                    onClickProcessRequest(
                                                        request,
                                                        TeamRequestAction.ACCEPT,
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
                                                modifier = Modifier.padding(end = 16.dp),
                                                onClick = {
                                                    onClickProcessRequest(
                                                        request,
                                                        TeamRequestAction.REJECT,
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
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamJoinersSheetHeader(
    modifier: Modifier,
    state: TeamScreenState,
    onDismissSheet: () -> Unit,
    requestsData: LazyPagingItems<TeamAccountJoinRequestDomain>,
    invitesData: LazyPagingItems<TeamAccountInvitesDomain>,
    pagerState: PagerState,
) {
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = modifier.fillMaxWidth()) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = onDismissSheet) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "close sheet",
                    )
                }
            },
            title = {
                SafiTopBarHeader(
                    title = state.team?.team?.name ?: "",
                    subtitle = "Join Team Requests and Invited Accounts",
                )
            },
            actions = {
                IconButton(
                    onClick = {
                        requestsData.refresh()
                        invitesData.refresh()
                    },
                    enabled = requestsData.loadState.refresh is LoadState.NotLoading,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "refresh requests",
                    )
                }
            },
        )
        TabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = pagerState.currentPage,
        ) {
            state.joinerTabs.forEachIndexed { index, tab ->
                val isSelected = pagerState.currentPage == index
                Tab(
                    selected = isSelected,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(state.joinerTabs.indexOf(tab))
                        }
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(0.25f),
                ) {
                    SafiCenteredRow(modifier = Modifier.padding(16.dp)) {
                        Text(text = tab.label)
                    }
                }
            }
        }
    }
}
