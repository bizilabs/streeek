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
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.helpers.isPastDue
import com.bizilabs.streeek.lib.domain.models.ContributionDomain
import com.bizilabs.streeek.lib.domain.models.UserEventDomain
import com.bizilabs.streeek.lib.domain.repositories.ContributionRepository
import com.bizilabs.streeek.lib.domain.repositories.PreferenceRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.TimeUnit

private val uuid = UUID.randomUUID()

private val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .setRequiresBatteryNotLow(true)
    .setRequiresStorageNotLow(true)
    .build()

private val parameters = Data.Builder()
    .build()

fun Context.startPeriodicDailySyncContributionsWork() {

    val request = PeriodicWorkRequestBuilder<SyncDailyContributionsWork>(30, TimeUnit.MINUTES)
        .addTag(SyncDailyContributionsWork.TAG)
        .setConstraints(constraints)
        .setInputData(parameters)
        .setId(uuid)
        .build()

    WorkManager.getInstance(this)
        .enqueueUniquePeriodicWork(
            SyncDailyContributionsWork.TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )

}

fun Context.startImmediateDailySyncContributionsWork() {

    val request = OneTimeWorkRequestBuilder<SyncDailyContributionsWork>()
        .addTag(SyncDailyContributionsWork.TAG)
        .setConstraints(constraints)
        .setInputData(parameters)
        .setId(uuid)
        .build()

    WorkManager.getInstance(this).enqueue(request)

}

class SyncDailyContributionsWork(
    val context: Context,
    val params: WorkerParameters,
    val preferences: PreferenceRepository,
    val repository: ContributionRepository,
) : CoroutineWorker(context, params) {
    companion object {
        const val TAG = "DAILY_CONTRIBUTIONS_WORKER"
    }

    override suspend fun doWork(): Result {
        Timber.d("Starting sync work")

        preferences.setIsSyncingContributions(isSyncing = true)

        val today = Clock.System.todayIn(TimeZone.UTC)

        val list = mutableListOf<UserEventDomain>()
        val supabaseContributions = mutableListOf<ContributionDomain>()

        var page: Int? = 1
        while (page != null) {
            val eventsResult = repository.getEvents(page = page)
            if (eventsResult is DataResult.Error) {
                preferences.setIsSyncingContributions(isSyncing = false)
                return Result.failure().also { Timber.e(eventsResult.message) }
            }
            val events = (eventsResult as DataResult.Success).data
            Timber.d(
                "Events -> ${
                    events.map {
                        mapOf(
                            "type" to it.type,
                            "createdAt" to it.createdAt.asString()
                        )
                    }
                }"
            )
            for (event in events) {
                val eventDate = event.createdAt
                Timber.d("Today      -> ${today.asString()}")
                Timber.d("Event Date -> ${eventDate.asString()}")
                if (eventDate.isPastDue()) {
                    Timber.d("Event date is past INCEPTION date ...")
                    page = null
                    break
                }
                Timber.d("Today is the same day...")
                val saved =
                    repository.getContributionWithGithubEventId(githubEventId = event.id)
                if (saved is DataResult.Error) {
                    preferences.setIsSyncingContributions(isSyncing = false)
                    return Result.failure().also { Timber.e(saved.message) }
                }
                val contribution = (saved as DataResult.Success).data
                if (contribution == null) {
                    Timber.d("Event has not been uploaded to supabase")
                    list.add(event)
                } else {
                    Timber.d("Event has been uploaded to supabase")
                    supabaseContributions.add(contribution)
                }
            }
            page = if (list.isNotEmpty() && page != null) page.plus(1) else null
        }

        // save events to supabase as contributions
        Timber.d("Saving list of events to supabase")
        val result = repository.saveContribution(events = list)
        if (result is DataResult.Error) {
            preferences.setIsSyncingContributions(isSyncing = false)
            return Result.failure().also { Timber.e(result.message) }
        }
        val data = (result as DataResult.Success).data
        val contributions = data.toMutableList().plus(supabaseContributions).toMutableList()

        if (contributions.isEmpty()) return Result.success()

        // save contributions locally
        Timber.d("Saving list of contributions to local cache")
        val iterator = contributions.iterator()
        while (iterator.hasNext()) {
            val contribution = iterator.next()
            val result = repository.getContributionsLocally(id = contribution.id)
            if (result is DataResult.Success && result.data != null) iterator.remove()
        }
        val local = repository.saveContributionLocally(contributions = contributions)
        if (local is DataResult.Error) {
            preferences.setIsSyncingContributions(isSyncing = false)
            return Result.failure().also { Timber.e(local.message) }
        }

        // return success if everything is okay
        preferences.setIsSyncingContributions(isSyncing = false)
        return Result.success()
    }
}