package com.bizilabs.streeek.feature.landing

import android.R.attr.text
import android.R.attr.top
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import androidx.compose.runtime.getValue

val featureLanding = screenModule {
    register<SharedScreen.Landing> { LandingScreen }
}

object LandingScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val authenticationScreen = rememberScreen(SharedScreen.Authentication)
        val tabsScreen = rememberScreen(SharedScreen.Tabs)

        val screenModel : LandingScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()

        LandingScreenContent(state = state){destination ->
            when(destination){
                LandingScreenDestination.CURRENT -> Unit
                LandingScreenDestination.AUTHENTICATE -> navigator.replace(authenticationScreen)
                LandingScreenDestination.TABS -> navigator.replace(tabsScreen)
            }
        }
    }
}

@Composable
fun LandingScreenContent(state: LandingScreenState, navigate: (LandingScreenDestination) -> Unit) {

    if (state.destination != LandingScreenDestination.CURRENT) navigate(state.destination)

    Scaffold { paddingValues ->
        SafiCenteredColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Text(text = "Streeek")
            LinearProgressIndicator(
                modifier = Modifier
                    .padding(16.dp)
                    .width(75.dp)
            )
        }
    }
}