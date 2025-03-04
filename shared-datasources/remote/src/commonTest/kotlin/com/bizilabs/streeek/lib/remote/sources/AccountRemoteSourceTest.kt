package com.bizilabs.streeek.lib.remote.sources

import com.bizilabs.streeek.lib.remote.client.fakeSupabaseClient
import com.bizilabs.streeek.lib.remote.helpers.JsonLoader
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.fakeDataStore
import com.bizilabs.streeek.lib.remote.models.AccountCreateRequestDTO
import com.bizilabs.streeek.lib.remote.models.AccountDTO
import com.bizilabs.streeek.lib.remote.models.AccountFullDTO
import com.bizilabs.streeek.lib.remote.sources.account.AccountRemoteSource
import com.bizilabs.streeek.lib.remote.sources.account.AccountRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.preferences.RemotePreferencesSource
import com.bizilabs.streeek.lib.remote.sources.preferences.RemotePreferencesSourceImpl
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AccountRemoteSourceTest {
    private val testDispatcher = StandardTestDispatcher()

    val preferences: RemotePreferencesSource = RemotePreferencesSourceImpl(dataStore = fakeDataStore())

    fun getSource(
        status: HttpStatusCode,
        response: String,
    ): AccountRemoteSource =
        AccountRemoteSourceImpl(
            supabase = fakeSupabaseClient(status = status, response = response),
            remotePreferencesSource = preferences,
        )

    @Test
    fun `fetchAccountWithGithubId should return account on success`() =
        runTest(testDispatcher) {
            val response = JsonLoader().load("account.json")
            val source = getSource(status = HttpStatusCode.OK, response = response)
            val result = source.fetchAccountWithGithubId(id = 12345678)
            assertTrue { result is NetworkResult.Success }
            val expected = JsonLoader().load<List<AccountDTO>>("account.json").firstOrNull()
            val actual = (result as NetworkResult.Success).data
            assertEquals(expected, actual)
        }

    @Test
    fun `fetchAccountWithGithubId should return account with matching id on success`() =
        runTest(testDispatcher) {
            val id: Int = 12345678
            val response = JsonLoader().load("account.json")
            val source = getSource(status = HttpStatusCode.OK, response = response)
            val result = source.fetchAccountWithGithubId(id = id)
            assertTrue { result is NetworkResult.Success }
            val actual = (result as NetworkResult.Success).data
            assertEquals(id.toLong(), actual?.githubId)
        }

    @Test
    fun `fetchAccountWithGithubId should return failure on invalid response`() =
        runTest(testDispatcher) {
            val source = getSource(status = HttpStatusCode.OK, response = "")
            val result = source.fetchAccountWithGithubId(id = 12345678)
            assertTrue { result is NetworkResult.Failure }
        }

    @Test
    fun `createAccount should return account with valid details`() =
        runTest(testDispatcher) {
            val request =
                AccountCreateRequestDTO(
                    githubId = 12345678,
                    username = "coding_ninja",
                    email = "ninja@example.com",
                    bio = "A passionate developer turning coffee into code.",
                    createdAt = Clock.System.now().toString(),
                    updatedAt = Clock.System.now().toString(),
                    avatarUrl = "https://avatars.githubusercontent.com/u/12345678?v=4",
                )
            val response = JsonLoader().load("account.json")
            val source = getSource(status = HttpStatusCode.OK, response = response)
            val result = source.createAccount(request = request)
            assertTrue { result is NetworkResult.Success }
            val account = (result as NetworkResult.Success).data
            assertEquals(request.githubId.toLong(), account.githubId)
            assertEquals(request.username, account.username)
            assertEquals(request.email, account.email)
            assertEquals(request.avatarUrl, account.avatarUrl)
        }

    @Test
    fun `saveToken should return boolean when request is valid`() =
        runTest(testDispatcher) {
            val source = getSource(status = HttpStatusCode.OK, response = "")
            val result = source.saveFcmToken(accountId = 1, token = "")
            assertTrue { result is NetworkResult.Success }
            val data = (result as NetworkResult.Success).data
            assertTrue(data)
        }

    @Test
    fun `getAccount should return boolean when request is valid`() =
        runTest(testDispatcher) {
            val response = JsonLoader().load("account_full.json")
            val source = getSource(status = HttpStatusCode.OK, response = response)
            val result = source.getAccount(id = 1)
            assertTrue { result is NetworkResult.Success }
            val expected = JsonLoader().load<AccountFullDTO>("account_full.json")
            val actual = (result as NetworkResult.Success).data
            assertEquals(expected, actual)
        }

    @Test
    fun `clear should remove all account details`() =
        runTest(testDispatcher) {
            val source = getSource(status = HttpStatusCode.OK, response = "")
            preferences.updateAccessToken(token = "token")
            source.logout()
            val token = preferences.accessToken.firstOrNull()
            assertTrue { token == null }
        }
}
