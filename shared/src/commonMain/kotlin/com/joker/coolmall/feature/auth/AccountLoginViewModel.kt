package com.joker.coolmallkmp.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.repository.AuthRepository
import com.joker.coolmallkmp.core.platform.ThirdPartyCapabilities
import com.joker.coolmallkmp.core.platform.ThirdPartyLogin
import com.joker.coolmallkmp.core.platform.ThirdPartyLoginResult
import com.joker.coolmallkmp.core.ui.MessageCode
import com.joker.coolmallkmp.core.ui.UiMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class AccountLoginUiState(
    val phone: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: UiMessage? = null,
    val isLoginEnabled: Boolean = false,
)

class AccountLoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val phone = MutableStateFlow("")
    private val password = MutableStateFlow("")
    private val isLoading = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<UiMessage?>(null)

    private val _uiState = MutableStateFlow(AccountLoginUiState())
    val uiState: StateFlow<AccountLoginUiState> = _uiState.asStateFlow()

    private val _loginSuccess = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val loginSuccess: SharedFlow<Unit> = _loginSuccess.asSharedFlow()

    val showQqLogin: Boolean get() = ThirdPartyCapabilities.supportsQqLogin
    val showWeChatLogin: Boolean get() = ThirdPartyCapabilities.supportsWeChatLogin

    init {
        viewModelScope.launch {
            combine(phone, password, isLoading, errorMessage) { phoneValue, passwordValue, loading, error ->
                AccountLoginUiState(
                    phone = phoneValue,
                    password = passwordValue,
                    isLoading = loading,
                    errorMessage = error,
                    isLoginEnabled = isValidPhone(phoneValue) &&
                        isValidPassword(passwordValue) &&
                        !loading,
                )
            }.collect { _uiState.value = it }
        }
    }

    fun updatePhone(value: String) {
        phone.value = value.trim()
        errorMessage.value = null
    }

    fun updatePassword(value: String) {
        password.value = value
        errorMessage.value = null
    }

    fun login() {
        val phoneValue = phone.value
        val passwordValue = password.value
        if (!isValidPhone(phoneValue)) {
            errorMessage.value = UiMessage.Code(MessageCode.InvalidPhone)
            return
        }
        if (!isValidPassword(passwordValue)) {
            errorMessage.value = UiMessage.Code(MessageCode.PasswordTooShort)
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            runCatching {
                authRepository.loginByPassword(phoneValue, passwordValue)
            }.onSuccess { response ->
                isLoading.value = false
                if (response.isSucceeded && response.data != null) {
                    _loginSuccess.emit(Unit)
                } else {
                    errorMessage.value = response.message?.let { UiMessage.Raw(it) }
                        ?: UiMessage.Code(MessageCode.LoginFailed, response.code.toString())
                }
            }.onFailure { error ->
                isLoading.value = false
                errorMessage.value = error.message?.let { UiMessage.Raw(it) }
                    ?: UiMessage.Code(MessageCode.NetworkError)
            }
        }
    }

    /**
     * Android optional：先走 SDK stub，成功后再调后端 QQ 登录。
     * 未配置 SDK 时 toast 提示，不崩溃。
     */
    fun loginWithQq() {
        if (!ThirdPartyCapabilities.supportsQqLogin) {
            errorMessage.value = UiMessage.Code(MessageCode.ThirdPartyNotConfigured)
            return
        }
        if (isLoading.value) return
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            when (val sdk = ThirdPartyLogin.loginWithQq()) {
                is ThirdPartyLoginResult.Success -> {
                    runCatching {
                        authRepository.loginByQqApp(
                            accessToken = sdk.accessToken,
                            openId = sdk.openId,
                            extra = sdk.extra,
                        )
                    }.onSuccess { response ->
                        isLoading.value = false
                        if (response.isSucceeded && response.data != null) {
                            _loginSuccess.emit(Unit)
                        } else {
                            errorMessage.value = response.message?.let { UiMessage.Raw(it) }
                                ?: UiMessage.Code(MessageCode.LoginFailed, response.code.toString())
                        }
                    }.onFailure { error ->
                        isLoading.value = false
                        errorMessage.value = error.message?.let { UiMessage.Raw(it) }
                            ?: UiMessage.Code(MessageCode.NetworkError)
                    }
                }
                ThirdPartyLoginResult.Cancelled -> {
                    isLoading.value = false
                }
                is ThirdPartyLoginResult.Failed -> {
                    isLoading.value = false
                    errorMessage.value = UiMessage.Raw(sdk.message)
                }
                is ThirdPartyLoginResult.NotConfigured -> {
                    isLoading.value = false
                    errorMessage.value = UiMessage.Raw(sdk.message)
                }
            }
        }
    }

    /**
     * Android optional：微信登录骨架；未配置时提示。
     */
    fun loginWithWeChat() {
        if (!ThirdPartyCapabilities.supportsWeChatLogin) {
            errorMessage.value = UiMessage.Code(MessageCode.ThirdPartyNotConfigured)
            return
        }
        if (isLoading.value) return
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            when (val sdk = ThirdPartyLogin.loginWithWeChat()) {
                is ThirdPartyLoginResult.Success -> {
                    val code = sdk.accessToken.ifBlank {
                        sdk.extra["code"].orEmpty()
                    }
                    runCatching {
                        authRepository.loginByWxApp(code = code, extra = sdk.extra)
                    }.onSuccess { response ->
                        isLoading.value = false
                        if (response.isSucceeded && response.data != null) {
                            _loginSuccess.emit(Unit)
                        } else {
                            errorMessage.value = response.message?.let { UiMessage.Raw(it) }
                                ?: UiMessage.Code(MessageCode.LoginFailed, response.code.toString())
                        }
                    }.onFailure { error ->
                        isLoading.value = false
                        errorMessage.value = error.message?.let { UiMessage.Raw(it) }
                            ?: UiMessage.Code(MessageCode.NetworkError)
                    }
                }
                ThirdPartyLoginResult.Cancelled -> {
                    isLoading.value = false
                }
                is ThirdPartyLoginResult.Failed -> {
                    isLoading.value = false
                    errorMessage.value = UiMessage.Raw(sdk.message)
                }
                is ThirdPartyLoginResult.NotConfigured -> {
                    isLoading.value = false
                    errorMessage.value = UiMessage.Raw(sdk.message)
                }
            }
        }
    }

    private fun isValidPhone(value: String): Boolean =
        value.length == 11 && value.all { it.isDigit() }

    private fun isValidPassword(value: String): Boolean =
        value.length >= 6
}
