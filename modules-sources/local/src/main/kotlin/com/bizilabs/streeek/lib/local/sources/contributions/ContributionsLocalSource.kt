package com.bizilabs.streeek.lib.local.sources.contributions

import com.bizilabs.streeek.lib.local.helpers.LocalResult
import com.bizilabs.streeek.lib.local.helpers.safeTransaction
import com.bizilabs.streeek.lib.local.models.ContributionCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest

interface ContributionsLocalSource {
    val contributions: Flow<List<ContributionCache>>
    suspend fun create(contribution: ContributionCache): LocalResult<Boolean>
    suspend fun create(contributions: List<ContributionCache>): LocalResult<Boolean>
    suspend fun update(contribution: ContributionCache): LocalResult<ContributionCache>
    suspend fun update(contributions: List<ContributionCache>): LocalResult<Boolean>
    suspend fun get(id: Long): LocalResult<ContributionCache?>
    fun getByDate(date: String): Flow<List<ContributionCache>>
    suspend fun delete(id: Long): LocalResult<Boolean>
}

class ContributionsLocalSourceImpl(
    private val dao: ContributionDAO
) : ContributionsLocalSource {
    override val contributions: Flow<List<ContributionCache>>
        get() = dao.selectAll().mapLatest { it.map { it.toCache() } }

    override suspend fun create(contribution: ContributionCache): LocalResult<Boolean> =
        safeTransaction {
            dao.insert(contribution.toEntity())
            true
        }

    override suspend fun create(contributions: List<ContributionCache>): LocalResult<Boolean> =
        safeTransaction {
            dao.insert(contributions.map { it.toEntity() })
            true
        }

    override suspend fun update(contribution: ContributionCache): LocalResult<ContributionCache> =
        safeTransaction {
            dao.update(contribution.toEntity())
            dao.selectFlow(id = contribution.id).first().toCache()
        }

    override suspend fun update(contributions: List<ContributionCache>): LocalResult<Boolean> =
        safeTransaction {
            dao.update(contributions.map { it.toEntity() })
            true
        }

    override suspend fun get(id: Long): LocalResult<ContributionCache?> = safeTransaction {
        dao.select(id = id)?.toCache()
    }

    override fun getByDate(date: String): Flow<List<ContributionCache>> =
        dao.selectByDate(date = "%$date%").mapLatest { list -> list.map { it.toCache() } }

    override suspend fun delete(id: Long): LocalResult<Boolean> = safeTransaction {
        dao.delete(id = id)
        true
    }

}
