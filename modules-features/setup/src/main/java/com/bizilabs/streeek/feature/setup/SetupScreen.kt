package com.bizilabs.streeek.feature.setup

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.bizilabs.streeek.lib.resources.images.SafiDrawables
import com.bizilabs.streeek.lib.resources.strings.SafiStrings

object SetupScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val tabsScreen = rememberScreen(SharedScreen.Tabs)

        val screenModel: SetupScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()
        SetupScreenContent(state = state) {
            navigator?.replace(tabsScreen)
        }
    }
}

@Composable
fun SetupScreenContent(state: SetupScreenState, navigate: () -> Unit) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(modifier = Modifier.padding(8.dp)) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(SafiDrawables.Logo),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = stringResource(SafiStrings.AppName),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                }
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "Setting Up",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier.fillMaxWidth(0.75f),
                    text = "Let’s get everything ready for your coding adventure!"
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AnimatedContent(
                        modifier = Modifier.fillMaxSize(),
                        targetState = state.userState,
                        label = "user_animation"
                    ) {
                        SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
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
                                        Text(
                                            text = "Getting Github User",
                                            fontWeight = FontWeight.Bold
                                        )
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
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.padding(
                                                            16.dp
                                                        )
                                                    )
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
        }
    }
}
