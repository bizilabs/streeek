package com.bizilabs.streeek.feature.tabs.screens.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.bizilabs.streeek.feature.tabs.screens.notifications.components.NotificationItemComponent
import com.bizilabs.streeek.lib.common.components.paging.SafiPagingComponent
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.design.components.SafiTopBarHeader
import com.bizilabs.streeek.lib.domain.models.NotificationDomain
import com.bizilabs.streeek.lib.resources.strings.SafiStrings

object NotificationsScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel: NotificationsScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()
        val notifications = screenModel.notifications.collectAsLazyPagingItems()
        NotificationsScreenContent(
            state = state,
            notifications = notifications,
            onClickNotification = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreenContent(
    state: NotificationsScreenState,
    notifications: LazyPagingItems<NotificationDomain>,
    onClickNotification: (NotificationDomain) -> Unit,
) {
    Scaffold(
        topBar = { NotificationsScreenHeader(modifier = Modifier.fillMaxWidth()) },
    ) { innerPadding ->
        NotificationsScreenSection(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            notifications = notifications,
            onClickNotification = onClickNotification,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreenHeader(
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TopAppBar(
                title = { SafiTopBarHeader(title = stringResource(SafiStrings.Labels.Notifications)) },
            )
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationsScreenSection(
    notifications: LazyPagingItems<NotificationDomain>,
    onClickNotification: (NotificationDomain) -> Unit,
    modifier: Modifier = Modifier,
) {
    SafiPagingComponent(
        modifier = modifier,
        data = notifications,
        refreshEmpty = {
            SafiInfoSection(
                icon = Icons.Rounded.Notifications,
                title = stringResource(SafiStrings.Labels.NoNotifications),
                description = stringResource(SafiStrings.Messages.NoNotifications),
            ) {
            }
        },
    ) { notification ->
        NotificationItemComponent(
            notification = notification,
            onClickNotification = onClickNotification,
        )
    }
}
