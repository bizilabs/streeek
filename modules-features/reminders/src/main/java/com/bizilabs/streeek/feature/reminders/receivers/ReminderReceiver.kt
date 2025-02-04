package com.bizilabs.streeek.feature.reminders.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.media.MediaPlayer
import androidx.core.app.NotificationCompat
import com.bizilabs.streeek.feature.reminders.manager.ReminderManager
import com.bizilabs.streeek.lib.common.notifications.AppNotificationChannel
import com.bizilabs.streeek.lib.common.notifications.notify
import com.bizilabs.streeek.lib.domain.helpers.asJson
import com.bizilabs.streeek.lib.domain.helpers.buildUri
import com.bizilabs.streeek.lib.domain.models.notifications.NotificationResult
import com.bizilabs.streeek.lib.domain.workers.startReminderWork
import com.bizilabs.streeek.lib.domain.workers.stopReminderWork
import com.bizilabs.streeek.lib.resources.images.SafiDrawables
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

class ReminderReceiver : BroadcastReceiver() {
    private val manager: ReminderManager by inject(ReminderManager::class.java)
    private val quirkyNotifications =
        listOf(
            "Keep the Streak Alive!" to "GitHub called. It misses you! Commit now! 💻",
            "Don't Break the Chain!" to "Your streak is crying in the corner. Save it with a commit! 😢",
            "Code. Commit. Conquer." to "Be a hero. Commit today and keep the streak alive! 🦸‍♂️",
            "Oops! Almost Forgot?" to "Your GitHub streak is on thin ice. Time to commit! ❄️",
            "Git it Done!" to "One commit today keeps the streak alive forever. Maybe. 😉",
            "Tick-Tock!" to "The clock is ticking, and so is your streak's patience! 🕒",
            "The Streak is Strong With You!" to "Don't let the dark side break your streak. 🛡️",
            "One More Commit!" to "It's just one commit. What's stopping you? 😏",
            "Save the Streak!" to "Your streak deserves better. Show it some love! ❤️",
            "Streaks are Sacred!" to "A commit a day keeps the streak alive. Don’t ghost it! 👻",
        )

    lateinit var label: String
    lateinit var day: Number
    lateinit var code: Number

    enum class ReminderActions(
        val action: String,
        val label: String,
    ) {
        SNOOZE("streeek.action.reminder.snooze", "snooze"),
        CANCEL("streeek.action.reminder.cancel", "cancel"),
    }

    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        if (intent == null) return
        if (context == null) return
        val isReminder = intent.getStringExtra("streeek.receiver.type").equals("reminder")
        if (isReminder.not()) return
        label = intent.getStringExtra("reminder.label") ?: ""
        day = intent.getIntExtra("reminder.day", -1)
        code = intent.getIntExtra("reminder.code", -1)
        val type = intent.getStringExtra("streeek.reminder.type")
        when (type) {
            "action" -> context.handleAction(intent = intent)
            "ring" -> handleRing(context = context)
            else -> {}
        }
    }

    private fun handleRing(context: Context) {
        notify(context = context)
        context.stopReminderWork()
        context.startReminderWork()
    }

    private fun Context.handleAction(intent: Intent) {
        val action = intent.action ?: return
        stopReminderWork()
        when (action) {
            ReminderActions.SNOOZE.action -> snoozeReminder()
            ReminderActions.CANCEL.action -> cancelReminder()
        }
    }

    private fun Context.snoozeReminder() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = calendar.timeInMillis + 30 * 60 * 1000
        manager.cancelAlarm(label, day.toInt(), code.toInt())
        manager.createAlarm(
            label = label,
            day = day.toInt(),
            hour = calendar.get(Calendar.HOUR_OF_DAY),
            minute = calendar.get(Calendar.MINUTE),
        )
    }

    private fun Context.cancelReminder() {
        manager.cancelAlarm(label = label, day = day.toInt(), code = code.toInt())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = calendar.timeInMillis + 7 * 24 * 60 * 60 * 1000
        manager.createAlarm(
            label = label,
            day = day.toInt(),
            hour = calendar.get(Calendar.HOUR_OF_DAY),
            minute = calendar.get(Calendar.MINUTE),
        )
    }

    private fun notify(context: Context) {
        val actions = getNotificationActions(context = context)
        val contentIntent = getContentIntent(context = context)
        val (title, body) = quirkyNotifications.random()
        context.notify(
            title = title,
            body = body,
            channel = AppNotificationChannel.REMINDERS,
            actions = actions,
            contentIntent = contentIntent,
        )
    }

    private fun getNotificationActions(context: Context): List<NotificationCompat.Action> {
        val actions =
            ReminderActions.entries.map { value ->
                val intent =
                    Intent(context, this::class.java).apply {
                        putExtra("streeek.receiver.type", "reminder")
                        putExtra("streeek.reminder.type", "action")
                        putExtra("reminder.label", label)
                        putExtra("reminder.code", code)
                        putExtra("reminder.day", day)
                        action = value.action
                    }
                val pendingIntent: PendingIntent =
                    PendingIntent.getBroadcast(
                        context,
                        System.currentTimeMillis().toInt(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    )
                NotificationCompat.Action.Builder(
                    SafiDrawables.IconNotification,
                    value.label,
                    pendingIntent,
                ).build()
            }
        return actions
    }

    private fun getContentIntent(context: Context): PendingIntent? {
        val intent =
            Intent().apply {
                setClassName(
                    context,
                    "com.bizilabs.streeek.lib.presentation.MainActivity",
                )
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(
                    "notification_result",
                    NotificationResult(
                        type = "reminder",
                        uri =
                            buildUri(
                                "type" to "navigation",
                                "destination" to "reminder",
                                "label" to label,
                                "day" to day,
                                "code" to code,
                            ),
                    ).asJson(),
                )
            }
        val contentIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        return contentIntent
    }
}
