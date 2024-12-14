package com.bizilabs.streeek.feature.setup

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiInfoSection

object SetupScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val tabsScreen = rememberScreen(SharedScreen.Tabs)

        val screenModel: SetupScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()
        SetupScreenContent(state = state){
            navigator?.replace(tabsScreen)
        }
    }
}

@Composable
fun SetupScreenContent(state: SetupScreenState, navigate: () -> Unit) {
    Scaffold { paddingValues ->
        SafiCenteredColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AnimatedContent(
                modifier = Modifier.fillMaxSize(),
                targetState = state.userState,
                label = "user_animation"
            ) {
                when (it) {
                    is FetchState.Error -> {
                        SafiCenteredColumn {
                            SafiInfoSection(
                                icon = Icons.Rounded.AccountCircle,
                                title = "\uD83D\uDE1E",
                                description = it.message,
                                action = {
                                    Button(onClick = {}) {
                                        Text(text = "Retry")
                                    }
                                }
                            )
                        }
                    }

                    is FetchState.Loading -> {
                        SafiCenteredColumn {
                            Text(
                                modifier = Modifier.padding(16.dp),
                                text = "\uD83E\uDD14",
                                fontSize = 80.sp
                            )
                            Text(text = "Getting Github User", fontWeight = FontWeight.Bold)
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }

                    is FetchState.Success -> {
                        val user = it.value
                        AnimatedContent(
                            targetState = state.accountState,
                            label = "account_animation"
                        ) { accountState ->
                            when (accountState) {
                                is FetchState.Error -> {
                                    SafiCenteredColumn {
                                        SafiInfoSection(
                                            icon = Icons.Rounded.AccountCircle,
                                            title = "Error",
                                            description = accountState.message,
                                            action = {
                                                Button(onClick = {}) {
                                                    Text(text = "Retry")
                                                }
                                            }
                                        )
                                    }
                                }

                                FetchState.Loading -> {
                                    SafiCenteredColumn {
                                        Text(text = "\uD83D\uDE0E", fontSize = 80.sp)
                                        Text(
                                            modifier = Modifier.padding(16.dp),
                                            text = "Hi ${user.name}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 24.sp
                                        )
                                        Text(text = "Crunching The Numbers....")
                                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                                    }
                                }

                                is FetchState.Success -> {
                                    val account = accountState.value
                                    SafiCenteredColumn {
                                        Text(text = "\uD83E\uDD29", fontSize = 80.sp)
                                        Text(
                                            modifier = Modifier.padding(16.dp),
                                            text = account.username,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 32.sp
                                        )
                                        Text(text = "Welcome Back!")
                                        Button(onClick = navigate) {
                                            Text(text = "Continue")
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
