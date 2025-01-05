package com.bizilabs.streeek.lib.local.sources.notifications

import com.bizilabs.streeek.lib.local.entities.NotificationEntity
import com.bizilabs.streeek.lib.local.helpers.LocalResult
import com.bizilabs.streeek.lib.local.helpers.safeTransaction
import com.bizilabs.streeek.lib.local.models.NotificationCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

interface NotificationLocalSource {

    val notifications: Flow<List<NotificationCache>>
    val unreadNotifications: Flow<List<NotificationCache>>

    fun getAll(): Flow<List<NotificationCache>>
    fun getUnread(): Flow<List<NotificationCache>>
    suspend fun insert(item: NotificationCache): LocalResult<Boolean>
    suspend fun insert(items: List<NotificationCache>): LocalResult<Boolean>
    suspend fun update(item: NotificationCache): LocalResult<Boolean>
    suspend fun update(items: List<NotificationCache>): LocalResult<Boolean>
    suspend fun delete(item: NotificationCache): LocalResult<Boolean>
    suspend fun delete(id: Long): LocalResult<Boolean>
    suspend fun deleteAll(): LocalResult<Boolean>

}

internal class NotificationLocalSourceImpl(
    private val dao: NotificationDAO
) : NotificationLocalSource {

    private fun Flow<List<NotificationEntity>>.toCache() =
        mapLatest { list -> list.map { it.toCache() } }

    override val notifications: Flow<List<NotificationCache>>
        get() = dao.getAll().toCache()

    override val unreadNotifications: Flow<List<NotificationCache>>
        get() = dao.getAllUnread().toCache()

    override fun getAll(): Flow<List<NotificationCache>> = dao.getAll().toCache()

    override fun getUnread(): Flow<List<NotificationCache>> = dao.getAllUnread().toCache()

    override suspend fun insert(item: NotificationCache): LocalResult<Boolean> =
        safeTransaction {
            dao.insert(item.toEntity())
            true
        }

    override suspend fun insert(items: List<NotificationCache>): LocalResult<Boolean> =
        safeTransaction {
            dao.insert(items.map { it.toEntity() })
            true
        }

    override suspend fun update(item: NotificationCache): LocalResult<Boolean> =
        safeTransaction {
            dao.update(item.toEntity())
            true
        }

    override suspend fun update(items: List<NotificationCache>): LocalResult<Boolean> =
        safeTransaction {
            dao.update(items.map { it.toEntity() })
            true
        }

    override suspend fun delete(item: NotificationCache): LocalResult<Boolean> =
        safeTransaction {
            dao.delete(item.toEntity())
            true
        }

    override suspend fun delete(id: Long): LocalResult<Boolean> =
        safeTransaction {
            dao.delete(id)
            true
        }

    override suspend fun deleteAll(): LocalResult<Boolean> =
        safeTransaction {
            dao.deleteAll()
            true
        }

}