package com.bizilabs.streeek.feature.tabs.screens.teams

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.common.models.FetchListState
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.TeamDomain
import com.bizilabs.streeek.lib.domain.models.TeamWithDetailDomain
import com.bizilabs.streeek.lib.domain.repositories.TeamRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module

internal val TeamsModule = module {
    factory<TeamsScreenModel> {
        TeamsScreenModel(repository = get())
    }
}

data class TeamsScreenState(
    val isJoining: Boolean = false,
    val isCreating: Boolean = false,
    val teamId: Long? = null,
    val teamsState: FetchListState<TeamWithDetailDomain> = FetchListState.Loading
)

class TeamsScreenModel(
    private val repository: TeamRepository
) : StateScreenModel<TeamsScreenState>(TeamsScreenState()) {

    init {
//        observeTeams()
    }

    private fun observeTeams() {
        screenModelScope.launch {
            mutableState.update { it.copy(teamsState = FetchListState.Loading) }
            val update = when (val result = repository.getAccountTeams()) {
                is DataResult.Error -> FetchListState.Error(result.message)
                is DataResult.Success -> {
                    val list = result.data
                    if (list.isEmpty())
                        FetchListState.Empty
                    else
                        FetchListState.Success(result.data)
                }
            }
            mutableState.update { it.copy(teamsState = update) }
        }
    }

    fun onClickMenuSearch() {
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

    fun onClickTeam(team: TeamDomain){
        screenModelScope.launch {
            mutableState.update { it.copy(teamId = team.id) }
            delay(250)
            mutableState.update { it.copy(teamId = null) }
        }
    }

}
