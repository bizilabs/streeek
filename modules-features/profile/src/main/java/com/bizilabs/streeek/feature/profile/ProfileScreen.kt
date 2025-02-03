package com.bizilabs.streeek.feature.profile

import android.R.attr.visible
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
import androidx.compose.material.icons.automirrored.rounded.LibraryBooks
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material.icons.rounded.FontDownload
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.bizilabs.streeek.lib.design.atoms.SafiTypography
import com.bizilabs.streeek.lib.design.components.DialogState
import com.bizilabs.streeek.lib.design.components.SafiBottomDialog
import com.bizilabs.streeek.lib.design.components.SafiBottomSheetPicker
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
        val screenLanding = rememberScreen(SharedScreen.Landing)
        val screenIssues = rememberScreen(SharedScreen.Issues)
        val screenPoints = rememberScreen(SharedScreen.Points)

        val screenModel: ProfileScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()
        ProfileScreenContent(
            state = state,
            onClickNavigateBackIcon = { navigator?.pop() },
            onClickLogout = screenModel::onClickLogout,
            navigateToLanding = { navigator?.replaceAll(screenLanding) },
            onClickConfirmLogout = screenModel::onClickConfirmLogout,
            onClickCardIssues = { navigator?.push(screenIssues) },
            onClickCardPoints = { navigator?.push(screenPoints) },
            onToggleSelectTypography = screenModel::onToggleSelectTypography,
            onClickTypography = screenModel::onClickTypography,
            navigate = { screen -> navigator?.push(screen) },
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
    onToggleSelectTypography: (Boolean) -> Unit,
    onClickTypography: (SafiTypography) -> Unit,
    navigate: (Screen) -> Unit,
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

    if (state.isSelectingTypography) {
        SafiBottomSheetPicker(
            modifier = Modifier.fillMaxWidth(),
            title = "Select Typography",
            selected = state.typography,
            list = state.typographies.toList(),
            onDismiss = { onToggleSelectTypography(false) },
            onItemSelected = onClickTypography,
            name = { it.label },
        )
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
                    icon = Icons.AutoMirrored.Rounded.LibraryBooks,
                    title = "Arcane Knowledge",
                    message = "Learn how to earn experience points (EXP).",
                    onClick = onClickCardPoints,
                )

                ProfileItemComponent(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                    icon = Icons.Rounded.FontDownload,
                    title = "Typography",
                    message = "Change app's look and feel by changing the font.",
                    onClick = { onToggleSelectTypography(true) },
                )

                val screenReminders = rememberScreen(SharedScreen.Reminders)
                ProfileItemComponent(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                    icon = Icons.Rounded.Timer,
                    title = "Reminders",
                    message = "Add custom reminders to maintain a steady streak",
                    onClick = { navigate(screenReminders) },
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
                    Text(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), text = title)
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
