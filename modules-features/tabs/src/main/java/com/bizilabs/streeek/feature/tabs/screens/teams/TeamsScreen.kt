package com.bizilabs.streeek.feature.tabs.screens.teams

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TransitEnterexit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn

object TeamsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        val teamScreen = rememberScreen(SharedScreen.Team(teamId = 1))

        TeamsScreenContent(
            onClickMenuSearch = {},
            onClickMenuCreateTeam = { navigator?.push(teamScreen) },
            onClickMenuJoinTeam = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreenContent(
    onClickMenuSearch: () -> Unit,
    onClickMenuCreateTeam: () -> Unit,
    onClickMenuJoinTeam: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Leaderboard") },
                actions = {
                    TeamScreenMenu(
                        onClickMenuSearch = onClickMenuSearch,
                        onClickMenuCreateTeam = onClickMenuCreateTeam,
                        onClickMenuJoinTeam = onClickMenuJoinTeam
                    )
                }
            )
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

@Composable
fun RowScope.TeamScreenMenu(
    onClickMenuSearch: () -> Unit,
    onClickMenuCreateTeam: () -> Unit,
    onClickMenuJoinTeam: () -> Unit,
) {

    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = onClickMenuSearch) {
        Icon(Icons.Outlined.Search, contentDescription = "Search Teams")
    }

    Box(
        modifier = Modifier
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            Text(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                text = "Teams",
                style = MaterialTheme.typography.labelSmall
            )

            DropdownMenuItem(
                contentPadding = PaddingValues(start = 16.dp, end = 24.dp),
                text = { Text("Create Team") },
                leadingIcon = { Icon(Icons.Outlined.Create, contentDescription = null) },
                onClick = onClickMenuCreateTeam
            )
            DropdownMenuItem(
                contentPadding = PaddingValues(start = 16.dp, end = 24.dp),
                text = { Text("Join Team") },
                leadingIcon = { Icon(Icons.Outlined.TransitEnterexit, contentDescription = null) },
                onClick = onClickMenuJoinTeam
            )

        }
    }
}
