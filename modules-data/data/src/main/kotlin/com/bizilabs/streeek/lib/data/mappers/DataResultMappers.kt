package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult

fun <T, R> NetworkResult<T>.asDataResult(block: (T) -> R): DataResult<R> {
    return when (this) {
        is NetworkResult.Failure -> DataResult.Error(message = exception.localizedMessage)
        is NetworkResult.Success -> DataResult.Success(block(data))
    }
}
