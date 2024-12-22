package com.bizilabs.streeek.lib.domain.repositories

import com.bizilabs.streeek.lib.domain.helpers.DataResult

interface TeamRepository {
    suspend fun createTeam(
        name: String,
        public: Boolean,
    ): DataResult<Boolean>
}
