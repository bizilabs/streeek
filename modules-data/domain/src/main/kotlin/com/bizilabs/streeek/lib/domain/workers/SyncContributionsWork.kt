package com.bizilabs.streeek.lib.domain.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.repositories.ContributionRepository
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import java.util.UUID

fun Context.startSyncContributionsWork() {
    val uuid = UUID.randomUUID()

    val constraints =
        Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(true)
            .build()

    val parameters =
        Data.Builder()
            .build()

    val request =
        OneTimeWorkRequestBuilder<SyncContributionsWork>()
            .addTag(SyncContributionsWork.TAG)
            .setConstraints(constraints)
            .setInputData(parameters)
            .setId(uuid)
            .build()

    WorkManager.getInstance(this).enqueue(request)
}

class SyncContributionsWork(
    val context: Context,
    val params: WorkerParameters,
    val contributionRepository: ContributionRepository,
) : CoroutineWorker(context, params) {
    companion object {
        const val TAG = "CONTRIBUTIONS_WORKER"
    }

    override suspend fun doWork(): Result {
        val lastSyncTime = contributionRepository.lastSync.firstOrNull()
        if (lastSyncTime != null) return Result.success()
        var page: Int? = 1
        while (page != null) {
            val contributions = contributionRepository.getContributions(page = page)
            if (contributions is DataResult.Error) {
                return Result.failure().also { Timber.e(contributions.message) }
            }
            val list = (contributions as DataResult.Success).data
            Timber.d("Contributions -> $list")
            contributionRepository.saveContributionLocally(contributions = list)
            page = if (list.isNotEmpty()) page.plus(1) else null
        }
        context.startPeriodicDailySyncContributionsWork()
        contributionRepository.updateLastSync(timeInMillis = System.currentTimeMillis())
        return Result.success()
    }
}
