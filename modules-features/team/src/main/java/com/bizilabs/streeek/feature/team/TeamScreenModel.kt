package com.bizilabs.streeek.feature.team

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.People
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.paging.PagingData
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.common.components.paging.getPagingDataLoading
import com.bizilabs.streeek.lib.common.models.FetchListState
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.DialogState
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.domain.models.TeamDetailsDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberRole
import com.bizilabs.streeek.lib.domain.models.TeamWithMembersDomain
import com.bizilabs.streeek.lib.domain.models.asTeamDetails
import com.bizilabs.streeek.lib.domain.models.sortedByRank
import com.bizilabs.streeek.lib.domain.models.team.CreateTeamInvitationDomain
import com.bizilabs.streeek.lib.domain.models.team.JoinTeamInvitationDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamAccountJoinRequestDomain
import com.bizilabs.streeek.lib.domain.models.team.TeamInvitationDomain
import com.bizilabs.streeek.lib.domain.repositories.TeamInvitationRepository
import com.bizilabs.streeek.lib.domain.repositories.TeamRepository
import com.bizilabs.streeek.lib.domain.repositories.team.TeamRequestRepository
import com.bizilabs.streeek.lib.domain.workers.startImmediateSyncTeamsWork
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module

val FeatureTeamModule =
    module {
        factory {
            TeamScreenModel(
                context = get(),
                teamRepository = get(),
                teamInvitationRepository = get(),
                teamRequestRepository = get(),
            )
        }
    }

enum class TeamRequestAction {
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

enum class SelectionAction {
    SELECT_ALL,
    CLEAR_ALL,
}

enum class TeamMenuAction {
    EDIT,
    DELETE,
    INVITE,
    LEAVE,
    ;

    companion object {
        fun get(role: TeamMemberRole): List<TeamMenuAction> {
            return when (role.isAdmin) {
                true -> entries.toList()
                false -> listOf(LEAVE)
            }
        }
    }

    val label: String
        get() =
            when (this) {
                EDIT -> "Edit"
                DELETE -> "Delete"
                INVITE -> "Invite"
                LEAVE -> "Leave"
            }

