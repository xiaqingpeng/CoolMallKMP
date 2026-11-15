package com.joker.coolmall.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.AuthRepository
import com.joker.coolmall.core.model.entity.Captcha
import com.joker.coolmall.core.ui.MessageCode
import com.joker.coolmall.core.ui.UiMessage
import com.joker.coolmall.core.platform.notifyVerificationCode
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

data class SmsLoginUiState(
    val phone: String = "",
    val smsCode: String = "",
    val isSendingCode: Boolean = false,
    val countdown: Int = 0,
    val isLoading: Boolean = false,
    val captcha: Captcha = Captcha(),
    val imageCode: String = "",
    val showCaptchaDialog: Boolean = false,
    val errorMessage: UiMessage? = null,
) {
    val isPhoneValid: Boolean get() = phone.length == 11 && phone.all { it.isDigit() }

    /** 验证码 4~6 位 */
    val isCodeValid: Boolean get() = smsCode.length in 4..6 && smsCode.all { it.isDigit() }

    val canSendCode: Boolean
        get() = isPhoneValid && countdown == 0 && !isSendingCode

    val canLogin: Boolean get() = isPhoneValid && isCodeValid && !isLoading
}

/**
 * 短信验证码登录（对齐 CoolMallKotlin SmsLogin，Android/iOS 通用，无三方 SDK）
 */
class SmsLoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SmsLoginUiState())
    val uiState: StateFlow<SmsLoginUiState> = _uiState.asStateFlow()

    private val _loginSuccess = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val loginSuccess: SharedFlow<Unit> = _loginSuccess.asSharedFlow()

    private var countdownJob: Job? = null

    fun updatePhone(value: String) {
        _uiState.update { it.copy(phone = value.filter { c -> c.isDigit() }.take(11), errorMessage = null) }
    }

    fun updateSmsCode(value: String) {
        _uiState.update { it.copy(smsCode = value.filter { c -> c.isDigit() }.take(6), errorMessage = null) }
    }

    fun updateImageCode(value: String) {
        _uiState.update { it.copy(imageCode = value.filter { c -> c.isDigit() }.take(4), errorMessage = null) }
    }

    fun dismissCaptcha() {
        _uiState.update { it.copy(showCaptchaDialog = false, imageCode = "") }
    }

    fun refreshCaptcha() {
        viewModelScope.launch {
            runCatching { authRepository.getCaptcha() }
                .onSuccess { response ->
                    if (response.isSucceeded && response.data != null) {
                        _uiState.update {
                            it.copy(captcha = response.data, imageCode = "", errorMessage = null)
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                errorMessage = response.message?.let(UiMessage::Raw)
                                    ?: UiMessage.Code(MessageCode.NetworkError),
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            errorMessage = error.message?.let(UiMessage::Raw)
                                ?: UiMessage.Code(MessageCode.NetworkError),
                        )
                    }
                }
        }
    }

    /**
     * 发送短信验证码，成功后启动 60 秒倒计时。
     */
    fun sendSmsCode() {
        val state = _uiState.value
        if (!state.isPhoneValid) {
            _uiState.update { it.copy(errorMessage = UiMessage.Code(MessageCode.InvalidPhone)) }
            return
        }
        if (state.countdown > 0) return
        if (state.captcha.captchaId.isBlank() || state.captcha.data.isBlank()) {
            _uiState.update { it.copy(showCaptchaDialog = true, imageCode = "") }
            refreshCaptcha()
            return
        }
        if (state.imageCode.length != 4) {
            _uiState.update { it.copy(showCaptchaDialog = true) }
            return
        }
        sendSmsCodeWithCaptcha(state)
    }

    fun confirmCaptcha() {
        val state = _uiState.value
        if (state.imageCode.length != 4) return
        dismissCaptcha()
        sendSmsCodeWithCaptcha(state)
    }

    private fun sendSmsCodeWithCaptcha(state: SmsLoginUiState) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSendingCode = true, errorMessage = null) }
            runCatching {
                authRepository.sendSmsCode(
                    phone = state.phone,
                    captchaId = state.captcha.captchaId,
                    code = state.imageCode,
                )
            }.onSuccess { response ->
                _uiState.update { it.copy(isSendingCode = false) }
                if (response.isSucceeded) {
                    response.data?.takeIf { it.isNotBlank() }?.let { code ->
                        // Chucker publishes its response notification asynchronously. Keep
                        // the same order as the Android reference app: network activity first,
                        // then the verification-code notification.
                        viewModelScope.launch {
                            delay(350)
                            notifyVerificationCode(code)
                        }
                    }
                    startCountdown()
                } else {
                    _uiState.update {
                        it.copy(
                            errorMessage = response.message?.let { UiMessage.Raw(it) }
                                ?: UiMessage.Code(MessageCode.SendCodeFailed, response.code.toString()),
                            captcha = Captcha(),
                            imageCode = "",
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSendingCode = false,
                        errorMessage = error.message?.let { msg -> UiMessage.Raw(msg) }
                            ?: UiMessage.Code(MessageCode.NetworkError),
                        captcha = Captcha(),
                        imageCode = "",
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

    fun login() {
        val state = _uiState.value
        if (!state.isPhoneValid) {
            _uiState.update { it.copy(errorMessage = UiMessage.Code(MessageCode.InvalidPhone)) }
            return
        }
        if (!state.isCodeValid) {
            _uiState.update { it.copy(errorMessage = UiMessage.Code(MessageCode.InvalidSmsCode)) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                authRepository.loginBySms(state.phone, state.smsCode)
            }.onSuccess { response ->
                _uiState.update { it.copy(isLoading = false) }
                if (response.isSucceeded && response.data != null) {
                    _loginSuccess.emit(Unit)
                } else {
                    _uiState.update {
                        it.copy(
                            errorMessage = response.message?.let { UiMessage.Raw(it) }
                                ?: UiMessage.Code(MessageCode.LoginFailed, response.code.toString()),
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
