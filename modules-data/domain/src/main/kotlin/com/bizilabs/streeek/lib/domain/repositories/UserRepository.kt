package com.bizilabs.streeek.lib.domain.repositories

import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.UserDomain
import com.bizilabs.streeek.lib.domain.models.UserEventDomain

interface UserRepository {
    suspend fun getUser(): DataResult<UserDomain>
    suspend fun getUserEvents(username: String): DataResult<List<UserEventDomain>>
}