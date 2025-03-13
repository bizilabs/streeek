package com.bizilabs.streeek.lib.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
 * starts background worker to save firebase cloud messaging token
 */
fun Context.startSaveFCMTokenWork() {
    val request =
        OneTimeWorkRequestBuilder<SaveFCMTokenWork>()
            .addTag(SaveFCMTokenWork.TAG)
            .build()
    WorkManager.getInstance(this).enqueue(request)
}

/**
 * worker to save firebase cloud messaging token
 * @param context application context
 * @param params worker parameters
 * @param repository account repository to handle sending and receiving data
 */
class SaveFCMTokenWork(
    val context: Context,
    val params: WorkerParameters,
    val repository: AccountRepository,
) : CoroutineWorker(context, params) {
    companion object {
        const val TAG = "SaveFCMTokenWork"
    }

    /**
     * saves firebase cloud messaging token
     * gets cached and current firebase cloud messaging token
     * saves current token if they are different
     * syncs account data to update the fcm token
     */
    override suspend fun doWork(): Result {
        try {
            val currentToken =
                repository.account.firstOrNull().also {
                    Timber.d("Accounty -> $it")
                }?.fcmToken
            val updatedToken = Firebase.messaging.token.await()
            if (currentToken == updatedToken) return Result.success()
            val result = repository.saveFCMToken(updatedToken)
            if (result is DataResult.Error) {
                if (params.runAttemptCount > 1) {
                    Result.failure()
                } else {
                    Result.retry()
                }
            }
            repository.syncAccount()
            return Result.success()
        } catch (e: Exception) {
            Timber.e(e)
            return if (params.runAttemptCount > 1) Result.failure() else Result.retry()
        }
    }
}
