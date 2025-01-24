package com.bizilabs.streeek.lib.common.notifications

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bizilabs.streeek.lib.resources.images.SafiDrawables

@SuppressLint("MissingPermission")
fun Context.notify(
    title: String,
    body: String,
    channel: AppNotificationChannel,
    imageUrl: Uri? = null,
) {
    val intent =
        Intent().apply {
            setClassName(
                this@notify,
                "com.bizilabs.streeek.lib.presentation.MainActivity",
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    val pendingIntent =
        PendingIntent.getActivity(
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
            null
        }

    val notification =
        NotificationCompat.Builder(this, channel.id)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(SafiDrawables.IconNotification)
            .setAutoCancel(true)
            .setGroup(channel.group.id)
            .setContentIntent(pendingIntent)
            .apply {
                if (bitmap != null) {
                    setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                } else {
                    setStyle(NotificationCompat.BigTextStyle().bigText(body))
                }
            }
            .build()

    val id = System.currentTimeMillis().toInt()
    NotificationManagerCompat.from(this).notify(id, notification)
}
