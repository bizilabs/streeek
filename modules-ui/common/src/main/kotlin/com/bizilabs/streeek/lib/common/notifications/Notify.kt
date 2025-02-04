package com.bizilabs.streeek.lib.common.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.bizilabs.streeek.lib.resources.images.SafiDrawables
import timber.log.Timber

fun Context.notify(
    title: String,
    body: String,
    channel: AppNotificationChannel,
    imageUrl: Uri? = null,
) {
    createNotificationAndSend(
        title = title,
        body = body,
        channel = channel,
        imageUrl = imageUrl,
    )
}

fun Context.notify(
    title: String,
    body: String,
    channel: AppNotificationChannel,
    imageUrl: Uri? = null,
    contentIntent: PendingIntent? = null,
    actions: List<NotificationCompat.Action> = emptyList(),
) {
    createNotificationAndSend(
        title = title,
        body = body,
        channel = channel,
        imageUrl = imageUrl,
        pendingIntent = contentIntent,
        actions = actions,
    )
}

@SuppressLint("MissingPermission")
private fun Context.createNotificationAndSend(
    title: String,
    body: String,
    channel: AppNotificationChannel,
    imageUrl: Uri? = null,
    pendingIntent: PendingIntent? = null,
    actions: List<NotificationCompat.Action> = emptyList(),
) {
    val notification =
        createNotification(
            pendingIntent = pendingIntent,
            imageUrl = imageUrl,
            channel = channel,
            title = title,
            body = body,
            actions = actions,
        )

    val id = System.currentTimeMillis().toInt()
    val notificationManager =
        this.getSystemService(NotificationManager::class.java) as NotificationManager

    notificationManager.notify(id, notification)
}

fun Context.createNotification(
    pendingIntent: PendingIntent?,
    imageUrl: Uri?,
    channel: AppNotificationChannel,
    title: String,
    body: String,
    actions: List<NotificationCompat.Action>,
): Notification {
    val intent =
        Intent().apply {
            setClassName(
                this@createNotification,
                "com.bizilabs.streeek.lib.presentation.MainActivity",
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

    val contentIntent =
        pendingIntent ?: PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

    val bitmap =
        try {
            imageUrl?.let {
                val source = ImageDecoder.createSource(contentResolver, imageUrl)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: RuntimeException) {
            Timber.e(e)
            null
        }

    val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

    val notification =
        NotificationCompat.Builder(this, channel.id)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(SafiDrawables.IconNotification)
            .setAutoCancel(true)
            .setGroup(channel.group.id)
            .setContentIntent(contentIntent)
            .setSound(sound)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .apply {
                actions.forEach { addAction(it) }
                if (bitmap != null) {
                    setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                } else {
                    setStyle(NotificationCompat.BigTextStyle().bigText(body))
                }
            }
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    return notification
}
