package com.joker.coolmall.core.platform

/**
 * 三方登录 / 支付能力开关。
 * iOS 明确不做 QQ / 微信 / 支付宝；Android 可按需接 SDK，未配置时 stub 返回 NotConfigured。
 */
expect object ThirdPartyCapabilities {
    val supportsQqLogin: Boolean
    val supportsWeChatLogin: Boolean
    val supportsAlipay: Boolean
    val supportsWeChatPay: Boolean
}

/**
 * 三方登录结果（optional 骨架；真 SDK 接入后填 accessToken 等字段）。
 */
sealed class ThirdPartyLoginResult {
    data class Success(
        val accessToken: String,
        val openId: String = "",
        val extra: Map<String, String> = emptyMap(),
    ) : ThirdPartyLoginResult()

    data object Cancelled : ThirdPartyLoginResult()

    data class Failed(val message: String) : ThirdPartyLoginResult()

    /**
     * 当前构建未接入对应 SDK 或能力关闭。
     */
    data class NotConfigured(val message: String = "未配置三方登录 SDK") : ThirdPartyLoginResult()
}

/**
 * Android optional：未装 SDK 时返回 [ThirdPartyLoginResult.NotConfigured]。
 * iOS：始终 NotConfigured，且 UI 层不展示入口。
 */
expect object ThirdPartyLogin {
    suspend fun loginWithQq(): ThirdPartyLoginResult
    suspend fun loginWithWeChat(): ThirdPartyLoginResult
}
