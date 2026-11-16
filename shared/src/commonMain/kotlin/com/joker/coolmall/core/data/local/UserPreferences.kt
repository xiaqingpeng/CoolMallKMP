package com.joker.coolmallkmp.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.joker.coolmallkmp.core.model.entity.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

/**
 * 用户资料本地缓存（替代 Android MMKV UserInfoStore）。
 */
class UserPreferences(
    private val dataStore: DataStore<Preferences>,
) {
    private val userKey = stringPreferencesKey("user_info")
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun saveUser(user: User) {
        dataStore.edit { prefs ->
            prefs[userKey] = json.encodeToString(User.serializer(), user)
        }
    }

    suspend fun getUser(): User? {
        val raw = dataStore.data.map { prefs -> prefs[userKey].orEmpty() }.first()
        if (raw.isBlank()) return null
        return runCatching {
            json.decodeFromString(User.serializer(), raw)
        }.getOrNull()
    }

    suspend fun clearUser() {
        dataStore.edit { prefs ->
            prefs.remove(userKey)
        }
    }
}
