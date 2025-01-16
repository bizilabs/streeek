package com.bizilabs.streeek.feature.tabs.screens.notifications

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material.icons.rounded.People
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.model.StateScreenModel
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.design.components.DialogState
import com.bizilabs.streeek.lib.domain.models.NotificationDomain
import com.bizilabs.streeek.lib.domain.repositories.NotificationRepository
import com.bizilabs.streeek.lib.domain.repositories.team.TeamRequestRepository
import kotlinx.coroutines.flow.update
import org.koin.dsl.module
import kotlin.enums.EnumEntries

internal val ModuleNotifications =
    module {
        factory<NotificationsScreenModel> {
            NotificationsScreenModel(notificationsRepository = get(), teamRequestRepository = get())
        }
    }

enum class NotificationSection {
    GENERAL, REQUESTS;

    val label: String
        get() = when (this) {
            GENERAL -> "General"
            REQUESTS -> "Requests"
        }

    val icon: Pair<ImageVector, ImageVector>
        get() = when (this) {
            GENERAL -> Pair(Icons.Outlined.Inbox, Icons.Rounded.Inbox)
            REQUESTS -> Pair(Icons.Outlined.People, Icons.Rounded.People)
        }

}

data class RequestTeamState(
    val teamId: Long,
    val requestState: FetchState<Boolean> = FetchState.Loading
)

data class NotificationsScreenState(
    val notifications: List<NotificationDomain> = emptyList(),
    val sections: EnumEntries<NotificationSection> = NotificationSection.entries,
    val section: NotificationSection = NotificationSection.GENERAL,
    val dialogState: DialogState? = null,
    val requestState: RequestTeamState? = null,
    val requestedTeamIds: List<Long> = emptyList(),
    val selectedTeamIds: List<Long> = emptyList()
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

}