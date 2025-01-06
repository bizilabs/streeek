package com.bizilabs.streeek.feature.authentication

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.lib.common.helpers.StartActivity
import com.bizilabs.streeek.lib.common.helpers.findActivity
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiBottomInfoComponent
import com.bizilabs.streeek.lib.design.helpers.onSuccess
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.resources.images.SafiDrawables
import com.bizilabs.streeek.lib.resources.strings.SafiStrings

object AuthenticationScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screen = rememberScreen(SharedScreen.Setup)
        val screenModel = getScreenModel<AuthenticationScreenModel>()
        val state by screenModel.state.collectAsStateWithLifecycle()
        AuthenticationScreenContent(
            state = state,
            onClickAuthenticate = screenModel::onClickAuthenticate,
            onUriReceived = screenModel::onUriReceived,
            navigateToSetup = { navigator?.replace(screen) },
        )
    }
}

@Composable
fun AuthenticationScreenContent(
    state: AuthenticationScreenState,
    onClickAuthenticate: () -> Unit,
    onUriReceived: (Uri) -> Unit,
    navigateToSetup: () -> Unit,
) {
    if (state.intent != null && state.fetchState == null) {
        StartActivity(intent = state.intent)
    }

    if (state.uri == null) {
        HandleIntent(onUriReceived = onUriReceived)
    }

    if (state.navigateToTabs) {
        navigateToSetup()
    }

    Scaffold { paddingValues ->
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
                text = "Welcome to Streeek",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                modifier = Modifier.fillMaxWidth(0.75f),
                text = "Turn your GitHub contributions into a streak-chasing adventure",
            )

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.weight(1f))
                AnimatedVisibility(
                    visible = (state.intent == null && state.uri == null).not(),
                ) {
                    AnimatedContent(
                        targetState = state.fetchState,
                        label = "animate auth state",
                    ) { result ->
                        when (result) {
                            is FetchState.Error -> {
                                SafiBottomInfoComponent(
                                    title = "Authentication Error",
                                    message = result.message,
                                    contentColor = MaterialTheme.colorScheme.onError,
                                    containerColor = MaterialTheme.colorScheme.error,
                                ) {
                                    TextButton(onClick = onClickAuthenticate) {
                                        Text(
                                            text = "Retry",
                                            color = MaterialTheme.colorScheme.onError,
                                        )
                                    }
                                }
                            }

                            is FetchState.Success -> {
                                SafiBottomInfoComponent(
                                    title = "Success",
                                    message = "Authenticated with Github successfully",
                                    contentColor = MaterialTheme.colorScheme.onSuccess,
                                    containerColor = MaterialTheme.colorScheme.success,
                                ) {
                                    TextButton(onClick = navigateToSetup) {
                                        Text(
                                            text = "Continue",
                                            color = MaterialTheme.colorScheme.onSuccess,
                                        )
                                    }
                                }
                            }

                            else -> {
                                SafiBottomInfoComponent(
                                    title = "Authenticating",
                                    message = "Sending request to authenticate with GitHub...",
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.primary,
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = state.intent == null && state.uri == null,
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onClickAuthenticate,
                ) {
                    Text(text = "Authenticate")
                }
            }
        }
    }
}

@Composable
fun HandleIntent(onUriReceived: (Uri) -> Unit) {
    val activity = LocalContext.current as ComponentActivity
    val uri: Uri? = activity.findActivity()?.intent?.data
    if (uri != null) onUriReceived(uri)
}
