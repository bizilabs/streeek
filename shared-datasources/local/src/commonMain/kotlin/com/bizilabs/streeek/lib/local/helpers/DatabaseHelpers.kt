package com.bizilabs.streeek.lib.local.helpers

import androidx.room.RoomDatabase
import com.bizilabs.streeek.lib.local.database.StreeekDatabase
import org.koin.core.module.Module

expect val DatabaseModule: Module

fun getDatabase(builder: RoomDatabase.Builder<StreeekDatabase>): StreeekDatabase {
    return builder
        .fallbackToDestructiveMigration(false)
        .build()
}
