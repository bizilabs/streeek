package com.bizilabs.streeek.lib.domain.repositories

import kotlin.time.Duration

sealed interface WorkerType {
    object Once : WorkerType

    data class Periodic(val duration: Duration) : WorkerType
}

interface WorkersRepository {
    fun runSaveToken(token: String)

    fun runSyncAccount(type: WorkerType)

    fun runSyncContributions()

    fun runSyncDailyContributions()

    fun runSyncLeaderboard(type: WorkerType)

    fun runSyncLevels(type: WorkerType)

    fun runSyncTeams(type: WorkerType)
}
