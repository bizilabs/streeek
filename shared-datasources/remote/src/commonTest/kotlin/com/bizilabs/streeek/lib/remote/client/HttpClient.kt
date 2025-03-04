package com.bizilabs.streeek.lib.remote.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val TestJson =
    Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

/**
 * Generates a mock [HttpClientEngine] to use for testing
 * @param status the status code the response should return
 * @param response the response body as string that the request should return
 * @return [MockEngine]
 */
fun fakeHttpEngine(
    status: HttpStatusCode,
    response: String,
) = MockEngine {
    val headers =
        headers {
            append("Accept", "application/json")
            append("Content-Type", "application/json")
        }
    respond(content = response, status = status, headers = headers)
}

/**
 * Generates a fake [HttpClient] to use for testing
 * @param engine is the engine to use to testing, in any test case should be [MockEngine]
 * @return [HttpClient]
 */
fun fakeHttpClient(
    status: HttpStatusCode,
    response: String,
) = HttpClient(fakeHttpEngine(status = status, response = response)) {
    install(ContentNegotiation) {
        json(TestJson)
    }
    install(DefaultRequest) {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }
}
