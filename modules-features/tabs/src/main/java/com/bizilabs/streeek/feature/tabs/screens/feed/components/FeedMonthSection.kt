package com.bizilabs.streeek.feature.tabs.screens.feed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.feature.tabs.screens.feed.FeedScreenState
import com.bizilabs.streeek.feature.tabs.screens.feed.MonthAction
import com.bizilabs.streeek.feature.tabs.screens.feed.asStreakPosition
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.YearMonth
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@Composable
fun FeedMonthViewSection(
    state: FeedScreenState,
    modifier: Modifier = Modifier,
    onClickDate: (LocalDate) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val selectedDate = state.selectedDate
    val currentMonth = YearMonth(state.selectedDate.year, selectedDate.month)
    val startMonth = remember { currentMonth.minusMonths(3) }
    val endMonth = remember { currentMonth.plusMonths(3) }
    val calendarState =
        rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth,
        )

    fun scrollToMonth(month: YearMonth) {
        scope.launch {
            calendarState.animateScrollToMonth(month = month)
        }
    }

    Column {
        FeedCalendarMonthsHeader(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            month = calendarState.firstVisibleMonth,
            onClickMonthAction = {
                when (it) {
                    MonthAction.PREVIOUS -> {
                        scrollToMonth(calendarState.firstVisibleMonth.yearMonth.minusMonths(1))
                    }

                    MonthAction.NEXT -> {
                        scrollToMonth(calendarState.firstVisibleMonth.yearMonth.plusMonths(1))
                    }
                }
            },
        )
        HorizontalCalendar(
            modifier = modifier,
            state = calendarState,
            dayContent = { day: CalendarDay ->
                FeedCalendarItemComponent(
                    hasContribution = state.dates.contains(day.date),
                    isMonthView = true,
                    modifier = Modifier.fillMaxWidth(),
                    day = day.date,
                    selectedDate = selectedDate,
                    onClickDate = onClickDate,
                    streakPosition = day.date.asStreakPosition(state.dates),
                )
            },
            monthHeader = {
                FeedCalendarDaysHeader(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    calendarMonth = it,
                )
            },
        )
    }
}

@Composable
fun FeedCalendarDaysHeader(
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
fun FeedCalendarMonthsHeader(
    month: CalendarMonth,
    modifier: Modifier = Modifier,
    onClickMonthAction: (MonthAction) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = { onClickMonthAction(MonthAction.PREVIOUS) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                contentDescription = "previous month",
            )
        }
        Text(
            text = month.yearMonth.month.name,
            style = MaterialTheme.typography.titleSmall,
        )
        IconButton(onClick = { onClickMonthAction(MonthAction.NEXT) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "next month",
            )
        }
    }
}
