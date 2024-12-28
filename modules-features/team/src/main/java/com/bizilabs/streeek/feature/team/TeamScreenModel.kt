package com.bizilabs.streeek.feature.team

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
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
import com.bizilabs.streeek.lib.domain.models.TeamMemberRole
import com.bizilabs.streeek.lib.domain.models.TeamWithMembersDomain
import com.bizilabs.streeek.lib.domain.models.team.CreateTeamInvitationDomain
import com.bizilabs.streeek.lib.domain.models.team.JoinTeamInvitationDomain
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

    EDIT, DELETE, INVITE, LEAVE;

    companion object {
        fun get(role: TeamMemberRole): List<TeamMenuAction> {
            return when (role.isAdmin) {
                true -> entries.toList()
                false -> listOf(LEAVE)
            }
        }
    }

    val label: String
        get() = when (this) {
            EDIT -> "Edit"
            DELETE -> "Delete"
            INVITE -> "Invite"
            LEAVE -> "Leave"
        }

    val icon: ImageVector
        get() = when (this) {
            EDIT -> Icons.Rounded.Edit
            DELETE -> Icons.Rounded.Delete
            INVITE -> Icons.Rounded.People
            LEAVE -> Icons.AutoMirrored.Rounded.ExitToApp
        }

}

data class TeamScreenState(
    val isEditing: Boolean = false,
    val isJoining: Boolean = false,
    val shouldNavigateBack: Boolean = false,
    val account: AccountDomain? = null,
    val hasAlreadyUpdatedNavVariables: Boolean = false,
    val teamId: Long? = null,
    val token: String = "",
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
    val joinTeamState: FetchState<JoinTeamInvitationDomain>? = null,
) {
    val isManagingTeam: Boolean
        get() = isEditing || teamId == null

    val isPublic: Boolean
        get() = value.equals("public", true)

    val isActionEnabled: Boolean
        get() = if (fetchState is FetchState.Success) {
            val team = fetchState.value.team
            name.isNotBlank() && (!team.name.equals(
                name,
                ignoreCase = false
            ) || team.public != isPublic)
        } else {
            name.isNotBlank() && value.isNotBlank()
        }

    val isJoinActionEnabled: Boolean
        get() = token.length == 6 && dialogState == null

}

class TeamScreenModel(
    private val teamRepository: TeamRepository,
    private val teamInvitationRepository: TeamInvitationRepository
) : StateScreenModel<TeamScreenState>(TeamScreenState()) {

    fun setNavigationVariables(isJoining: Boolean, teamId: Long?) {
        if (state.value.hasAlreadyUpdatedNavVariables) return
        mutableState.update {
            it.copy(
                isJoining = isJoining,
                teamId = teamId,
                hasAlreadyUpdatedNavVariables = true
            )
        }
        teamId?.let { getTeam(id = it) }
    }

    private fun dismissDialog() {
        mutableState.update { it.copy(dialogState = null) }
    }

    private fun getTeam(id: Long) {
        screenModelScope.launch {
            val update = when (val result = teamRepository.getTeam(id = id, page = 1)) {
                is DataResult.Error -> FetchState.Error(result.message)
                is DataResult.Success -> {
                    val team = result.data.team
                    onValueChangeName(name = team.name)
                    onValueChangePublic(value = if (team.public) "public" else "private")
                    FetchState.Success(result.data)
                }
            }
            mutableState.update { it.copy(fetchState = update) }
        }
    }

    private fun createTeam() {
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

    private fun updateTeam() {
        val value = state.value
        val teamId = value.teamId ?: return
        val name = value.name
        val public = value.isPublic
        mutableState.update { it.copy(dialogState = DialogState.Loading()) }
        screenModelScope.launch {
            val update = when (val result = teamRepository.updateTeam(teamId, name, public)) {
                is DataResult.Error -> DialogState.Error(title = "Error", message = result.message)
                is DataResult.Success -> {
                    getTeam(id = teamId)
                    DialogState.Success(
                        title = "Success",
                        message = "Updated team successfully"
                    )
                }
            }
            mutableState.update { it.copy(dialogState = update) }
        }
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
                            dialogState = DialogState.Error(
                                title = "Error",
                                message = result.message
                            )
                        )
                    }
                }

                is DataResult.Success -> {
                    mutableState.update {
                        it.copy(
                            isJoining = false,
                            teamId = result.data.teamId,
                            dialogState = DialogState.Success(
                                title = "Success",
                                message = "Joined team successfully as a ${result.data.role}"
                            )
                        )
                    }
                    getTeam(id = result.data.teamId)
                    delay(2000)
                    mutableState.update { it.copy(dialogState = null) }
                }
            }
        }
    }

    private fun leaveTeam() {
        val teamId = state.value.teamId ?: return
        mutableState.update { it.copy(dialogState = DialogState.Loading()) }
        screenModelScope.launch {
            when (val result = teamRepository.leaveTeam(teamId = teamId)) {
                is DataResult.Error -> {
                    mutableState.update {
                        it.copy(
                            dialogState = DialogState.Error(
                                title = "Error",
                                message = result.message
                            )
                        )
                    }
                }

                is DataResult.Success -> {
                    mutableState.update {
                        it.copy(
                            dialogState = DialogState.Success(
                                title = "Success",
                                message = "Left team successfully. \nHope you come back soon!"
                            )
                        )
                    }
                    delay(2000)
                    mutableState.update { it.copy(dialogState = null, shouldNavigateBack = true) }
                }
            }
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
            mutableState.update {
                it.copy(
                    invitationsState = update,
                    isLoadingInvitationsPartially = false
                )
            }
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

    //<editor-fold desc="actions">
    fun onClickMenuAction(menu: TeamMenuAction) {
        when (menu) {
            TeamMenuAction.EDIT -> {
                mutableState.update { it.copy(isEditing = true) }
            }

            TeamMenuAction.DELETE -> {}
            TeamMenuAction.INVITE -> {
                mutableState.update { it.copy(isInvitationsOpen = true) }
                if (state.value.invitationsState !is FetchListState.Success) getInvitations()
            }

            TeamMenuAction.LEAVE -> {
                leaveTeam()
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
        if (state.value.teamId != null)
            updateTeam()
        else
            createTeam()
    }

    fun onClickManageCancelAction() {
        mutableState.update { it.copy(isEditing = false) }
    }

    fun onClickManageDeleteAction() {
    }

    fun onValueChangeTeamCode(value: String) {
        mutableState.update { it.copy(token = value) }
        if (value.length == 6) joinTeam()
    }

    fun onClickJoin() {
        joinTeam()
    }

    //</editor-fold>

}
