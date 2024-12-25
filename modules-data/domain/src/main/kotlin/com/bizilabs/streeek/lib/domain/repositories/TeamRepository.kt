package com.bizilabs.streeek.lib.domain.repositories

import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.TeamWithMembersDomain

interface TeamRepository {
    suspend fun createTeam(
        name: String,
        public: Boolean,
    ): DataResult<Long>

    suspend fun getTeam(id: Long, page: Int) : DataResult<TeamWithMembersDomain>

}
