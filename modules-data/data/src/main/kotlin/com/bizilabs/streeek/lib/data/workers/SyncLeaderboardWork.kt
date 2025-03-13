package com.bizilabs.streeek.lib.data.workers

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
import com.bizilabs.streeek.lib.domain.models.updateOrCreate
import com.bizilabs.streeek.lib.domain.repositories.LeaderboardRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
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
        // sync weekly leaderboard
        val weeklyResult = repository.getWeekly(page = 1)
        if (weeklyResult is DataResult.Error) return getWorkerResult()
        val weekly = (weeklyResult as DataResult.Success).data
        val weeklyUpdate = cached[weekly.name]?.updateOrCreate(value = weekly) ?: weekly
        repository.update(leaderboard = weeklyUpdate)
        // sync monthly leaderboard
        val monthlyResult = repository.getMonthly(page = 1)
        if (monthlyResult is DataResult.Error) return getWorkerResult()
        val monthly = (monthlyResult as DataResult.Success).data
        val monthlyUpdate = cached[monthly.name]?.updateOrCreate(value = monthly) ?: monthly
        repository.update(leaderboard = monthlyUpdate)
        // sync ultimate leaderboard
        val ultimateResult = repository.getUltimate(page = 1)
        if (ultimateResult is DataResult.Error) return getWorkerResult()
        val ultimate = (ultimateResult as DataResult.Success).data
        val ultimateUpdate = cached[ultimate.name]?.updateOrCreate(value = ultimate) ?: ultimate
        repository.update(leaderboard = ultimateUpdate)
        // set selected
        val selectedId = repository.selectedLeaderBoardId.firstOrNull()
        val selected = cached[selectedId] ?: weeklyUpdate
        repository.set(leaderboard = selected)
        // finish
        repository.setIsSyncing(isSyncing = false)
        return Result.success()
    }

    private suspend fun getWorkerResult(): Result {
        repository.setIsSyncing(isSyncing = false)
        if (params.runAttemptCount > 2) return Result.failure()
        return Result.retry()
    }
}
