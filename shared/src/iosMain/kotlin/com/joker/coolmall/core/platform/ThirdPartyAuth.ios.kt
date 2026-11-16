package com.joker.coolmallkmp.core.platform

/** iOS：QQ / 微信 / 支付宝均不实现 */
actual object ThirdPartyCapabilities {
    actual val supportsQqLogin: Boolean = false
    actual val supportsWeChatLogin: Boolean = false
    actual val supportsAlipay: Boolean = false
    actual val supportsWeChatPay: Boolean = false
}

actual object ThirdPartyLogin {
    actual suspend fun loginWithQq(): ThirdPartyLoginResult =
        ThirdPartyLoginResult.NotConfigured("iOS 不支持 QQ 登录")

    actual suspend fun loginWithWeChat(): ThirdPartyLoginResult =
        ThirdPartyLoginResult.NotConfigured("iOS 不支持微信登录")
}
