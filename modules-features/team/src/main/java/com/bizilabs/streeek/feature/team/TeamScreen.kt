package com.bizilabs.streeek.feature.team

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.feature.team.components.TeamInvitationBottomSheet
import com.bizilabs.streeek.feature.team.components.TeamMemberComponent
import com.bizilabs.streeek.feature.team.components.TeamTopMemberComponent
import com.bizilabs.streeek.feature.team.section.TeamJoinRequestsBottomSheet
import com.bizilabs.streeek.lib.common.components.paging.SafiPagingComponent
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiBottomDialog
import com.bizilabs.streeek.lib.design.components.SafiBottomSheetPicker
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiDropdownComponent
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.design.components.SafiRefreshBox
import com.bizilabs.streeek.lib.design.components.SafiTopBarHeader
import com.bizilabs.streeek.lib.domain.models.TeamMemberDomain
import com.bizilabs.streeek.lib.domain.models.team.AccountsNotInTeamDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamAccountInvitesDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamAccountJoinRequestDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamInvitationDomain
import com.bizilabs.streeek.lib.resources.strings.SafiStrings
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

val screenTeam =
    screenModule {
        register<SharedScreen.Team> { parameters -> TeamScreen(parameters.teamId) }
    }

class TeamScreen(val teamId: Long?) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel: TeamScreenModel = getScreenModel()
        screenModel.setNavigationVariables(teamId = teamId)
        val state by screenModel.state.collectAsStateWithLifecycle()
        val members = screenModel.pages.collectAsLazyPagingItems()
        val requests = screenModel.requests.collectAsLazyPagingItems()
        val accountsNotInTeam = screenModel.accountsNotInTeam.collectAsLazyPagingItems()
        val teamAccountsInvites = screenModel.teamAccountInvites.collectAsLazyPagingItems()

        TeamScreenContent(
            state = state,
            data = members,
            requests = requests,
            teamAccountsInvites = teamAccountsInvites,
            onClickBack = { navigator?.pop() },
            onValueChangeName = screenModel::onValueChangeName,
            onValueChangePublic = screenModel::onValueChangePublic,
            onValueChangePublicDropdown = screenModel::onValueChangePublicDropDown,
            onClickDismissDialog = screenModel::onClickDismissDialog,
            onClickManageAction = screenModel::onClickManageAction,
            onDismissInvitationsSheet = screenModel::onDismissInvitationsSheet,
            onDismissRequestsSheet = screenModel::onDismissRequestsSheet,
            onClickMenuAction = screenModel::onClickMenuAction,
            onClickRefreshInvitation = screenModel::onClickRefreshInvitation,
            onClickInvitationCreate = screenModel::onClickInvitationCreate,
            onClickInvitationRetry = screenModel::onClickInvitationRetry,
            onSwipeInvitationDelete = screenModel::onSwipeInvitationDelete,
            onClickActionCancel = screenModel::onClickManageCancelAction,
            onClickActionDelete = screenModel::onClickManageDeleteAction,
            onClickInviteMore = screenModel::onClickInviteMore,
            onRefreshTeams = screenModel::onRefreshTeams,
            onClickRequests = screenModel::onClickRequests,
            onClickToggleSelectRequest = screenModel::onClickToggleSelectRequest,
            onClickProcessSelectedRequests = screenModel::onClickProcessSelectedRequests,
            onClickSelectedRequestsSelection = screenModel::onClickSelectedRequestsSelection,
            onClickProcessRequest = screenModel::onClickProcessRequest,
            onSuccessOrErrorCodeCreation = screenModel::onSuccessOrErrorCodeCreation,
            accountsNotInTeam = accountsNotInTeam,
            onClickInviteAccount = screenModel::onClickInviteAccount,
            onSearchParamChanged = screenModel::onSearchParamChanged,
            onClickClearSearch = screenModel::onClickClearSearch,
            onClickWithdraw = screenModel::onClickWithdrawAccount,
        )
    }
}

