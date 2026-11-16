package com.joker.coolmallkmp.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.repository.AuthRepository
import com.joker.coolmallkmp.core.model.entity.Captcha
import com.joker.coolmallkmp.core.ui.MessageCode
import com.joker.coolmallkmp.core.ui.UiMessage
import com.joker.coolmallkmp.core.platform.notifyVerificationCode
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

data class ResetPasswordUiState(
    val phone: String = "",
    val smsCode: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val countdown: Int = 0,
    val captcha: Captcha = Captcha(),
    val imageCode: String = "",
    val showCaptchaDialog: Boolean = false,
    val errorMessage: UiMessage? = null,
) {
    val canSendCode: Boolean
        get() = phone.length >= 11 && countdown == 0 && !isLoading

    val canSubmit: Boolean
        get() = phone.length >= 11 &&
            smsCode.isNotBlank() &&
            newPassword.length >= 6 &&
            newPassword == confirmPassword &&
            !isLoading
}

/**
 * 重置密码：原 CoolMallKotlin 亦为空实现。
 * 发送验证码复用 smsCode；真正 reset API 未确认前只做校验提示。
 */
class ResetPasswordViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    private val _message = MutableSharedFlow<UiMessage>(extraBufferCapacity = 1)
    val message: SharedFlow<UiMessage> = _message.asSharedFlow()

    private val _done = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val done: SharedFlow<Unit> = _done.asSharedFlow()

    private var countdownJob: Job? = null

    fun updatePhone(value: String) {
        _uiState.update { it.copy(phone = value.filter { c -> c.isDigit() }.take(11), errorMessage = null) }
    }

    fun updateSmsCode(value: String) {
        _uiState.update { it.copy(smsCode = value.filter { c -> c.isDigit() }.take(6), errorMessage = null) }
    }

    fun updateNewPassword(value: String) {
        _uiState.update { it.copy(newPassword = value, errorMessage = null) }
    }

    fun updateConfirmPassword(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    }

    fun updateImageCode(value: String) {
        _uiState.update { it.copy(imageCode = value.filter(Char::isDigit).take(4)) }
    }

    fun dismissCaptcha() {
        _uiState.update { it.copy(showCaptchaDialog = false, imageCode = "") }
    }

    fun refreshCaptcha() {
        viewModelScope.launch {
            runCatching { authRepository.getCaptcha() }.onSuccess { response ->
                if (response.isSucceeded && response.data != null) {
                    _uiState.update { it.copy(captcha = response.data, imageCode = "") }
                }
            }
        }
    }

    fun sendCode() {
        val state = _uiState.value
        if (!state.canSendCode) return
        if (state.captcha.captchaId.isBlank() || state.captcha.data.isBlank()) {
            _uiState.update { it.copy(showCaptchaDialog = true) }
            refreshCaptcha()
            return
        }
        if (state.imageCode.length != 4) {
            _uiState.update { it.copy(showCaptchaDialog = true) }
            return
        }
        sendCodeWithCaptcha(state)
    }

    fun confirmCaptcha() {
        if (_uiState.value.imageCode.length != 4) return
        val state = _uiState.value
        dismissCaptcha()
        sendCodeWithCaptcha(state)
    }

    private fun sendCodeWithCaptcha(state: ResetPasswordUiState) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                authRepository.sendSmsCode(
                    phone = state.phone,
                    captchaId = state.captcha.captchaId,
                    code = state.imageCode,
                )
            }.onSuccess { response ->
                _uiState.update { it.copy(isLoading = false) }
                if (response.isSucceeded) {
                    response.data?.takeIf { it.isNotBlank() }?.let(::notifyVerificationCode)
                    _message.emit(UiMessage.Code(MessageCode.CodeSent))
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
                        isLoading = false,
                        errorMessage = error.message?.let { msg -> UiMessage.Raw(msg) }
                            ?: UiMessage.Code(MessageCode.NetworkError),
                    )
                }
            }
        }
    }

    fun submit() {
        val state = _uiState.value
        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = UiMessage.Code(MessageCode.PasswordMismatch)) }
            return
        }
        if (state.newPassword.length < 6) {
            _uiState.update { it.copy(errorMessage = UiMessage.Code(MessageCode.PasswordTooShort)) }
            return
        }
        viewModelScope.launch {
            // 后端路径未确认：原工程 TODO。提示用户并返回（不伪造成功）。
            _message.emit(UiMessage.Code(MessageCode.ResetPasswordPending))
        }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (i in 60 downTo 1) {
                _uiState.update { it.copy(countdown = i) }
                delay(1000)
            }
            _uiState.update { it.copy(countdown = 0) }
        }
    }
}
