package com.bizilabs.streeek.lib.remote.sources

import com.bizilabs.streeek.lib.remote.client.fakeHttpClient
import com.bizilabs.streeek.lib.remote.helpers.JsonLoader
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.asJson
import com.bizilabs.streeek.lib.remote.helpers.fakeDataStore
import com.bizilabs.streeek.lib.remote.models.AccessTokenDTO
import com.bizilabs.streeek.lib.remote.sources.auth.AuthenticationRemoteSource
import com.bizilabs.streeek.lib.remote.sources.auth.AuthenticationRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.preferences.RemotePreferencesSource
import com.bizilabs.streeek.lib.remote.sources.preferences.RemotePreferencesSourceImpl
import com.eygraber.uri.Uri
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import streeek.shared_datasources.remote.StreeekRemoteConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthenticationRemoteSourceTest {
    private val testDispatcher = StandardTestDispatcher()

    val preferences: RemotePreferencesSource =
        RemotePreferencesSourceImpl(dataStore = fakeDataStore())

    fun fakeSource(
        response: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ): AuthenticationRemoteSource =
        AuthenticationRemoteSourceImpl(
            client = fakeHttpClient(status = status, response = response),
            preferences = preferences,
        )

    @Test
    fun `getAuthenticationUri should return valid Uri data`() =
        runTest(testDispatcher) {
            val uri = fakeSource(response = "").getAuthenticationUri().toString()
            val url = "https://github.com/login/oauth/authorize?"
            assertTrue(uri.contains(url))
            val map = mutableMapOf<String, String?>()
            uri.replace(url, "")
                .split("&")
                .forEach {
                    val value = it.split("=")
                    val key = value.first()
                    map.put(key, value.last())
                }
            assertEquals(StreeekRemoteConfig.GithubClientId, map["client_id"])
            assertEquals("repo,user,project,read:org", map["scope"])
            assertEquals(StreeekRemoteConfig.GithubClientRedirectUrl, map["redirect_uri"])
        }

    @Test
    fun `getAuthenticationToken should return valid token on success`() =
        runTest(testDispatcher) {
            val response = JsonLoader().load("token.json")
            val expected = JsonLoader().load<AccessTokenDTO>("token.json")
            val uri =
                Uri.parse("https://a.com?code=1234&redirect_url=${StreeekRemoteConfig.GithubClientRedirectUrl}")
            val result = fakeSource(response = response).getAuthenticationToken(uri = uri)
            assertEquals(NetworkResult.Success(expected), result)
            val data = (result as NetworkResult.Success).data
            assertEquals(expected.accessToken, data.accessToken)
            assertEquals("repo,user,project,read:org", data.scope)
        }

    @Test
    fun `getAuthenticationToken should fail without redirect url `() =
        runTest(testDispatcher) {
            val result = fakeSource(response = "").getAuthenticationToken(uri = Uri.parse(""))
            assertTrue {
                result is NetworkResult.Failure
            }
        }

    @Test
    fun `getAuthenticationToken should fail without url code `() =
        runTest(testDispatcher) {
            val result = fakeSource(response = "").getAuthenticationToken(uri = Uri.parse(""))
            assertTrue {
                result is NetworkResult.Failure
            }
        }

    @Test
    fun `updateAccessToken should save token`() =
        runTest(testDispatcher) {
            val expected = JsonLoader().load<AccessTokenDTO>("token.json").accessToken
            fakeSource(response = expected.asJson()).updateAccessToken(token = expected)
            val actual = preferences.accessToken.first()
            assertEquals(expected, actual)
            preferences.clear()
        }
}
