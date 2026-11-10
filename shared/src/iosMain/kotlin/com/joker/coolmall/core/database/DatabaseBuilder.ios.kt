package com.joker.coolmall.core.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import platform.Foundation.NSHomeDirectory

actual fun createAppDatabaseBuilder(context: Any?): RoomDatabase.Builder<AppDatabase> {
    val dbFile = "${NSHomeDirectory()}/Documents/${AppDatabase.DATABASE_NAME}"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile,
    )
}
