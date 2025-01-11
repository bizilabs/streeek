package com.bizilabs.streeek.feature.tabs.screens.feed

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CalendarViewWeek
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PushPin
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.feature.tabs.screens.feed.components.ContributionItemComponent
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.design.components.SafiTopBarHeader
import com.bizilabs.streeek.lib.design.helpers.onSuccess
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.domain.helpers.dayShort
import com.bizilabs.streeek.lib.domain.helpers.isSameDay
import com.bizilabs.streeek.lib.domain.models.ContributionDomain
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.now
import kotlinx.datetime.LocalDate

object FeedScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenNotifications = rememberScreen(SharedScreen.Notifications)

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
            onClosesNotifications = { navigator?.push(screenNotifications) },
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
    onClosesNotifications: () -> Unit,
) {
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
                        onClickNotifications = onClosesNotifications,
                    )
                    AnimatedContent(
                        modifier = Modifier.fillMaxWidth(),
                        targetState = state.isMonthView,
                        label = "animated month",
                    ) { isMonthView ->
                        when (isMonthView) {
                            true -> {
                                HorizontalCalendar(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 8.dp),
                                    dayContent = { day: CalendarDay ->
                                        CalendarItem(
                                            hasContribution = state.dates.contains(day.date),
                                            isMonthView = isMonthView,
                                            modifier = Modifier.fillMaxWidth(),
                                            day = day.date,
                                            selectedDate = date,
                                            onClickDate = onClickDate,
                                        )
                                    },
                                    monthHeader = {
                                        MonthHeader(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 8.dp),
                                            calendarMonth = it,
                                        )
                                    },
                                )
                            }

                            false -> {
                                WeekCalendar(
                                    dayContent = { weekDay ->
                                        CalendarItem(
                                            hasContribution = state.dates.contains(weekDay.date),
                                            isMonthView = isMonthView,
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                            day = weekDay.date,
                                            selectedDate = date,
                                            onClickDate = onClickDate,
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
                                ContributionItemComponent(
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
private fun MonthHeader(
    calendarMonth: CalendarMonth,
    modifier: Modifier = Modifier,
) {
    val daysOfWeek = calendarMonth.weekDays.first().map { it.date.dayOfWeek }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (dayOfWeek in daysOfWeek) {
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    text = dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() },
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun CalendarItem(
    isMonthView: Boolean,
    hasContribution: Boolean,
    selectedDate: LocalDate,
    day: LocalDate,
    modifier: Modifier = Modifier,
    onClickDate: (LocalDate) -> Unit,
) {
    val date = day
    val isToday = date.isSameDay(LocalDate.now())
    val isSelected = selectedDate == date

    val isFutureDate = day > LocalDate.now()
    val isBeforeInceptionDate = day < LocalDate(year = 2025, monthNumber = 1, dayOfMonth = 1)

    val border =
        when {
            isSelected -> BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            else -> BorderStroke(0.dp, Color.Transparent)
        }
    val containerColor =
        when {
            isSelected -> MaterialTheme.colorScheme.primary
            isToday -> MaterialTheme.colorScheme.onSurface.copy(0.25f)
            else -> Color.Transparent
        }
    val contentColor =
        when {
            isSelected -> MaterialTheme.colorScheme.onPrimary
            isToday -> MaterialTheme.colorScheme.onSurface
            else -> MaterialTheme.colorScheme.onSurface
        }

    val enabled = isFutureDate.not() && isBeforeInceptionDate.not()

    Card(
        modifier = modifier,
        onClick = { onClickDate(date) },
        enabled = enabled,
        border = border,
        colors =
            CardDefaults.cardColors(
                contentColor = contentColor,
                containerColor = containerColor,
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                disabledContainerColor = Color.Transparent,
            ),
    ) {
        SafiCenteredColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
        ) {
            AnimatedVisibility(visible = isMonthView.not()) {
                Text(
                    text = date.dayShort,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                )
            }
            Card(
                shape = CircleShape,
                enabled = enabled,
                onClick = { onClickDate(date) },
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            when {
                                isSelected -> containerColor
                                hasContribution -> MaterialTheme.colorScheme.success
                                else -> Color.Transparent
                            },
                        contentColor =
                            when {
                                isSelected -> contentColor
                                hasContribution -> MaterialTheme.colorScheme.onSuccess
                                else -> contentColor
                            },
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                        disabledContainerColor = Color.Transparent,
                    ),
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = if (date.dayOfMonth < 10) "0${date.dayOfMonth}" else "${date.dayOfMonth}",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun FeedHeader(
    selectedDate: LocalDate,
    state: FeedScreenState,
    onClickToggleMonthView: () -> Unit,
    onClickNotifications: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SafiTopBarHeader(
            modifier = Modifier.padding(start = 24.dp).weight(1f),
            title = if (state.isToday) "Today" else selectedDate.month.name,
        )

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

        IconButton(
            modifier = Modifier.padding(end = 8.dp),
            onClick = onClickToggleMonthView,
        ) {
            Icon(
                imageVector = if (state.isMonthView.not()) Icons.Rounded.CalendarMonth else Icons.Rounded.CalendarViewWeek,
                contentDescription = "pin",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }

        IconButton(
            modifier = Modifier.padding(end = 16.dp),
            onClick = onClickNotifications,
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "notifications",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
