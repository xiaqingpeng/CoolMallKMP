package com.joker.coolmall.core.database

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase

actual fun createAppDatabaseBuilder(context: Any?): RoomDatabase.Builder<AppDatabase> {
    require(context is Context) { "Android Context is required to create AppDatabase" }
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(AppDatabase.DATABASE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
