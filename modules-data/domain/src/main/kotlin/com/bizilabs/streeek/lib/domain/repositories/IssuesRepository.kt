package com.bizilabs.streeek.lib.domain.repositories

import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.CreateIssueDomain
import com.bizilabs.streeek.lib.domain.models.IssueDomain

interface IssuesRepository {
    suspend fun createIssue(createIssueDomain: CreateIssueDomain): DataResult<IssueDomain>
    suspend fun getIssues(): DataResult<List<IssueDomain>>
}