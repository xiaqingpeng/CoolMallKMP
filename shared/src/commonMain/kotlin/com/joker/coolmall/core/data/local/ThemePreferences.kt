package com.joker.coolmall.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
    ;

    companion object {
        fun fromStorage(value: String?): ThemeMode =
            entries.find { it.name == value } ?: SYSTEM
    }
}

enum class ThemeColor {
    DEFAULT,
    PURPLE,
    GREEN,
    ORANGE,
    RED,
    ;

    companion object {
        fun fromStorage(value: String?): ThemeColor =
            entries.find { it.name == value } ?: DEFAULT
    }
}

/**
 * 外观主题（DataStore）
 */
class ThemePreferences(
    private val dataStore: DataStore<Preferences>,
) {
    private val themeModeKey = stringPreferencesKey("theme_mode")
    private val themeColorKey = stringPreferencesKey("theme_color")

    val themeMode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        ThemeMode.fromStorage(prefs[themeModeKey])
    }
    val themeColor: Flow<ThemeColor> = dataStore.data.map { prefs ->
        ThemeColor.fromStorage(prefs[themeColorKey])
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[themeModeKey] = mode.name
        }
    }

    suspend fun setThemeColor(color: ThemeColor) {
        dataStore.edit { prefs -> prefs[themeColorKey] = color.name }
    }
}
