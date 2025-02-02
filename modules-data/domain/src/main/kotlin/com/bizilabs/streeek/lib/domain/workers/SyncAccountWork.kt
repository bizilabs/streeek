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
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import java.util.UUID
import java.util.concurrent.TimeUnit

private val constraints =
    Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .setRequiresStorageNotLow(true)
        .build()

fun Context.startImmediateAccountSyncWork() {
    val parameters =
        Data.Builder()
            .build()

    val request =
        OneTimeWorkRequestBuilder<SyncAccountWork>()
            .addTag(SyncAccountWork.TAG)
            .setConstraints(constraints)
            .setInputData(parameters)
            .setId(UUID.randomUUID())
            .build()

    WorkManager.getInstance(this).enqueue(request)
}

fun Context.startPeriodicAccountSyncWork() {
    val parameters =
        Data.Builder()
            .build()

    val request =
        PeriodicWorkRequestBuilder<SyncAccountWork>(30, TimeUnit.MINUTES)
            .addTag(SyncAccountWork.TAG)
            .setConstraints(constraints)
            .setInputData(parameters)
            .setId(UUID.randomUUID())
            .build()

    WorkManager.getInstance(this)
        .enqueueUniquePeriodicWork(
            SyncAccountWork.TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
}

class SyncAccountWork(
    val context: Context,
    val params: WorkerParameters,
    val accountRepository: AccountRepository,
) : CoroutineWorker(context, params) {
    companion object {
        const val TAG = "SyncAccountWorker"
    }

    override suspend fun doWork(): Result {
        val result = accountRepository.syncAccount()
        if (result is DataResult.Error) {
            if (params.runAttemptCount > 2) return Result.failure()
            return Result.retry()
        }
        return Result.success()
    }
}
