package com.bizilabs.streeek.lib.domain.workers

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bizilabs.streeek.lib.resources.SafiResources
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.UUID

fun Context.startReminderWork(): String {
    val uuid = UUID.randomUUID()
    val parameters =
        Data.Builder()
            .putBoolean("reminder.playing", true)
            .build()

    val request =
        OneTimeWorkRequestBuilder<ReminderWorker>()
            .addTag(ReminderWorker.TAG)
            .setId(uuid)
            .setInputData(parameters)
            .build()

    WorkManager.getInstance(this).enqueue(request)
    return uuid.toString()
}

fun Context.stopReminderWork() {
    val parameters =
        Data.Builder()
            .putBoolean("reminder.playing", false)
            .build()

    val request =
        OneTimeWorkRequestBuilder<ReminderWorker>()
            .addTag(ReminderWorker.TAG)
            .setInputData(parameters)
            .build()

    with(WorkManager.getInstance(this)) {
        cancelAllWorkByTag(ReminderWorker.TAG)
        enqueue(request)
    }
}

class ReminderWorker(
    val context: Context,
    val params: WorkerParameters,
) : CoroutineWorker(context, params) {
    companion object {
        const val TAG = "ReminderWorker"
    }

    override suspend fun doWork(): Result {
        Timber.d("Staring Reminder work")
        val playing = params.inputData.getBoolean("reminder.playing", false)
        if (playing) {
            playReminderSound()
        } else {
            stopReminderSound()
        }
        return Result.success()
    }

    private suspend fun playReminderSound() {
        Timber.d("Start playing sound for Reminder Work")
        val player = MediaPlayer.create(context, SafiResources.Audio.reminder)
        player.apply {
            isLooping = true
        }
        player.start()
        delay(1000 * 60 * 5)
        player.stop()
        player.release()
    }

    private fun stopReminderSound() {
        Timber.d("Stop playing sound for Reminder Work")
        val player =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                MediaPlayer(context)
            } else {
                MediaPlayer()
            }
        player.stop()
        player.release()
    }
}
