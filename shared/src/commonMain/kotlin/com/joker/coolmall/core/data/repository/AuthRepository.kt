package com.joker.coolmall.core.data.repository

import com.joker.coolmall.core.data.local.AuthPreferences
import com.joker.coolmall.core.model.entity.Auth
import com.joker.coolmall.core.model.response.NetworkResponse
import com.joker.coolmall.core.network.KtorAuthNetworkDataSource
import com.joker.coolmall.core.session.AuthSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AuthRepository(
    private val dataSource: KtorAuthNetworkDataSource,
    private val authPreferences: AuthPreferences,
    private val userRepository: UserRepository,
) {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    suspend fun restoreSession() {
        val auth = authPreferences.getAuth()
        val now = currentTimeMillis()
        if (auth != null && auth.token.isNotBlank() && !auth.isExpired(now)) {
            AuthSession.update(auth.token, auth.refreshToken)
            _isLoggedIn.value = true
            userRepository.restoreFromCache()
            // 后台刷新资料，失败不影响会话
            runCatching { userRepository.refreshPersonInfo() }
        } else {
            if (auth != null) {
                authPreferences.clearAuth()
            }
            AuthSession.clear()
            userRepository.clear()
            _isLoggedIn.value = false
        }
    }

    suspend fun loginByPassword(
        phone: String,
        password: String,
    ): NetworkResponse<Auth> = withContext(Dispatchers.IO) {
        dataSource.loginByPassword(
            mapOf(
                "phone" to phone,
                "password" to password,
            )
        ).also { response ->
            response.data?.let { persistAuth(it) }
        }
    }

    suspend fun loginByPhone(
        phone: String,
        smsCode: String,
    ): NetworkResponse<Auth> = withContext(Dispatchers.IO) {
        dataSource.loginByPhone(
            mapOf(
                "phone" to phone,
                "smsCode" to smsCode,
            )
        ).also { response ->
            response.data?.let { persistAuth(it) }
        }
    }

    suspend fun logout() {
        authPreferences.clearAuth()
        AuthSession.clear()
        userRepository.clear()
        _isLoggedIn.value = false
    }

    private suspend fun persistAuth(auth: Auth) {
        val now = currentTimeMillis()
        val stored = if (auth.createdAt == 0L) auth.copy(createdAt = now) else auth
        authPreferences.saveAuth(stored)
        AuthSession.update(stored.token, stored.refreshToken)
        _isLoggedIn.value = stored.token.isNotBlank()
        if (_isLoggedIn.value) {
            runCatching { userRepository.refreshPersonInfo() }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun currentTimeMillis(): Long =
        Clock.System.now().toEpochMilliseconds()

    /**
     * 发送短信验证码。
     */
    suspend fun sendSmsCode(phone: String): NetworkResponse<String> = withContext(Dispatchers.IO) {
        dataSource.getSmsCode(mapOf("phone" to phone))
    }

    /**
     * 短信验证码登录。
     */
    suspend fun loginBySms(
        phone: String,
        smsCode: String,
    ): NetworkResponse<Auth> = loginByPhone(phone = phone, smsCode = smsCode)

    /**
     * 注册（原工程走 user/login/register，返回 Auth 直接登录）。
     */
    suspend fun register(
        phone: String,
        password: String,
        smsCode: String,
    ): NetworkResponse<Auth> = withContext(Dispatchers.IO) {
        dataSource.register(
            mapOf(
                "phone" to phone,
                "password" to password,
                "smsCode" to smsCode,
            )
        ).also { response ->
            response.data?.let { persistAuth(it) }
        }
    }

    /**
     * QQ App 登录（Android optional；需先由 SDK 拿到 accessToken/openId）。
     */
    suspend fun loginByQqApp(
        accessToken: String,
        openId: String,
        extra: Map<String, String> = emptyMap(),
    ): NetworkResponse<Auth> = withContext(Dispatchers.IO) {
        val params = buildMap {
            put("accessToken", accessToken)
            put("openId", openId)
            putAll(extra)
        }
        dataSource.loginByQqApp(params).also { response ->
            response.data?.let { persistAuth(it) }
        }
    }

    /**
     * 微信 App 登录（Android optional；需先由 SDK 拿到 code 等参数）。
     */
    suspend fun loginByWxApp(
        code: String,
        extra: Map<String, String> = emptyMap(),
    ): NetworkResponse<Auth> = withContext(Dispatchers.IO) {
        val params = buildMap {
            put("code", code)
            putAll(extra)
        }
        dataSource.loginByWxApp(params).also { response ->
            response.data?.let { persistAuth(it) }
        }
    }
}
