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
import com.bizilabs.streeek.lib.domain.models.LeaderboardDomain
import com.bizilabs.streeek.lib.domain.models.updateOrCreate
import com.bizilabs.streeek.lib.domain.repositories.LeaderboardRepository
import kotlinx.coroutines.flow.first
import java.util.UUID
import java.util.concurrent.TimeUnit

private val constraints =
    Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .setRequiresStorageNotLow(true)
        .build()

private val parameters =
    Data.Builder()
        .build()

fun Context.startImmediateSyncLeaderboardWork() {
    val request =
        OneTimeWorkRequestBuilder<SyncLeaderboardWork>()
            .addTag(SyncLeaderboardWork.TAG)
            .setConstraints(constraints)
            .setInputData(parameters)
            .setId(UUID.randomUUID())
            .build()

    WorkManager.getInstance(this).enqueue(request)
}

fun Context.startPeriodicLeaderboardSyncWork() {
    val request =
        PeriodicWorkRequestBuilder<SyncLeaderboardWork>(15, TimeUnit.MINUTES)
            .addTag(SyncLeaderboardWork.TAG)
            .setConstraints(constraints)
            .setInputData(parameters)
            .setId(UUID.randomUUID())
            .build()

    WorkManager.getInstance(this)
        .enqueueUniquePeriodicWork(
            SyncLeaderboardWork.TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
}

class SyncLeaderboardWork(
    val context: Context,
    val params: WorkerParameters,
    val repository: LeaderboardRepository,
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "SyncLeaderboardWorker"
    }

    override suspend fun doWork(): Result {
        repository.setIsSyncing(isSyncing = true)
        val cached = repository.leaderboards.first().toMutableMap()
        val dailyResult = repository.getDaily(page = 1)
        if (dailyResult is DataResult.Error) return getWorkerResult()
        val daily = (dailyResult as DataResult.Success).data
        val dailyUpdate = cached[daily.name]?.updateOrCreate(value = daily) ?: daily
        repository.update(leaderboard = dailyUpdate)
        repository.set(leaderboard = dailyUpdate)
        repository.setIsSyncing(isSyncing = false)
        return Result.success()
    }

    private suspend fun getWorkerResult(): Result {
        repository.setIsSyncing(isSyncing = false)
        if (params.runAttemptCount > 2) return Result.failure()
        return Result.retry()
    }

}
