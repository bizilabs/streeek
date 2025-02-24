package com.bizilabs.streeek.lib.remote.sources.taunt

import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.StreeekEndpoint
import com.bizilabs.streeek.lib.remote.helpers.safeApiCall
import com.bizilabs.streeek.lib.remote.models.TauntDTO
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url

interface TauntRemoteSource {
    suspend fun taunt(tauntedId: String, taunterId: String): NetworkResult<TauntDTO>
}

internal class TauntRemoteSourceImpl(
    private val client: HttpClient
) : TauntRemoteSource {
    override suspend fun taunt(tauntedId: String, taunterId: String): NetworkResult<TauntDTO> =
        safeApiCall {
            client.get {
                url(StreeekEndpoint.Taunt(tauntedId = tauntedId, taunterId = taunterId).url)
            }
        }
}