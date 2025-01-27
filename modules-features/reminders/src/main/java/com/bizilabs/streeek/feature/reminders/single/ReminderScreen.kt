package com.bizilabs.streeek.feature.reminders.single

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

class ReminderScreen(val id: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        ReminderScreenContent()
    }
}

@Composable
fun ReminderScreenContent() {
    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
        }
    }
}
