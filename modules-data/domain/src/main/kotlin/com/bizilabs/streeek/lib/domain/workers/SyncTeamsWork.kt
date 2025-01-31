package com.bizilabs.streeek.lib.domain.workers

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
import com.bizilabs.streeek.lib.domain.helpers.asJson
import com.bizilabs.streeek.lib.domain.models.TeamDetailsDomain
import com.bizilabs.streeek.lib.domain.models.notifications.ClimbedPositionMessage
import com.bizilabs.streeek.lib.domain.models.notifications.DroppedPositionMessage
import com.bizilabs.streeek.lib.domain.models.notifications.NotificationResult
import com.bizilabs.streeek.lib.domain.models.updateOrCreate
import com.bizilabs.streeek.lib.domain.repositories.TeamRepository
import com.bizilabs.streeek.lib.resources.images.SafiDrawables
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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

fun Context.startImmediateSyncTeamsWork() {
    val request =
        OneTimeWorkRequestBuilder<SyncTeamsWork>()
            .addTag(SyncTeamsWork.TAG)
            .setConstraints(constraints)
            .setInputData(parameters)
            .setId(UUID.randomUUID())
            .build()

    WorkManager.getInstance(this).enqueue(request)
}

fun Context.startPeriodicTeamsSyncWork() {
    val request =
        PeriodicWorkRequestBuilder<SyncTeamsWork>(30, TimeUnit.MINUTES)
            .addTag(SyncTeamsWork.TAG)
            .setConstraints(constraints)
            .setInputData(parameters)
            .setId(UUID.randomUUID())
            .build()

    WorkManager.getInstance(this)
        .enqueueUniquePeriodicWork(
            SyncTeamsWork.TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
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
        repository.updateIsSyncing(true)
        val remoteTeams = repository.getAccountTeams()
        if (remoteTeams is DataResult.Error) return getWorkerResult()
        val remoteTeamsList = (remoteTeams as DataResult.Success).data
        val remoteIds = remoteTeamsList.map { it.team.id }
        for (cached in cachedTeamsMap.values.filter { it.team.id !in remoteIds }) {
            repository.deleteTeamLocally(team = cached)
        }
        for (team in remoteTeamsList) {
            val current = cachedTeamsMap[team.team.id]
            val result = repository.getTeam(id = team.team.id, page = 1)
            if (result is DataResult.Error) continue
            val teamWithMembers = (result as DataResult.Success).data
            val update = current.updateOrCreate(page = current?.page ?: 1, team = teamWithMembers)
            repository.updateTeamLocally(update)
            notify(index = remoteTeamsList.indexOf(team), details = update)
        }
        val currentSelectedTeamId = repository.teamId.first()
        val updatedCachedTeams = repository.teams.first()
        if (currentSelectedTeamId == null) {
            val first = updatedCachedTeams.values.firstOrNull()
            first?.let { repository.setSelectedTeam(it) }
        }
        repository.updateIsSyncing(isSyncing = false)
        return Result.success()
    }

    private suspend fun getWorkerResult(): Result {
        repository.updateIsSyncing(isSyncing = false)
        if (params.runAttemptCount > 2) return Result.failure()
        return Result.retry()
    }

    private fun notify(
        index: Int,
        details: TeamDetailsDomain,
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val prev = details.rank.previous
            if (prev == null) return@launch
            val current = details.rank.current
            val message =
                when {
                    prev > current -> DroppedPositionMessage.random()
                    prev < current -> ClimbedPositionMessage.random()
                    else -> null
                }
            if (message == null) return@launch
            delay(1000 * 60 * index.toLong())
            context.notify(
                teamId = details.team.id,
                title = message.title.replace("{team_name}", details.team.name),
                body = message.body,
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun Context.notify(
        teamId: Long,
        title: String,
        body: String,
    ) {
        val intent =
            Intent().apply {
                setClassName(
                    this@notify,
                    "com.bizilabs.streeek.lib.presentation.MainActivity",
                )
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        with(intent) {
            putExtra(
                "notification_result",
                NotificationResult(
                    type = "navigation",
                    uri = "https://app.streeek.com/v1?action=navigate&destination=TEAM&teamId=$teamId",
                ).asJson(),
            )
        }

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        val notification =
            NotificationCompat.Builder(this, "streeek.team.updates")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(SafiDrawables.IconNotification)
                .setAutoCancel(true)
                .setGroup("streeek.group.user")
                .setContentIntent(pendingIntent)
                .build()

        val id = System.currentTimeMillis().toInt()
        NotificationManagerCompat.from(this).notify(id, notification)
    }
}
