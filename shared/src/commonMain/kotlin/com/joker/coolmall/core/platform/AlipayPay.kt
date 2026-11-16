package com.joker.coolmallkmp.core.platform

/**
 * 支付宝 App 支付结果。
 * iOS 不实现；Android 调 PayTask.payV2。
 */
enum class AlipayPayStatus {
    Success,
    Cancel,
    Failed,
    Unsupported,
}

data class AlipayPayResult(
    val status: AlipayPayStatus,
    val resultStatus: String = "",
    val memo: String = "",
)

/**
 * 调起支付宝 SDK（orderStr 来自 order/pay/alipayAppPay）。
 * 必须在主线程外/平台允许的上下文调用；Android actual 内部切线程。
 */
expect suspend fun payWithAlipay(orderStr: String): AlipayPayResult
