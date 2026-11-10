package com.joker.coolmall.core.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * 各平台提供 databaseBuilder，再统一 setDriver 构建。
 */
expect fun createAppDatabaseBuilder(context: Any?): RoomDatabase.Builder<AppDatabase>

fun createAppDatabase(context: Any?): AppDatabase {
    return createAppDatabaseBuilder(context)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        // 开发期 schema 变更直接重建；正式迁移策略后续补 Migration
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
}
