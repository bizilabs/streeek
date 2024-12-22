package com.bizilabs.streeek.lib.data.repositories

import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.repositories.TeamRepository
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.remote.sources.team.TeamRemoteSource

class TeamRepositoryImpl(
    private val remoteSource: TeamRemoteSource,
    private val accountLocalSource: AccountLocalSource
) : TeamRepository  {
    override suspend fun createTeam(name: String, public: Boolean): DataResult<Boolean> {
        TODO("Not yet implemented")
    }
}
