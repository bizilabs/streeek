package com.bizilabs.streeek.feature.reminders.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.lib.design.components.SafiTopBarHeader

object ReminderListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        ReminderListScreen(
            onClickNavigateBack = { navigator?.pop() },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListScreen(onClickNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = onClickNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "navigate back",
                        )
                    }
                },
                title = {
                    SafiTopBarHeader(
                        title = "Reminders",
                        subtitle = "Set reminders for contributing to GitHub",
                    )
                },
            )
        },
        snackbarHost = {
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Text(text = "Reminders")
        }
    }
}
