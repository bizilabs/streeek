package com.bizilabs.streeek.feature.tabs.screens.feed.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.feature.tabs.screens.feed.StreakPosition
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.helpers.onSuccess
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.design.theme.SafiTheme
import com.bizilabs.streeek.lib.domain.helpers.dayShort
import com.bizilabs.streeek.lib.domain.helpers.isSameDay
import com.kizitonwose.calendar.core.now
import kotlinx.datetime.LocalDate

fun StreakPosition?.asShape(): RoundedCornerShape {
    return when (this) {
        StreakPosition.FIRST -> RoundedCornerShape(16.dp, 0.dp, 0.dp, 16.dp)
        StreakPosition.MIDDLE -> RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp)
        StreakPosition.LAST -> RoundedCornerShape(0.dp, 16.dp, 16.dp, 0.dp)
        StreakPosition.ALONE -> RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp)
        null -> RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp)
    }
}

@Composable
fun FeedCalendarItemComponent(
    isMonthView: Boolean,
    hasContribution: Boolean,
    selectedDate: LocalDate,
    day: LocalDate,
    streakPosition: StreakPosition?,
    modifier: Modifier = Modifier,
    onClickDate: (LocalDate) -> Unit,
) {
    val date = day
    val isToday = date.isSameDay(LocalDate.now())
    val isSelected = selectedDate == date

    val isFutureDate = day > LocalDate.now()
    val isBeforeInceptionDate = day < LocalDate(year = 2025, monthNumber = 1, dayOfMonth = 1)

    val containerColor =
        when {
            isSelected -> MaterialTheme.colorScheme.primary
            isToday -> MaterialTheme.colorScheme.primary.copy(0.25f)
            else -> Color.Transparent
        }

    val enabled = isFutureDate.not() && isBeforeInceptionDate.not()

    Card(
        modifier = modifier,
        onClick = { onClickDate(date) },
        enabled = enabled,
        shape = streakPosition.asShape(),
        colors =
            CardDefaults.cardColors(
                containerColor = Color.Transparent,
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                disabledContainerColor = Color.Transparent,
            ),
    ) {
        SafiCenteredColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            AnimatedVisibility(visible = isMonthView.not()) {
                Text(
                    text = date.dayShort,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = streakPosition.asShape(),
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
                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                hasContribution -> MaterialTheme.colorScheme.onSuccess
                                else -> MaterialTheme.colorScheme.onSurface
                            },
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                        disabledContainerColor = Color.Transparent,
                    ),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    text = if (date.dayOfMonth < 10) "0${date.dayOfMonth}" else "${date.dayOfMonth}",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FeedCalendarItemComponentMonthPreview() {
    SafiTheme {
        Surface {
            SafiCenteredColumn(modifier = Modifier.padding(16.dp)) {
                Text(text = "same day no contribution")
                FeedCalendarItemComponent(
                    modifier = Modifier.padding(vertical = 8.dp),
                    isMonthView = true,
                    hasContribution = false,
                    selectedDate = LocalDate(2025, 1, 1),
                    day = LocalDate(2025, 1, 1),
                    streakPosition = StreakPosition.FIRST,
                    onClickDate = {},
                )

                Text(text = "same day with contribution")
                FeedCalendarItemComponent(
                    modifier = Modifier.padding(vertical = 8.dp),
                    isMonthView = true,
                    hasContribution = true,
                    selectedDate = LocalDate(2025, 1, 1),
                    day = LocalDate(2025, 1, 1),
                    streakPosition = StreakPosition.FIRST,
                    onClickDate = {},
                )

                Text(text = "different day with contribution")
                FeedCalendarItemComponent(
                    modifier = Modifier.padding(vertical = 8.dp),
                    isMonthView = true,
                    hasContribution = true,
                    selectedDate = LocalDate(2025, 1, 1),
                    day = LocalDate(2025, 1, 2),
                    streakPosition = StreakPosition.FIRST,
                    onClickDate = {},
                )

                Text(text = "different day with no contribution")
                FeedCalendarItemComponent(
                    modifier = Modifier.padding(vertical = 8.dp),
                    isMonthView = true,
                    hasContribution = false,
                    selectedDate = LocalDate(2025, 1, 1),
                    day = LocalDate(2025, 1, 2),
                    streakPosition = StreakPosition.FIRST,
                    onClickDate = {},
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FeedCalendarItemComponentDayPreview() {
    SafiTheme {
        Surface {
            SafiCenteredColumn(modifier = Modifier.padding(16.dp)) {
                Text(text = "same day no contribution")
                FeedCalendarItemComponent(
                    modifier = Modifier.padding(vertical = 8.dp),
                    isMonthView = false,
                    hasContribution = false,
                    selectedDate = LocalDate(2025, 1, 1),
                    day = LocalDate(2025, 1, 1),
                    streakPosition = StreakPosition.FIRST,
                    onClickDate = {},
                )

                Text(text = "same day with contribution")
                FeedCalendarItemComponent(
                    modifier = Modifier.padding(vertical = 8.dp),
                    isMonthView = false,
                    hasContribution = true,
                    selectedDate = LocalDate(2025, 1, 1),
                    day = LocalDate(2025, 1, 1),
                    streakPosition = StreakPosition.FIRST,
                    onClickDate = {},
                )

                Text(text = "different day with contribution")
                FeedCalendarItemComponent(
                    modifier = Modifier.padding(vertical = 8.dp),
                    isMonthView = false,
                    hasContribution = true,
                    selectedDate = LocalDate(2025, 1, 1),
                    day = LocalDate(2025, 1, 2),
                    streakPosition = StreakPosition.FIRST,
                    onClickDate = {},
                )

                Text(text = "different day with no contribution")
                FeedCalendarItemComponent(
                    modifier = Modifier.padding(vertical = 8.dp),
                    isMonthView = false,
                    hasContribution = false,
                    selectedDate = LocalDate(2025, 1, 1),
                    day = LocalDate(2025, 1, 2),
                    streakPosition = StreakPosition.FIRST,
                    onClickDate = {},
                )
            }
        }
    }
}
