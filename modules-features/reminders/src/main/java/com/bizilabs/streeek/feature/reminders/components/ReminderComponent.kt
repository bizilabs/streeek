package com.bizilabs.streeek.feature.reminders.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.lib.design.theme.SafiTheme
import com.bizilabs.streeek.lib.domain.extensions.asMinimumTwoValues
import com.bizilabs.streeek.lib.domain.models.ReminderDomain
import java.time.DayOfWeek

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReminderComponent(
    reminder: ReminderDomain,
    modifier: Modifier = Modifier,
    onClick: (ReminderDomain) -> Unit,
    onLongClick: (ReminderDomain) -> Unit,
) {
    val containerColor =
        if (reminder.enabled) MaterialTheme.colorScheme.primary else CardDefaults.cardColors().containerColor
    val contentColor =
        if (reminder.enabled) MaterialTheme.colorScheme.onPrimary else CardDefaults.cardColors().contentColor

    Card(
        modifier = modifier,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(0.2f)),
        colors =
            CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { onClick(reminder) },
                        onLongClick = { onLongClick(reminder) },
                    )
                    .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Label,
                    contentDescription = "${reminder.label} label",
                )
                Text(modifier = Modifier.padding(start = 16.dp), text = reminder.label)
            }
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = "${reminder.hour.toLong().asMinimumTwoValues()}:${
                    reminder.minute.toLong().asMinimumTwoValues()
                }",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
            )
            LazyRow {
                items(DayOfWeek.entries) { day ->
                    val isSelected = reminder.repeat.contains(day)
                    val backgroundColor =
                        when {
                            reminder.enabled && isSelected -> MaterialTheme.colorScheme.onPrimary
                            isSelected -> MaterialTheme.colorScheme.onSurface
                            else -> Color.Transparent
                        }
                    val borderColor =
                        when {
                            reminder.enabled && isSelected -> MaterialTheme.colorScheme.onPrimary
                            reminder.enabled -> MaterialTheme.colorScheme.onPrimary
                            isSelected -> MaterialTheme.colorScheme.onSurface
                            else -> MaterialTheme.colorScheme.onSurface
                        }

                    val textColor =
                        when {
                            reminder.enabled && isSelected -> MaterialTheme.colorScheme.onSurface
                            reminder.enabled -> MaterialTheme.colorScheme.onPrimary
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    Box(
                        modifier =
                            Modifier
                                .padding(end = 4.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .border(1.dp, borderColor, MaterialTheme.shapes.medium)
                                .background(backgroundColor),
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            text = day.name.take(3),
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ReminderComponentPreview() {
    SafiTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                ReminderComponent(
                    reminder =
                        ReminderDomain(
                            label = "Oontz",
                            enabled = false,
                            hour = 1,
                            minute = 4,
                            repeat = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                        ),
                    onClick = {},
                    onLongClick = {},
                )
                Spacer(modifier = Modifier.padding(16.dp))
                ReminderComponent(
                    reminder =
                        ReminderDomain(
                            label = "Oontz",
                            enabled = false,
                            hour = 10,
                            minute = 18,
                            repeat = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                        ),
                    onClick = {},
                    onLongClick = {},
                )
                Spacer(modifier = Modifier.padding(16.dp))
                ReminderComponent(
                    reminder =
                        ReminderDomain(
                            label = "Oontz",
                            enabled = true,
                            hour = 21,
                            minute = 56,
                            repeat = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                        ),
                    onClick = {},
                    onLongClick = {},
                )
            }
        }
    }
}
