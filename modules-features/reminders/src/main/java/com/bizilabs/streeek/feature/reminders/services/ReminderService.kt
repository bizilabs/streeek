package com.bizilabs.streeek.feature.reminders.services

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.bizilabs.streeek.feature.reminders.receivers.ReminderReceiver.ReminderActions
import com.bizilabs.streeek.lib.common.notifications.AppNotificationChannel
import com.bizilabs.streeek.lib.common.notifications.createNotification
import com.bizilabs.streeek.lib.domain.helpers.asJson
import com.bizilabs.streeek.lib.domain.helpers.buildUri
import com.bizilabs.streeek.lib.domain.models.notifications.NotificationResult
import com.bizilabs.streeek.lib.resources.images.SafiDrawables
import timber.log.Timber

class ReminderService : Service() {
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

    enum class ReminderActions(
        val action: String,
        val label: String,
    ) {
        SNOOZE("streeek.action.reminder.snooze", "snooze"),
        CANCEL("streeek.action.reminder.cancel", "cancel"),
    }

    lateinit var label: String
    lateinit var day: Number
    lateinit var code: Number

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        Timber.d("STREEEKNOTIFAI foreground service started")
        if (intent == null) return START_NOT_STICKY
        label = intent.getStringExtra("reminder.label") ?: ""
        day = intent.getIntExtra("reminder.day", -1)
        code = intent.getIntExtra("reminder.code", -1)

        Timber.d("STREEEKNOTIFAI foreground service started 1: label>$label day>$day code>$code")
        notify(this)
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        Timber.d("STREEEKNOTIFAI foreground service started 2: label>$label day>$day code>$code")
        return null
    }

    private fun notify(context: Context) {
        val actions = getNotificationActions(context = context)
        val contentIntent = getContentIntent(context = context)
        val (title, body) = quirkyNotifications.random()

        val notifcation =
            context.createNotification(
                title = title,
                body = body,
                channel = AppNotificationChannel.REMINDERS,
                actions = actions,
                pendingIntent = contentIntent,
                imageUrl = null,
            )

        Timber.d("STREEEKNOTIFAI foreground service started 4: label>$label day>$day code>$code")

        ServiceCompat.startForeground(
            this,
            1,
            notifcation,
            0x40000000,
        )

        Timber.d("STREEEKNOTIFAI foreground service started 5: label>$label day>$day code>$code")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
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
