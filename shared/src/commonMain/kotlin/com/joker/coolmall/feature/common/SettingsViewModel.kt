package com.joker.coolmallkmp.feature.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.local.ThemeColor
import com.joker.coolmallkmp.core.data.local.ThemeMode
import com.joker.coolmallkmp.core.data.local.ThemePreferences
import com.joker.coolmallkmp.core.data.local.SplashPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val themePreferences: ThemePreferences,
    private val splashPreferences: SplashPreferences? = null,
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = themePreferences.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ThemeMode.SYSTEM,
        )
    val themeColor: StateFlow<ThemeColor> = themePreferences.themeColor
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeColor.DEFAULT)
    val showSplash: StateFlow<Boolean> = (splashPreferences?.showSplash
        ?: kotlinx.coroutines.flow.flowOf(true))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themePreferences.setThemeMode(mode)
        }
    }

    fun setThemeColor(color: ThemeColor) {
        viewModelScope.launch { themePreferences.setThemeColor(color) }
    }

    fun setShowSplash(show: Boolean) {
        viewModelScope.launch { splashPreferences?.setShowSplash(show) }
    }
}
