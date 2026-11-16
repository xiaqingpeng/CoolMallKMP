package com.joker.coolmallkmp.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SplashPreferences(
    private val dataStore: DataStore<Preferences>,
) {
    private val showSplashKey = booleanPreferencesKey("show_splash")

    val showSplash: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[showSplashKey] ?: true
    }

    suspend fun setShowSplash(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[showSplashKey] = show
        }
    }
}
