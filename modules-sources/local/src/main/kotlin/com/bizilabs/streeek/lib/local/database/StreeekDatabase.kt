package com.bizilabs.streeek.lib.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bizilabs.streeek.lib.local.entities.ContributionEntity
import com.bizilabs.streeek.lib.local.sources.contributions.ContributionDAO

@Database(
    entities = [ContributionEntity::class],
    exportSchema = true,
    version = 1
)
@TypeConverters()
abstract class StreeekDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "streeek.db"
    }
    abstract val contributions: ContributionDAO
}
