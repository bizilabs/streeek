package com.bizilabs.streeek.feature.team

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.DialogState
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.domain.models.TeamWithMembersDomain
import com.bizilabs.streeek.lib.domain.repositories.TeamRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module
import timber.log.Timber

val FeatureTeamModule = module {
    factory { TeamScreenModel(repository = get()) }
}

data class TeamScreenState(
    val account: AccountDomain? = null,
    val hasAlreadySetTeamId: Boolean = false,
    val teamId: Long? = null,
    val name: String = "",
    val isOpen: Boolean = false,
    val visibilityOptions: List<String> = listOf("public", "private"),
    val value: String = "public",
    val dialogState: DialogState? = null,
    val fetchState: FetchState<TeamWithMembersDomain> = FetchState.Loading
) {
    val isCreate: Boolean
        get() = teamId == null

    val isPublic: Boolean
        get() = value.equals("public", true)

    val isActionEnabled: Boolean
        get() = name.isNotBlank() && value.isNotBlank()

    val isMenusVisible: Boolean
        get() = fetchState is FetchState.Success && fetchState.value.details.role.isAdmin

}

class TeamScreenModel(
    private val repository: TeamRepository
) : StateScreenModel<TeamScreenState>(TeamScreenState()) {

    private fun dismissDialog() {
        mutableState.update { it.copy(dialogState = null) }
    }

    private fun getTeam(id: Long) {
        screenModelScope.launch {
            val update = when (val result = repository.getTeam(id = id, page = 1)) {
                is DataResult.Error -> FetchState.Error(result.message)
                is DataResult.Success -> FetchState.Success(result.data)
            }
            mutableState.update { it.copy(fetchState = update) }
        }
    }

    fun setTeamId(teamId: Long?) {
        if (state.value.hasAlreadySetTeamId) return
        Timber.d("Setting Team ID -> $teamId")
        mutableState.update { it.copy(teamId = teamId, hasAlreadySetTeamId = true) }
        teamId?.let { getTeam(id = it) }
    }

    fun onValueChangeName(name: String) {
        mutableState.update { it.copy(name = name) }
    }

    fun onValueChangePublic(value: String) {
        mutableState.update { it.copy(value = value) }
        onValueChangePublicDropDown(isOpen = false)
    }

    fun onValueChangePublicDropDown(isOpen: Boolean) {
        mutableState.update { it.copy(isOpen = isOpen) }
    }

    fun onClickDismissDialog() {
        dismissDialog()
    }

    fun onClickAction() {
        val value = state.value
        val name = value.name
        val public = value.isPublic
        mutableState.update { it.copy(dialogState = DialogState.Loading()) }
        screenModelScope.launch {
            val update = when (val result = repository.createTeam(name, public)) {
                is DataResult.Error -> DialogState.Error(title = "Error", message = result.message)
                is DataResult.Success -> {
                    val teamId = result.data
                    getTeam(id = teamId)
                    DialogState.Success(
                        title = "Success",
                        message = "Created team successfully"
                    )
                }
            }
            mutableState.update { it.copy(dialogState = update) }
        }
    }

}
