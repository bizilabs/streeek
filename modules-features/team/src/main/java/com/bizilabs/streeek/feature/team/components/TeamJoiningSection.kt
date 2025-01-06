package com.bizilabs.streeek.feature.team.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.feature.team.TeamScreenState
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.design.components.SafiOTPField

@Composable
fun TeamJoiningSection(
    state: TeamScreenState,
    modifier: Modifier = Modifier,
    onValueChangeTeamCode: (String) -> Unit,
    onClickJoin: () -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "Enter Team Code",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "Enter the 6 digit code you received from a team admin to proceed",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(0.75f),
        )
        SafiCenteredRow(modifier = Modifier.fillMaxWidth()) {
            SafiOTPField(
                modifier =
                    Modifier
                        .fillMaxWidth(0.75f)
                        .padding(vertical = 24.dp),
                text = state.token,
                onClickDone = onClickJoin,
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done,
                    ),
                isEnabled = state.dialogState == null,
            ) { text, bool ->
                onValueChangeTeamCode(text)
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClickJoin,
            enabled = state.isJoinActionEnabled,
        ) {
            Text(text = "Join")
        }
    }
}
