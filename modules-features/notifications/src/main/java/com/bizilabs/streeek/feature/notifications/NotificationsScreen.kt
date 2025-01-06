package com.bizilabs.streeek.feature.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.feature.notifications.components.NotificationItemComponent
import com.bizilabs.streeek.lib.common.components.paging.SafiPagingComponent
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.domain.models.NotificationDomain
import com.bizilabs.streeek.lib.resources.strings.SafiStrings

object NotificationsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = getScreenModel<NotificationsScreenModel>()
        val state by screenModel.state.collectAsStateWithLifecycle()
        val notifications = screenModel.notifications.collectAsLazyPagingItems()
        NotificationsScreenComponent(
            state = state,
            notifications = notifications,
            onClickNavigateBack = { navigator?.pop() },
            onClickNotification = screenModel::onClickNotification,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreenComponent(
    state: NotificationsScreenState,
    notifications: LazyPagingItems<NotificationDomain>,
    onClickNavigateBack: () -> Unit,
    onClickNotification: (NotificationDomain) -> Unit,
) {
    Scaffold(
        topBar = {
            NotificationsScreenHeader(
                modifier = Modifier.fillMaxWidth(),
                onClickNavigateBack = onClickNavigateBack,
            )
        },
    ) { innerPadding ->
        NotificationsContent(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            notifications = notifications,
            onClickNotification = onClickNotification,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreenHeader(
    onClickNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(),
                        onClick = onClickNavigateBack,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "navigate back",
                        )
                    }
                },
                title = {
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = stringResource(SafiStrings.Labels.Notifications).uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                    )
                },
            )
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationsContent(
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
                SmallFloatingActionButton(
                    onClick = { notifications.refresh() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null)
                }
            }
        },
    ) { notification ->
        NotificationItemComponent(
            notification = notification,
            onClickNotification = onClickNotification,
        )
    }
}
