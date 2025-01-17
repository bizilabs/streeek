package com.bizilabs.streeek.lib.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.bizilabs.streeek.lib.data.mappers.team.toDomain
import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.data.paging.PagingHelpers.START_PAGE
import com.bizilabs.streeek.lib.domain.models.TeamAndMembersDomain
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.PAGE_SIZE
import com.bizilabs.streeek.lib.remote.sources.team.TeamRemoteSource
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class TeamsPagingSource(
    private val teamRemoteSource: TeamRemoteSource,
    private val accountLocalSource: AccountLocalSource,
) : PagingSource<Int, TeamAndMembersDomain>() {
    override fun getRefreshKey(state: PagingState<Int, TeamAndMembersDomain>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TeamAndMembersDomain> {
        val page = params.key ?: START_PAGE

        val accountId =
            accountLocalSource.account.firstOrNull()?.id
                ?: return LoadResult.Error(Exception("Couldn't get logged in account"))

        val result = teamRemoteSource.fetchTeamAndMembers(accountId = accountId, page = page)

        if (result is NetworkResult.Failure) return LoadResult.Error(result.exception)

        val data = (result as NetworkResult.Success).data

        val list = data.map { it.toDomain() }

        val nextKey =
            when {
                list.isEmpty() || list.size < PagingHelpers.PAGE_SIZE -> null
                else -> page.plus(1)
            }

        Timber.d(
            "Teams Data -> \n${
                buildString {
                    append("Page    : $page\n")
                    append("Teams   : $list")
                }
            }",
        )

        val previousKey = if (page == START_PAGE) null else page.minus(1)

        return LoadResult.Page(data = list, prevKey = previousKey, nextKey = nextKey)
    }
}
