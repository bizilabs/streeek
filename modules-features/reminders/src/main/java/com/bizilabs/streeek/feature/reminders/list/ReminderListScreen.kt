package com.bizilabs.streeek.feature.reminders.list

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.feature.reminders.components.ReminderComponent
import com.bizilabs.streeek.feature.reminders.components.ReminderEditBottomSheet
import com.bizilabs.streeek.lib.common.helpers.requestSinglePermission
import com.bizilabs.streeek.lib.common.models.FetchListState
import com.bizilabs.streeek.lib.design.components.SafiBottomAction
import com.bizilabs.streeek.lib.design.components.SafiBottomSheetPicker
import com.bizilabs.streeek.lib.design.components.SafiBottomValue
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCircularProgressIndicator
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.design.components.SafiTopBarHeader
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.domain.models.ReminderDomain
import kotlinx.datetime.DayOfWeek

object ReminderListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        val screenModel: ReminderListScreenModel = getScreenModel()
        val state by screenModel.state.collectAsState()

        ReminderListScreenContent(
            state = state,
            onClickNavigateBack = { navigator?.pop() },
            onClickReminder = screenModel::onClickReminder,
            onClickCreateReminder = screenModel::onClickCreate,
            onValueChangeReminderLabel = screenModel::onValueChangeReminderLabel,
            onClickReminderDayOfWeek = screenModel::onClickReminderDayOfWeek,
            onDismissSheet = screenModel::onDismissSheet,
            onOpenTimePicker = screenModel::onOpenTimePicker,
            onDismissTimePicker = screenModel::onDismissTimePicker,
            onCreateReminder = screenModel::onCreateReminder,
            onLongClick = screenModel::onLongClick,
            onDismissLongClick = screenModel::onDismissUpdateSheet,
            editReminder = screenModel::editReminder,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListScreenContent(
    state: ReminderListScreenState,
    onClickReminder: (ReminderDomain) -> Unit,
    onClickNavigateBack: () -> Unit,
    onClickCreateReminder: () -> Unit,
    onValueChangeReminderLabel: (String) -> Unit,
    onClickReminderDayOfWeek: (DayOfWeek) -> Unit,
    onDismissSheet: () -> Unit,
    onOpenTimePicker: () -> Unit,
    onCreateReminder: () -> Unit,
    onDismissTimePicker: (Int, Int) -> Unit,
    onLongClick: (ReminderDomain) -> Unit,
    onDismissLongClick: () -> Unit,
    editReminder: (String) -> Unit,
) {
    val activity = LocalContext.current as ComponentActivity

    if (state.isEditing) {
        ReminderEditBottomSheet(
            state = state,
            onValueChangeReminderLabel = onValueChangeReminderLabel,
            onClickReminderDayOfWeek = onClickReminderDayOfWeek,
            onDismiss = onDismissSheet,
            onOpenTimePicker = onOpenTimePicker,
            onCreateReminder = onCreateReminder,
            onDismissTimePicker = onDismissTimePicker,
        )
    }

    if (state.isUpdating) {
        SafiBottomSheetPicker(
            modifier = Modifier.fillMaxWidth(),
            title = "Update Reminder",
            selected = "",
            list = listOf(if (state.reminder?.enabled == true) "disable" else "enable", "delete"),
            onDismiss = onDismissLongClick,
            onItemSelected = { editReminder(it) },
            name = { it },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = onClickNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "navigate back",
                        )
                    }
                },
                title = {
                    SafiTopBarHeader(
                        title = "Reminders",
                        subtitle = "Set reminders for contributing to GitHub",
                    )
                },
                actions = {
                    IconButton(onClick = onClickCreateReminder) {
                        Icon(Icons.Default.Add, contentDescription = "")
                    }
                },
            )
        },
        snackbarHost = {
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = state.isAlarmPermissionGranted.not(),
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                SafiBottomAction(
                    title = "Permission to Set Alarms",
                    description =
                        "We need your permission to set daily alarms. This will help us " +
                            "remind you at the perfect time. \nTap 'Allow' to proceed!",
                    icon = Icons.Rounded.Timer,
                    iconTint = MaterialTheme.colorScheme.success,
                    primaryAction =
                        SafiBottomValue("allow") {
                            activity.requestSinglePermission(permission = android.Manifest.permission.USE_EXACT_ALARM)
                        },
                )
            }
        },
    ) { innerPadding ->
        AnimatedContent(
            label = "animate reminders",
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            targetState = state.fetchListState,
        ) { result ->
            when (result) {
                FetchListState.Empty -> {
                    SafiCenteredColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        SafiInfoSection(
                            icon = Icons.Rounded.Timer,
                            title = "No reminders set",
                            description = "No reminders found. Create a reminder to continue your streak.",
                        ) {
                            AnimatedVisibility(
                                visible = state.isAlarmPermissionGranted,
                                enter = scaleIn(),
                                exit = scaleOut(),
                            ) {
                                Button(onClick = onClickCreateReminder) {
                                    Text(text = "create")
                                }
                            }
                        }
                    }
                }

                FetchListState.Loading -> {
                    SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                        SafiCircularProgressIndicator()
                    }
                }

                is FetchListState.Error -> {
                    SafiCenteredColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        SafiInfoSection(
                            icon = Icons.Rounded.Timer,
                            title = "Found An Error",
                            description = result.message,
                        )
                    }
                }

                is FetchListState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(result.list) { reminder ->
                            ReminderComponent(
                                reminder = reminder,
                                onClick = onClickReminder,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp)
                                        .padding(top = 8.dp),
                                onLongClick = onLongClick,
                            )
                        }
                    }
                }
            }
        }
    }
}
