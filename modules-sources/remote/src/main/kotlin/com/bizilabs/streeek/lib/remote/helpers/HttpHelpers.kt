package com.bizilabs.streeek.lib.remote.helpers

import com.bizilabs.streeek.lib.remote.interceptor.AuthorizationInterceptor
import com.bizilabs.streeek.lib.remote.interceptor.NetworkInterceptor
import com.chuckerteam.chucker.api.ChuckerInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import okhttp3.logging.HttpLoggingInterceptor

sealed class Header(val name: String, val value: String) {
    data class Authorization(val key: String) : Header(name = "Authorization", value = key)

    data class Language(val language: String) :
        Header(name = "language", value = language.lowercase())

    data object User {
        data class Locale(val locale: String) :
            Header(name = "User-Locale", value = locale.lowercase())

        data class Language(val language: String) :
            Header(name = "User-Language", value = language.lowercase())

        data class Application(val packageName: String) :
            Header(name = "User-Application", value = packageName)
    }

    data class Locale(val locale: String) : Header(name = "locale", value = locale.lowercase())

    data object SourceType : Header(name = "Source-Type", value = "MOBILE")

    data class AcceptLanguage(val language: String) :
        Header(name = "accept-language", value = language.lowercase())

    data object ContentType {
        data object Json : Header(name = HttpHeaders.ContentType, value = "application/json")
    }

    data object Accept {
        data object Json : Header(name = HttpHeaders.Accept, value = "application/json")
    }
}

fun DefaultRequest.DefaultRequestBuilder.addHeader(header: Header) {
    header(header.name, header.value)
}

private fun DefaultRequest.DefaultRequestBuilder.addStandardHeaders() {
    addHeader(Header.Accept.Json)
    addHeader(Header.ContentType.Json)
}

fun createHttpClient(
    networkInterceptor: NetworkInterceptor,
    chuckerInterceptor: ChuckerInterceptor,
    loggingInterceptor: HttpLoggingInterceptor,
    authorizationInterceptor: AuthorizationInterceptor,
) = HttpClient(OkHttp) {
    engine {
        addInterceptor(networkInterceptor)
        addInterceptor(authorizationInterceptor)
        addInterceptor(loggingInterceptor)
        addInterceptor(chuckerInterceptor)
    }
    install(DefaultRequest) {
        addStandardHeaders()
    }
    install(ContentNegotiation) {
        json(json = createJson())
    }
}
