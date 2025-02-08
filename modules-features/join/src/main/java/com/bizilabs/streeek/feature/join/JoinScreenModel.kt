package com.bizilabs.streeek.feature.join

import androidx.paging.PagingData
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.common.components.paging.getPagingDataLoading
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.DialogState
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.TeamAndMembersDomain
import com.bizilabs.streeek.lib.domain.models.team.AccountTeamInvitesDomain
import com.bizilabs.streeek.lib.domain.repositories.TeamRepository
import com.bizilabs.streeek.lib.domain.repositories.team.TeamMemberInvitationRepository
import com.bizilabs.streeek.lib.domain.repositories.team.TeamRequestRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module
import kotlin.enums.EnumEntries

val FeatureJoin =
    module {
        factory {
            JoinScreenModel(
                teamRepository = get(),
                teamRequestRepository = get(),
                teamMemberInvitationRepository = get(),
            )
        }
    }

data class RequestTeamState(
    val teamId: Long,
    val requestState: FetchState<Boolean> = FetchState.Loading,
)

enum class JoinTab {
    PUBLIC_TEAMS,
    TEAM_INVITES,
    ;

    val label: String
        get() =
            name
                .split('_')
                .joinToString(" ") {
                    it.lowercase().replaceFirstChar { char -> char.uppercase() }
                }
}

enum class TeamInviteAction {
    ACCEPT,
    REJECT,
    ;

    val value: String
        get() =
            when (this) {
                ACCEPT -> "accepted"
                REJECT -> "rejected"
            }
}

data class JoinScreenState(
    val isSearching: Boolean = false,
    val hasClickedCreateTeam: Boolean = false,
    val query: String = "",
    val requestedTeamIds: List<Long> = emptyList(),
    val requestState: RequestTeamState? = null,
    val dialogState: DialogState? = null,
    val token: String = "",
    val teamId: Long? = null,
    val joiningWithCode: Boolean = false,
    val joinerTabs: EnumEntries<JoinTab> = JoinTab.entries,
    // This is the map of the state and list
    val processedInvites: Map<TeamInviteAction, List<Long>> = emptyMap(),
    val singleInviteState: Map<Long, FetchState<Boolean>> = emptyMap(),
) {
    val isJoinActionEnabled: Boolean
        get() = token.length == 6 && dialogState == null
}