@Composable
fun TeamScreenContent(
    state: TeamScreenState,
    data: LazyPagingItems<TeamMemberDomain>,
    requests: LazyPagingItems<TeamAccountJoinRequestDomain>,
    teamAccountsInvites: LazyPagingItems<TeamAccountInvitesDomain>,
    onClickBack: () -> Unit,
    onValueChangeName: (String) -> Unit,
    onValueChangePublic: (String) -> Unit,
    onValueChangePublicDropdown: (Boolean) -> Unit,
    onClickDismissDialog: () -> Unit,
    onClickManageAction: () -> Unit,
    onClickActionDelete: () -> Unit,
    onClickActionCancel: () -> Unit,
    onDismissInvitationsSheet: () -> Unit,
    onDismissRequestsSheet: () -> Unit,
    onClickMenuAction: (TeamMenuAction) -> Unit,
    onClickRefreshInvitation: () -> Unit,
    onClickInvitationCreate: () -> Unit,
    onClickInvitationRetry: () -> Unit,
    onSwipeInvitationDelete: (TeamInvitationDomain) -> Unit,
    onClickInviteMore: () -> Unit,
    onRefreshTeams: () -> Unit,
    onClickRequests: () -> Unit,
    onClickToggleSelectRequest: (TeamAccountJoinRequestDomain) -> Unit,
    onClickProcessSelectedRequests: (Boolean) -> Unit,
    onClickSelectedRequestsSelection: (SelectionAction, List<TeamAccountJoinRequestDomain>) -> Unit,
    onClickProcessRequest: (TeamAccountJoinRequestDomain, TeamRequestAction) -> Unit,
    onSuccessOrErrorCodeCreation: (SnackBarType) -> Unit,
    accountsNotInTeam: LazyPagingItems<AccountsNotInTeamDomain>,
    onClickInviteAccount: (AccountsNotInTeamDomain) -> Unit,
    onSearchParamChanged: (String) -> Unit,
    onClickClearSearch: () -> Unit,
    onClickWithdraw: (TeamAccountInvitesDomain) -> Unit,
) {
    val activity = LocalContext.current as Activity

    if (state.shouldNavigateBack) onClickBack()

    if (state.isOpen) {
        SafiBottomSheetPicker(
            title = stringResource(SafiStrings.SelectTeamVisibility),
            selected = state.value,
            list = state.visibilityOptions,
            onDismiss = { onValueChangePublicDropdown(false) },
            onItemSelected = { onValueChangePublic(it) },
            name = { it.replaceFirstChar { it.uppercase() } },
        )
    }

    if (state.isRequestsSheetOpen) {
        TeamJoinRequestsBottomSheet(
            state = state,
            requestsData = requests,
            teamAccountsInvites = teamAccountsInvites,
            onDismissSheet = onDismissRequestsSheet,
            onClickToggleSelectRequest = onClickToggleSelectRequest,
            onClickProcessSelectedRequests = onClickProcessSelectedRequests,
            onClickSelectedRequestsSelection = onClickSelectedRequestsSelection,
            onClickProcessRequest = onClickProcessRequest,
            onClickWithdraw = onClickWithdraw,
        )
    }

    if (state.dialogState != null) {
        SafiBottomDialog(
            state = state.dialogState,
            onClickDismiss = onClickDismissDialog,
        )
    }

    if (state.isInvitationsOpen) {
        TeamInvitationBottomSheet(
            activity = activity,
            state = state,
            onDismissSheet = onDismissInvitationsSheet,
            onClickRefreshInvitation = onClickRefreshInvitation,
            onClickInvitationRetry = onClickInvitationRetry,
            onClickInvitationCreate = onClickInvitationCreate,
            onSwipeInvitationDelete = onSwipeInvitationDelete,
            onSuccessOrErrorCodeCreation = onSuccessOrErrorCodeCreation,
            accountsNotInTeam = accountsNotInTeam,
            onClickInviteAccount = onClickInviteAccount,
            onSearchParamChanged = onSearchParamChanged,
            onClickClearSearch = onClickClearSearch,
        )
    }

    Scaffold(
        topBar = {
            TeamScreenHeaderComponent(
                onClickBack = onClickBack,
                state = state,
                onClickMenuAction = onClickMenuAction,
                onClickRequests = onClickRequests,
            )
        },
    ) { innerPadding ->
        AnimatedContent(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            targetState = state.isManagingTeam,
            label = "",
        ) { isManaging ->
            when (isManaging) {
                true -> {
                    ManageTeamSection(
                        state = state,
                        onValueChangeName = onValueChangeName,
                        onValueChangePublicDropdown = onValueChangePublicDropdown,
                        onClickAction = onClickManageAction,
                        onClickActionDelete = onClickActionDelete,
                        onClickActionCancel = onClickActionCancel,
                    )
                }

                false -> {
                    ViewTeamSection(
                        state = state,
                        data = data,
                        onClickInviteMore = onClickInviteMore,
                        onRefreshTeams = onRefreshTeams,
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TeamScreenHeaderComponent(
    state: TeamScreenState,
    onClickBack: () -> Unit,
    onClickMenuAction: (TeamMenuAction) -> Unit,
    onClickRequests: () -> Unit,
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onClickBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                )
            }
        },
        title = {
            val team = state.fetchState
            AnimatedContent(
                modifier = Modifier.fillMaxWidth(),
                targetState = state.isManagingTeam,
                label = "animate_team_title",
            ) { isManaging ->
                when {
                    isManaging -> {
                        SafiTopBarHeader(title = "Create Team")
                    }

                    else -> {
                        if (team is FetchState.Success) {
                            val count = team.value.team.count
                            SafiTopBarHeader(
                                modifier = Modifier.fillMaxWidth(),
                                title = team.value.team.name,
                                subtitle =
                                    buildString {
                                        append(count)
                                        append(" Member")
                                        append(if (count > 1) "s" else "")
                                    },
                            )
                        }
                    }
                }
            }
        },
        actions = {
            var expanded by remember { mutableStateOf(false) }
            AnimatedVisibility(
                visible = state.fetchState is FetchState.Success,
            ) {
                if (state.fetchState is FetchState.Success) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnimatedVisibility(state.fetchState.value.details.role.isAdmin) {
                            IconButton(onClick = onClickRequests) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                )
                            }
                        }
                        Box(
                            modifier = Modifier,
                        ) {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More options")
                            }
                            DropdownMenu(
                                modifier = Modifier.defaultMinSize(minWidth = 150.dp),
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                TeamMenuAction
                                    .get(role = state.fetchState.value.details.role)
                                    .forEach { menu ->
                                        DropdownMenuItem(
                                            contentPadding =
                                                PaddingValues(
                                                    start = 16.dp,
                                                    end = 24.dp,
                                                ),
                                            text = { Text(menu.label) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = menu.icon,
                                                    contentDescription = null,
                                                )
                                            },
                                            onClick = {
                                                expanded = false
                                                onClickMenuAction(menu)
                                            },
                                        )
                                    }
                            }
                        }
                    }
                }
            }
        },
    )
}

