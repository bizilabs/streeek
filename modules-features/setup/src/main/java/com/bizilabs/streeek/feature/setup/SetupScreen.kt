package com.bizilabs.streeek.feature.setup

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiBottomInfoComponent
import com.bizilabs.streeek.lib.design.helpers.onSuccess
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.resources.images.SafiDrawables
import com.bizilabs.streeek.lib.resources.strings.SafiStrings

object SetupScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val tabsScreen = rememberScreen(SharedScreen.Tabs)

        val screenModel: SetupScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()
        SetupScreenContent(
            state = state,
            onClickGetUserRetry = screenModel::onClickGetUserRetry,
            onClickGetAccountRetry = screenModel::onClickGetAccountRetry,
        ) {
            navigator?.replace(tabsScreen)
        }
    }
}

@Composable
fun SetupScreenContent(
    state: SetupScreenState,
    onClickGetUserRetry: () -> Unit,
    onClickGetAccountRetry: () -> Unit,
    navigate: () -> Unit,
) {
    Scaffold { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(16.dp),
            ) {
                Row(modifier = Modifier.padding(8.dp)) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(SafiDrawables.Logo),
                        contentDescription = "",
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = stringResource(SafiStrings.AppName),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                    )
                }
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "Setting Up",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    modifier = Modifier.fillMaxWidth(0.75f),
                    text = "Let’s get everything ready for your adventure!",
                )
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                ) {
                    AnimatedContent(
                        modifier = Modifier.fillMaxSize(),
                        targetState = state.userState,
                        label = "user_animation",
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Spacer(modifier = Modifier.weight(1f))
                            when (it) {
                                is FetchState.Error -> {
                                    SafiBottomInfoComponent(
                                        title = "User Error",
                                        message = it.message,
                                        contentColor = MaterialTheme.colorScheme.onError,
                                        containerColor = MaterialTheme.colorScheme.error,
                                        action = {
                                            TextButton(
                                                onClick = onClickGetUserRetry,
                                            ) {
                                                Text(
                                                    text = "Retry",
                                                    color = MaterialTheme.colorScheme.onError,
                                                )
                                            }
                                        },
                                    )
                                }

                                is FetchState.Loading -> {
                                    SafiBottomInfoComponent(
                                        title = "Github",
                                        message = "Getting your github user details...",
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                        containerColor = MaterialTheme.colorScheme.primary,
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                        )
                                    }
                                }

                                is FetchState.Success -> {
                                    val user = it.value
                                    AnimatedContent(
                                        targetState = state.accountState,
                                        label = "account_animation",
                                    ) { accountState ->
                                        when (accountState) {
                                            is FetchState.Error -> {
                                                SafiBottomInfoComponent(
                                                    title = "Account Error",
                                                    message = accountState.message,
                                                    contentColor = MaterialTheme.colorScheme.onError,
                                                    containerColor = MaterialTheme.colorScheme.error,
                                                    action = {
                                                        TextButton(onClick = onClickGetAccountRetry) {
                                                            Text(
                                                                text = "Retry",
                                                                color = MaterialTheme.colorScheme.onError,
                                                            )
                                                        }
                                                    },
                                                )
                                            }

                                            FetchState.Loading -> {
                                                SafiBottomInfoComponent(
                                                    title = "Hi ${user.name}",
                                                    message = "Setting up your Streeek account...",
                                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                                    containerColor = MaterialTheme.colorScheme.primary,
                                                ) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(24.dp),
                                                        color = MaterialTheme.colorScheme.onPrimary,
                                                    )
                                                }
                                            }

                                            is FetchState.Success -> {
                                                val account = accountState.value
                                                SafiBottomInfoComponent(
                                                    title = "Success",
                                                    message = "You're all set up ${account.username}",
                                                    containerColor = MaterialTheme.colorScheme.success,
                                                    contentColor = MaterialTheme.colorScheme.onSuccess,
                                                ) {
                                                    TextButton(onClick = navigate) {
                                                        Text(
                                                            text = "Continue",
                                                            color = MaterialTheme.colorScheme.onSuccess,
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
            }
        }
    }
}
