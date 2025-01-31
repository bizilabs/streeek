package com.bizilabs.streeek.feature.tabs.screens.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.bizilabs.streeek.feature.tabs.screens.notifications.components.NotificationItemComponent
import com.bizilabs.streeek.feature.tabs.screens.notifications.components.NotificationRequestsSection
import com.bizilabs.streeek.lib.common.components.paging.SafiPagingComponent
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.design.components.SafiTopBarHeader
import com.bizilabs.streeek.lib.domain.models.NotificationDomain
import com.bizilabs.streeek.lib.domain.models.team.MemberAccountRequestDomain
import com.bizilabs.streeek.lib.resources.strings.SafiStrings
import kotlinx.coroutines.launch

object NotificationsScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel: NotificationsScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()
        val notifications = screenModel.notifications.collectAsLazyPagingItems()
        val requests = screenModel.requests.collectAsLazyPagingItems()
        NotificationsScreenContent(
            state = state,
            notifications = notifications,
            requests = requests,
            onClickNotification = {},
            onClickCancelRequest = screenModel::onClickCancelRequest,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreenContent(
    state: NotificationsScreenState,
    notifications: LazyPagingItems<NotificationDomain>,
    requests: LazyPagingItems<MemberAccountRequestDomain>,
    onClickNotification: (NotificationDomain) -> Unit,
    onClickCancelRequest: (MemberAccountRequestDomain) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = state.sections.indexOf(state.section)) { 2 }

    Scaffold(
        topBar = {
            NotificationsScreenHeader(
                state = state,
                pagerState = pagerState,
            )
        },
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
        ) { index ->
            when (index) {
                0 -> {
                    NotificationsScreenSection(
                        modifier = Modifier.fillMaxSize(),
                        notifications = notifications,
                        onClickNotification = onClickNotification,
                    )
                }

                else -> {
                    NotificationRequestsSection(
                        modifier = Modifier.fillMaxSize(),
                        state = state,
                        data = requests,
                        onClickCancelRequest = onClickCancelRequest,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreenHeader(
    state: NotificationsScreenState,
    pagerState: PagerState,
) {
    val coroutineScope = rememberCoroutineScope()
    Surface {
        Column {
            SafiTopBarHeader(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                title = stringResource(SafiStrings.Labels.Notifications),
                align = TextAlign.Center,
            )
            TabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = pagerState.currentPage,
            ) {
                state.sections.forEachIndexed { index, section ->
                    val isSelected = pagerState.currentPage == index
                    val (unselectedIcon, selectedIcon) = section.icon
                    Tab(
                        selected = isSelected,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(state.sections.indexOf(section))
                            }
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor =
                            MaterialTheme.colorScheme.onSurface.copy(0.25f),
                    ) {
                        SafiCenteredRow(modifier = Modifier.padding(16.dp)) {
                            Icon(
                                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                                contentDescription = section.label,
                            )
                            Spacer(modifier = Modifier.padding(8.dp))
                            Text(
                                text = section.label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            )
                        }
                    }
                }
            }
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
