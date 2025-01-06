package com.bizilabs.streeek.lib.remote.sources.auth

import android.content.Intent
import android.net.Uri
import com.bizilabs.streeek.lib.remote.BuildConfig
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.safeApiCall
import com.bizilabs.streeek.lib.remote.models.AccessTokenDTO
import com.bizilabs.streeek.lib.remote.sources.preferences.RemotePreferencesSource
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

interface AuthenticationRemoteSource {
    val authenticated: Flow<Boolean>

    suspend fun updateAccessToken(token: String)

    suspend fun getAuthenticationIntent(): Intent

    suspend fun getAuthenticationToken(uri: Uri): NetworkResult<AccessTokenDTO>
}

class AuthenticationRemoteSourceImpl(
    private val client: HttpClient,
    private val preferences: RemotePreferencesSource,
) : AuthenticationRemoteSource {
    override val authenticated: Flow<Boolean>
        get() = preferences.accessToken.mapLatest { it != null }

    override suspend fun getAuthenticationIntent(): Intent {
        val url =
            buildString {
                append("https://github.com/login/oauth/authorize")
                append("?client_id=${BuildConfig.GithubClientId}")
                append("&scope=repo,user,project,read:org")
                append("&redirect_uri=${BuildConfig.GithubClientRedirectUrl}")
            }
        return Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }

    override suspend fun getAuthenticationToken(uri: Uri): NetworkResult<AccessTokenDTO> {
        val hasRedirectUrl = uri.toString().contains(BuildConfig.GithubClientRedirectUrl)
        if (hasRedirectUrl.not()) return NetworkResult.Failure(Exception("No redirect url found"))
        val code =
            uri.getQueryParameter("code")
                ?: return NetworkResult.Failure(Exception("No code found"))
        return safeApiCall {
            client.submitForm(
                url = "https://github.com/login/oauth/access_token",
                formParameters =
                    Parameters.build {
                        append("client_id", BuildConfig.GithubClientId)
                        append("client_secret", BuildConfig.GithubClientSecret)
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
