package com.bizilabs.streeek.lib.remote.sources.auth

import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.safeApiCall
import com.bizilabs.streeek.lib.remote.models.AccessTokenDTO
import com.bizilabs.streeek.lib.remote.sources.preferences.RemotePreferencesSource
import com.eygraber.uri.Uri
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import streeek.shared_datasources.remote.StreeekRemoteConfig

interface AuthenticationRemoteSource {
    val authenticated: Flow<Boolean>

    suspend fun updateAccessToken(token: String)

    suspend fun getAuthenticationIntent(): Uri

    suspend fun getAuthenticationToken(uri: Uri): NetworkResult<AccessTokenDTO>
}

class AuthenticationRemoteSourceImpl(
    private val client: HttpClient,
    private val preferences: RemotePreferencesSource,
) : AuthenticationRemoteSource {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val authenticated: Flow<Boolean>
        get() = preferences.accessToken.mapLatest { it != null }

    override suspend fun getAuthenticationIntent(): Uri {
        val url =
            buildString {
                append("https://github.com/login/oauth/authorize")
                append("?client_id=${StreeekRemoteConfig.GithubClientId}")
                append("&scope=repo,user,project,read:org")
                append("&redirect_uri=${StreeekRemoteConfig.GithubClientRedirectUrl}")
            }
        return Uri.parse(url)
    }

    override suspend fun getAuthenticationToken(uri: Uri): NetworkResult<AccessTokenDTO> {
        val hasRedirectUrl = uri.toString().contains(StreeekRemoteConfig.GithubClientRedirectUrl)
        if (hasRedirectUrl.not()) return NetworkResult.Failure(Exception("No redirect url found"))
        val code =
            uri.getQueryParameter("code") ?: return NetworkResult.Failure(Exception("No code found"))
        return safeApiCall {
            client.submitForm(
                url = "https://github.com/login/oauth/access_token",
                formParameters =
                    Parameters.build {
                        append("client_id", StreeekRemoteConfig.GithubClientId)
                        append("client_secret", StreeekRemoteConfig.GithubClientSecret)
                        append("code", code)
                    },
                encodeInQuery = true,
            ) {
            }
        }
    }

    override suspend fun updateAccessToken(token: String) {
        preferences.updateAccessToken(token = token)
    }
}
