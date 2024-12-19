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
import timber.log.Timber
import java.util.UUID

fun Context.startSyncContributionsWork() {

    val uuid = UUID.randomUUID()

    val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .setRequiresStorageNotLow(true)
        .build()

    val parameters = Data.Builder()
        .build()

    val request = OneTimeWorkRequestBuilder<SyncContributionsWork>()
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
        var page: Int? = 1
        while (page != null) {
            val contributions = contributionRepository.getContributions(page = page)
            if (contributions is DataResult.Error)
                return Result.failure().also { Timber.e(contributions.message) }
            val list = (contributions as DataResult.Success).data
            page = if (list.isNotEmpty()) page.plus(1) else null
            list.forEach { contributionRepository.saveContributionLocally(contribution = it) }
        }
        context.startPeriodicDailySyncContributionsWork()
        return Result.success()
    }
}
