package com.bizilabs.streeek.feature.reminders.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.rounded.AccessTimeFilled
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.feature.reminders.list.ReminderListScreenState
import com.bizilabs.streeek.lib.design.components.SafiButton
import com.bizilabs.streeek.lib.design.theme.SafiTheme
import com.bizilabs.streeek.lib.resources.strings.SafiStrings
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderEditBottomSheet(
    state: ReminderListScreenState,
    onValueChangeReminderLabel: (String) -> Unit,
    onClickReminderDayOfWeek: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onOpenTimePicker: () -> Unit,
    onCreateReminder: () -> Unit,
    onDismissTimePicker: (Int, Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun hideSheet() {
        scope.launch { sheetState.hide() }
    }

    if (state.isTimePickerOpen) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { },
            title = { Text("Pick Time") },
            text = {
                TimePickerComponent(
                    onConfirm = { hour, minute ->
                        onDismissTimePicker(hour, minute)
                    },
                    onDismiss = { hour, minute ->
                        onDismissTimePicker(hour, minute)
                    },
                )
            },
            confirmButton = {},
            dismissButton = {},
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = {
            hideSheet()
            onDismiss()
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        ReminderBottomSheetContent(
            state = state,
            onValueChangeReminderLabel = onValueChangeReminderLabel,
            onClickReminderDayOfWeek = onClickReminderDayOfWeek,
            onOpenTimePicker = onOpenTimePicker,
            onCreateReminder = onCreateReminder,
        )
    }
}

@Composable
private fun ReminderBottomSheetContent(
    state: ReminderListScreenState,
    onValueChangeReminderLabel: (String) -> Unit,
    onClickReminderDayOfWeek: (DayOfWeek) -> Unit,
    onCreateReminder: () -> Unit,
    onOpenTimePicker: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Reminder".uppercase(),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            TextField(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                value = state.label,
                onValueChange = onValueChangeReminderLabel,
                label = { Text(text = "Label") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Label,
                        contentDescription = "update label",
                    )
                },
                isError = !state.isValidLabel && state.label.isNotBlank(),
                supportingText = {
                    if (!state.isValidLabel && state.label.isNotBlank()) {
                        Text(text = stringResource(SafiStrings.InvalidReminderLabel))
                    }
                },
                trailingIcon = {
                    if (!state.isValidLabel && state.label.isNotBlank()) {
                        Icon(Icons.Default.Error, "error")
                    }
                },
            )
            TextField(
                value = state.time ?: "",
                onValueChange = {},
                label = { Text(text = "Time") },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .clickable {
                            onOpenTimePicker()
                        },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.AccessTimeFilled,
                        contentDescription = "update time",
                    )
                },
                trailingIcon = {
                },
                enabled = false,
            )
            Text(
                text = "Repeat",
                modifier = Modifier.fillMaxWidth(),
            )
            LazyRow(
                modifier =
                    Modifier
                        .fillMaxWidth(),
            ) {
                items(DayOfWeek.entries) { day ->
                    val isSelected = state.selectedDays.contains(day)
                    Card(
                        modifier = Modifier.padding(end = 4.dp),
                        onClick = { onClickReminderDayOfWeek(day) },
                        border =
                            BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
                            ),
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            text = day.name.take(3),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
            SafiButton(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                onClick = onCreateReminder,
                enabled = state.isEditActionEnabled,
            ) {
                Text(text = if (state.selectedReminder != null) "update" else "create")
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ReminderEditBottomSheetPreview() {
    SafiTheme {
        Scaffold(
            bottomBar = {
                Surface {
                    ReminderBottomSheetContent(
                        state = ReminderListScreenState(),
                        onValueChangeReminderLabel = {},
                        onClickReminderDayOfWeek = {},
                        onCreateReminder = {},
                        onOpenTimePicker = {},
                    )
                }
            },
        ) {
            Box(modifier = Modifier.padding(it))
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ReminderEditBottomSheetSelectedPreview() {
    SafiTheme {
        Scaffold(
            bottomBar = {
                Surface {
                    ReminderBottomSheetContent(
                        state =
                            ReminderListScreenState(
                                label = "poach",
                                selectedHour = 13,
                                selectedMinute = 15,
                                selectedDays = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
                            ),
                        onValueChangeReminderLabel = {},
                        onClickReminderDayOfWeek = {},
                        onCreateReminder = {},
                        onOpenTimePicker = {},
                    )
                }
            },
        ) {
            Box(modifier = Modifier.padding(it))
        }
    }
}
