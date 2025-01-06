package com.bizilabs.streeek.lib.data.mappers

import android.R.attr.data
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.local.helpers.LocalResult
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult

fun <T, R> NetworkResult<T>.asDataResult(block: (T) -> R): DataResult<R> {
    return when (this) {
        is NetworkResult.Failure -> DataResult.Error(message = exception.localizedMessage)
        is NetworkResult.Success -> DataResult.Success(block(data))
    }
}

fun <T, R> LocalResult<T>.asDataResult(block: (T) -> R): DataResult<R> {
    return when (this) {
        is LocalResult.Error -> DataResult.Error(message = message)
        is LocalResult.Success -> DataResult.Success(block(data))
    }
}
