package com.bizilabs.streeek.feature.reminders.services

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.IBinder
import com.bizilabs.streeek.lib.resources.SafiResources
import timber.log.Timber

class ReminderService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        Timber.d("Muchas received oncreate")
        mediaPlayer =
            MediaPlayer
                .create(this, SafiResources.Audio.reminder)
                .apply {
                    setVolume(100f, 100f)
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build(),
                    )
                    isLooping = true
                }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        Timber.d("Muchas received onstart")
        mediaPlayer?.start()
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        Timber.d("Muchas received onbind")
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Muchas received on destroy")
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }
}
