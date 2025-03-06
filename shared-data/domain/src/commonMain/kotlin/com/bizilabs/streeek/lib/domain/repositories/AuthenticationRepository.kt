package com.bizilabs.streeek.lib.domain.repositories

import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.eygraber.uri.Uri
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    val authenticated: Flow<Boolean>

    suspend fun getAuthenticationUri(): Uri

    suspend fun getAuthenticationToken(uri: Uri): DataResult<String>
}
