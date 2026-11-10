package com.joker.coolmall.core.platform

/**
 * Android 端能力开关。
 * 支付宝已接 PayTask；QQ/微信登录与微信支付为 optional 骨架（未装 SDK 时关闭入口）。
 * 接入真 SDK 后将对应 supports* 改为 true，并在 [ThirdPartyLogin] 中实现授权回调。
 */
actual object ThirdPartyCapabilities {
    actual val supportsQqLogin: Boolean = false
    actual val supportsWeChatLogin: Boolean = false
    actual val supportsAlipay: Boolean = true
    actual val supportsWeChatPay: Boolean = false
}

actual object ThirdPartyLogin {
    actual suspend fun loginWithQq(): ThirdPartyLoginResult {
        if (!ThirdPartyCapabilities.supportsQqLogin) {
            return ThirdPartyLoginResult.NotConfigured("QQ 登录 SDK 未接入")
        }
        // TODO: 接入腾讯 QQ SDK 后在此拉起授权并回填 token
        return ThirdPartyLoginResult.NotConfigured("QQ 登录 SDK 未接入")
    }

    actual suspend fun loginWithWeChat(): ThirdPartyLoginResult {
        if (!ThirdPartyCapabilities.supportsWeChatLogin) {
            return ThirdPartyLoginResult.NotConfigured("微信登录 SDK 未接入")
        }
        // TODO: 接入微信 OpenSDK 后在此拉起授权并回填 code/token
        return ThirdPartyLoginResult.NotConfigured("微信登录 SDK 未接入")
    }
}
