package com.bizilabs.streeek.feature.leaderboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bizilabs.streeek.lib.common.components.paging.SafiPagingComponent
import com.bizilabs.streeek.lib.domain.extensions.asRank
import com.bizilabs.streeek.lib.domain.models.LeaderboardAccountDomain

val ScreenLeaderboard =
    screenModule {
        register<SharedScreen.Leaderboard> { LeaderboardScreen(it.name) }
    }

data class LeaderboardScreen(val name: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        val screenModel = getScreenModel<LeaderboardScreenModel>()
        screenModel.onValueChange(name = name)
        val state by screenModel.state.collectAsStateWithLifecycle()
        val data = screenModel.pages.collectAsLazyPagingItems()

        LeaderboardScreenContent(
            state = state,
            data = data,
            onClickNavigateBack = { navigator?.pop() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreenContent(
    state: LeaderboardScreenState,
    data: LazyPagingItems<LeaderboardAccountDomain>,
    onClickNavigateBack: () -> Unit,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "navigate back"
                        )
                    }
                },
                title = {
                    Column {
                        Text(
                            text = "${
                                state.leaderboard?.name?.lowercase()
                                    ?.replaceFirstChar { it.uppercase() }
                            }",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            modifier = Modifier.padding(),
                            text = "Leaderboard".uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        SafiPagingComponent(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            data = data
        ) { item ->
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                onClick = {}
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = item.account.username)
                    Text(text = item.rank.position.asRank())
                }
            }
        }

    }

}
