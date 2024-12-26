package com.bizilabs.streeek.feature.tabs.screens.teams

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module

internal val TeamsModule = module() {
    factory<TeamsScreenModel> {
        TeamsScreenModel()
    }
}

data class TeamsScreenState(
    val isJoining: Boolean = false,
    val isCreating: Boolean = false,
    val teamId: Long? = null
)

class TeamsScreenModel : StateScreenModel<TeamsScreenState>(TeamsScreenState()) {

    fun onClickMenuSearch() {
    }

    fun onClickMenuTeamCreate() {
        screenModelScope.launch{
            mutableState.update { it.copy(isCreating = true) }
            delay(250)
            mutableState.update { it.copy(isCreating = false) }
        }
    }

    fun onClickMenuTeamJoin() {
        screenModelScope.launch{
            mutableState.update { it.copy(isJoining = true) }
            delay(250)
            mutableState.update { it.copy(isJoining = false) }
        }
    }

}