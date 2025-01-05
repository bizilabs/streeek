package com.bizilabs.streeek.lib.local.sources.contributions

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bizilabs.streeek.lib.local.helpers.LocalResult
import com.bizilabs.streeek.lib.local.helpers.safeTransaction
import com.bizilabs.streeek.lib.local.models.ContributionCache
import com.bizilabs.streeek.lib.local.sources.preference.LocalPreferenceSource
import com.bizilabs.streeek.lib.local.sources.preference.PreferenceSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest

interface ContributionsLocalSource {
    val contributions: Flow<List<ContributionCache>>
    val dates: Flow<List<ContributionCache>>
    val lastSync: Flow<Long?>
    suspend fun updateLastSync(timeInMillis: Long)
    suspend fun create(contribution: ContributionCache): LocalResult<Boolean>
    suspend fun create(contributions: List<ContributionCache>): LocalResult<Boolean>
    suspend fun update(contribution: ContributionCache): LocalResult<ContributionCache>
    suspend fun update(contributions: List<ContributionCache>): LocalResult<Boolean>
    suspend fun get(id: Long): LocalResult<ContributionCache?>
    fun getByDate(date: String): Flow<List<ContributionCache>>
    suspend fun delete(id: Long): LocalResult<Boolean>
    suspend fun deleteAll(): LocalResult<Boolean>
}

class ContributionsLocalSourceImpl(
    private val dao: ContributionDAO,
    private val preferenceSource: PreferenceSource
) : ContributionsLocalSource {

    object Keys {
        val LAST_SYNC = longPreferencesKey("sync.contributions")
    }

    override val contributions: Flow<List<ContributionCache>>
        get() = dao.selectAll().mapLatest { it.map { it.toCache() } }

    override val dates: Flow<List<ContributionCache>>
        get() = dao.getDistinctContributionsByDate().mapLatest { it.map { it.toCache() } }


    override val lastSync: Flow<Long?>
        get() = preferenceSource.getNullable(key = Keys.LAST_SYNC)

    override suspend fun updateLastSync(timeInMillis: Long) {
        preferenceSource.update(key = Keys.LAST_SYNC, value = timeInMillis)
    }

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

    override suspend fun deleteAll(): LocalResult<Boolean> =safeTransaction {
        dao.deleteAll()
        true
    }
}
