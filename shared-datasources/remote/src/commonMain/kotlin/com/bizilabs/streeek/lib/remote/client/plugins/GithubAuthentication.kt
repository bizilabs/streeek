package com.bizilabs.streeek.lib.remote.client.plugins

import com.bizilabs.streeek.lib.remote.sources.preferences.RemotePreferencesSource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.util.AttributeKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

internal fun HttpRequestBuilder.addGithubHeaders(preferences: RemotePreferencesSource) {
    val token = runBlocking { preferences.accessToken.firstOrNull() }
    if (token.isNullOrBlank()) return
    header("Authorization", "Bearer $token")
    header("Accept", "application/vnd.github+json")
    header("X-GitHub-Api-Version", "2022-11-28")
}

internal class GithubAuthentication(
    private val block: HttpRequestBuilder.() -> Unit,
) {
    companion object Plugin : HttpClientPlugin<HttpRequestBuilder, GithubAuthentication> {
        override val key: AttributeKey<GithubAuthentication>
            get() = AttributeKey("GithubAuthentication")

        override fun prepare(block: HttpRequestBuilder.() -> Unit): GithubAuthentication = GithubAuthentication(block)

        override fun install(
            plugin: GithubAuthentication,
            scope: HttpClient,
        ) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Before) {
                if (context.url.toString().contains("github.com")) plugin.block(context)
            }
        }
    }
}
