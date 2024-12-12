package com.bizilabs.streeek.lib.domain.helpers

sealed interface DataResult<out T> {
    data class Error(val message: String) : DataResult<Nothing>
    data class Success<T>(val data: T) : DataResult<T>
}
