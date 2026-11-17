package com.joker.coolmallkmp.feature.launch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.local.GuidePreferences
import com.joker.coolmallkmp.core.data.local.SplashPreferences
import com.joker.coolmallkmp.core.data.repository.AuthRepository
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
    private var sessionRestored = false
    private var animationFinished = false

    init {
        viewModelScope.launch {
            runCatching { authRepository.restoreSession() }
            sessionRestored = true
            if (splashPreferences?.showSplash?.first() == false) {
                animationFinished = true
            }
            navigateIfReady()
        }
    }

    /**
     * 由 Compose Splash 动画完成后调用。
     *
     * 登录态恢复和启动动画并行执行，但只有两者都完成后才进入下一页，
     * 保持原项目固定的启动页展示节奏。
     */
    fun onSplashAnimationFinished() {
        viewModelScope.launch {
            animationFinished = true
            navigateIfReady()
        }
    }

    private suspend fun navigateIfReady() {
        if (!sessionRestored || !animationFinished || _destination.value != null) return
        _destination.value = if (guidePreferences.isGuideShown()) {
            SplashDestination.Main
        } else {
            SplashDestination.Guide
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

