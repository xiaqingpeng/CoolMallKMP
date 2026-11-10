package com.joker.coolmall.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.AuthRepository
import com.joker.coolmall.core.ui.MessageCode
import com.joker.coolmall.core.ui.UiMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val phone: String = "",
    val smsCode: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isSendingCode: Boolean = false,
    val countdown: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: UiMessage? = null,
) {
    private val isPhoneValid: Boolean get() = phone.length == 11 && phone.all { it.isDigit() }
    private val isCodeValid: Boolean get() = smsCode.length in 4..6 && smsCode.all { it.isDigit() }
    private val isPasswordValid: Boolean get() = password.length >= 6
    private val isConfirmMatched: Boolean get() = password == confirmPassword

    val canSendCode: Boolean get() = isPhoneValid && countdown == 0 && !isSendingCode

    val canRegister: Boolean
        get() = isPhoneValid && isCodeValid && isPasswordValid && isConfirmMatched && !isLoading
}

/**
 * 注册（对齐 CoolMallKotlin Register：手机号 + 验证码 + 密码）
 */
class RegisterViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _registerSuccess = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val registerSuccess: SharedFlow<Unit> = _registerSuccess.asSharedFlow()

    private var countdownJob: Job? = null

    fun updatePhone(value: String) = _uiState.update {
        it.copy(phone = value.filter { c -> c.isDigit() }.take(11), errorMessage = null)
    }

    fun updateSmsCode(value: String) = _uiState.update {
        it.copy(smsCode = value.filter { c -> c.isDigit() }.take(6), errorMessage = null)
    }

    fun updatePassword(value: String) = _uiState.update {
        it.copy(password = value, errorMessage = null)
    }

    fun updateConfirmPassword(value: String) = _uiState.update {
        it.copy(confirmPassword = value, errorMessage = null)
    }

    fun sendSmsCode() {
        val state = _uiState.value
        if (state.phone.length != 11) {
            _uiState.update { it.copy(errorMessage = UiMessage.Code(MessageCode.InvalidPhone)) }
            return
        }
        if (state.countdown > 0) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSendingCode = true, errorMessage = null) }
            runCatching {
                authRepository.sendSmsCode(state.phone)
            }.onSuccess { response ->
                _uiState.update { it.copy(isSendingCode = false) }
                if (response.isSucceeded) {
                    startCountdown()
                } else {
                    _uiState.update {
                        it.copy(
                            errorMessage = response.message?.let { UiMessage.Raw(it) }
                                ?: UiMessage.Code(MessageCode.SendCodeFailed, response.code.toString()),
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSendingCode = false,
                        errorMessage = error.message?.let { msg -> UiMessage.Raw(msg) }
                            ?: UiMessage.Code(MessageCode.NetworkError),
                    )
                }
            }
        }
    }

    private fun startCountdown(seconds: Int = 60) {
        countdownJob?.cancel()
        _uiState.update { it.copy(countdown = seconds) }
        countdownJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                delay(1000)
                remaining -= 1
                _uiState.update { it.copy(countdown = remaining) }
            }
        }
    }

    fun register() {
        val state = _uiState.value
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = UiMessage.Code(MessageCode.PasswordMismatch)) }
            return
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = UiMessage.Code(MessageCode.PasswordTooShort)) }
            return
        }
        if (!state.canRegister) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                authRepository.register(
                    phone = state.phone,
                    password = state.password,
                    smsCode = state.smsCode,
                )
            }.onSuccess { response ->
                _uiState.update { it.copy(isLoading = false) }
                if (response.isSucceeded && response.data != null) {
                    _registerSuccess.emit(Unit)
                } else {
                    _uiState.update {
                        it.copy(
                            errorMessage = response.message?.let { UiMessage.Raw(it) }
                                ?: UiMessage.Code(MessageCode.RegisterFailed, response.code.toString()),
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message?.let { msg -> UiMessage.Raw(msg) }
                            ?: UiMessage.Code(MessageCode.NetworkError),
                    )
                }
            }
        }
    }
}
