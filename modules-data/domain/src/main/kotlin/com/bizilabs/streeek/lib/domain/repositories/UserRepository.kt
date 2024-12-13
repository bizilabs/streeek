package com.bizilabs.streeek.lib.domain.repositories

import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.UserDomain

interface UserRepository {
    suspend fun getUser(): DataResult<UserDomain>
}