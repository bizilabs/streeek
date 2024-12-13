package com.bizilabs.streeek.feature.tabs

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
        SafiCenteredColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(targetState = state.userState, label = "user_animation") { result ->
                when (result) {
                    is FetchState.Error -> {
                        SafiCenteredColumn {
                            Text(text = "Error", modifier = Modifier.padding(16.dp), fontSize = 24.sp)
                            Text(text = result.message)
                        }
                    }

                    FetchState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is FetchState.Success -> {
                        val user = result.value
                        SafiCenteredColumn {
                            AsyncImage(
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(16.dp),
                                model = user.url,
                                contentDescription = null,
                            )
                            Text(modifier = Modifier.padding(16.dp), text = user.id.toString())
                            Text(text = user.name)
                            Text(text = user.email)
                            Text(text = user.bio, modifier = Modifier.weight(0.5f))
                        }
                    }
                }
            }
        }
    }
}