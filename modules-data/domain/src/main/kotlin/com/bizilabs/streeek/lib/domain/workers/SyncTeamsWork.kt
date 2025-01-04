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
import com.bizilabs.streeek.lib.domain.models.TeamWithMembersDomain
import com.bizilabs.streeek.lib.domain.models.updateOrCreate
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import com.bizilabs.streeek.lib.domain.repositories.TeamRepository
import kotlinx.coroutines.flow.first
import java.util.UUID
import java.util.concurrent.TimeUnit

private val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .setRequiresBatteryNotLow(true)
    .setRequiresStorageNotLow(true)
    .build()

private val parameters = Data.Builder()
    .build()

fun Context.startImmediateSyncTeamsWork() {

    val request = OneTimeWorkRequestBuilder<SyncTeamsWork>()
        .addTag(SyncTeamsWork.TAG)
        .setConstraints(constraints)
        .setInputData(parameters)
        .setId(UUID.randomUUID())
        .build()

    WorkManager.getInstance(this).enqueue(request)

}

fun Context.startPeriodicTeamsSyncWork() {

    val request = PeriodicWorkRequestBuilder<SyncTeamsWork>(30, TimeUnit.MINUTES)
        .addTag(SyncTeamsWork.TAG)
        .setConstraints(constraints)
        .setInputData(parameters)
        .setId(UUID.randomUUID())
        .build()

    WorkManager.getInstance(this)
        .enqueueUniquePeriodicWork(
            SyncTeamsWork.TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )

}

class SyncTeamsWork(
    val context: Context,
    val params: WorkerParameters,
    val repository: TeamRepository,
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "SyncTeamsWorker"
    }

    override suspend fun doWork(): Result {
        val cachedTeamsMap = repository.teams.first()
        val remoteTeams = repository.getAccountTeams()
        if (remoteTeams is DataResult.Error) return getWorkerResult()
        val remoteTeamsList = (remoteTeams as DataResult.Success).data
        val remoteIds = remoteTeamsList.map { it.team.id }
        for (cached in cachedTeamsMap.values){
            if (cached.team.id !in remoteIds)
                repository.deleteTeamLocally(team = cached)
        }
        for (team in remoteTeamsList) {
            val current = cachedTeamsMap[team.team.id]
            val result = repository.getTeam(id = team.team.id, page = 1)
            if (result is DataResult.Error) continue
            val teamWithMembers = (result as DataResult.Success).data
            val update = current.updateOrCreate(page = current?.page ?: 1, team = teamWithMembers)
            repository.updateTeamLocally(update)
        }
        val currentSelectedTeamId = repository.teamId.first()
        val updatedCachedTeams = repository.teams.first()
        if (currentSelectedTeamId == null) {
            val first = updatedCachedTeams.values.firstOrNull()
            first?.let { repository.setSelectedTeam(it) }
        }
        return Result.success()
    }

    private fun getWorkerResult(): Result {
        if (params.runAttemptCount > 2) return Result.failure()
        return Result.retry()
    }

}