package com.bizilabs.streeek.feature.landing

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
import com.bizilabs.streeek.lib.resources.images.SafiDrawables

val featureLanding =
    screenModule {
        register<SharedScreen.Landing> { LandingScreen }
    }

object LandingScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val intentDestinations = getNavigationDestinationFromURI()
        val onBoardingScreen = rememberScreen(SharedScreen.OnBoarding)
        val authenticationScreen = rememberScreen(SharedScreen.Authentication)
        val setupScreen = rememberScreen(SharedScreen.Setup)
        val tabsScreen = rememberScreen(SharedScreen.Tabs)

        val screenModel: LandingScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()

        LandingScreenContent(state = state) { destination ->
            if (intentDestinations.isNotEmpty() && destination != LandingScreenDestination.AUTHENTICATE) {
                navigator.replaceAll(intentDestinations)
            } else {
                when (destination) {
                    LandingScreenDestination.CURRENT -> Unit
                    LandingScreenDestination.AUTHENTICATE -> navigator.replace(authenticationScreen)
                    LandingScreenDestination.TABS -> navigator.replace(tabsScreen)
                    LandingScreenDestination.SETUP -> navigator.replace(setupScreen)
                    LandingScreenDestination.ONBOARDING -> navigator.replace(onBoardingScreen)
                }
            }
        }
    }
}

@Composable
fun LandingScreenContent(
    state: LandingScreenState,
    navigate: (LandingScreenDestination) -> Unit,
) {
    if (state.destination != LandingScreenDestination.CURRENT) navigate(state.destination)

    Scaffold { paddingValues ->
        SafiCenteredColumn(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
        ) {
            Icon(
                modifier =
                    Modifier
                        .padding(16.dp)
                        .size(48.dp),
                painter = painterResource(SafiDrawables.Logo),
                contentDescription = "logo",
                tint = MaterialTheme.colorScheme.onBackground,
            )
            LinearProgressIndicator(
                modifier =
                    Modifier
                        .padding(16.dp)
                        .width(75.dp),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
