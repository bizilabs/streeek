package com.bizilabs.streeek.lib.remote.helpers

sealed class GithubEndpoint(private val route: String) {
    val url : String
        get() = buildString {
            append("https://api.github.com")
            append(route)
        }
    object User : GithubEndpoint(route = "/user")
}