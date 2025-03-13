package com.bizilabs.streeek.lib.domain.managers

import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationChannel as AndroidChannel

fun NotificationChannel.asAndroidNotificationChannel(): AndroidChannel =
    AndroidChannel(
        id,
        label,
        importance,
    ).apply {
        description = this@asAndroidNotificationChannel.description
    }

fun Context.initNotificationChannels() {
    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannels(Notifications.Channels.values.map { it.asAndroidNotificationChannel() })
}
