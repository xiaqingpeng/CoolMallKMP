package com.joker.coolmall.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 * 搜索历史（DataStore，对齐 CoolMallKotlin 本地持久化语义）
 */
class SearchHistoryPreferences(
    private val dataStore: DataStore<Preferences>,
) {
    private val key = stringPreferencesKey("search_history_json")
    private val json = Json { ignoreUnknownKeys = true }
    private val serializer = ListSerializer(String.serializer())

    val history: Flow<List<String>> = dataStore.data.map { prefs ->
        val raw = prefs[key].orEmpty()
        if (raw.isBlank()) emptyList()
        else runCatching { json.decodeFromString(serializer, raw) }.getOrDefault(emptyList())
    }

    suspend fun addKeyword(keyword: String, limit: Int = 10) {
        val trimmed = keyword.trim()
        if (trimmed.isEmpty()) return
        dataStore.edit { prefs ->
            val current = prefs[key].orEmpty().let { raw ->
                if (raw.isBlank()) emptyList()
                else runCatching { json.decodeFromString(serializer, raw) }.getOrDefault(emptyList())
            }
            val next = (listOf(trimmed) + current.filterNot { it.equals(trimmed, ignoreCase = true) })
                .take(limit)
            prefs[key] = json.encodeToString(serializer, next)
        }
    }

    suspend fun clear() {
        dataStore.edit { prefs -> prefs.remove(key) }
    }

    suspend fun historyValue(): List<String> = dataStore.data.map { prefs ->
        decode(prefs[key].orEmpty())
    }.first()

    suspend fun replace(values: List<String>) {
        dataStore.edit { prefs ->
            if (values.isEmpty()) {
                prefs.remove(key)
            } else {
                prefs[key] = json.encodeToString(serializer, values)
            }
        }
    }

    private fun decode(raw: String): List<String> =
        if (raw.isBlank()) emptyList()
        else runCatching { json.decodeFromString(serializer, raw) }.getOrDefault(emptyList())
}
