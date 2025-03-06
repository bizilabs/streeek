package com.bizilabs.streeek.lib.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.bizilabs.streeek.lib.local.entities.ContributionEntity
import com.bizilabs.streeek.lib.local.entities.LevelEntity
import com.bizilabs.streeek.lib.local.entities.NotificationEntity
import com.bizilabs.streeek.lib.local.sources.contributions.ContributionDAO
import com.bizilabs.streeek.lib.local.sources.level.LevelDAO
import com.bizilabs.streeek.lib.local.sources.notifications.NotificationDAO

@Database(
    entities = [ContributionEntity::class, LevelEntity::class, NotificationEntity::class],
    exportSchema = true,
    version = 3,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class StreeekDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "streeek.db"
    }

    abstract val contributions: ContributionDAO
    abstract val levels: LevelDAO
    abstract val notifications: NotificationDAO
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<StreeekDatabase> {
    override fun initialize(): StreeekDatabase
}
