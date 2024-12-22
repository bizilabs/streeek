package com.bizilabs.streeek.feature.tabs.screens.teams

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn

object TeamsScreen : Screen {
    @Composable
    override fun Content() {
        TeamsScreenContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreenContent() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Leaderboard") })
        }
    ) { paddingValues ->
        SafiCenteredColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Text(text = "Teams")
        }
    }
}
