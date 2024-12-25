package com.bizilabs.streeek.lib.common.models

sealed interface FetchListState<out T> {

    data class Error(val message: String) : FetchListState<Nothing>

    data object Loading : FetchListState<Nothing>

    data object Empty : FetchListState<Nothing>

    data class Success<T>(val list: List<T>) : FetchListState<T>

}