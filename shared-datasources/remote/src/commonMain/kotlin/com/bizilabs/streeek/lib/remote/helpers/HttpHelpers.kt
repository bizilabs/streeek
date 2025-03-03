package com.bizilabs.streeek.lib.remote.helpers

import com.bizilabs.streeek.lib.remote.client.plugins.GithubAuthentication
import com.bizilabs.streeek.lib.remote.client.plugins.addGithubHeaders
import com.bizilabs.streeek.lib.remote.sources.preferences.RemotePreferencesSource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json

fun DefaultRequest.DefaultRequestBuilder.addStandardHeaders() {
    header("Accept", "application/json")
    header("Content-Type", "application/json")
}

fun createHttpClient(preferences: RemotePreferencesSource) =
    HttpClient {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(json = createJson())
        }
        install(DefaultRequest) {
            addStandardHeaders()
        }
        install(GithubAuthentication) {
            addGithubHeaders(preferences = preferences)
        }
    }