    val icon: ImageVector
        get() =
            when (this) {
                EDIT -> Icons.Rounded.Edit
                DELETE -> Icons.Rounded.Delete
                INVITE -> Icons.Rounded.People
                LEAVE -> Icons.AutoMirrored.Rounded.ExitToApp
            }
}

data class ProcessRequestDomain(
    val requestIds: List<Long>,
    val fetchState: FetchState<Boolean> = FetchState.Loading,
)

data class TeamScreenState(
    val isSyncing: Boolean = false,
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
    val isRequestsSheetOpen: Boolean = false,
    val isLoadingInvitationsPartially: Boolean = false,
    val invitationsState: FetchListState<TeamInvitationDomain> = FetchListState.Loading,
    val createInvitationState: FetchState<CreateTeamInvitationDomain>? = null,
    val joinTeamState: FetchState<JoinTeamInvitationDomain>? = null,
    val team: TeamDetailsDomain? = null,
    val showConfetti: Boolean = false,
    val selectedRequestIds: List<Long> = listOf(),
    val processingSingleRequestsState: Map<Long, FetchState<Boolean>> = emptyMap(),
    val processingMultipleRequestsState: ProcessRequestDomain? = null,
    val processedRequests: Map<String, List<Long>> = emptyMap(),
) {
    val isManagingTeam: Boolean
        get() = isEditing || teamId == null

    val isPublic: Boolean
        get() = value.equals("public", true)

    val isValidName: Boolean
        get() = name.matches("^[a-z]+$".toRegex()) && name.length > 3

    val isActionEnabled: Boolean
        get() =
            if (fetchState is FetchState.Success) {
                val team = fetchState.value.team
                name.isNotBlank() && (
                    !team.name.equals(
                        name,
                        ignoreCase = false,
                    ) || team.public != isPublic
                )
            } else {
                isValidName && value.isNotBlank()
            }

    val isJoinActionEnabled: Boolean
        get() = token.length == 6 && dialogState == null

    val list: List<TeamMemberDomain>
        get() =
            when {
                team == null -> emptyList()
                team.page == 1 ->
                    team.members.filterIndexed { index, _ -> index > 2 }
                        .sortedByRank()

                else -> team.members.sortedByRank()
            }
}

class TeamScreenModel(
    private val context: Context,
    private val teamRepository: TeamRepository,
    private val teamInvitationRepository: TeamInvitationRepository,
    private val teamRequestRepository: TeamRequestRepository,
) : StateScreenModel<TeamScreenState>(TeamScreenState()) {
    private var _pages = MutableStateFlow(getPagingDataLoading<TeamMemberDomain>())
    val pages: StateFlow<PagingData<TeamMemberDomain>> = _pages.asStateFlow()

    private var _requests: Flow<PagingData<TeamAccountJoinRequestDomain>> =
        MutableStateFlow(getPagingDataLoading<TeamAccountJoinRequestDomain>()).asStateFlow()
    val requests: Flow<PagingData<TeamAccountJoinRequestDomain>>
        get() = _requests

    private val clickedTeam =
        combine(teamRepository.teamId, teamRepository.teams) { id, map -> map[id] }

    init {
        observeTeamsSyncing()
    }

    private fun observeTeamsSyncing() {
        screenModelScope.launch {
            teamRepository.isSyncing.collectLatest { value ->
                mutableState.update { it.copy(isSyncing = value) }
            }
        }
    }

    fun setNavigationVariables(
        isJoining: Boolean,
        teamId: Long?,
    ) {
        if (state.value.hasAlreadyUpdatedNavVariables) return
        mutableState.update {
            it.copy(
                isJoining = isJoining,
                teamId = teamId,
                hasAlreadyUpdatedNavVariables = true,
            )
        }
        teamId?.let {
            getTeam(id = it)
            observeTeamDetails()
            observeTeamRequests()
        }
    }

    private fun observeTeamRequests() {
        val id = state.value.teamId ?: return
        _requests = teamRequestRepository.getTeamRequests(teamId = id)
    }

    private fun observeTeamDetails() {
        screenModelScope.launch {
            clickedTeam.collectLatest { value ->
                if (value != null) {
                    val paging = teamRepository.getPagedData(team = value).first()
                    mutableState.update { it.copy(team = value) }
                    _pages.update { paging }
                }
                if (value?.rank?.current == 1L) showConfetti()
            }
        }
    }

    private fun showConfetti() {
        screenModelScope.launch {
            mutableState.update { it.copy(showConfetti = true) }
            delay(2000)
            mutableState.update { it.copy(showConfetti = false) }
        }
    }

    private fun dismissDialog() {
        mutableState.update { it.copy(dialogState = null) }
    }

    private fun getTeam(
        id: Long,
        shouldSaveTeam: Boolean = false,
    ) {
        screenModelScope.launch {
            val update =
                when (val result = teamRepository.getTeam(id = id, page = 1)) {
                    is DataResult.Error -> FetchState.Error(result.message)
                    is DataResult.Success -> {
                        val team = result.data.team
                        onValueChangeName(name = team.name)
                        onValueChangePublic(value = if (team.public) "public" else "private")
                        if (shouldSaveTeam) saveTeam(team = result.data.copy(members = result.data.members))
                        FetchState.Success(result.data.copy(members = result.data.members.sortedByRank()))
                    }
                }
            mutableState.update { it.copy(fetchState = update) }
        }
    }

    private fun saveTeam(team: TeamWithMembersDomain) {
        screenModelScope.launch {
            val teamDetails = team.asTeamDetails(page = 1)
            mutableState.update { it.copy(team = teamDetails) }
            teamRepository.addTeamLocally(team = team.asTeamDetails(page = 1))
        }
    }

    private fun createTeam() {
        val value = state.value
        val name = value.name
        val public = value.isPublic
        mutableState.update { it.copy(dialogState = DialogState.Loading()) }
        screenModelScope.launch {
            val update =
                when (val result = teamRepository.createTeam(name, public)) {
                    is DataResult.Error ->
                        DialogState.Error(
                            title = "Error",
                            message = result.message,
                        )

                    is DataResult.Success -> {
                        val teamId = result.data
                        mutableState.update { it.copy(teamId = teamId) }
                        getTeam(id = teamId, shouldSaveTeam = true)
                        DialogState.Success(
                            title = "Success",
                            message = "Created team successfully",
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
            val update =
                when (val result = teamRepository.updateTeam(teamId, name, public)) {
                    is DataResult.Error ->
                        DialogState.Error(
                            title = "Error",
                            message = result.message,
                        )

                    is DataResult.Success -> {
                        getTeam(id = teamId)
                        DialogState.Success(
                            title = "Success",
                            message = "Updated team successfully",
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
                            isJoining = false,
                            teamId = result.data.teamId,
                            dialogState =
                                DialogState.Success(
                                    title = "Success",
                                    message = "Joined team successfully as a ${result.data.role}",
                                ),
                        )
                    }
                    getTeam(id = result.data.teamId, shouldSaveTeam = true)
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
                                    message = "Left team successfully. \nHope you come back soon!",
                                ),
                        )
                    }
                    delay(2000)
                    mutableState.update { it.copy(dialogState = null, shouldNavigateBack = true) }
                }
            }
        }
    }