@Composable
fun ViewTeamSection(
    state: TeamScreenState,
    data: LazyPagingItems<TeamMemberDomain>,
    onClickInviteMore: () -> Unit,
    onRefreshTeams: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = state.fetchState,
            label = "",
        ) { result ->
            when (result) {
                FetchState.Loading -> {
                    SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator()
                    }
                }

                is FetchState.Error -> {
                    SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                        SafiInfoSection(
                            icon = Icons.Rounded.People,
                            title = "Error",
                            description = result.message,
                        )
                    }
                }

                is FetchState.Success -> {
                    TeamDetailsSection(
                        state = state,
                        data = data,
                        onClickInviteMore = onClickInviteMore,
                        onRefreshTeams = onRefreshTeams,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailsSection(
    state: TeamScreenState,
    data: LazyPagingItems<TeamMemberDomain>,
    onClickInviteMore: () -> Unit,
    onRefreshTeams: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        SafiRefreshBox(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = false,
            onRefresh = onRefreshTeams,
        ) {
            SafiPagingComponent(
                modifier = Modifier.fillMaxSize(),
                data = data,
                prependSuccess = {
                    TeamsScreenTopSection(
                        state = state,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                    )
                },
                refreshEmpty = {
                    SafiCenteredColumn(
                        modifier =
                            Modifier
                                .fillMaxSize(),
                    ) {
                        SafiInfoSection(
                            icon = Icons.Rounded.People,
                            title = "No Other Members",
                            description = "You're only ${state.team?.members?.size} members in this team, invite others to continue.",
                        ) {
                            Button(
                                modifier = Modifier.padding(16.dp),
                                onClick = { onClickInviteMore() },
                            ) {
                                Text(text = "Invite More")
                            }
                        }
                    }
                },
            ) { member ->
                TeamMemberComponent(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 8.dp),
                    member = member,
                )
            }
        }
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = state.showConfetti,
        ) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties =
                    remember {
                        listOf(
                            Party(
                                speed = 0f,
                                maxSpeed = 30f,
                                damping = 0.9f,
                                spread = 360,
                                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                                position = Position.Relative(0.5, 0.3),
                                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                            ),
                            Party(
                                speed = 0f,
                                maxSpeed = 30f,
                                damping = 0.9f,
                                spread = 360,
                                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                                position = Position.Relative(0.5, 0.3),
                                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                            ),
                        )
                    },
            )
        }
    }
}

