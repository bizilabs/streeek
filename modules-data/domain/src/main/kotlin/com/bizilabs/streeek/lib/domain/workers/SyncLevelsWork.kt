package com.bizilabs.streeek.lib.domain.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.repositories.LevelRepository
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.TimeUnit


private val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .setRequiresBatteryNotLow(true)
    .setRequiresStorageNotLow(true)
    .build()

private val parameters = Data.Builder()
    .build()

fun Context.startImmediateLevelsSyncWork() {

    val request = OneTimeWorkRequestBuilder<SyncLevelsWork>()
        .addTag(SyncLevelsWork.TAG)
        .setConstraints(constraints)
        .setInputData(parameters)
        .setId(UUID.randomUUID())
        .build()

    WorkManager.getInstance(this).enqueue(request)

}

fun Context.startPeriodicLevelsSyncWork() {

    val request = PeriodicWorkRequestBuilder<SyncLevelsWork>(24, TimeUnit.HOURS)
        .addTag(SyncLevelsWork.TAG)
        .setConstraints(constraints)
        .setInputData(parameters)
        .setId(UUID.randomUUID())
        .build()

    WorkManager.getInstance(this)
        .enqueueUniquePeriodicWork(
            SyncLevelsWork.TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )

}

class SyncLevelsWork(
    val context: Context,
    val params: WorkerParameters,
    val repository: LevelRepository,
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "SyncLevelsWorker"
    }

    override suspend fun doWork(): Result {
        Timber.d("Levels -> Starting work")
        val result = repository.getLevels()
        if (result is DataResult.Error) getWorkerResult()
        Timber.d("Levels -> Result is not error")
        val list = (result as DataResult.Success).data
        Timber.d("Levels -> $list")
        repository.saveLevels(levels = list)
        Timber.d("Levels -> saved list successfully")
        return Result.success()
    }

    private fun getWorkerResult(): Result {
        if (params.runAttemptCount > 2) return Result.failure()
        return Result.retry()
    }

}