    private fun deleteTeam() {
        val teamId = state.value.teamId ?: return
        mutableState.update { it.copy(dialogState = DialogState.Loading()) }
        screenModelScope.launch {
            when (val result = teamRepository.deleteTeam(teamId)) {
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
                                    message = "Team deleted successfully!",
                                ),
                            shouldNavigateBack = true,
                        )
                    }
                }
            }
        }
    }

    // <editor-fold desc="team invitations">
    private fun createInvitationCode() {
        val teamId = state.value.teamId ?: return
        screenModelScope.launch {
            if (state.value.invitationsState !is FetchListState.Success) {
                mutableState.update { it.copy(invitationsState = FetchListState.Loading) }
            }
            mutableState.update { it.copy(createInvitationState = FetchState.Loading) }
            val update =
                when (
                    val result =
                        teamInvitationRepository.createInvitation(
                            teamId = teamId,
                            duration = 86400,
                        )
                ) {
                    is DataResult.Error -> {
                        mutableState.update {
                            it.copy(
                                invitationsState =
                                    FetchListState.Error(
                                        message = result.message,
                                    ),
                            )
                        }
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
            if (state.value.invitationsState is FetchListState.Success) {
                mutableState.update { it.copy(isLoadingInvitationsPartially = true) }
            } else {
                mutableState.update { it.copy(invitationsState = FetchListState.Loading) }
            }
            val update =
                when (val result = teamInvitationRepository.getInvitations(teamId = teamId)) {
                    is DataResult.Error -> FetchListState.Error(result.message)
                    is DataResult.Success -> {
                        val list = result.data
                        if (list.isEmpty()) {
                            FetchListState.Empty
                        } else {
                            FetchListState.Success(list = list)
                        }
                    }
                }
            mutableState.update {
                it.copy(
                    invitationsState = update,
                    isLoadingInvitationsPartially = false,
                )
            }
        }
    }

    fun onDismissInvitationsSheet() {
        mutableState.update { it.copy(isInvitationsOpen = false) }
    }

    fun onDismissRequestsSheet() {
        mutableState.update { it.copy(isRequestsSheetOpen = false) }
    }

    fun onClickInvitationGet() {
        getInvitations()
    }

    fun onClickInvitationRetry() {
        if (state.value.invitationsState is FetchListState.Empty) {
            createInvitationCode()
        } else {
            getInvitations()
        }
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

    // </editor-fold>

    // <editor-fold desc="actions">
    fun onClickMenuAction(menu: TeamMenuAction) {
        when (menu) {
            TeamMenuAction.EDIT -> {
                mutableState.update { it.copy(isEditing = true) }
            }

            TeamMenuAction.DELETE -> {
                deleteTeam()
            }

            TeamMenuAction.INVITE -> {
                onClickInviteMore()
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
        if (state.value.teamId != null) {
            updateTeam()
            mutableState.update { it.copy(isEditing = false) }
        } else {
            createTeam()
        }
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

    fun onClickInviteMore() {
        mutableState.update { it.copy(isInvitationsOpen = true) }
        if (state.value.invitationsState !is FetchListState.Success) getInvitations()
    }

    fun onRefreshTeams() {
        context.startImmediateSyncTeamsWork()
    }

    fun onClickRequests() {
        mutableState.update { it.copy(isRequestsSheetOpen = true) }
    }

    fun onClickToggleSelectRequest(request: TeamAccountJoinRequestDomain) {
        val list = (state.value.selectedRequestIds).toMutableList()
        if (list.contains(request.request.id)) {
            list.remove(request.request.id)
        } else {
            list.add(request.request.id)
        }
        mutableState.update { it.copy(selectedRequestIds = list) }
    }

    fun onClickSelectedRequestsSelection(
        action: SelectionAction,
        list: List<TeamAccountJoinRequestDomain>,
    ) {
        when (action) {
            SelectionAction.SELECT_ALL -> selectAllRequests(list = list)
            SelectionAction.CLEAR_ALL -> clearAllSelectedRequests()
        }
    }

    @SuppressLint("CheckResult")
    private fun selectAllRequests(list: List<TeamAccountJoinRequestDomain>) {
        mutableState.update { it.copy(selectedRequestIds = list.map { it.request.id }) }
    }

    private fun clearAllSelectedRequests() {
        mutableState.update { it.copy(selectedRequestIds = listOf()) }
    }

    fun onClickProcessSelectedRequests(value: Boolean) {
        val action =
            when (value) {
                true -> TeamRequestAction.ACCEPT
                false -> TeamRequestAction.REJECT
            }
        processSelectedRequests(action = action)
    }

    private fun processSelectedRequests(action: TeamRequestAction) {
        val selectedRequestIds = state.value.selectedRequestIds
        if (selectedRequestIds.isEmpty()) return
        val teamId = state.value.teamId ?: return
        screenModelScope.launch {
            mutableState.update {
                it.copy(processingMultipleRequestsState = ProcessRequestDomain(requestIds = selectedRequestIds))
            }
            val result =
                teamRequestRepository.processMultipleRequest(
                    teamId = teamId,
                    requestIds = selectedRequestIds,
                    status = action.value,
                )
            when (result) {
                is DataResult.Error -> {
                    mutableState.update {
                        it.copy(
                            processingMultipleRequestsState =
                                it.processingMultipleRequestsState?.copy(
                                    fetchState =
                                        FetchState.Error(result.message),
                                ),
                        )
                    }
                }

                is DataResult.Success -> {
                    mutableState.update {
                        it.copy(
                            processingMultipleRequestsState =
                                it.processingMultipleRequestsState?.copy(
                                    fetchState =
                                        FetchState.Success(true),
                                ),
                        )
                    }
                }
            }
            delay(2000)
            val fetchState = state.value.processingMultipleRequestsState?.fetchState
            mutableState.update {
                it.copy(processingMultipleRequestsState = null, selectedRequestIds = emptyList())
            }
            if (fetchState is FetchState.Success) {
                val processed = state.value.processedRequests.toMutableMap()
                val list = processed[action.value]?.toMutableList() ?: mutableListOf()
                list.addAll(selectedRequestIds)
                processed[action.value] = list
                mutableState.update { it.copy(processedRequests = processed) }
            }
        }
    }

    fun onClickProcessRequest(
        value: TeamAccountJoinRequestDomain,
        action: TeamRequestAction,
    ) {
        processRequest(value = value, action = action)
    }

    private fun getMutableMap() = state.value.processingSingleRequestsState.toMutableMap()

    @OptIn(InternalCoroutinesApi::class)
    private fun processRequest(
        value: TeamAccountJoinRequestDomain,
        action: TeamRequestAction,
    ) {
        val teamId = state.value.teamId ?: return
        screenModelScope.launch {
            val map = getMutableMap()
            map[value.request.id] = FetchState.Loading
            mutableState.update { it.copy(processingSingleRequestsState = map) }
            val result =
                teamRequestRepository.processSingleRequest(
                    teamId = teamId,
                    requestId = value.request.id,
                    status = action.value,
                )
            when (result) {
                is DataResult.Error -> {
                    val map = getMutableMap()
                    map[value.request.id] = FetchState.Error(result.message)
                    mutableState.update { it.copy(processingSingleRequestsState = map) }
                }

                is DataResult.Success -> {
                    val map = getMutableMap()
                    map[value.request.id] = FetchState.Success(true)
                    mutableState.update { it.copy(processingSingleRequestsState = map) }
                }
            }
            delay(2000)
            val update = getMutableMap()
            val fetchState = update[value.request.id]
            update.remove(value.request.id)
            if (fetchState is FetchState.Success) {
                mutableState.update { it.copy(processingSingleRequestsState = update) }
                val processed = state.value.processedRequests.toMutableMap()
                val list = processed[action.value]?.toMutableList() ?: mutableListOf()
                list.add(value.request.id)
                processed[action.value] = list
                mutableState.update { it.copy(processedRequests = processed) }
            }
        }
    }

    // </editor-fold>
}
