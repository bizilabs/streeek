package com.bizilabs.streeek.feature.reminders.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerComponent(
    onConfirm: (Int, Int) -> Unit,
    onDismiss: (Int, Int) -> Unit,
) {
    val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val timePickerState =
        rememberTimePickerState(
            initialHour = currentTime.hour,
            initialMinute = currentTime.minute,
            is24Hour = true,
        )

    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
    ) {
        TimePicker(
            state = timePickerState,
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onDismiss(timePickerState.hour, timePickerState.minute) },
        ) {
            Text("Dismiss picker")
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onConfirm(timePickerState.hour, timePickerState.minute) },
        ) {
            Text("Confirm selection")
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TimePickerDialogPreview() {
    TimePickerComponent(
        onConfirm = { _, _ -> },
        onDismiss = { _, _ -> },
    )
}
