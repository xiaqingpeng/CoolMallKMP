package com.joker.coolmallkmp.core.platform

import com.alipay.sdk.app.PayTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun payWithAlipay(orderStr: String): AlipayPayResult {
    if (orderStr.isBlank()) {
        return AlipayPayResult(
            status = AlipayPayStatus.Failed,
            memo = "支付参数为空",
        )
    }
    val activity = CurrentActivityHolder.current
        ?: return AlipayPayResult(
            status = AlipayPayStatus.Failed,
            memo = "无法获取 Activity",
        )
    return withContext(Dispatchers.IO) {
        runCatching {
            @Suppress("UNCHECKED_CAST")
            val raw = PayTask(activity).payV2(orderStr, true) as Map<String, String>
            val resultStatus = raw["resultStatus"].orEmpty()
            val memo = raw["memo"].orEmpty()
            val status = when (resultStatus) {
                "9000" -> AlipayPayStatus.Success
                "6001" -> AlipayPayStatus.Cancel
                else -> AlipayPayStatus.Failed
            }
            AlipayPayResult(
                status = status,
                resultStatus = resultStatus,
                memo = memo,
            )
        }.getOrElse { e ->
            AlipayPayResult(
                status = AlipayPayStatus.Failed,
                memo = e.message ?: "支付异常",
            )
        }
    }
}
