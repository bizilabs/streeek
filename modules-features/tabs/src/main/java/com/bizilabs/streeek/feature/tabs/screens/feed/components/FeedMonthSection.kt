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
import kotlinx.datetime.LocalDate

@Composable
fun FeedMonthViewSection(
    state: FeedScreenState,
    modifier: Modifier = Modifier,
    onClickMonthAction: (MonthAction) -> Unit,
    onClickDate: (LocalDate) -> Unit,
) {
    val selectedDate = state.selectedDate
    val calendarState =
        rememberCalendarState(
            startMonth =
                YearMonth(
                    state.selectedDate.year,
                    selectedDate.month,
                ),
            firstVisibleMonth =
                YearMonth(
                    state.selectedDate.year,
                    selectedDate.month,
                ),
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
        monthFooter = {
            FeedMonthFooter(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                state = state,
                onClickMonthAction = onClickMonthAction,
            )
        },
        monthHeader = {
            FeedMonthHeader(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                calendarMonth = it,
            )
        },
    )
}

@Composable
fun FeedMonthHeader(
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
fun FeedMonthFooter(
    state: FeedScreenState,
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
            text = state.selectedDate.month.name,
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
