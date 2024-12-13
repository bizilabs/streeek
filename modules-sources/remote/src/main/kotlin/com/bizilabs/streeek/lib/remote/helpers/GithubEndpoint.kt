package com.bizilabs.streeek.lib.remote.helpers

sealed class GithubEndpoint(private val route: String) {
    val url: String
        get() = buildString {
            append("https://api.github.com")
            append(route)
        }

    object User : GithubEndpoint(route = "/user")
    data class Events(val username: String) :
        GithubEndpoint(route = "/users/$username/events")
}
