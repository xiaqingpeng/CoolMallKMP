package com.joker.coolmall.core.database

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.AutoMigration
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.joker.coolmall.core.database.dao.CartDao
import com.joker.coolmall.core.database.dao.FootprintDao
import com.joker.coolmall.core.database.dao.SearchHistoryDao
import com.joker.coolmall.core.database.entity.CartEntity
import com.joker.coolmall.core.database.entity.FootprintEntity
import com.joker.coolmall.core.database.entity.SearchHistoryEntity

@Database(
    entities = [CartEntity::class, FootprintEntity::class, SearchHistoryEntity::class],
    version = 3,
    autoMigrations = [AutoMigration(from = 2, to = 3)],
    exportSchema = true,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun footprintDao(): FootprintDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        const val DATABASE_NAME = "coolmall-database.db"
    }
}

/**
 * Room 编译器自动生成各平台 actual，无需手写实现。
 */
@Suppress(
    "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING",
    "NO_ACTUAL_FOR_EXPECT",
    "KotlinNoActualForExpect",
)
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>
