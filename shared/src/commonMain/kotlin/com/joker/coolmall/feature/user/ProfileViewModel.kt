package com.joker.coolmall.feature.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.AuthRepository
import com.joker.coolmall.core.data.repository.UserRepository
import com.joker.coolmall.core.model.entity.User
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 个人资料：只读展示用户信息 + 退出登录
 */
class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn
    val user: StateFlow<User?> = userRepository.user

    init {
        viewModelScope.launch {
            if (authRepository.isLoggedIn.value) {
                runCatching { userRepository.refreshPersonInfo() }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
