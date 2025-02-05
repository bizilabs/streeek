package com.bizilabs.streeek.feature.reminders.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.lib.design.components.SafiButton
import com.bizilabs.streeek.lib.design.components.SafiOutlinedButton
import com.bizilabs.streeek.lib.design.theme.SafiTheme
import com.bizilabs.streeek.lib.domain.helpers.SystemLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerComponent(
    onConfirm: (Int, Int) -> Unit,
    onDismiss: (Int, Int) -> Unit,
) {
    val currentTime = SystemLocalDateTime

    val timePickerState =
        rememberTimePickerState(
            initialHour = currentTime.hour,
            initialMinute = currentTime.minute,
            is24Hour = true,
        )

    Surface(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            TimePicker(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                state = timePickerState,
                colors =
                    TimePickerDefaults.colors(
                        timeSelectorSelectedContentColor = MaterialTheme.colorScheme.primary,
                        timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary.copy(0.1f),
                    ),
            )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SafiOutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onDismiss(timePickerState.hour, timePickerState.minute) },
                ) {
                    Text("dismiss")
                }
                SafiButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onConfirm(timePickerState.hour, timePickerState.minute) },
                ) {
                    Text("confirm")
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TimePickerDialogPreview() {
    SafiTheme {
        Surface {
            TimePickerComponent(
                onConfirm = { _, _ -> },
                onDismiss = { _, _ -> },
            )
        }
    }
}
