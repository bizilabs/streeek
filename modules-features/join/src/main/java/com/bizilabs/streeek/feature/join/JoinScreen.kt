package com.bizilabs.streeek.feature.join

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.lib.common.components.TeamItemComponent
import com.bizilabs.streeek.lib.common.components.paging.SafiPagingComponent
import com.bizilabs.streeek.lib.common.helpers.requestFocusOnGainingVisibility
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiBottomDialog
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.design.components.SafiOTPField
import com.bizilabs.streeek.lib.design.components.SafiRefreshBox
import com.bizilabs.streeek.lib.design.components.SafiTopBarHeader
import com.bizilabs.streeek.lib.domain.extensions.asCount
import com.bizilabs.streeek.lib.domain.models.TeamAndMembersDomain

val ScreenJoinTeam =
    screenModule {
        register<SharedScreen.Join> { JoinScreen }
    }

object JoinScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel: JoinScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()
        val teams = screenModel.teams.collectAsLazyPagingItems()
        JoinScreenContent(
            state = state,
            teams = teams,
            onClickTeamRequest = screenModel::onClickTeamRequest,
            onClickNavigateBack = { navigator?.pop() },
            onClickDismissDialog = screenModel::onClickDismissDialog,
            onValueChangeTeamCode = screenModel::onValueChangeTeamCode,
            onClickJoin = screenModel::onClickJoin,
            onClickJoinWithCode = screenModel::onClickJoinWithCode,
            onClickCreateTeam = screenModel::onClickCreateTeam,
            navigate = { screen -> navigator?.replace(screen) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinScreenContent(
    state: JoinScreenState,
    teams: LazyPagingItems<TeamAndMembersDomain>,
    onClickNavigateBack: () -> Unit,
    onClickTeamRequest: (TeamAndMembersDomain) -> Unit,
    onClickDismissDialog: () -> Unit,
    onValueChangeTeamCode: (String) -> Unit,
    onClickJoin: () -> Unit,
    onClickJoinWithCode: (Boolean) -> Unit,
    onClickCreateTeam: () -> Unit,
    navigate: (Screen) -> Unit,
) {
    if (state.teamId != null) {
        navigate(rememberScreen(SharedScreen.Team(teamId = state.teamId)))
    }

    if (state.hasClickedCreateTeam) {
        navigate(rememberScreen(SharedScreen.Team(teamId = null)))
    }

    if (state.dialogState != null) {
        SafiBottomDialog(
            state = state.dialogState,
            onClickDismiss = onClickDismissDialog,
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        if (state.joiningWithCode) {
                            onClickJoinWithCode(false)
                        } else {
                            onClickNavigateBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "navigate back",
                        )
                    }
                },
                title = {
                    SafiTopBarHeader(
                        title = "Join Team",
                        subtitle = "Request to join a team or enter team code",
                    )
                },
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = state.joiningWithCode.not(),
            ) {
                ExtendedFloatingActionButton(
                    onClick = { onClickJoinWithCode(true) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Text(text = "Use Code")
                    Icon(imageVector = Icons.Rounded.QrCode, contentDescription = null)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        AnimatedContent(
            label = "animated view",
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
            targetState = state.joiningWithCode,
        ) { joinWithCode ->
            when (joinWithCode) {
                true -> {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 16.dp),
                            text = "Enter Team Code",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = "Enter the 6 digit code you received from a team admin to proceed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.75f),
                        )
                        SafiCenteredRow(modifier = Modifier.fillMaxWidth()) {
                            SafiOTPField(
                                modifier =
                                    Modifier
                                        .fillMaxWidth(0.75f)
                                        .padding(vertical = 24.dp)
                                        .requestFocusOnGainingVisibility(),
                                text = state.token,
                                onClickDone = onClickJoin,
                                keyboardOptions =
                                    KeyboardOptions(
                                        keyboardType = KeyboardType.NumberPassword,
                                        imeAction = ImeAction.Done,
                                        showKeyboardOnFocus = true,
                                    ),
                                isEnabled = state.dialogState == null,
                            ) { text, bool ->
                                onValueChangeTeamCode(text)
                            }
                        }

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onClickJoin,
                            enabled = state.isJoinActionEnabled,
                        ) {
                            Text(text = "Join")
                        }
                        OutlinedButton(
                            modifier =
                                Modifier
                                    .padding(top = 8.dp)
                                    .fillMaxWidth(),
                            onClick = { onClickJoinWithCode(false) },
                            colors = ButtonDefaults.outlinedButtonColors(),
                        ) {
                            Text(text = "Cancel")
                        }
                    }
                }

                false -> {
                    SafiRefreshBox(
                        modifier = Modifier.fillMaxSize(),
                        isRefreshing = teams.loadState.refresh is LoadState.Loading,
                        onRefresh = teams::refresh,
                    ) {
                        SafiPagingComponent(
                            data = teams,
                            modifier = Modifier.fillMaxSize(),
                            refreshEmpty = {
                                SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                                    SafiInfoSection(
                                        icon = Icons.Rounded.People,
                                        title = "No Public Teams",
                                        description = "No public teams found. \nCreate a team to start collaborating",
                                    )
                                    Button(onClick = onClickCreateTeam) {
                                        Text(text = "create")
                                    }
                                }
                            },
                            refreshError = {
                                SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                                    SafiInfoSection(
                                        icon = Icons.Rounded.People,
                                        title = "Failed Getting Teams",
                                        description = it.localizedMessage ?: "",
                                    )
                                    Button(onClick = teams::retry) {
                                        Text(text = "retry")
                                    }
                                }
                            },
                        ) { team ->
                            val requested = team.team.id in state.requestedTeamIds
                            TeamItemComponent(
                                modifier =
                                    Modifier
                                        .padding(vertical = 8.dp)
                                        .padding(horizontal = 16.dp),
                                teamId = team.team.id,
                                teamName = team.team.name,
                                teamCount = team.team.count,
                                teamCountLabel = "member".asCount(team.team.count),
                                requested = requested,
                                enabled = state.requestState == null && !requested,
                                requestTeamId = state.requestState?.teamId,
                                requestState = state.requestState?.requestState,
                                membersAvatarUrl = team.members.map { it.avatarUrl },
                            ) { onClickTeamRequest(team) }
                        }
                    }
                }
            }
        }
    }
}
