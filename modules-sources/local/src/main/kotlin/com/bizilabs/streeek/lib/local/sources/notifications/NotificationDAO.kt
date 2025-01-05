package com.bizilabs.streeek.lib.local.sources.notifications

import androidx.room.Dao
import androidx.room.Query
import com.bizilabs.streeek.lib.local.database.dao.BaseDAO
import com.bizilabs.streeek.lib.local.entities.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDAO : BaseDAO<NotificationEntity> {

    @Query("SELECT * FROM notifications ORDER BY createdAtMillis DESC")
    fun getAll(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE readAt IS NULL ORDER BY createdAtMillis DESC")
    fun getAllUnread(): Flow<List<NotificationEntity>>

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM notifications")
    suspend fun deleteAll()

}
