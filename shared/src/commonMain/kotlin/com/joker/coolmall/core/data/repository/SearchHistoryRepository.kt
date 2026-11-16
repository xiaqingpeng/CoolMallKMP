package com.joker.coolmallkmp.core.data.repository

import com.joker.coolmallkmp.core.data.local.SearchHistoryPreferences
import com.joker.coolmallkmp.core.database.dao.SearchHistoryDao
import com.joker.coolmallkmp.core.database.entity.SearchHistoryEntity
import com.joker.coolmallkmp.core.model.entity.SearchHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * 搜索历史数据库仓库。旧版本 DataStore 数据会在首次使用时导入 Room。
 */
class SearchHistoryRepository(
    private val dao: SearchHistoryDao,
    private val preferences: SearchHistoryPreferences,
) {
    val history: Flow<List<SearchHistory>> = dao.observeAll().map { values ->
        values.map { SearchHistory(it.id, it.keyword, it.createTime) }
    }

    @OptIn(ExperimentalTime::class)
    suspend fun add(keyword: String) {
        val normalized = keyword.trim()
        if (normalized.isEmpty()) return
        migrateLegacyIfNeeded()
        dao.deleteByKeyword(normalized)
        dao.insert(
            SearchHistoryEntity(
                keyword = normalized,
                createTime = Clock.System.now().toEpochMilliseconds(),
            ),
        )
        trimToLimit()
    }

    suspend fun remove(keyword: String) {
        migrateLegacyIfNeeded()
        dao.deleteByKeyword(keyword)
    }

    suspend fun clear() {
        migrateLegacyIfNeeded()
        dao.clear()
    }

    private suspend fun migrateLegacyIfNeeded() {
        if (dao.observeAll().first().isNotEmpty()) return
        preferences.historyValue().asReversed().forEachIndexed { index, keyword ->
            dao.insert(
                SearchHistoryEntity(
                    keyword = keyword,
                    createTime = index.toLong(),
                ),
            )
        }
        preferences.clear()
    }

    private suspend fun trimToLimit() {
        dao.observeAll().first().drop(10).forEach { dao.deleteByKeyword(it.keyword) }
    }
}
