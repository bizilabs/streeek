package com.bizilabs.streeek.feature.tabs.screens.notifications

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material.icons.rounded.People
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.feature.tabs.screens.teams.RequestTeamState
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.DialogState
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.NotificationDomain
import com.bizilabs.streeek.lib.domain.models.team.MemberAccountRequestDomain
import com.bizilabs.streeek.lib.domain.repositories.NotificationRepository
import com.bizilabs.streeek.lib.domain.repositories.team.TeamRequestRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module
import kotlin.enums.EnumEntries

internal val ModuleNotifications =
    module {
        factory<NotificationsScreenModel> {
            NotificationsScreenModel(notificationsRepository = get(), teamRequestRepository = get())
        }
    }

enum class NotificationSection {
    GENERAL,
    REQUESTS,
    ;

    val label: String
        get() =
            when (this) {
                GENERAL -> "General"
                REQUESTS -> "Requests"
            }

    val icon: Pair<ImageVector, ImageVector>
        get() =
            when (this) {
                GENERAL -> Pair(Icons.Outlined.Inbox, Icons.Rounded.Inbox)
                REQUESTS -> Pair(Icons.Outlined.People, Icons.Rounded.People)
            }
}

data class RequestTeamState(
    val teamId: Long,
    val requestState: FetchState<Boolean> = FetchState.Loading,
)

data class NotificationsScreenState(
    val notifications: List<NotificationDomain> = emptyList(),
    val sections: EnumEntries<NotificationSection> = NotificationSection.entries,
    val section: NotificationSection = NotificationSection.GENERAL,
    val dialogState: DialogState? = null,
    val requestState: RequestTeamState? = null,
    val cancelledTeamIds: List<Long> = emptyList(),
    val selectedTeamIds: List<Long> = emptyList(),
) {
    val selectedSectionIndex: Int
        get() = sections.indexOf(section)
}

class NotificationsScreenModel(
    private val notificationsRepository: NotificationRepository,
    private val teamRequestRepository: TeamRequestRepository,
) : StateScreenModel<NotificationsScreenState>(NotificationsScreenState()) {
    val notifications = notificationsRepository.notifications
    val requests = teamRequestRepository.getMyRequests()

    fun onClickSection(section: NotificationSection) {
        mutableState.update { it.copy(section = section) }
    }

    fun onClickCancelRequest(request: MemberAccountRequestDomain) {
        cancelJoinRequest(request = request)
    }

    private fun cancelJoinRequest(request: MemberAccountRequestDomain) {
        screenModelScope.launch {
            mutableState.update { it.copy(requestState = RequestTeamState(teamId = request.team.id)) }
            val result = teamRequestRepository.cancelRequest(id = request.request.id)
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
                                it.requestState?.copy(
                                    requestState = FetchState.Error(result.message),
                                ),
                        )
                    }
                    delay(2000)
                    mutableState.update { it.copy(dialogState = null, requestState = null) }
                }

                is DataResult.Success -> {
                    val requests = state.value.cancelledTeamIds.toMutableList()
                    requests.add(request.team.id)
                    mutableState.update {
                        it.copy(
                            cancelledTeamIds = requests,
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
