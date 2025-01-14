package com.bizilabs.streeek.lib.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.bizilabs.streeek.lib.data.mappers.toCache
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.data.paging.PagingHelpers.START_PAGE
import com.bizilabs.streeek.lib.domain.models.TeamDetailsDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberDomain
import com.bizilabs.streeek.lib.domain.models.updateOrCreate
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.local.sources.team.TeamLocalSource
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.PAGE_SIZE
import com.bizilabs.streeek.lib.remote.sources.team.TeamRemoteSource
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class TeamsPagingSource(
    private val team: TeamDetailsDomain,
    private val teamLocalSource: TeamLocalSource,
    private val teamRemoteSource: TeamRemoteSource,
    private val accountLocalSource: AccountLocalSource,
) : PagingSource<Int, TeamMemberDomain>() {
    override fun getRefreshKey(state: PagingState<Int, TeamMemberDomain>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TeamMemberDomain> {
        val page = params.key ?: PagingHelpers.START_PAGE

        val accountId =
            accountLocalSource.account.firstOrNull()?.id
                ?: return LoadResult.Error(Exception("Couldn't get logged in account"))

        if (page == team.page) {
            val prev = if (page == START_PAGE) null else page.minus(1)
            val next = if (team.members.size < PAGE_SIZE) null else page + 1
            return LoadResult.Page(data = team.members.filterNot { member -> member.rank <= 3 }, prevKey = prev, nextKey = next)
        }

        val result =
            teamRemoteSource.fetchTeam(teamId = team.team.id, accountId = accountId, page = page)

        if (result is NetworkResult.Failure) return LoadResult.Error(result.exception)

        val data = (result as NetworkResult.Success).data.toDomain()
        val cache = team.updateOrCreate(page = page, team = data).toCache()
        if (page == START_PAGE) teamLocalSource.update(cache)

        val list = data.members.filterNot { member -> member.rank <= 3 }

        val nextKey =
            when {
                list.isEmpty() || list.size < PagingHelpers.PAGE_SIZE -> null
                else -> page.plus(1)
            }

        Timber.d(
            "Members -> \n${
                buildString {
                    append("Page    : $page\n")
                    append("Members : $list")
                }
            }",
        )

        val previousKey = if (page == START_PAGE) null else page.minus(1)

        return LoadResult.Page(data = list, prevKey = previousKey, nextKey = nextKey)
    }
}
