package com.bizilabs.streeek.feature.points

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.lib.common.models.FetchListState
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCircularProgressIndicator
import com.bizilabs.streeek.lib.design.components.SafiTopBarHeader
import com.bizilabs.streeek.lib.domain.models.points.EventPointsDomain

val ScreenPoints = screenModule { register<SharedScreen.Points> { PointsScreen } }

object PointsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = getScreenModel<PointsScreenModel>()
        val state by screenModel.state.collectAsStateWithLifecycle()
        PointsScreenContent(state = state) { navigator?.pop() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsScreenContent(
    state: PointsScreenState,
    onClickNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            Surface(modifier = Modifier.fillMaxWidth()) {
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
                            title = "Arcane Knowledge",
                            subtitle = "Learn how to earn experience points (EXP).",
                        )
                    },
                )
            }
        },
    ) { innerPadding ->
        AnimatedContent(
            label = "animate points list",
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            targetState = state.fetch,
        ) { fetch ->
            when (fetch) {
                is FetchListState.Success -> {
                    LazyColumn {
                        items(fetch.list) { event ->
                            Card(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 8.dp),
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(0.1f),
                                    ),
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                ) {
                                    Row(
                                        modifier =
                                            Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = event.title,
                                                style = MaterialTheme.typography.titleMedium,
                                            )
                                            Text(
                                                modifier = Modifier.padding(bottom = 8.dp),
                                                text = event.subtitle,
                                                style = MaterialTheme.typography.labelMedium,
                                            )
                                        }

                                        if (event is EventPointsDomain.SingleActioned) {
                                            Text(
                                                modifier = Modifier.padding(start = 16.dp),
                                                text =
                                                    buildString {
                                                        append(event.points)
                                                        append(" EXP")
                                                    },
                                                style = MaterialTheme.typography.labelMedium,
                                            )
                                        }
                                    }

                                    if (event is EventPointsDomain.MultipleActioned) {
                                        event.actions.forEach { action ->
                                            HorizontalDivider(
                                                modifier = Modifier.fillMaxWidth(),
                                            )
                                            Row(
                                                modifier =
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .padding(4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Column(
                                                    modifier = Modifier.weight(1f),
                                                ) {
                                                    Text(
                                                        text = action.action,
                                                        style = MaterialTheme.typography.labelLarge,
                                                    )
                                                    Text(
                                                        text = action.description,
                                                        style = MaterialTheme.typography.labelSmall,
                                                    )
                                                }

                                                Text(
                                                    text =
                                                        buildString {
                                                            append(action.points)
                                                            append(" EXP")
                                                        },
                                                    style = MaterialTheme.typography.labelMedium,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {
                    SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                        SafiCircularProgressIndicator()
                    }
                }
            }
        }
    }
}
