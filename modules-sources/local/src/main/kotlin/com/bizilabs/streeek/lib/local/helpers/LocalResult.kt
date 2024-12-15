package com.bizilabs.streeek.lib.local.helpers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

sealed interface LocalResult<out T> {
    data class Error(val message: String) : LocalResult<Nothing>

    data class Success<T>(val data: T) : LocalResult<T>

}

suspend fun <T> safeTransaction(
    scope: CoroutineDispatcher = Dispatchers.IO,
    block: suspend () -> T
): LocalResult<T> {
    return withContext(scope) {
        try {
            val data = block()
            LocalResult.Success(data = data)
        } catch (e: Exception) {
            Timber.e(t = e, message = "Error For Safe Transaction")
            LocalResult.Error(message = e.localizedMessage ?: "error")
        }
    }
}