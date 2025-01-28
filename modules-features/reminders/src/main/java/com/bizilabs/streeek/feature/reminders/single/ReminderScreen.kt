package com.bizilabs.streeek.feature.reminders.single

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

class ReminderScreen(val label: String, val day: Int, val code: Int) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        ReminderScreenContent(label)
    }
}

@Composable
fun ReminderScreenContent(label: String) {
    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text("Welcome to this screen: $label")
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ReminderScreenContentPreview(modifier: Modifier = Modifier) {
    ReminderScreenContent("")
}
