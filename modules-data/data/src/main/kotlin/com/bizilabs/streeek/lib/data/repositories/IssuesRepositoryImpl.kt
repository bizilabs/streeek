package com.bizilabs.streeek.lib.data.repositories

import com.bizilabs.streeek.lib.data.mappers.asDataResult
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.domain.helpers.DataResult
import com.bizilabs.streeek.lib.domain.models.CreateIssueDomain
import com.bizilabs.streeek.lib.domain.models.IssueDomain
import com.bizilabs.streeek.lib.domain.repositories.IssuesRepository
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.remote.models.CreateIssueDto
import com.bizilabs.streeek.lib.remote.sources.issues.IssuesRemoteSource
import kotlinx.coroutines.flow.first

class IssuesRepositoryImpl(
    private val remoteSource: IssuesRemoteSource,
    private val accountLocalSource: AccountLocalSource
) : IssuesRepository {
    override suspend fun createIssue(createIssueDomain: CreateIssueDomain): DataResult<IssueDomain> {
        return remoteSource.createIssue(createIssueDomain.toCreateIssue())
            .asDataResult { it.toDomain() }
    }

    override suspend fun getIssues(): DataResult<List<IssueDomain>> {
        val username = accountLocalSource.account.first()?.username ?: ""
        return remoteSource.getIssues(username = username)
            .asDataResult { list -> list.map { it.toDomain() } }
    }

    private fun CreateIssueDomain.toCreateIssue(): CreateIssueDto = CreateIssueDto(
        title = title,
        body = body,
        label = label
    )
}