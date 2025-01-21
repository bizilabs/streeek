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
        const val LAUNCHER_ACTIVITY_CLASS_NAME =
            "com.bizilabs.streeek.lib.presentation.MainActivity"
        const val EVENT_NOTIFICATIONS = "Event Notifications"
        const val NOTIFICATION_DESCRIPTION = "Notifications for app events"
    }

    fun initNotificationChannel() {
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                EVENT_NOTIFICATIONS,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = NOTIFICATION_DESCRIPTION
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
        imageResId: Int? = null,
    ) {
        val intent =
            Intent().apply {
                setClassName(
                    context,
                    LAUNCHER_ACTIVITY_CLASS_NAME,
                )
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
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
