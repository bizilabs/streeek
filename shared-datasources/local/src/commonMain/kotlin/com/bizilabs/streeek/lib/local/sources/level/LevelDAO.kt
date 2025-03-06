package com.bizilabs.streeek.lib.local.sources.level

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bizilabs.streeek.lib.local.entities.LevelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelDAO {
    @Query("SELECT * FROM levels")
    fun getLevels(): Flow<List<LevelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevels(levels: List<LevelEntity>)

    @Update
    suspend fun updateLevels(levels: List<LevelEntity>)
}
