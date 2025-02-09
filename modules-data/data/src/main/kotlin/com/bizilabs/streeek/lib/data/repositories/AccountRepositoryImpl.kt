package com.bizilabs.streeek.lib.data.repositories

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.bizilabs.streeek.lib.data.mappers.asDataResult
import com.bizilabs.streeek.lib.data.mappers.toCache
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.asJson
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.helpers.buildUri
import com.bizilabs.streeek.lib.domain.managers.notifications.AppNotificationChannel
import com.bizilabs.streeek.lib.domain.managers.notifications.notify
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.domain.models.notifications.LevelledUpMessages
import com.bizilabs.streeek.lib.domain.models.notifications.NotificationResult
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.local.sources.contributions.ContributionsLocalSource
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.models.AccountCreateRequestDTO
import com.bizilabs.streeek.lib.remote.sources.account.AccountRemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.Clock

class AccountRepositoryImpl(
    private val context: Context,
    private val remote: AccountRemoteSource,
    private val local: AccountLocalSource,
    private val contributionsLocalSource: ContributionsLocalSource,
) : AccountRepository {
    override val account: Flow<AccountDomain?>
        get() = local.account.mapLatest { it?.toDomain() }

    override val isSyncingAccount: Flow<Boolean>
        get() = local.isSyncingAccount

    override suspend fun getAccountWithGithubId(id: Int): DataResult<AccountDomain?> {
        return when (val result = remote.fetchAccountWithGithubId(id)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> {
                val account = result.data?.toDomain()
                account?.let { local.updateAccount(account = it.toCache()) }
                if (account != null) {
                    getAccount(id = account.id)
                } else {
                    DataResult.Success(result.data?.toDomain())
                }
            }
        }
    }

    override suspend fun getAccount(id: Long): DataResult<AccountDomain> {
        return when (val result = remote.getAccount(id)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> DataResult.Success(result.data.toDomain())
        }
    }

    override suspend fun createAccount(
        githubId: Int,
        username: String,
        email: String,
        bio: String,
        avatarUrl: String,
    ): DataResult<AccountDomain> {
        val request =
            AccountCreateRequestDTO(
                githubId = githubId,
                username = username,
                email = email,
                bio = bio,
                avatarUrl = avatarUrl,
                createdAt = Clock.System.now().asString(DateFormats.ISO_8601_Z) ?: "",
                updatedAt = Clock.System.now().asString(DateFormats.ISO_8601_Z) ?: "",
            )
        return when (val result = remote.createAccount(request)) {
            is NetworkResult.Failure -> DataResult.Error(result.exception.localizedMessage)
            is NetworkResult.Success -> {
                val account = result.data.toDomain()
                local.updateAccount(account = account.toCache())
                DataResult.Success(result.data.toDomain())
            }
        }
    }

    override suspend fun syncAccount(): DataResult<Boolean> {
        val id = account.first()?.id ?: return DataResult.Error("Account not found")
        updateIsSyncingAccount(value = true)
        return when (val result = remote.getAccount(id = id)) {
            is NetworkResult.Failure -> DataResult.Error(message = result.exception.localizedMessage)
            is NetworkResult.Success -> {
                val previous = account.firstOrNull()
                val account = result.data.toDomain()
                checkAndNotifyLevelChange(previous = previous, current = account)
                local.updateAccount(account = account.toCache())
                DataResult.Success(true)
            }
        }.also {
            updateIsSyncingAccount(value = false)
        }
    }

    private fun checkAndNotifyLevelChange(
        previous: AccountDomain?,
        current: AccountDomain,
    ) {
        val previousLevel = previous?.level ?: return
        val currentLevel = current.level ?: return
        if (currentLevel.number <= previousLevel.number) return
        val message = LevelledUpMessages.random()
        val clickIntent =
            Intent().apply {
                setClassName(
                    context,
                    "com.bizilabs.streeek.lib.presentation.MainActivity",
                )
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(
                    "notification_result",
                    NotificationResult(
                        type = "update",
                        uri =
                            buildUri(
                                "action" to "navigate",
                                "destination" to "ACHIEVEMENTS",
                            ),
                    ).asJson(),
                )
            }
        val clickPendingIntent =
            PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                clickIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        context.notify(
            title = message.title.replace("{level_name}", currentLevel.name),
            body = message.body,
            channel = AppNotificationChannel.LEADERBOARD,
            clickIntent = clickPendingIntent,
        )
    }

    override suspend fun updateIsSyncingAccount(value: Boolean) {
        local.updateIsSyncingAccount(value = value)
    }

    override suspend fun logout() {
        remote.logout()
        local.logout()
        contributionsLocalSource.deleteAll()
    }

    override suspend fun saveFCMToken(token: String): DataResult<Boolean> {
        val accountId = account.first()?.id ?: return DataResult.Error("Account not found")
        return remote.saveFcmToken(accountId = accountId, token = token).asDataResult { it }
    }
}
