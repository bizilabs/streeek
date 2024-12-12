package com.bizilabs.streeek.lib.remote.interceptor

import com.bizilabs.streeek.lib.remote.sources.preferences.RemotePreferencesSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthorizationInterceptor(
    private val remotePreferencesSource: RemotePreferencesSource
) : Interceptor {
    private fun Request.Builder.addAccessToken() {
        val token = runBlocking { remotePreferencesSource.accessToken.first() }
        if (token.isNullOrBlank()) return
        addHeader("Authorization", "Bearer $token")
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        with(builder) {
            addAccessToken()
        }
        val request = builder.build()
        return chain.proceed(request)
    }
}
