package com.bizilabs.streeek.feature.leaderboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.lib.common.components.LeaderboardComponent
import com.bizilabs.streeek.lib.common.components.paging.SafiPagingComponent
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiBottomDialog
import com.bizilabs.streeek.lib.design.components.SafiTopBarHeader
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
            onClickNavigateBack = { navigator?.pop() },
            onClickMember = screenModel::onClickMember,
            onClickDismissDialog = screenModel::onClickDismissDialog
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreenContent(
    state: LeaderboardScreenState,
    data: LazyPagingItems<LeaderboardAccountDomain>,
    onClickMember: (Long, Long, String) -> Unit,
    onClickNavigateBack: () -> Unit,
    onClickDismissDialog:()-> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "navigate back",
                        )
                    }
                },
                title = {
                    SafiTopBarHeader(
                        modifier = Modifier.fillMaxWidth(),
                        title = state.leaderboard?.name ?: "",
                        subtitle = "Leaderboard",
                    )
                },
            )
        },
    ) { innerPadding ->

        if (state.dialogState != null) {
            SafiBottomDialog(
                state = state.dialogState,
                onClickDismiss = onClickDismissDialog,
            )
        }

        SafiPagingComponent(
            modifier =
            Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            data = data,
        ) { item ->
            LeaderboardComponent(
                imageUrl = item.account.avatarUrl,
                username = item.account.username,
                points = item.rank.points,
                rank = item.rank.position.asRank(),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onClickMember(
                        item.rank.points,
                        item.account.id,
                        item.account.username
                    )
                }
            )
        }
    }
}
