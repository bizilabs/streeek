package com.bizilabs.streeek.feature.tabs.screens.feed

import android.R.attr.contentDescription
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CalendarViewWeek
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Notifications
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.bizilabs.streeek.feature.tabs.screens.feed.components.ContributionItemComponent
import com.bizilabs.streeek.feature.tabs.screens.feed.components.FeedMonthView
import com.bizilabs.streeek.feature.tabs.screens.feed.components.MonthFooter
import com.bizilabs.streeek.lib.common.helpers.requestSinglePermission
import com.bizilabs.streeek.lib.design.components.SafiBottomAction
import com.bizilabs.streeek.lib.design.components.SafiBottomValue
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
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.YearMonth
import com.kizitonwose.calendar.core.now
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import java.util.jar.Manifest

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
            onClickMonthAction = screenModel::onClickMonthAction
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
    onClickMonthAction: (MonthAction) -> Unit
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
                                FeedMonthView(
                                    state = state,
                                    onClickMonthAction = onClickMonthAction,
                                    onClickDate = onClickDate,
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
fun CalendarItem(
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
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            modifier = Modifier
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
                text = (if (state.isToday) "Today" else selectedDate.month.name)
                    .lowercase()
                    .replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
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
