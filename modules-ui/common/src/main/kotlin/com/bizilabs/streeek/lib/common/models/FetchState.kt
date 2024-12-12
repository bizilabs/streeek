package com.bizilabs.streeek.lib.common.models

sealed interface FetchState<out T> {

    data class Error(val message: String) : FetchState<Nothing>

    data object Loading : FetchState<Nothing>

    data class Success<T>(val value: T) : FetchState<T>

}
