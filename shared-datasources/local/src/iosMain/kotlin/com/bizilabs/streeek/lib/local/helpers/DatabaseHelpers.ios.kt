package com.bizilabs.streeek.lib.local.helpers

import androidx.room.Room
import androidx.room.RoomDatabase
import com.bizilabs.streeek.lib.local.database.StreeekDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val DatabaseModule: Module =
    module {
        single<RoomDatabase.Builder<StreeekDatabase>> { getDatabaseBuilder() }
    }

private fun getDatabaseBuilder(): RoomDatabase.Builder<StreeekDatabase> {
    val dbPath = documentDirectory() + "/${StreeekDatabase.DATABASE_NAME}"
    return Room.databaseBuilder<StreeekDatabase>(
        name = dbPath,
    )
}
