package com.bizilabs.streeek.feature.notifications

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.domain.models.NotificationDomain
import com.bizilabs.streeek.lib.resources.strings.SafiStrings

object NotificationsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = getScreenModel<NotificationsScreenModel>()
        val state by screenModel.state.collectAsStateWithLifecycle()
        NotificationsScreenComponent(
            state = state,
            onClickNavigateBack = { navigator?.pop() },
            onClickTab = screenModel::onClickTab,
            onRefreshNotifications = screenModel::onRefreshNotifications,
            onClickNotification = screenModel::onClickNotification
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreenComponent(
    state: NotificationsScreenState,
    onClickNavigateBack: () -> Unit,
    onClickTab: (NotificationsTab) -> Unit,
    onRefreshNotifications: () -> Unit,
    onClickNotification: (NotificationDomain) -> Unit
) {
    Scaffold(
        topBar = {
            NotificationsScreenHeader(
                state = state,
                modifier = Modifier.fillMaxWidth(),
                onClickNavigateBack = onClickNavigateBack,
                onClickTab = onClickTab,
            )
        }
    ) { innerPadding ->
        NotificationsContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            state = state,
            onClickNotification = onClickNotification,
            onRefreshNotifications = onRefreshNotifications
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreenHeader(
    state: NotificationsScreenState,
    onClickNavigateBack: () -> Unit,
    onClickTab: (NotificationsTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(),
                        onClick = onClickNavigateBack,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "navigate back"
                        )
                    }
                },
                title = {
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = stringResource(SafiStrings.Labels.Notifications).uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                }
            )

            TabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = state.tabs.indexOf(state.tab)
            ) {
                state.tabs.forEach { tab ->
                    val isSelected = tab == state.tab
//                    val (selectedIcon, unselectedIcon) = tab.icon
                    Tab(
                        selected = isSelected,
                        onClick = { onClickTab(tab) },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(0.25f)
                    ) {
                        SafiCenteredRow(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = tab.label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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
fun NotificationsContent(
    state: NotificationsScreenState,
    onRefreshNotifications: () -> Unit,
    onClickNotification:(NotificationDomain) -> Unit,
    modifier: Modifier = Modifier
) {

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isSyncing,
        onRefresh = onRefreshNotifications
    )

    AnimatedContent(
        modifier = modifier,
        targetState = state.notifications,
        label = "animate notifications"
    ) { notifications ->
        when {
            state.pastInitial.not() -> {
                SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }

            notifications.isEmpty() -> {
                SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                    SafiInfoSection(
                        icon = Icons.Rounded.Notifications,
                        title = "No Notifications",
                        description = "You haven't received any notifications yet."
                    ) {
                        SmallFloatingActionButton(
                            onClick = onRefreshNotifications,
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "refresh notifications"
                            )
                        }
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState)
                ) {
                    LazyColumn {
                        items(notifications) { notification ->
                            val isRead = notification.readAt != null
                            val containerColor = if (isRead)
                                MaterialTheme.colorScheme.background
                            else
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { onClickNotification(notification) },
                                shape = RectangleShape,
                                colors = CardDefaults.cardColors(
                                    contentColor = MaterialTheme.colorScheme.onBackground,
                                    containerColor = containerColor
                                )
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = notification.title,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(text = notification.message)
                                    }
                                    HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                                }
                            }
                        }
                    }
                    PullRefreshIndicator(
                        backgroundColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.background,
                        refreshing = state.isSyncing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }


    }
}
