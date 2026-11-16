package com.joker.coolmallkmp.core.platform

/** iOS 明确不做支付宝 */
actual suspend fun payWithAlipay(orderStr: String): AlipayPayResult =
    AlipayPayResult(
        status = AlipayPayStatus.Unsupported,
        memo = "iOS 不支持支付宝支付",
    )
