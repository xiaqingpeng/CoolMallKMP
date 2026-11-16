package com.joker.coolmallkmp.core.session

/**
 * 进程内当前登录 token，供 Ktor Authorization 头读取。
 * 持久化由 [com.joker.coolmallkmp.core.data.local.AuthPreferences] 负责，
 * 启动时通过 [com.joker.coolmallkmp.core.data.repository.AuthRepository.restoreSession] 恢复。
 */
object AuthSession {
    private var _token: String = ""
    private var _refreshToken: String = ""

    val token: String
        get() = _token

    val refreshToken: String
        get() = _refreshToken

    fun update(token: String, refreshToken: String = "") {
        _token = token.trim()
        _refreshToken = refreshToken.trim()
        networkAuthLog(
            "session updated token=${maskToken(_token)} refreshToken=${maskToken(_refreshToken)}",
        )
    }

    fun clear() {
        _token = ""
        _refreshToken = ""
        networkAuthLog("session cleared")
    }

    fun tokenSummary(): String = maskToken(_token)

    private fun maskToken(value: String): String =
        if (value.isBlank()) "<empty>" else "len=${value.length},prefix=${value.take(8)}***"
}

expect fun networkAuthLog(message: String)
