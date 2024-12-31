package com.bizilabs.streeek.feature.tabs.screens.teams

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.common.models.FetchListState
import com.bizilabs.streeek.lib.domain.models.TeamDetailsDomain
import com.bizilabs.streeek.lib.domain.models.TeamDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberDomain
import com.bizilabs.streeek.lib.domain.models.TeamWithDetailDomain
import com.bizilabs.streeek.lib.domain.repositories.TeamRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module
import timber.log.Timber
import kotlin.collections.get

internal val TeamsModule = module {
    factory<TeamsScreenModel> {
        TeamsScreenModel(repository = get())
    }
}

data class TeamsScreenState(
    val isJoining: Boolean = false,
    val isCreating: Boolean = false,
    val teamId: Long? = null,
    val teamsState: FetchListState<TeamWithDetailDomain> = FetchListState.Loading,
    val team: TeamDetailsDomain? = null,
    val teams: List<TeamDetailsDomain> = emptyList()
){
    val list: List<TeamMemberDomain>
        get() = when{
            team == null -> emptyList()
            team.page == 1 -> team.members.filterIndexed { index, _ -> index > 2 }
            else -> team.members
        }
}

class TeamsScreenModel(
    private val repository: TeamRepository
) : StateScreenModel<TeamsScreenState>(TeamsScreenState()) {

    private val selectedTeam = combine(repository.teamId, repository.teams) { id, map ->
        Timber.d("Teams Map -> $map")
        Timber.d("Selected Team being updated....")
        Timber.d("Selected Team : ${mutableState.value.team}")
        map[id]
    }

    init {
        observeTeams()
        observeTeamDetails()
    }

    private fun observeTeamDetails() {
        screenModelScope.launch {
            selectedTeam.collectLatest { value ->
                mutableState.update { it.copy(team = value) }
            }
        }
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

    fun onClickTeam(team: TeamDomain) {
        screenModelScope.launch {
            mutableState.update { it.copy(teamId = team.id) }
            delay(250)
            mutableState.update { it.copy(teamId = null) }
        }
    }

    fun onValueChangeTeam(team: TeamDetailsDomain) {
        screenModelScope.launch {
            repository.setSelectedTeam(team = team)
        }
    }

}
