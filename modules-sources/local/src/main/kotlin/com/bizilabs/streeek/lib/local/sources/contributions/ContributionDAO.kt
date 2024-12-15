package com.bizilabs.streeek.lib.local.sources.contributions

import androidx.room.Dao
import androidx.room.Query
import com.bizilabs.streeek.lib.local.database.dao.BaseDAO
import com.bizilabs.streeek.lib.local.entities.ContributionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContributionDAO : BaseDAO<ContributionEntity> {
    @Query("SELECT * FROM contributions")
    fun selectAll() : Flow<List<ContributionEntity>>

    @Query("SELECT * FROM contributions WHERE id = :id")
    fun select(id: Long) : Flow<ContributionEntity>

    @Query("DELETE FROM contributions WHERE id = :id")
    fun delete(id: Long)
}
