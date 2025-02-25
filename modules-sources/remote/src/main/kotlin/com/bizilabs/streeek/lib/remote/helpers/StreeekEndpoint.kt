package com.bizilabs.streeek.lib.remote.helpers

import com.bizilabs.streeek.lib.remote.BuildConfig

sealed class StreeekEndpoint(private val route: String) {
    val url: String
        get() =
            buildString {
                append(BuildConfig.StreeekUrl)
                append(route)
            }

    data class Taunt(val tauntedId: String, val taunterId: String) :
        StreeekEndpoint(route = "/$tauntedId/taunt?taunter_id=$taunterId")
}
