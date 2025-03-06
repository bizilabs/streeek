package com.bizilabs.streeek.lib.local.helpers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

sealed interface LocalResult<out T> {
    data class Error(val message: String) : LocalResult<Nothing>

    data class Success<T>(val data: T) : LocalResult<T>
}

suspend fun <T> safeTransaction(
    scope: CoroutineDispatcher = Dispatchers.IO,
    block: suspend () -> T,
): LocalResult<T> {
    return withContext(scope) {
        try {
            val data = block()
            LocalResult.Success(data = data)
        } catch (e: Exception) {
            LocalResult.Error(message = e.message ?: "error")
        }
    }
}
