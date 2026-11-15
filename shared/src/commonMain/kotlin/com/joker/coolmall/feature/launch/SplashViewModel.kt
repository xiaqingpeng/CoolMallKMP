package com.joker.coolmall.feature.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.local.GuidePreferences
import com.joker.coolmall.core.data.local.SplashPreferences
import com.joker.coolmall.core.data.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SplashDestination {
    data object Guide : SplashDestination
    data object Main : SplashDestination
}

class SplashViewModel(
    private val guidePreferences: GuidePreferences,
    private val authRepository: AuthRepository,
    private val splashPreferences: SplashPreferences? = null,
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { authRepository.restoreSession() }
            if (splashPreferences?.showSplash?.first() != false) {
                delay(1200)
            }
            _destination.value = if (guidePreferences.isGuideShown()) {
                SplashDestination.Main
            } else {
                SplashDestination.Guide
            }
        }
    }

    /** 跳过启动页：同时记为已看过引导，避免冷启反复进引导 */
    fun skipToMain() {
        viewModelScope.launch {
            guidePreferences.setGuideShown(true)
            _destination.value = SplashDestination.Main
        }
    }
}

