package com.bizilabs.streeek.feature.tabs.screens.feed

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CalendarViewWeek
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.bizilabs.streeek.feature.tabs.screens.feed.components.FeedCalendarItemComponent
import com.bizilabs.streeek.feature.tabs.screens.feed.components.FeedContributionItemComponent
import com.bizilabs.streeek.feature.tabs.screens.feed.components.FeedMonthViewSection
import com.bizilabs.streeek.lib.common.helpers.requestSinglePermission
import com.bizilabs.streeek.lib.design.components.SafiBottomAction
import com.bizilabs.streeek.lib.design.components.SafiBottomValue
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.domain.models.ContributionDomain
import com.kizitonwose.calendar.compose.WeekCalendar
import kotlinx.datetime.LocalDate

object FeedScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel: FeedScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()
        val date by screenModel.date.collectAsStateWithLifecycle()
        val contributions by screenModel.contributions.collectAsStateWithLifecycle(emptyList())

        FeedScreenContent(
            state = state,
            date = date,
            contributions = contributions,
            onClickDate = screenModel::onClickDate,
            onRefreshContributions = screenModel::onRefreshContributions,
            onClickToggleMonthView = screenModel::onClickToggleMonthView,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun FeedScreenContent(
    state: FeedScreenState,
    date: LocalDate,
    contributions: List<ContributionDomain>,
    onClickDate: (LocalDate) -> Unit,
    onRefreshContributions: () -> Unit,
    onClickToggleMonthView: () -> Unit,
) {
    val activity = LocalContext.current as ComponentActivity

    val pullRefreshState =
        rememberPullRefreshState(
            refreshing = state.isSyncing,
            onRefresh = onRefreshContributions,
        )

    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    FeedHeader(
                        selectedDate = date,
                        state = state,
                        onClickToggleMonthView = onClickToggleMonthView,
                    )
                    AnimatedContent(
                        modifier = Modifier.fillMaxWidth(),
                        targetState = state.isMonthView,
                        label = "animated month",
                    ) { isMonthView ->
                        when (isMonthView) {
                            true -> {
                                FeedMonthViewSection(
                                    state = state,
                                    onClickDate = onClickDate,
                                )
                            }

                            false -> {
                                WeekCalendar(
                                    dayContent = { weekDay ->
                                        FeedCalendarItemComponent(
                                            hasContribution = state.dates.contains(weekDay.date),
                                            isMonthView = isMonthView,
                                            modifier = Modifier.fillMaxWidth(),
                                            day = weekDay.date,
                                            selectedDate = date,
                                            onClickDate = onClickDate,
                                            streakPosition = weekDay.date.asStreakPosition(state.dates),
                                        )
                                    },
                                )
                            }
                        }
                    }

                    HorizontalDivider()
                }
            }
        },
        snackbarHost = {
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = state.isPermissionGranted.not(),
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                SafiBottomAction(
                    title = "Enable Notifications",
                    description = "We can't seem to send you notifications. Please enable them for a better  experience",
                    icon = Icons.Filled.Notifications,
                    primaryAction =
                        SafiBottomValue("enable") {
                            activity.requestSinglePermission(permission = android.Manifest.permission.POST_NOTIFICATIONS)
                        },
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = innerPadding.calculateTopPadding())
                    .pullRefresh(pullRefreshState),
        ) {
            FeedContent(
                state = state,
                contributions = contributions,
                onRefreshContributions = onRefreshContributions,
            )

            PullRefreshIndicator(
                backgroundColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.background,
                refreshing = state.isSyncing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}

@Composable
private fun FeedContent(
    state: FeedScreenState,
    contributions: List<ContributionDomain>,
    onRefreshContributions: () -> Unit,
) {
    AnimatedContent(
        modifier = Modifier.fillMaxSize(),
        targetState = state.isFetchingContributions,
        label = "list_animation",
    ) {
        when (it) {
            true -> {
                SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }

            false -> {
                when {
                    contributions.isEmpty() -> {
                        SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                            SafiCenteredColumn {
                                SafiInfoSection(
                                    icon = Icons.Rounded.PushPin,
                                    title = "No Contributions Found",
                                    description =
                                        if (state.isToday) {
                                            "You haven't been busy today... Push some few commits!"
                                        } else {
                                            "Seems you we\'ren\'t busy on ${
                                                buildString {
                                                    append(
                                                        state.selectedDate.dayOfWeek.name.lowercase()
                                                            .replaceFirstChar { it.uppercase() },
                                                    )
                                                    append(" ")
                                                    append(state.selectedDate.dayOfMonth)
                                                    append(" ")
                                                    append(
                                                        state.selectedDate.month.name.lowercase()
                                                            .replaceFirstChar { it.uppercase() },
                                                    )
                                                    append(" ")
                                                    append(state.selectedDate.year)
                                                }
                                            }"
                                        },
                                )
                                AnimatedVisibility(
                                    visible = state.isSyncing.not() && state.isToday,
                                ) {
                                    SmallFloatingActionButton(onClick = onRefreshContributions) {
                                        Icon(
                                            imageVector = Icons.Rounded.Refresh,
                                            contentDescription = "refresh",
                                        )
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(contributions) { contribution ->
                                FeedContributionItemComponent(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                    contribution = contribution,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedHeader(
    selectedDate: LocalDate,
    state: FeedScreenState,
    onClickToggleMonthView: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier =
                Modifier
                    .padding(start = 4.dp)
                    .clickable { onClickToggleMonthView() }
                    .padding(start = 12.dp)
                    .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (state.isMonthView.not()) Icons.Rounded.CalendarMonth else Icons.Rounded.CalendarViewWeek,
                contentDescription = "pin",
                tint = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text =
                    (if (state.isToday) "Today" else selectedDate.month.name)
                        .lowercase()
                        .replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Icon(
                modifier = Modifier.padding(end = 16.dp),
                imageVector = if (state.isMonthView.not()) Icons.Rounded.KeyboardArrowDown else Icons.Rounded.KeyboardArrowUp,
                contentDescription = "toggle down or up",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }

        SafiCenteredRow(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(
                modifier = Modifier.padding(end = 8.dp),
                text = (state.account?.streak?.current ?: 0).toString(),
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
            )
            Icon(
                imageVector = Icons.Rounded.LocalFireDepartment,
                contentDescription = "pin",
                tint = Color(0xFFFF4F00),
            )
        }
    }
}
