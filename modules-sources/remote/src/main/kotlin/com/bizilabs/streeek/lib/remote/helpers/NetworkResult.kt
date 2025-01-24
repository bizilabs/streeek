package com.bizilabs.streeek.lib.remote.helpers

import com.bizilabs.streeek.lib.remote.models.github.GithubErrorResponseDTO
import io.ktor.client.call.DoubleReceiveException
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import timber.log.Timber

sealed interface NetworkResult<out T> {
    data class Failure(val exception: Exception) : NetworkResult<Nothing>

    data class Success<T>(val data: T) : NetworkResult<T>
}

suspend inline fun <reified T> safeApiCall(block: () -> HttpResponse): NetworkResult<T> {
    return try {
        val response = block.invoke()
        NetworkResult.Success(data = response.body())
    } catch (e: NoTransformationFoundException) {
        Timber.e(e)
        NetworkResult.Failure(exception = e)
    } catch (e: DoubleReceiveException) {
        Timber.e(e)
        NetworkResult.Failure(exception = e)
    } catch (e: Exception) {
        Timber.e(e)
        NetworkResult.Failure(exception = e)
    }
}

suspend inline fun <reified T> safeGithubApiCall(block: () -> HttpResponse): NetworkResult<T> {
    return try {
        val response = block.invoke()
        if (response.status.isSuccess()) {
            NetworkResult.Success(data = response.body())
        } else {
            NetworkResult.Failure(exception = Exception(response.body<GithubErrorResponseDTO>().message))
        }
    } catch (e: Exception) {
        NetworkResult.Failure(e)
    }
}
