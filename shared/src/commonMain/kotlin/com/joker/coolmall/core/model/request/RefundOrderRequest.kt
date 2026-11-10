package com.joker.coolmall.core.model.request

import kotlinx.serialization.Serializable

/**
 * 退款申请请求（对齐 CoolMallKotlin RefundOrderRequest）
 */
@Serializable
data class RefundOrderRequest(
    val orderId: Long,
    val reason: String,
)
