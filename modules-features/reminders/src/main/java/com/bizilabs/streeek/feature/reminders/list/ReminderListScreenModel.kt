package com.bizilabs.streeek.feature.reminders.list

import android.Manifest
import android.content.Context
import android.os.Build
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.feature.reminders.manager.ReminderManager
import com.bizilabs.streeek.lib.common.helpers.launcherState
import com.bizilabs.streeek.lib.common.helpers.permissionIsGranted
import com.bizilabs.streeek.lib.common.models.FetchListState
import com.bizilabs.streeek.lib.domain.models.ReminderDomain
import com.bizilabs.streeek.lib.domain.repositories.ReminderRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlin.enums.EnumEntries

data class ReminderListScreenState(
    val isEditing: Boolean = false,
    val fetchListState: FetchListState<ReminderDomain> = FetchListState.Loading,
    val label: String = "",
    val repeat: EnumEntries<DayOfWeek> = DayOfWeek.entries,
    val reminder: ReminderDomain? = null,
    val isAlarmPermissionGranted: Boolean = true,
    val selectedReminder: ReminderDomain? = null,
    val selectedDays: List<DayOfWeek> = emptyList(),
    val selectedHour: Int? = null,
    val selectedMinute: Int? = null,
    val isTimePickerOpen: Boolean = false,
    val isUpdating: Boolean = false,
) {
    val isEditActionEnabled: Boolean
        get() =
            when {
                reminder != null -> {
                    label != reminder.label || selectedDays != reminder.repeat ||
                        selectedHour != reminder.hour || selectedMinute != reminder.minute
                }

                else -> {
                    isValidLabel && selectedDays.isNotEmpty() && selectedHour != null && selectedMinute != null
                }
            }

    val time: String?
        get() =
            when {
                selectedHour != null && selectedMinute != null -> "$selectedHour:$selectedMinute"
                else -> ""
            }

    val isValidLabel: Boolean
        get() = label.isNotBlank() && label.length > 3
}

class ReminderListScreenModel(
    private val context: Context,
    private val manager: ReminderManager,
    private val repository: ReminderRepository,
) : StateScreenModel<ReminderListScreenState>(ReminderListScreenState()) {
    init {
        observePermissionState()
        observeReminders()
    }

    private fun observePermissionState() {
        screenModelScope.launch {
            launcherState.collectLatest { checkAlarmPermission() }
        }
    }

    private fun observeReminders() {
        screenModelScope.launch {
            repository.reminders.collectLatest {
                val list = it.values.toList()
                mutableState.update { state ->
                    state.copy(
                        fetchListState =
                            if (list.isEmpty()) {
                                FetchListState.Empty
                            } else {
                                FetchListState.Success(list)
                            },
                    )
                }
            }
        }
    }

    private fun checkAlarmPermission() {
        val granted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.permissionIsGranted(permission = Manifest.permission.USE_EXACT_ALARM)
            } else {
                true
            }
        mutableState.update { it.copy(isAlarmPermissionGranted = granted) }
    }

    fun onDismissSheet() {
        mutableState.update { it.copy(isEditing = false, reminder = null, isUpdating = false) }
    }

    fun onClickCreate() {
        mutableState.update {
            it.copy(
                isEditing = true,
                label = "",
                selectedHour = null,
                selectedMinute = null,
                selectedDays = emptyList(),
                selectedReminder = null,
                reminder = null,
            )
        }
    }

    fun onClickReminder(reminder: ReminderDomain) {
        mutableState.update {
            it.copy(
                isEditing = true,
                selectedReminder = reminder,
                selectedHour = reminder.hour,
                selectedMinute = reminder.minute,
                selectedDays = reminder.repeat,
                label = reminder.label,
            )
        }
    }

    fun onValueChangeReminderLabel(label: String) {
        mutableState.update { it.copy(label = label) }
    }

    fun onClickReminderDayOfWeek(day: DayOfWeek) {
        val days = (state.value.selectedDays).toMutableList()
        if (days.contains(day)) {
            days.remove(day)
        } else {
            days.add(day)
        }
        mutableState.update {
            it.copy(
                isEditing = true,
                selectedDays = days,
            )
        }
    }

    fun onCreateReminder() {
        screenModelScope.launch {
            val reminderDomain =
                ReminderDomain(
                    label = state.value.label,
                    repeat = state.value.selectedDays,
                    enabled = true,
                    hour = state.value.selectedHour ?: 0,
                    minute = state.value.selectedMinute ?: 0,
                )
            repository.update(
                reminder = reminderDomain,
                currentLabel = state.value.selectedReminder?.label,
            )

            manager.createAlarm(reminderDomain)

            mutableState.update {
                it.copy(isEditing = false)
            }
        }
    }

    fun onOpenTimePicker() {
        mutableState.update {
            it.copy(
                isTimePickerOpen = true,
            )
        }
    }

    fun onDismissTimePicker(
        hour: Int,
        minute: Int,
    ) {
        mutableState.update {
            it.copy(
                isTimePickerOpen = false,
                selectedHour = hour,
                selectedMinute = minute,
            )
        }
    }

    fun onLongClick(reminder: ReminderDomain) {
        mutableState.update {
            it.copy(
                isUpdating = true,
                reminder = reminder,
                selectedReminder = reminder,
            )
        }
    }

    fun onDismissUpdateSheet() {
        mutableState.update {
            it.copy(
                isUpdating = false,
            )
        }
    }

    fun editReminder(action: String) {
        screenModelScope.launch {
            when (action) {
                UpdateReminderActions.ENABLE.name.lowercase() -> {
                    repository.update(
                        reminder =
                            state.value.selectedReminder?.copy(enabled = true)
                                ?: return@launch,
                        "",
                    )
                    mutableState.update {
                        it.copy(isUpdating = false)
                    }
                }

                UpdateReminderActions.DISABLE.name.lowercase() -> {
                    repository.update(
                        reminder =
                            state.value.selectedReminder?.copy(enabled = false)
                                ?: return@launch,
                        "",
                    )
                    mutableState.update {
                        it.copy(
                            isUpdating = false,
                        )
                    }
                }

                UpdateReminderActions.DELETE.name.lowercase() -> {
                    repository.delete(reminder = state.value.selectedReminder ?: return@launch)
                    mutableState.update {
                        it.copy(
                            isUpdating = false,
                        )
                    }
                }
            }
        }
    }
}

enum class UpdateReminderActions {
    ENABLE,
    DISABLE,
    DELETE,
}
