package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.Serializable

/**
 * 认证令牌模型（KMP 版）
 */
@Serializable
data class Auth(
    val token: String = "",
    val refreshToken: String = "",
    val expire: Long = 0,
    val refreshExpire: Long = 0,
    val createdAt: Long = 0,
) {
    fun isExpired(nowMillis: Long = createdAt): Boolean {
        if (createdAt == 0L || expire == 0L) return false
        val expirationTime = createdAt + (expire * 1000)
        return nowMillis >= expirationTime
    }

    fun isRefreshTokenExpired(nowMillis: Long = createdAt): Boolean {
        if (createdAt == 0L || refreshExpire == 0L) return false
        val expirationTime = createdAt + (refreshExpire * 1000)
        return nowMillis >= expirationTime
    }

    fun shouldRefresh(nowMillis: Long): Boolean {
        if (createdAt == 0L || expire == 0L) return false
        val refreshTime = createdAt + (expire * 1000) - (15 * 60 * 1000)
        return nowMillis >= refreshTime && !isRefreshTokenExpired(nowMillis)
    }
}
