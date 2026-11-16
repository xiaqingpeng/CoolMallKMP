package com.joker.coolmallkmp.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.joker.coolmallkmp.core.model.entity.Auth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

/**
 * 认证信息本地持久化（替代 Android MMKV AuthStore）。
 */
class AuthPreferences(
    private val dataStore: DataStore<Preferences>,
) {
    private val authKey = stringPreferencesKey("auth_info")
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun saveAuth(auth: Auth) {
        dataStore.edit { prefs ->
            prefs[authKey] = json.encodeToString(Auth.serializer(), auth)
        }
    }

    suspend fun getAuth(): Auth? {
        val authJson = dataStore.data.map { prefs -> prefs[authKey].orEmpty() }.first()
        if (authJson.isBlank()) return null
        return runCatching {
            json.decodeFromString(Auth.serializer(), authJson)
        }.getOrNull()
    }

    suspend fun getToken(): String? = getAuth()?.token?.takeIf { it.isNotBlank() }

    suspend fun clearAuth() {
        dataStore.edit { prefs ->
            prefs.remove(authKey)
        }
    }
}
