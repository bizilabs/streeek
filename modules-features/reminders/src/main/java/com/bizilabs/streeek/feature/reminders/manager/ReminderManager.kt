package com.bizilabs.streeek.feature.reminders.manager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.bizilabs.streeek.feature.reminders.receivers.ReminderReceiver
import com.bizilabs.streeek.lib.domain.managers.notifications.NotificationCode
import com.bizilabs.streeek.lib.domain.models.ReminderDomain
import kotlinx.datetime.DayOfWeek
import java.util.Calendar

interface ReminderManager {
    fun createAlarm(reminder: ReminderDomain)

    fun createAlarm(
        label: String,
        day: Int,
        millis: Long,
    )

    fun cancelAlarm(reminder: ReminderDomain)

    fun cancelAlarm(
        label: String,
        day: Int,
        code: Int,
    )
}

internal class ReminderManagerImpl(
    private val context: Context,
) : ReminderManager {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun createAlarm(reminder: ReminderDomain) {
        if (reminder.enabled.not()) return
        reminder.repeat.forEach { dayOfWeek ->
            val calendar =
                getNextAlarmTime(
                    dayOfWeek = dayOfWeek,
                    hour = reminder.hour,
                    minute = reminder.minute,
                )

            val alarmIntent = createPendingIntent(reminder = reminder, dayOfWeek = dayOfWeek)
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmIntent,
            )
        }
    }

    override fun createAlarm(
        label: String,
        day: Int,
        millis: Long,
    ) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        calendar.apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val alarmIntent =
            createPendingIntent(
                label = label,
                day = day,
                requestCode = NotificationCode.REMINDER.code,
            )
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmIntent,
        )
    }

    override fun cancelAlarm(reminder: ReminderDomain) {
        reminder.repeat.forEach { dayOfWeek ->
            val alarmIntent = createPendingIntent(reminder = reminder, dayOfWeek = dayOfWeek)
            alarmManager.cancel(alarmIntent)
        }
    }

    override fun cancelAlarm(
        label: String,
        day: Int,
        code: Int,
    ) {
        val alarmIntent = createPendingIntent(label = label, day = day, requestCode = code)
        alarmManager.cancel(alarmIntent)
    }

    private fun getNextAlarmTime(
        dayOfWeek: DayOfWeek,
        hour: Int,
        minute: Int,
    ): Calendar {
        val now = Calendar.getInstance()
        val calendar =
            Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

        // Calculate the next occurrence of the given day
        val currentDayOfWeek = now.get(Calendar.DAY_OF_WEEK)
        val targetDayOfWeek = dayOfWeek.value % 7 + 1 // DayOfWeek to Calendar conversion

        val daysUntilNext =
            if (targetDayOfWeek >= currentDayOfWeek) {
                targetDayOfWeek - currentDayOfWeek
            } else {
                7 - (currentDayOfWeek - targetDayOfWeek)
            }

        if (daysUntilNext > 0 || calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, daysUntilNext)
        }

        return calendar
    }

    private fun createPendingIntent(
        reminder: ReminderDomain,
        dayOfWeek: DayOfWeek,
    ): PendingIntent {
        return createPendingIntent(
            label = reminder.label,
            day = dayOfWeek.value,
            requestCode = NotificationCode.REMINDER.code,
        )
    }

    private fun createPendingIntent(
        label: String,
        day: Int,
        requestCode: Int,
    ): PendingIntent {
        val intent =
            Intent(context, ReminderReceiver::class.java).apply {
                putExtra("streeek.receiver.type", "reminder")
                putExtra("streeek.reminder.type", "ring")
                putExtra("reminder.label", label)
                putExtra("reminder.day", day)
                putExtra("reminder.code", requestCode)
            }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
