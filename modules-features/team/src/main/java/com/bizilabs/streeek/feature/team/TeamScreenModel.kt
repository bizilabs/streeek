package com.bizilabs.streeek.feature.team

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.People
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.common.models.FetchListState
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.DialogState
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.domain.models.TeamWithMembersDomain
import com.bizilabs.streeek.lib.domain.models.team.CreateTeamInvitationDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamInvitationDomain
import com.bizilabs.streeek.lib.domain.repositories.TeamInvitationRepository
import com.bizilabs.streeek.lib.domain.repositories.TeamRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module

val FeatureTeamModule = module {
    factory { TeamScreenModel(teamRepository = get(), teamInvitationRepository = get()) }
}

enum class TeamMenuAction {
    EDIT, DELETE, INVITE;

    val label: String
        get() = when (this) {
            EDIT -> "Edit"
            DELETE -> "Delete"
            INVITE -> "Invite"
        }

    val icon: ImageVector
        get() = when (this) {
            EDIT -> Icons.Rounded.Edit
            DELETE -> Icons.Rounded.Delete
            INVITE -> Icons.Rounded.People
        }

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
    val fetchState: FetchState<TeamWithMembersDomain> = FetchState.Loading,
    val isInvitationsOpen: Boolean = false,
    val isLoadingInvitationsPartially: Boolean = false,
    val invitationsState: FetchListState<TeamInvitationDomain> = FetchListState.Loading,
    val createInvitationState: FetchState<CreateTeamInvitationDomain>? = null,
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
    private val teamRepository: TeamRepository,
    private val teamInvitationRepository: TeamInvitationRepository
) : StateScreenModel<TeamScreenState>(TeamScreenState()) {

    private fun dismissDialog() {
        mutableState.update { it.copy(dialogState = null) }
    }

    private fun getTeam(id: Long) {
        screenModelScope.launch {
            val update = when (val result = teamRepository.getTeam(id = id, page = 1)) {
                is DataResult.Error -> FetchState.Error(result.message)
                is DataResult.Success -> FetchState.Success(result.data)
            }
            mutableState.update { it.copy(fetchState = update) }
        }
    }

    //<editor-fold desc="team invitations">
    private fun createInvitationCode() {
        val teamId = state.value.teamId ?: return
        screenModelScope.launch {
            if (state.value.invitationsState !is FetchListState.Success)
                mutableState.update { it.copy(invitationsState = FetchListState.Loading) }
            mutableState.update { it.copy(createInvitationState = FetchState.Loading) }
            val update = when (val result = teamInvitationRepository.createInvitation(
                teamId = teamId,
                duration = 86400
            )) {
                is DataResult.Error -> {
                    mutableState.update { it.copy(invitationsState = FetchListState.Error(message = result.message)) }
                    FetchState.Error(result.message)
                }

                is DataResult.Success -> FetchState.Success(result.data)
            }
            mutableState.update { it.copy(createInvitationState = update) }
            if (update is FetchState.Success) getInvitations()
            delay(5000)
            mutableState.update { it.copy(createInvitationState = null) }
        }
    }

    private fun getInvitations() {
        val teamId = state.value.teamId ?: return
        screenModelScope.launch {
            if (state.value.invitationsState is FetchListState.Success)
                mutableState.update { it.copy(isLoadingInvitationsPartially = true) }
            else
                mutableState.update { it.copy(invitationsState = FetchListState.Loading) }
            val update =
                when (val result = teamInvitationRepository.getInvitations(teamId = teamId)) {
                    is DataResult.Error -> FetchListState.Error(result.message)
                    is DataResult.Success -> {
                        val list = result.data
                        if (list.isEmpty())
                            FetchListState.Empty
                        else
                            FetchListState.Success(list = list)
                    }
                }
            mutableState.update { it.copy(invitationsState = update) }
        }
    }

    fun onDismissInvitationsSheet() {
        mutableState.update { it.copy(isInvitationsOpen = false) }
    }

    fun onClickInvitationGet() {
        getInvitations()
    }

    fun onClickInvitationRetry() {
        if (state.value.invitationsState is FetchListState.Empty)
            createInvitationCode()
        else
            getInvitations()
    }

    fun onClickInvitationCreate() {
        createInvitationCode()
    }

    fun onSwipeInvitationDelete(invitation: TeamInvitationDomain) {
        val list = (state.value.invitationsState as FetchListState.Success).list.toMutableList()
        list.remove(invitation)
        mutableState.update { it.copy(invitationsState = FetchListState.Success(list = list)) }
        screenModelScope.launch {
            val result = teamInvitationRepository.deleteInvitation(id = invitation.id)
            if (result is DataResult.Error) {
                list.add(invitation)
                mutableState.update { it.copy(invitationsState = FetchListState.Success(list = list)) }
            }
        }
    }

    //</editor-fold>

    fun setTeamId(teamId: Long?) {
        if (state.value.hasAlreadySetTeamId) return
        mutableState.update { it.copy(teamId = teamId, hasAlreadySetTeamId = true) }
        teamId?.let { getTeam(id = it) }
    }

    fun onClickMenuAction(menu: TeamMenuAction) {
        when (menu) {
            TeamMenuAction.EDIT -> {}
            TeamMenuAction.DELETE -> {}
            TeamMenuAction.INVITE -> {
                mutableState.update { it.copy(isInvitationsOpen = true) }
                if (state.value.invitationsState !is FetchListState.Success) getInvitations()
            }
        }
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

    fun onClickManageAction() {
        val value = state.value
        val name = value.name
        val public = value.isPublic
        mutableState.update { it.copy(dialogState = DialogState.Loading()) }
        screenModelScope.launch {
            val update = when (val result = teamRepository.createTeam(name, public)) {
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
