package com.bizilabs.streeek.feature.reminders.single

import android.content.Context
import android.icu.util.Calendar
import android.media.MediaPlayer
import android.os.Build
import cafe.adriel.voyager.core.model.StateScreenModel
import com.bizilabs.streeek.feature.reminders.manager.ReminderManager
import com.bizilabs.streeek.lib.domain.workers.stopReminderWork
import kotlinx.coroutines.flow.update
import timber.log.Timber

data class ReminderScreenState(
    val dismiss: Boolean = false,
    val isUpdated: Boolean = false,
    val label: String = "",
    val day: Int = 0,
    val code: Int = 0,
)

class ReminderScreenModel(
    private val context: Context,
    private val manager: ReminderManager,
) : StateScreenModel<ReminderScreenState>(ReminderScreenState()) {
    fun updateValues(
        label: String,
        day: Int,
        code: Int,
    ) {
        if (state.value.isUpdated) return
        mutableState.value =
            state.value.copy(
                isUpdated = true,
                label = label,
                day = day,
                code = code,
            )
    }

    fun onClickSnooze() {
        stopPlayingReminderTone()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = calendar.timeInMillis + (15 * 60 * 1000)
        manager.createAlarm(
            label = state.value.label,
            day = state.value.day,
            millis = calendar.timeInMillis,
        )
        mutableState.update { it.copy(dismiss = true) }
    }

    fun onSwipeDismiss() {
        stopPlayingReminderTone()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = calendar.timeInMillis + (7 * 24 * 60 * 60 * 1000)
        manager.createAlarm(
            label = state.value.label,
            day = state.value.day,
            millis = calendar.timeInMillis,
        )
        mutableState.update { it.copy(dismiss = true) }
    }

    private fun stopPlayingReminderTone() {
        context.stopReminderWork()
        try {
            val player =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    MediaPlayer(context)
                } else {
                    MediaPlayer()
                }
            player.stop()
            player.release()
        } catch (e: Exception) {
            Timber.d("Exception stopping all sounds")
            Timber.e(e)
        }
    }
}
