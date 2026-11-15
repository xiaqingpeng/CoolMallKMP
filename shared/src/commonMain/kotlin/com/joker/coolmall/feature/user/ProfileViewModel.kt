package com.joker.coolmall.feature.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.AuthRepository
import com.joker.coolmall.core.data.repository.UserRepository
import com.joker.coolmall.core.model.entity.User
import com.joker.coolmall.core.ui.MessageCode
import com.joker.coolmall.core.ui.UiMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val _message = MutableSharedFlow<UiMessage>(extraBufferCapacity = 1)
    val message = _message.asSharedFlow()

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

    fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
        if (oldPassword.isBlank()) {
            _message.tryEmit(UiMessage.Raw("请输入当前密码"))
            return
        }
        if (newPassword.length < 6) {
            _message.tryEmit(UiMessage.Code(MessageCode.PasswordTooShort))
            return
        }
        if (newPassword != confirmPassword) {
            _message.tryEmit(UiMessage.Code(MessageCode.PasswordMismatch))
            return
        }
        viewModelScope.launch {
            runCatching { userRepository.updatePassword(oldPassword, newPassword) }
                .onSuccess { response ->
                    _message.emit(
                        if (response.isSucceeded) UiMessage.Raw("密码修改成功")
                        else UiMessage.Raw(response.message ?: "密码修改失败"),
                    )
                }
                .onFailure { _message.emit(UiMessage.Raw(it.message ?: "密码修改失败")) }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            runCatching { userRepository.logoff() }
                .onSuccess { response ->
                    if (response.isSucceeded) {
                        authRepository.logout()
                        onSuccess()
                    } else {
                        _message.emit(UiMessage.Raw(response.message ?: "注销账号失败"))
                    }
                }
                .onFailure { _message.emit(UiMessage.Raw(it.message ?: "注销账号失败")) }
        }
    }
}
