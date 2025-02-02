package com.bizilabs.streeek.feature.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.DialogState
import com.bizilabs.streeek.lib.design.components.SafiBottomDialog
import com.bizilabs.streeek.lib.design.components.SafiTopBarHeader
import com.bizilabs.streeek.lib.resources.strings.SafiStringLabels

val featureProfile =
    screenModule {
        register<SharedScreen.Profile> { ProfileScreen }
    }

object ProfileScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenIssues = rememberScreen(SharedScreen.Issues)
        val screenPoints = rememberScreen(SharedScreen.Points)

        val landingScreen = rememberScreen(SharedScreen.Landing)
        val screenModel: ProfileScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()
        ProfileScreenContent(
            state = state,
            onClickNavigateBackIcon = { navigator?.pop() },
            onClickLogout = screenModel::onClickLogout,
            navigateToLanding = { navigator?.replaceAll(landingScreen) },
            onClickConfirmLogout = screenModel::onClickConfirmLogout,
            onClickCardIssues = { navigator?.push(screenIssues) },
            onClickCardPoints = { navigator?.push(screenPoints) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    state: ProfileScreenState,
    onClickNavigateBackIcon: () -> Unit,
    onClickLogout: () -> Unit,
    navigateToLanding: () -> Unit,
    onClickConfirmLogout: (Boolean) -> Unit,
    onClickCardIssues: () -> Unit,
    onClickCardPoints: () -> Unit,
) {
    val scrollState = rememberScrollState()

    if (state.shouldNavigateToLanding) navigateToLanding()

    if (state.shouldConfirmLogout) {
        SafiBottomDialog(
            state =
                DialogState.Info(
                    title = "Logout",
                    message = "Are you sure you want to logout?",
                ),
            onClickDismiss = { onClickConfirmLogout(false) },
        ) {
            Button(onClick = { onClickConfirmLogout(true) }) {
                Text(text = "Yes")
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = onClickNavigateBackIcon) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null,
                    )
                }
            },
            title = {
                SafiTopBarHeader(title = "Settings")
            },
        )
    }) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .scrollable(state = scrollState, orientation = Orientation.Vertical),
            ) {
                ProfileItemComponent(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                    icon = Icons.Rounded.Feedback,
                    title = "Feedback",
                    message = "For any feedback or suggestions",
                    onClick = onClickCardIssues,
                )

                ProfileItemComponent(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                    icon = Icons.Rounded.Bolt,
                    title = "Arcane Knowledge",
                    message = "Learn how to earn experience points (EXP).",
                    onClick = onClickCardPoints,
                )

                Button(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 24.dp),
                    onClick = onClickLogout,
                ) {
                    Text(text = stringResource(SafiStringLabels.LogOut))
                }
            }
            Text(
                text = "${state.versionCode} - v${state.versionName}",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun ProfileItemComponent(
    icon: ImageVector,
    title: String,
    message: String = "",
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                contentColor = MaterialTheme.colorScheme.onBackground,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(modifier = Modifier.weight(1f)) {
                Icon(imageVector = icon, contentDescription = title)
                Column(Modifier.padding(start = 16.dp)) {
                    Text(modifier = Modifier.fillMaxWidth(), text = title)
                    AnimatedVisibility(visible = message.isNotEmpty()) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = message,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }

            Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = title)
        }
    }
}
