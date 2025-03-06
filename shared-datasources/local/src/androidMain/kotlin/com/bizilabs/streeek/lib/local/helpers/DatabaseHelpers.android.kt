package com.bizilabs.streeek.lib.local.helpers

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bizilabs.streeek.lib.local.database.StreeekDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val DatabaseModule: Module =
    module {
        single<RoomDatabase.Builder<StreeekDatabase>> { getDatabaseBuilder(context = get()) }
    }

private fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<StreeekDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(StreeekDatabase.DATABASE_NAME)
    return Room.databaseBuilder(
        context = context,
        name = dbFile.absolutePath,
    )
}
