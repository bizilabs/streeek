package com.bizilabs.streeek.lib.data.repositories

import android.content.Context
import com.bizilabs.streeek.lib.data.workers.startImmediateAccountSyncWork
import com.bizilabs.streeek.lib.data.workers.startImmediateLevelsSyncWork
import com.bizilabs.streeek.lib.data.workers.startImmediateSyncLeaderboardWork
import com.bizilabs.streeek.lib.data.workers.startImmediateSyncTeamsWork
import com.bizilabs.streeek.lib.data.workers.startPeriodicDailySyncContributionsWork
import com.bizilabs.streeek.lib.data.workers.startPeriodicLeaderboardSyncWork
import com.bizilabs.streeek.lib.data.workers.startPeriodicLevelsSyncWork
import com.bizilabs.streeek.lib.data.workers.startPeriodicTeamsSyncWork
import com.bizilabs.streeek.lib.data.workers.startReminderWork
import com.bizilabs.streeek.lib.data.workers.startSaveFCMTokenWork
import com.bizilabs.streeek.lib.data.workers.startSyncContributionsWork
import com.bizilabs.streeek.lib.data.workers.stopReminderWork
import com.bizilabs.streeek.lib.domain.repositories.WorkerType
import com.bizilabs.streeek.lib.domain.repositories.WorkersRepository

class WorkersRepositoryImpl(
    private val context: Context,
) : WorkersRepository {
    override fun runSaveToken() {
        context.startSaveFCMTokenWork()
    }

    override fun runSyncAccount(type: WorkerType) {
        when (type) {
            WorkerType.Once -> context.startImmediateAccountSyncWork()
            is WorkerType.Periodic -> context.startImmediateAccountSyncWork()
        }
    }

    override fun runSyncContributions() {
        context.startSyncContributionsWork()
    }

    override fun runSyncDailyContributions() {
        context.startPeriodicDailySyncContributionsWork()
    }

    override fun runSyncLeaderboard(type: WorkerType) {
        when (type) {
            WorkerType.Once -> context.startImmediateSyncLeaderboardWork()
            is WorkerType.Periodic -> context.startPeriodicLeaderboardSyncWork()
        }
    }

    override fun runSyncLevels(type: WorkerType) {
        when (type) {
            WorkerType.Once -> context.startImmediateLevelsSyncWork()
            is WorkerType.Periodic -> context.startPeriodicLevelsSyncWork()
        }
    }

    override fun runSyncTeams(type: WorkerType) {
        when (type) {
            WorkerType.Once -> context.startImmediateSyncTeamsWork()
            is WorkerType.Periodic -> context.startPeriodicTeamsSyncWork()
        }
    }

    override fun runReminder(isStarting: Boolean) {
        when (isStarting) {
            true -> context.startReminderWork()
            false -> context.stopReminderWork()
        }
    }
}
