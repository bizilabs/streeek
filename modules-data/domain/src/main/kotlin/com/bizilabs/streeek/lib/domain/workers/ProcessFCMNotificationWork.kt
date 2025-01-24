package com.bizilabs.streeek.lib.domain.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bizilabs.streeek.lib.domain.helpers.asJson
import com.bizilabs.streeek.lib.domain.models.notifications.NotificationResult
import com.bizilabs.streeek.lib.domain.models.notifications.asNotificationResult
import timber.log.Timber
import java.util.UUID

fun Context.startProcessingNotificationWork(result: NotificationResult) {
    val parameters =
        Data.Builder()
            .putString("notification.result", result.asJson())
            .build()

    val request =
        OneTimeWorkRequestBuilder<ProcessFCMNotificationWork>()
            .addTag(ProcessFCMNotificationWork.TAG)
            .setInputData(parameters)
            .setId(UUID.randomUUID())
            .build()

    WorkManager.getInstance(this).enqueue(request)
}

class ProcessFCMNotificationWork(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    companion object {
        const val TAG = "ProcessFCMNotificationWork"
    }

    override suspend fun doWork(): Result {
        val result = inputData.getString("notification.result")?.asNotificationResult()
        // for any intensive task ddo it here
        Timber.d("Notification Result -> $result")
        return Result.success()
    }
}
