package com.bizilabs.streeek.feature.authentication

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.helpers.success

object AuthenticationScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val tabsScreen = rememberScreen(SharedScreen.Tabs)
        val screenModel = getScreenModel<AuthenticationScreenModel>()
        val state by screenModel.state.collectAsStateWithLifecycle()
        AuthenticationScreenContent(
            state = state,
            onClickAuthenticate = screenModel::onClickAuthenticate,
            onUriReceived = screenModel::onUriReceived,
            navigateToTabs = { navigator?.replace(tabsScreen) }
        )
    }
}

@Composable
fun AuthenticationScreenContent(
    state: AuthenticationScreenState,
    onClickAuthenticate: () -> Unit,
    onUriReceived: (Uri) -> Unit,
    navigateToTabs: () -> Unit,
) {

    if (state.intent != null && state.fetchState == null)
        StartActivity(intent = state.intent)

    if (state.uri == null)
        HandleIntent(onUriReceived = onUriReceived)

    if (state.navigateToTabs)
        navigateToTabs()

    Scaffold { paddingValues ->
        AnimatedContent(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            targetState = state.intent
        ) { intent ->
            SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                when (intent == null && state.uri == null) {
                    true -> {
                        Text(text = "Authentication")
                        Button(
                            modifier = Modifier.padding(top = 16.dp),
                            onClick = onClickAuthenticate
                        ) {
                            Text(text = "start")
                        }
                    }

                    false -> {
                        when (state.fetchState) {
                            is FetchState.Error -> {
                                Text(text = state.fetchState.message)
                                Button(modifier = Modifier.padding(top = 16.dp), onClick = {}) {
                                    Text(text = "retry")
                                }
                            }

                            is FetchState.Success -> {
                                Icon(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(75.dp),
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = "success",
                                    tint = MaterialTheme.colorScheme.success
                                )
                                Text(text = "Authenticated Successfully")
                                Text(text = state.fetchState.value)
                            }

                            FetchState.Loading -> {
                                CircularProgressIndicator()
                            }

                            null -> {
                                Text(text = "Something Happened And I don't know what")
                            }
                        }
                    }
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
