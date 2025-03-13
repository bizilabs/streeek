package com.bizilabs.streeek.feature.tabs.screens.teams

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.DialogState
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.TeamAndMembersDomain
import com.bizilabs.streeek.lib.domain.models.TeamDetailsDomain
import com.bizilabs.streeek.lib.domain.repositories.TeamRepository
import com.bizilabs.streeek.lib.domain.repositories.WorkerType
import com.bizilabs.streeek.lib.domain.repositories.WorkersRepository
import com.bizilabs.streeek.lib.domain.repositories.team.TeamRequestRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module

internal val TeamsListModule =
    module {
        factory<TeamsListScreenModel> {
            TeamsListScreenModel(
                teamRepository = get(),
                teamRequestRepository = get(),
                workersRepository = get(),
            )
        }
    }

data class RequestTeamState(
    val teamId: Long,
    val requestState: FetchState<Boolean> = FetchState.Loading,
)

data class TeamsListScreenState(
    val isSyncing: Boolean = false,
    val isJoining: Boolean = false,
    val isCreating: Boolean = false,
    val teamId: Long? = null,
    val teams: List<TeamDetailsDomain> = emptyList(),
    val showConfetti: Boolean = false,
    val requestedTeamIds: List<Long> = emptyList(),
    val requestState: RequestTeamState? = null,
    val dialogState: DialogState? = null,
)

class TeamsListScreenModel(
    private val teamRepository: TeamRepository,
    private val teamRequestRepository: TeamRequestRepository,
    private val workersRepository: WorkersRepository,
) : StateScreenModel<TeamsListScreenState>(TeamsListScreenState()) {
    val availableTeams = teamRepository.getTeamsAndMembers()

    init {
        observeTeams()
        observeTeamsSync()
    }

    private fun observeTeamsSync() {
        screenModelScope.launch {
            teamRepository.isSyncing.collectLatest { isSyncing ->
                mutableState.update { it.copy(isSyncing = isSyncing) }
            }
        }
    }

    private fun observeTeams() {
        screenModelScope.launch {
            teamRepository.teams.collectLatest { map ->
                mutableState.update { it.copy(teams = map.values.toList()) }
            }
        }
    }

    fun onClickMenuTeamCreate() {
        screenModelScope.launch {
            mutableState.update { it.copy(isCreating = true) }
            delay(250)
            mutableState.update { it.copy(isCreating = false) }
        }
    }

    fun onClickMenuTeamJoin() {
        screenModelScope.launch {
            mutableState.update { it.copy(isJoining = true) }
            delay(250)
            mutableState.update { it.copy(isJoining = false) }
        }
    }

    fun onClickTeam(teamDetails: TeamDetailsDomain) {
        screenModelScope.launch {
            teamRepository.setSelectedTeam(team = teamDetails)
            mutableState.update { it.copy(teamId = teamDetails.team.id) }
            delay(250)
            mutableState.update { it.copy(teamId = null) }
        }
    }

    fun onClickMenuRefreshTeam() {
        workersRepository.runSyncTeams(type = WorkerType.Once)
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
}
