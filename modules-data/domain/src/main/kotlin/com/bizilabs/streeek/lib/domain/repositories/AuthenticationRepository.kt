package com.bizilabs.streeek.lib.domain.repositories

import android.content.Intent
import android.net.Uri
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    val authenticated: Flow<Boolean>

    suspend fun getAuthenticationIntent(): Intent

    suspend fun getAuthenticationToken(uri: Uri): DataResult<String>
}
