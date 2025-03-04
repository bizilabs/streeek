package com.bizilabs.streeek.lib.remote.helpers

import streeek.shared_datasources.remote.StreeekRemoteConfig

sealed class StreeekEndpoint(private val route: String) {
    val url: String
        get() =
            buildString {
                append(StreeekRemoteConfig.StreeekUrl)
                append(route)
            }

    data class Taunt(val tauntedId: String, val taunterId: String) :
        StreeekEndpoint(route = "/$tauntedId/taunt?taunter_id=$taunterId")
}