class JoinScreenModel(
    private val teamRepository: TeamRepository,
    private val teamRequestRepository: TeamRequestRepository,
    private val teamMemberInvitationRepository: TeamMemberInvitationRepository,
) : StateScreenModel<JoinScreenState>(JoinScreenState()) {
    val teams = teamRepository.getTeamsAndMembers()

    private var _accountInvites: Flow<PagingData<AccountTeamInvitesDomain>> =
        MutableStateFlow(getPagingDataLoading<AccountTeamInvitesDomain>()).asStateFlow()
    val accountInvites: Flow<PagingData<AccountTeamInvitesDomain>>
        get() = _accountInvites

    init {
        observeAccountInvites()
    }

    fun onClickSearch() {
        mutableState.update { it.copy(isSearching = true) }
    }

    fun onClickDismissSearch() {
        mutableState.update { it.copy(isSearching = false) }
    }

    fun onClickDismissDialog() {
        mutableState.update { it.copy(dialogState = null) }
    }

    fun onClickTeamRequest(teamAndMembers: TeamAndMembersDomain) {
        requestToJoinTeam(teamId = teamAndMembers.team.id)
    }

    private fun requestToJoinTeam(teamId: Long) {
        screenModelScope.launch {
            mutableState.update { it.copy(requestState = RequestTeamState(teamId = teamId)) }
            val result = teamRequestRepository.requestToJoinTeam(teamId = teamId)
            when (result) {
                is DataResult.Error -> {
                    mutableState.update {
                        it.copy(
                            dialogState =
                                DialogState.Error(
                                    title = "Error",
                                    message = "Wasn't able to send request to join team",
                                ),
                            requestState =
                                RequestTeamState(
                                    teamId = teamId,
                                    requestState = FetchState.Error(result.message),
                                ),
                        )
                    }
                    delay(2000)
                    mutableState.update { it.copy(dialogState = null, requestState = null) }
                }

                is DataResult.Success -> {
                    val requests = state.value.requestedTeamIds.toMutableList()
                    requests.add(teamId)
                    mutableState.update {
                        it.copy(
                            requestedTeamIds = requests,
                            requestState =
                                it.requestState?.copy(
                                    requestState = FetchState.Success(result.data),
                                ),
                        )
                    }
                    delay(2000)
                    mutableState.update { it.copy(dialogState = null, requestState = null) }
                }
            }
        }
    }

    fun onValueChangeTeamCode(token: String) {
        mutableState.update { it.copy(token = token) }
    }

    fun onClickJoin() {
        joinTeam()
    }

    private fun joinTeam() {
        val token = state.value.token
        if (token.length != 6 && token.any { it.digitToIntOrNull() == null }) return
        mutableState.update { it.copy(dialogState = DialogState.Loading()) }
        screenModelScope.launch {
            when (val result = teamRepository.joinTeam(token = token)) {
                is DataResult.Error -> {
                    mutableState.update {
                        it.copy(
                            dialogState =
                                DialogState.Error(
                                    title = "Error",
                                    message = result.message,
                                ),
                        )
                    }
                }

                is DataResult.Success -> {
                    mutableState.update {
                        it.copy(
                            dialogState =
                                DialogState.Success(
                                    title = "Success",
                                    message = "Joined team successfully as a ${result.data.role}",
                                ),
                        )
                    }
                    delay(500)
                    mutableState.update { it.copy(teamId = result.data.teamId) }
                    delay(2000)
                    mutableState.update { it.copy(teamId = null, dialogState = null) }
                }
            }
        }
    }

    fun onClickJoinWithCode(isVisible: Boolean) {
        mutableState.update { it.copy(joiningWithCode = isVisible) }
    }

    fun onClickCreateTeam() {
        mutableState.update { it.copy(hasClickedCreateTeam = true) }
    }

    private fun observeAccountInvites() {
        _accountInvites = teamMemberInvitationRepository.getAllAccountInvites()
    }

    private fun getSingleInviteState() = state.value.singleInviteState.toMutableMap()

    fun onClickProcessInvite(
        teamInviteAction: TeamInviteAction,
        accountTeamInvitesDomain: AccountTeamInvitesDomain,
    ) {
        screenModelScope.launch {
            val singleInviteState = getSingleInviteState()
            singleInviteState[accountTeamInvitesDomain.invite.inviteId] = FetchState.Loading

            mutableState.update {
                it.copy(
                    singleInviteState = singleInviteState,
                )
            }

            val result =
                teamMemberInvitationRepository.processAccountInvite(
                    inviteId = accountTeamInvitesDomain.invite.inviteId,
                    status = teamInviteAction.value,
                )
            when (result) {
                is DataResult.Error -> {
                    val singleInviteState = getSingleInviteState()
                    singleInviteState[accountTeamInvitesDomain.invite.inviteId] =
                        FetchState.Error(result.message)

                    mutableState.update {
                        it.copy(
                            singleInviteState = singleInviteState,
                            dialogState =
                                DialogState.Error(
                                    title = "Error",
                                    message = result.message,
                                ),
                        )
                    }

                    delay(2000)
                    mutableState.update {
                        it.copy(
                            singleInviteState = emptyMap(),
                            dialogState = null,
                        )
                    }
                }

                is DataResult.Success -> {
                    val processedInvites = state.value.processedInvites.toMutableMap()
                    val list =
                        processedInvites[teamInviteAction]?.toMutableList() ?: mutableListOf()
                    list.add(accountTeamInvitesDomain.invite.inviteId)
                    processedInvites[teamInviteAction] = list

                    val singleInviteState = getSingleInviteState()
                    singleInviteState[accountTeamInvitesDomain.invite.inviteId] =
                        FetchState.Success(value = result.data)

                    mutableState.update {
                        it.copy(
                            singleInviteState = singleInviteState,
                        )
                    }
                    delay(2000)
                    mutableState.update {
                        it.copy(
                            singleInviteState = emptyMap(),
                            processedInvites = processedInvites,
                        )
                    }
                }
            }
        }
    }

    fun onRefreshTeamInvites() {
        observeAccountInvites()
        mutableState.update {
            it.copy(
                processedInvites = emptyMap(),
            )
        }
    }
}
