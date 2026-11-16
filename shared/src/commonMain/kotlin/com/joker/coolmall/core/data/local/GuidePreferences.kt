package com.joker.coolmallkmp.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * 引导页是否已展示（替代 Android MMKV guide_shown）
 */
class GuidePreferences(
    private val dataStore: DataStore<Preferences>,
) {
    private val guideShownKey = booleanPreferencesKey("guide_shown")

    suspend fun isGuideShown(): Boolean {
        return dataStore.data.map { prefs -> prefs[guideShownKey] ?: false }.first()
    }

    suspend fun setGuideShown(shown: Boolean = true) {
        dataStore.edit { prefs ->
            prefs[guideShownKey] = shown
        }
    }
}
