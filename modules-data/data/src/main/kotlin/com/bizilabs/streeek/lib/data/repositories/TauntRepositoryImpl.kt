package com.bizilabs.streeek.lib.data.repositories

import com.bizilabs.streeek.lib.data.mappers.asDataResult
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.TauntDomain
import com.bizilabs.streeek.lib.domain.repositories.TauntRepository
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.remote.sources.taunt.TauntRemoteSource
import kotlinx.coroutines.flow.firstOrNull

class TauntRepositoryImpl(
    private val tauntRemoteSource: TauntRemoteSource,
    private val accountLocalSource: AccountLocalSource,
) : TauntRepository {
    override suspend fun taunt(tauntedId: String): DataResult<TauntDomain> {
        val accountId = accountLocalSource.account.firstOrNull()?.id
        return tauntRemoteSource.taunt(tauntedId = tauntedId, taunterId = accountId.toString())
            .asDataResult { it.toDomain() }
    }
}
