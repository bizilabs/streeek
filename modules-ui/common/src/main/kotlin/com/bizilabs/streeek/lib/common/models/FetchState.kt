package com.bizilabs.streeek.lib.common.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface FetchState<out T> {

    data class Error(val message: String) : FetchState<Nothing>

    data object Loading : FetchState<Nothing>

    data class Success<T>(val value: T) : FetchState<T>

}

data class FetchData internal constructor(
    val icon: ImageVector,
    val image: Int?,
    val message: String,
    val title: String = "",
    val action: (@Composable () -> Unit)? = null,
)

data object FetchDefaults {

    @Composable
    fun empty(
        icon: ImageVector = Icons.AutoMirrored.Rounded.ViewList,
        image: Int? = null,
        title: String = "Empty",
        message: String = "",
        action: (@Composable () -> Unit)? = null,
    ) = FetchData(
        icon = icon,
        image = image,
        title = title,
        message = message,
        action = action
    )

    @Composable
    fun error(
        icon: ImageVector = Icons.Rounded.Error,
        image: Int? = null,
        title: String = "Error",
        message: String = "",
        action: (@Composable () -> Unit)? = null,
    ) = FetchData(
        icon = icon,
        image = image,
        title = title,
        message = message,
        action = action
    )

    @Composable
    fun success(
        icon: ImageVector = Icons.Rounded.CheckCircle,
        image: Int? = null,
        title: String = "Success",
        message: String = "",
        action: (@Composable () -> Unit)? = null,
    ) = FetchData(
        icon = icon,
        image = image,
        title = title,
        message = message,
        action = action
    )

}