@Composable
fun TeamsScreenTopSection(
    state: TeamScreenState,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = state.team != null,
            ) {
                if (state.team != null) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                    ) {
                        TeamTopMemberComponent(
                            isFirst = false,
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(top = 48.dp),
                            member = state.team.top[1],
                        )
                        TeamTopMemberComponent(
                            isFirst = true,
                            modifier = Modifier.weight(1f),
                            member = state.team.top[0],
                        )
                        TeamTopMemberComponent(
                            isFirst = false,
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(top = 48.dp),
                            member = state.team.top[2],
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ManageTeamSection(
    state: TeamScreenState,
    onValueChangeName: (String) -> Unit,
    onValueChangePublicDropdown: (Boolean) -> Unit,
    onClickAction: () -> Unit,
    onClickActionDelete: () -> Unit,
    onClickActionCancel: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        TextField(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp),
            value = state.name,
            onValueChange = onValueChangeName,
            label = {
                Text(text = "Name")
            },
            singleLine = true,
            isError = !state.isValidName && state.name.isNotBlank(),
            trailingIcon = {
                if (!state.isValidName && state.name.isNotBlank()) {
                    Icon(Icons.Rounded.Error, "error")
                }
            },
            supportingText = {
                if (!state.isValidName && state.name.isNotBlank()) {
                    Text(text = stringResource(SafiStrings.InvalidTeamName))
                }
            },
        )
        Spacer(modifier = Modifier.padding(8.dp))

        SafiDropdownComponent(
            modifier = Modifier,
            value = state.value.replaceFirstChar { it.uppercase() },
            onValueChange = onValueChangeName,
            onClickAction = { onValueChangePublicDropdown(true) },
        )

        Button(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp),
            onClick = onClickAction,
            enabled = state.isActionEnabled,
        ) {
            Text(text = if (state.teamId == null) "Create" else "Update")
        }

        AnimatedVisibility(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            visible = state.isEditing,
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedButton(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    onClick = onClickActionCancel,
                ) {
                    Text(text = "Cancel")
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    onClick = onClickActionDelete,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        ),
                ) {
                    Text(text = "Delete")
                }
            }
        }
    }
}
