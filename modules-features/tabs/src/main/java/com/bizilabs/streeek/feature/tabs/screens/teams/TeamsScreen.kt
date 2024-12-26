package com.bizilabs.streeek.feature.tabs.screens.teams

import android.R.attr.contentDescription
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.TransitEnterexit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import com.bizilabs.streeek.feature.tabs.screens.teams.components.TeamTopMemberComponent
import com.bizilabs.streeek.lib.common.models.FetchListState
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.domain.extensions.asRank
import com.bizilabs.streeek.lib.domain.models.TeamDomain
import com.bizilabs.streeek.lib.resources.SafiResources

object TeamsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        val screenModel: TeamsScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()

        TeamsScreenContent(
            state = state,
            onClickMenuSearch = screenModel::onClickMenuSearch,
            onClickMenuCreateTeam = screenModel::onClickMenuTeamCreate,
            onClickMenuJoinTeam = screenModel::onClickMenuTeamJoin,
            onClickTeam = screenModel::onClickTeam
        ) { screen ->
            navigator?.push(screen)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreenContent(
    state: TeamsScreenState,
    onClickMenuSearch: () -> Unit,
    onClickMenuCreateTeam: () -> Unit,
    onClickMenuJoinTeam: () -> Unit,
    onClickTeam: (TeamDomain) -> Unit,
    navigate: (Screen) -> Unit
) {

    if (state.isCreating)
        navigate(rememberScreen(SharedScreen.Team(isJoining = false, teamId = null)))

    if (state.isJoining)
        navigate(rememberScreen(SharedScreen.Team(isJoining = true, teamId = null)))

    if (state.teamId != null)
        navigate(rememberScreen(SharedScreen.Team(isJoining = false, teamId = state.teamId)))

    Scaffold(
        topBar = {
            TeamsScreenHeaderSection(modifier = Modifier.fillMaxWidth())
        }
    ) { paddingValues ->
        AnimatedContent(
            label = "animate teams",
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize(),
            targetState = state.teamsState
        ) { result ->
            when (result) {
                FetchListState.Empty -> {
                    SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                        SafiInfoSection(
                            icon = Icons.Rounded.People,
                            title = "Empty",
                            description = "Join a team to see the list"
                        )
                    }
                }

                FetchListState.Loading -> {
                    SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator()
                    }
                }

                is FetchListState.Error -> {
                    SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                        SafiInfoSection(
                            icon = Icons.Rounded.People,
                            title = "Error",
                            description = result.message
                        )
                    }
                }

                is FetchListState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(result.list) { item ->
                            Card(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 8.dp),
                                onClick = { onClickTeam(item.team) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Column(modifier = Modifier) {
                                        Text(
                                            text = item.team.name,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        val count = item.team.count
                                        Text(
                                            text = buildString {
                                                append(count)
                                                append(" Member")
                                                append(if (count > 1) "s" else "")
                                            },
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    SafiCenteredColumn {
                                        Text(
                                            modifier = Modifier.padding(16.dp),
                                            text = item.member.rank.asRank(),
                                            fontSize = if (item.member.rank < 100)
                                                MaterialTheme.typography.titleLarge.fontSize
                                            else
                                                MaterialTheme.typography.bodyLarge.fontSize
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamsScreenHeaderSection(modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Rounded.TransitEnterexit,
                        contentDescription = ""
                    )
                }

                Text(
                    modifier = Modifier.weight(1f),
                    text = "Leaderboard".uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )

                IconButton(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = ""
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                TeamTopMemberComponent(
                    isFirst = false,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 48.dp),
                    rank = "2",
                    image = "https://avatars.githubusercontent.com/u/3008932?v=4",
                    name = "tamzi",
                    exp = "7,650 EXP"
                )
                TeamTopMemberComponent(
                    isFirst = true, modifier = Modifier.weight(1f),
                    image = "https://avatars.githubusercontent.com/u/61080898?v=4",
                    rank = "1",
                    name = "kibettheophilus",
                    exp = "10,250 EXP"
                )
                TeamTopMemberComponent(
                    isFirst = false,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 48.dp),
                    rank = "3"
                )
            }
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
