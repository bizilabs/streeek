package com.bizilabs.streeek.lib.remote.helpers

sealed class StreeekEndpoint(private val route: String) {
    val url: String
        get() =
            buildString {
                append("http://streeek.up.railway.app/v1/accounts")
                append(route)
            }

    data class Taunt(val tauntedId: String, val taunterId: String) :
        StreeekEndpoint(route = "/$tauntedId/taunt?taunter_id=$taunterId")
}