package com.bizilabs.streeek.feature.tabs

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import coil.compose.AsyncImage
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.domain.helpers.asString

val featureTabs = screenModule {
    register<SharedScreen.Tabs> { TabsScreen }
}

object TabsScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel: TabsScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()
        TabsScreenContent(state = state)
    }
}

@Composable
fun TabsScreenContent(state: TabsScreenState) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AnimatedContent(
                modifier = Modifier.fillMaxWidth(),
                targetState = state.accountState,
                label = "account_animation"
            ) { result ->
                when (result) {
                    is FetchState.Error -> {
                        SafiCenteredColumn {
                            Text(
                                text = "Error",
                                modifier = Modifier.padding(16.dp),
                                fontSize = 24.sp
                            )
                            Text(text = result.message)
                        }
                    }

                    FetchState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is FetchState.Success -> {
                        val user = result.value
                        SafiCenteredColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(16.dp)
                                    .clip(CircleShape),
                                model = user.avatarUrl,
                                contentDescription = null,
                            )
                            Text(
                                modifier = Modifier.padding(16.dp),
                                text = user.id.toString()
                            )
                            Text(text = user.username)
                            Text(text = user.email)
                            Text(
                                text = user.bio,
                                modifier = Modifier.fillMaxWidth(0.5f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            AnimatedContent(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                targetState = state.eventsState,
                label = "events_animation"
            ) { events ->
                when (events) {
                    FetchState.Loading -> {
                        SafiCenteredColumn(modifier = Modifier.fillMaxSize()) { CircularProgressIndicator() }
                    }

                    is FetchState.Error -> {
                        SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                            Text(text = "Error")
                            Text(text = events.message)
                        }

                    }

                    is FetchState.Success -> {
                        val context = LocalContext.current
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(events.value) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    onClick = {
                                        Toast.makeText(
                                            context,
                                            it.type,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.onBackground.copy(
                                            0.2f
                                        )
                                    ),
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            text = it.createdAt.asString()
                                                ?: "Some Date..."
                                        )
                                        Text(text = it.id)
                                        SafiCenteredRow {
                                            Text(text = it.type)
                                            Text(text = " > ", fontSize = 24.sp)
                                            Text(text = it.repo.name)
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
}
