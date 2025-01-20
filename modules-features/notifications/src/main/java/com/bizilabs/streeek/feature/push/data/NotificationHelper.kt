package com.bizilabs.streeek.feature.push.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import timber.log.Timber

class NotificationHelper(private val context: Context) {
    companion object {
        private const val CHANNEL_ID = "event_notifications"
        private const val GROUP_KEY_EVENTS = "com.bizilabs.streeek.EVENTS"
        const val TAG = "FCMTopicSubscription"
    }

    fun initNotificationChannel() {
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "Event Notifications",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Notifications for app events"
                enableLights(true)
                lightColor =
                    context.getColor(com.bizilabs.streeek.lib.resources.R.color.ic_launcher_background)
                enableVibration(true)
            }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    fun showNotification(
        title: String,
        message: String,
        intent: Intent? = null,
        imageResId: Int? = null,
        actions: List<Pair<String, Intent>> = emptyList(),
    ) {
        val pendingIntent =
            intent?.let {
                PendingIntent.getActivity(
                    context,
                    0,
                    it,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
            }

        val largeIcon =
            imageResId?.let {
                BitmapFactory.decodeResource(context.resources, it)
            }

        val builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(com.bizilabs.streeek.lib.resources.R.drawable.icon_notification)
                .setAutoCancel(true)
                .setGroup(GROUP_KEY_EVENTS)
                .setContentIntent(pendingIntent)
                .apply {
                    if (largeIcon != null) {
                        setStyle(
                            NotificationCompat.BigPictureStyle()
                                .bigPicture(largeIcon),
                        )
                    } else {
                        setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    }
                    actions.forEachIndexed { index, (actionTitle, actionIntent) ->
                        val actionPendingIntent =
                            PendingIntent.getActivity(
                                context,
                                index,
                                actionIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                            )
                        addAction(0, actionTitle, actionPendingIntent)
                    }
                }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            sendPermissionRequestNotification(context)

            return
        }
        NotificationManagerCompat.from(context)
            .notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun sendPermissionRequestNotification(context: Context) {
        val intent =
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Enable Notifications")
                .setContentText("To receive notifications, please enable notification permissions in the app settings.")
                .setSmallIcon(com.bizilabs.streeek.lib.resources.R.drawable.icon_notification)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(context).notify(1001, builder.build())
    }

    // For just in case you need to Show Summary for Notifications
    fun showSummaryNotification() {
        val summaryNotification =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Multiple Events")
                .setContentText("You have new event notifications")
                .setSmallIcon(com.bizilabs.streeek.lib.resources.R.drawable.icon_notification)
                .setStyle(NotificationCompat.InboxStyle().setSummaryText("More events"))
                .setGroup(GROUP_KEY_EVENTS)
                .setGroupSummary(true)
                .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            sendPermissionRequestNotification(context)

            return
        }
        NotificationManagerCompat.from(context).notify(0, summaryNotification)
    }

    /**
     * Subscribes to a list of FCM topics.
     * @param topics List of topic names to subscribe to.
     */
    fun subscribeToTopics(topics: List<String>) {
        topics.forEach { topic ->
            Firebase.messaging.subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.tag(TAG).d("Successfully subscribed to topic: $topic")
                    } else {
                        Timber.tag(TAG).e(task.exception, "Failed to subscribe to topic: $topic")
                    }
                }
        }
    }

    /**
     * Subscribes to a single FCM topic.
     * @param topic The topic name to subscribe to.
     */
    fun subscribeToTopic(topic: String) {
        Firebase.messaging.subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.tag(TAG).d("Successfully subscribed to topic: $topic")
                } else {
                    Timber.tag(TAG).e(task.exception, "Failed to subscribe to topic: $topic")
                }
            }
    }
}
