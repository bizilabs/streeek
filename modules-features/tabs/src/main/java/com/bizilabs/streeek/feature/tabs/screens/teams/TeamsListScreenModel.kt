package com.bizilabs.streeek.feature.tabs.screens.teams

import android.content.Context
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.common.models.FetchListState
import com.bizilabs.streeek.lib.domain.models.TeamDetailsDomain
import com.bizilabs.streeek.lib.domain.models.TeamWithDetailDomain
import com.bizilabs.streeek.lib.domain.repositories.TeamRepository
import com.bizilabs.streeek.lib.domain.workers.startImmediateSyncTeamsWork
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module

internal val TeamsListModule =
    module {
        factory<TeamsListScreenModel> {
            TeamsListScreenModel(context = get(), repository = get())
        }
    }

data class TeamsListScreenState(
    val isJoining: Boolean = false,
    val isCreating: Boolean = false,
    val teamId: Long? = null,
    val teamsState: FetchListState<TeamWithDetailDomain> = FetchListState.Loading,
    val teams: List<TeamDetailsDomain> = emptyList(),
    val showConfetti: Boolean = false,
)

class TeamsListScreenModel(
    private val context: Context,
    private val repository: TeamRepository,
) : StateScreenModel<TeamsListScreenState>(TeamsListScreenState()) {
    init {
        observeTeams()
    }

    private fun observeTeams() {
        screenModelScope.launch {
            repository.teams.collectLatest { map ->
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
            repository.setSelectedTeam(team = teamDetails)
            mutableState.update { it.copy(teamId = teamDetails.team.id) }
            delay(250)
            mutableState.update { it.copy(teamId = null) }
        }
    }

    fun onClickMenuRefreshTeam() {
        context.startImmediateSyncTeamsWork()
    }
}
